package com.noknown.framework.cache.file.impl;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.noknown.framework.cache.file.FileCache;
import com.noknown.framework.cache.file.FileCacheOptions;
import com.noknown.framework.cache.file.PackedFile;
import com.noknown.framework.cache.file.ReleaseList;
import com.noknown.framework.common.util.BaseUtil;
import com.noknown.framework.common.util.StringUtil;
import com.noknown.framework.kv.BaseRocksDb;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.rocksdb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import sun.misc.Cleaner;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * FileCache的LRU实现，可以通过LRU的模式实现缓存自动清理
 *
 * @author guodong
 */
public class LruFileCache implements FileCache {

	private static final Logger logger = LoggerFactory.getLogger(LruFileCache.class);

	/**
	 * 当个cache目录名
	 */
	private static final String CACHE_IDENTIFY = "cache";
	/**
	 * 打包小文件目录名
	 */
	private static final String PACKED_IDENTIFY = "packed";
	/**
	 * 小文件索引数据库根目录
	 */
	private static final String PACKED_INDEX_DB_ROOT = "index";
	/**
	 * 全局索引DB
	 */
	private static final String GLOBAL_DB = "global";
	/**
	 * packed包含的key索引
	 */
	private static final String PACKED_DB = "packed";
	private static final int SUB_DIR_LENGTH = 2;
	/**
	 * 默认cache回收的空间比例
	 */
	private static final float DEFAULT_CLEAN_RADIO = (float) 0.4;
	/**
	 * 最小可用空间，如果小于该空间，cache不再可写，需要强制进行同步回收
	 */
	private static final long MIN_USABLESPACE = 1024 * 1024L * 1024 * 5L;
	private final ExecutorService executor = new ThreadPoolExecutor(2, 2, 0L, TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<>(1024), new ThreadFactoryBuilder().setNameFormat("CacheCleanThread-%d").build());
	private volatile boolean needRecovery;
	/**
	 * cache config
	 */
	private FileCacheOptions options;
	private final Map<String, RandomAccessFile> raMap = new ConcurrentHashMap<>();
	private final Map<String, FileChannel> channelMap = new ConcurrentHashMap<>();
	private final BlockingQueue<Collection<PackedFile>> packedFileQueue = new LinkedBlockingQueue<>();
	private final BlockingQueue<ReleaseList> releaseListQuery = new LinkedBlockingQueue<>();


	private final boolean freeRun = false;
	private final boolean autoRecoveryRun = false;

	private File singleDir;

	private File packedDir;

	private File indexDir;

	private final AtomicLong clearBigNum = new AtomicLong(0);

	private final AtomicLong clearSmallNum = new AtomicLong(0);

	private final AtomicLong autoClearBigNum = new AtomicLong(0);

	private final AtomicLong autoClearSmallNum = new AtomicLong(0);

	private final AtomicLong failClearBigNum = new AtomicLong(0);

	private final AtomicLong failClearSmallNum = new AtomicLong(0);

	private GlobalIndex globalIndex;

	private PackedIndex packedIndex;

	private final ScheduledExecutorService clearScheduled = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
		final AtomicInteger atomic = new AtomicInteger();

		@Override
		public Thread newThread(@NonNull Runnable r) {
			return new Thread(r, "clearCacheThread" + this.atomic.getAndIncrement());
		}
	});

	public LruFileCache() {
		needRecovery = false;
		Runnable packedCacheSave = new PackedCacheRecord();
		ExecutorService executorService = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<>(8), new ThreadFactoryBuilder().setNameFormat("PackedCacheSaveThread").build());
		executorService.execute(packedCacheSave);
		Runnable autoCacheClean = new AutoCacheClean();
		executor.execute(autoCacheClean);

		Runnable cacheClean = new CacheClean();
		clearScheduled.scheduleWithFixedDelay(cacheClean, 5, 1, TimeUnit.SECONDS);

	}

	private static void release(FileLock fileLock) {
		if (fileLock == null) {
			return;
		}
		try {
			fileLock.release();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 强制关闭MappedByteBuffer
	 *
	 * @param mbb 内存
	 */
	private static void forceClose(MappedByteBuffer mbb) {
		AccessController.doPrivileged((PrivilegedAction) () -> {
			try {
				Method getCleanerMethod = mbb.getClass().getMethod("cleaner");
				getCleanerMethod.setAccessible(true);
				Cleaner cleaner = (Cleaner)
						getCleanerMethod.invoke(mbb, new Object[0]);
				cleaner.clean();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		});
	}

	private static void close(Closeable... closeables) {


		if (closeables == null || closeables.length == 0) {
			return;
		}

		for (Closeable closeable : closeables) {
			if (closeable == null) {
				continue;
			}
			try {
				closeable.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean exist(String key) {
		File file = getCacheFile(key);
		return file.exists();
	}

	@Override
	public boolean existPacked(String key) {
		try {
			return globalIndex.exist(key);
		} catch (RocksDBException e) {
			return false;
		}
	}

	@Override
	public boolean writeing(String key) {
		return channelMap.containsKey(key);
	}

	@Override
	public String getCacheFilePath(String key) {
		File file = new File(singleDir, key);
		return file.getAbsolutePath();
	}

	@Override
	public File getCacheFile(String key) {
		File file = new File(getCacheFilePath(key));
		if (file.exists()) {
			file.setLastModified(System.currentTimeMillis());
		}
		return file;
	}

	@Override
	public boolean removeCacheFile(String key) {
		return FileUtils.deleteQuietly(getCacheFile(key));
	}

	@Override
	public boolean removePackedFile(long packedKey) {
		boolean ret = FileUtils.deleteQuietly(getPackedCacheFile(packedKey));
		List<String> keys = packedIndex.getPackedFileKey(packedKey);
		try {
			globalIndex.delete(keys);
			packedIndex.batchDelete(packedKey);
		} catch (RocksDBException e) {
			logger.error("Remove packed file error:{}", e.getLocalizedMessage(), e);
			ret = false;
		}
		return ret;
	}

	@Override
	public void releaseCacheFile(List<String> keys) {
		ReleaseList releaseList = new ReleaseList();
		releaseList.setKeys(keys);
		try {
			releaseListQuery.put(releaseList);
		} catch (InterruptedException ignored) {
		}
	}

	@Override
	public void releasePackedFile(List<Long> packedKeys) {
		ReleaseList releaseList = new ReleaseList();
		releaseList.setPackedKeys(packedKeys);
		try {
			releaseListQuery.put(releaseList);
		} catch (InterruptedException ignored) {
		}
	}

	@Override
	public void removeAllCacheFiles() {
		try {
			FileUtils.deleteDirectory(singleDir);
			FileUtils.deleteDirectory(packedDir);
			globalIndex.clearAll();
		} catch (IOException | RocksDBException e) {
			e.printStackTrace();
		}
	}

	private File getPackedCacheFile(long key) {
		String packedKey = String.valueOf(key);
		File file = new File(packedDir, packedKey);
		if (file.exists()) {
			file.setLastModified(System.currentTimeMillis());
		}
		return file;
	}

	@Override
	public byte[] readPacekedFile(String key) throws IOException {
		byte[] bytes;
		PackedFile pfile;
		try {
			pfile = globalIndex.get(key);
		} catch (RocksDBException e) {
			throw new IOException(e);
		}
		if (pfile == null) {
			return null;
		}
		return readPacekedFile(pfile);
	}

	@Override
	public byte[] readPacekedFile(PackedFile pfile) throws IOException {
		byte[] bytes;
		File file = getPackedCacheFile(pfile.getPackedKey());
		if (!file.exists()) {
			throw new IOException("Read small file error: package file does not exist(" + pfile.getPackedKey() + ")");
		} else if (!file.isFile()) {
			throw new IOException("Read small file error: package file is not file(" + pfile.getPackedKey() + ")");
		}
		if (file.length() < (pfile.getSeek() + pfile.getSize())) {
			throw new IOException("Read small file error: small file size over package file size(" + pfile.getPackedKey() + ")");
		}
		bytes = new byte[pfile.getSize()];
		try (RandomAccessFile raFile = new RandomAccessFile(file, "r")) {
			raFile.seek(pfile.getSeek());
			raFile.read(bytes, 0, pfile.getSize());
		}
		return bytes;
	}

	@Override
	public byte[] readPaceked(long packedKey) throws IOException {
		File file = getPackedCacheFile(packedKey);
		if (!file.exists()) {
			throw new FileNotFoundException("File package does not exist:" + packedKey);
		}
		return Files.readAllBytes(file.toPath());
	}

	@Override
	public boolean addCacheFile(String key, InputStream inputStream) throws IOException {
		if (StringUtil.isBlank(key) || inputStream == null) {
			return false;
		}
		forceCleanIfNeed();
		File file = newWriteCacheFile(key);
		Path path = Paths.get(file.getAbsolutePath());
		Files.copy(inputStream, path);
		complete(key);
		return true;
	}

	@Override
	public void writePackedCache(ByteBuffer buffer, Collection<PackedFile> caches) throws IOException, InterruptedException {
		long key = caches.iterator().next().getPackedKey();
		File file = getPackedCacheFile(key);
		RandomAccessFile raFile = null;
		FileChannel channel = null;
		forceCleanIfNeed();
		try {
			FileUtils.deleteQuietly(file);
			file = BaseUtil.createFile(file.getAbsolutePath());
			raFile = new RandomAccessFile(file, "rw");
			channel = raFile.getChannel();
			channel.write(buffer);
			packedFileQueue.put(caches);
		} finally {
			IOUtils.closeQuietly(channel);
			IOUtils.closeQuietly(raFile);
		}

	}

	@Override
	public File openCacheForWrite(String key, long size) throws IOException {
		File file = newWriteCacheFile(key);
		try (RandomAccessFile r = new RandomAccessFile(file, "rw")) {
			r.setLength(size);
		}
		return file;
	}

	@Override
	public void writeCache(String key, ByteBuffer buffer) throws IOException {
		FileChannel channel = channelMap.get(key);
		if (channel != null) {
			channel.write(buffer);
		} else {
			throw new IOException("channel is null");
		}

	}

	@Override
	public void writeCache(String key, long seek, ByteBuffer buffer) throws IOException {
		FileChannel channel = channelMap.get(key);
		if (channel != null) {
			channel.position(seek - buffer.limit());
			channel.write(buffer);
		} else {
			throw new IOException("channel is null");
		}

	}

	private File newWriteCacheFile(String key) throws IOException {
		File file = new File(getCacheFilePath(key) + "." + CACHE_IDENTIFY);
		file = BaseUtil.createFile(file.getAbsolutePath());
		return file;
	}

	private void complete(String key) {
		File fileW = new File(getCacheFilePath(key) + "." + CACHE_IDENTIFY);
		File fileC = getCacheFile(key);
		try {
			if (fileC.exists()) {
				FileUtils.deleteQuietly(fileW);
				return;
			}
			if (fileW.exists()) {
				Files.move(Paths.get(fileW.getAbsolutePath()), Paths.get(fileC.getAbsolutePath()), StandardCopyOption.REPLACE_EXISTING);
			} else {
				logger.warn("Complete cache failed , cache file({}) is not exist!", key);
			}
		} catch (IOException e) {
			logger.error("Complete cache({}) failed:{}", key, e.getLocalizedMessage(), e);
		}
	}

	private void complete(String key, String targetKey) {
		File fileW = new File(getCacheFilePath(key) + "." + CACHE_IDENTIFY);
		File fileC = getCacheFile(targetKey);
		try {
			if (fileC.exists()) {
				FileUtils.deleteQuietly(fileW);
				return;
			}
			if (fileW.exists()) {
				Files.move(Paths.get(fileW.getAbsolutePath()), Paths.get(fileC.getAbsolutePath()), StandardCopyOption.REPLACE_EXISTING);
			} else {
				logger.warn("Complete cache failed , cache file({}) is not exist!", key);
			}
		} catch (IOException e) {
			logger.error("Complete cache({}) failed:{}", key, e.getLocalizedMessage(), e);
		}
	}

	private void clearWriting(String key) {
		File fileW = new File(getCacheFilePath(key) + "." + CACHE_IDENTIFY);
		if (fileW.exists()) {
			FileUtils.deleteQuietly(fileW);
		}
	}

	private File[] getWritingFiles(File file) {
		return file.listFiles((dir, name) -> name.endsWith(CACHE_IDENTIFY));
	}

	protected void setFileLoadOptions(FileCacheOptions options) {
		this.options = options;
		singleDir = new File(options.getCacheRootPath(), CACHE_IDENTIFY);
		if (!singleDir.exists()) {
			final boolean mkdirs = singleDir.mkdirs();
			if (!mkdirs) {
				throw new RuntimeException("Single cache directory creation failed");
			}
		}
		packedDir = new File(options.getCacheRootPath(), PACKED_IDENTIFY);
		if (!packedDir.exists()) {
			final boolean mkdirs = packedDir.mkdirs();
			if (!mkdirs) {
				throw new RuntimeException("Packed cache directory creation failed");
			}
		}
		indexDir = new File(options.getCacheRootPath(), PACKED_INDEX_DB_ROOT);
		if (!indexDir.exists()) {
			final boolean mkdirs = indexDir.mkdirs();
			if (!mkdirs) {
				throw new RuntimeException("Cache index directory creation failed");
			}
		}
		try {
			if (options.isSupportPacked()) {
				packedInit();
			}
		} catch (RocksDBException e) {
			throw new RuntimeException("Cache index DB creation failed", e);
		}
	}

	/**
	 * 清理无效的cache
	 */
	private int clearInvalidWriting() {
		File[] wFiles = getWritingFiles(singleDir);
		int n = 0;
		for (File wFile : wFiles) {
			String key = wFile.getName().substring(0, wFile.getName().lastIndexOf(CACHE_IDENTIFY) - 1);
			if (!channelMap.containsKey(key)) {
				clearWriting(key);
				n++;
			}
		}
		return n;
	}

	private void packedInit() throws RocksDBException {
		if (globalIndex != null) {
			return;
		}
		File globalIndexDir = new File(indexDir, GLOBAL_DB);
		globalIndex = new GlobalIndex(globalIndexDir.getAbsolutePath());
		globalIndex.initDb(true, 0, CompressionType.NO_COMPRESSION);
		File packedIndexDir = new File(indexDir, PACKED_DB);
		packedIndex = new PackedIndex(packedIndexDir.getAbsolutePath());
		packedIndex.initDb(true, 8, CompressionType.NO_COMPRESSION);

	}

	private void doCacheRecovery() {
		int bn = 0, sn = 0;
		File file = new File(options.getCacheRootPath());
		long beforeSpace = file.getUsableSpace();
		if (logger.isDebugEnabled()) {
			logger.debug("Recovery cache space: {}|{}", StringUtil.formatSize(beforeSpace), StringUtil.formatSize(options.getMinUsableSpace()));
		}
		float cleanRadio = options.getCleanRadio();
		cleanRadio = cleanRadio <= 0 || cleanRadio > 0.8 ? DEFAULT_CLEAN_RADIO : cleanRadio;
		File[] childs = orderByDate(singleDir);
		if (childs != null && childs.length > 0) {
			int removeFactor = (int) ((cleanRadio * childs.length) + 1);
			if (logger.isDebugEnabled()) {
				logger.debug("Large file num: {}, {} can be deleted", childs.length, removeFactor);
			}
			for (int i = 0; i < removeFactor; i++) {
				if (childs[i].isFile()) {
					if (FileUtils.deleteQuietly(childs[i])) {
						bn++;
					} else {
						failClearBigNum.getAndIncrement();
					}
				}
			}
		}
		childs = orderByDate(packedDir);
		if (childs != null && childs.length > 0) {
			int removeFactor = (int) ((cleanRadio * childs.length) + 1);
			if (logger.isDebugEnabled()) {
				logger.debug("Small file num: {}, {} can be deleted", childs.length, removeFactor);
			}
			for (int i = 0; i < removeFactor; i++) {
				if (childs[i].isFile()) {
					long packedKey = Long.parseLong(childs[i].getName());
					if (removePackedFile(packedKey)) {
						sn++;
					} else {
						failClearSmallNum.getAndIncrement();
					}
				}
			}
		}
		bn += clearInvalidWriting();
		clearBigNum.getAndAdd(bn);
		clearSmallNum.getAndAdd(sn);
		long afterSpace = file.getUsableSpace();
		logger.debug("{} large files, {} small files, release {} space in total", bn, sn, StringUtil.formatSize(afterSpace - beforeSpace));
	}

	private synchronized boolean forceCleanIfNeed() {
		if (!needRecovery) {
			return false;
		}
		File file = new File(options.getCacheRootPath());
		do {
			if (file.getUsableSpace() < MIN_USABLESPACE) {
				if (options.isFreeCache()) {
					try {
						doCacheRecovery();
					} catch (Throwable e) {
						logger.error("Clean error：{}", e.getLocalizedMessage(), e);
					}
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					break;
				}
			} else {
				break;
			}
		} while (true);
		return true;
	}

	/**
	 * 文件时间排序
	 */
	private File[] orderByDate(File file) {
		File[] fs = file.listFiles((dir, name) -> {
			File child = new File(dir, name);
			return child.isFile();
		});
		if (fs != null) {
			Arrays.sort(fs, (f1, f2) -> {
				long diff = f1.lastModified() - f2.lastModified();
				if (diff > 0) {
					return 1;
				} else if (diff == 0) {
					return 0;
				} else {
					return -1;
				}
			});
		}
		return fs;
	}

	@Override
	public void writeCacheFile(String key, long offset, ByteBuffer buffer) throws IOException {

		File file = new File(getCacheFilePath(key) + "." + CACHE_IDENTIFY);
		RandomAccessFile randFile = null;
		FileChannel channel = null;
		MappedByteBuffer mbb = null;
		FileLock fileLock = null;
		try {
			randFile = new RandomAccessFile(file, "rw");
			channel = randFile.getChannel();
			mbb = channel.map(FileChannel.MapMode.READ_WRITE, offset, buffer.limit());
			fileLock = channel.lock(offset, buffer.limit(), true);
			while (fileLock == null || !fileLock.isValid()) {
				fileLock = channel.lock(offset, buffer.limit(), true);
				if (logger.isDebugEnabled()) {
					logger.debug("Lock failed, retry");
				}
			}
			mbb.put(buffer);
			mbb.force();

		} catch (OverlappingFileLockException e) {
			throw new IllegalArgumentException("The program design is unreasonable and the locked areas overlap each other");
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			throw e;
		} finally {
			release(fileLock);
			forceClose(mbb);
			close(channel, randFile);
		}
	}

	@Override
	public void closeWriteCache(String key, boolean success) {
		RandomAccessFile raFile = raMap.get(key);
		if (raFile != null) {
			raMap.remove(key);
			IOUtils.closeQuietly(raFile);
		}
		FileChannel channel = channelMap.get(key);
		if (channel != null) {
			channelMap.remove(key);
			IOUtils.closeQuietly(channel);
		}
		if (success) {
			complete(key);
		} else {
			clearWriting(key);
		}

	}

	@Override
	public void closeWriteCacheFile(String key, boolean success) {
		if (success) {
			complete(key);
		} else {
			clearWriting(key);
		}

	}

	@Override
	public void closeWriteCacheFile(String key, String targetKey) {
		complete(key, targetKey);
	}

	@Override
	public long getClearBigNum() {
		return clearBigNum.get();
	}

	@Override
	public long getClearSmallNum() {
		return clearSmallNum.get();
	}

	@Override
	public long getAutoClearBigNum() {
		return autoClearBigNum.get();
	}

	@Override
	public long getAutoClearSmallNum() {
		return autoClearSmallNum.get();
	}

	@Override
	public long getFailClearBigNum() {
		return failClearBigNum.get();
	}

	@Override
	public long getFailClearSmallNum() {
		return failClearSmallNum.get();
	}

	@Override
	public void clearAll() {
		if (globalIndex != null) {
			try {
				globalIndex.clearAll();
			} catch (RocksDBException e) {
				logger.error("Release globalIndex error: {}", e.getLocalizedMessage(), e);
			}
		}
		if (packedIndex != null) {
			try {
				packedIndex.clearAll();
			} catch (RocksDBException e) {
				logger.error("Release packedIndex error: {}", e.getLocalizedMessage(), e);
			}
		}
		try {
			FileUtils.deleteDirectory(singleDir);
			singleDir.mkdirs();
		} catch (IOException e) {
			logger.error("Release single dir error: {}", e.getLocalizedMessage(), e);
		}
		try {
			FileUtils.deleteDirectory(packedDir);
			packedDir.mkdirs();
		} catch (IOException e) {
			logger.error("Release packed dir error: {}", e.getLocalizedMessage(), e);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Release[{}] all data", options.getCacheRootPath());
		}
	}

	class GlobalIndex extends BaseRocksDb {

		GlobalIndex(String dbPath) {
			super(dbPath);
		}

		public PackedFile get(String md5) throws RocksDBException {
			PackedFile cache = null;
			if (db != null) {
				byte[] bv = db.get(md5.getBytes());
				if (bv != null) {
					cache = bytesToPackedFile(bv);
				}
			}
			return cache;
		}

		public void batchPut(Collection<PackedFile> caches) throws RocksDBException {
			try (final WriteOptions writeOpt = new WriteOptions()) {
				try (final org.rocksdb.WriteBatch batch = new org.rocksdb.WriteBatch()) {
					for (PackedFile cache : caches) {
						byte[] bytes = packedFileToBytes(cache);
						batch.put(cache.getMd5().getBytes(), bytes);
					}
					db.write(writeOpt, batch);
				}
			}
		}

		public void put(PackedFile cache) throws RocksDBException {
			byte[] bytes = packedFileToBytes(cache);
			db.put(cache.getMd5().getBytes(), bytes);
		}


		byte[] packedFileToBytes(PackedFile cache) {
			byte[] ba = BaseUtil.longToBytes(cache.getPackedKey());
			byte[] bb = BaseUtil.intToByteArray(cache.getSeek());
			byte[] bc = BaseUtil.intToByteArray(cache.getSize());
			byte[] bytes = new byte[16];
			System.arraycopy(ba, 0, bytes, 0, 8);
			System.arraycopy(bb, 0, bytes, 8, 4);
			System.arraycopy(bc, 0, bytes, 12, 4);
			return bytes;
		}

		PackedFile bytesToPackedFile(byte[] bv) {
			PackedFile cache = new PackedFile();
			int size = BaseUtil.byteArrayToInt(bv, bv.length - 1);
			int seek = BaseUtil.byteArrayToInt(bv, bv.length - 5);
			byte[] ba = new byte[8];
			System.arraycopy(bv, 0, ba, 0, 8);
			long packedKey = BaseUtil.bytesToLong(ba);
			cache.setPackedKey(packedKey).setSeek(seek).setSize(size);
			return cache;
		}
	}

	class PackedIndex extends BaseRocksDb {

		PackedIndex(String dbPath) {
			super(dbPath);
		}

		List<String> getPackedFileKey(long packedKey) {
			List<String> list = new ArrayList<>();
			try (final ReadOptions readOptions = new ReadOptions()) {
				readOptions.setPrefixSameAsStart(true);
				RocksIterator iterator = db.newIterator(readOptions);
				for (iterator.seek(BaseUtil.longToBytes(packedKey)); iterator.isValid(); iterator.next()) {
					byte[] keys = iterator.key();
					String cacheKey = new String(keys, 8, keys.length - 8);
					list.add(cacheKey);
				}
			}
			return list;
		}

		void batchPut(Collection<PackedFile> packedFiles, boolean canDelete) throws RocksDBException {
			try (final WriteOptions writeOpt = new WriteOptions()) {
				try (final org.rocksdb.WriteBatch batch = new org.rocksdb.WriteBatch()) {
					for (PackedFile cache : packedFiles) {
						byte[] key = getKey(cache);
						byte value = (byte) (canDelete ? 0x01 : 0x00);
						batch.put(key, new byte[]{value});
					}
					db.write(writeOpt, batch);
				}
			}
		}

		void batchDelete(long packedKey) throws RocksDBException {
			try (final ReadOptions readOptions = new ReadOptions();
			     final WriteOptions writeOpt = new WriteOptions();
			     final org.rocksdb.WriteBatch batch = new org.rocksdb.WriteBatch()) {
				readOptions.setPrefixSameAsStart(true);
				RocksIterator iterator = db.newIterator(readOptions);
				for (iterator.seek(BaseUtil.longToBytes(packedKey)); iterator.isValid(); iterator.next()) {
					byte[] keys = iterator.key();
					batch.delete(keys);
				}
				db.write(writeOpt, batch);
			}
		}

		public void put(PackedFile packedFile, boolean canDelete) throws RocksDBException {
			byte value = (byte) (canDelete ? 0x01 : 0x00);
			db.put(getKey(packedFile), new byte[]{value});
		}


		public byte[] get(PackedFile packedFile) throws RocksDBException {
			return db.get(getKey(packedFile));
		}

		private byte[] getKey(PackedFile packedFile) {
			byte[] md5 = packedFile.getMd5().getBytes();
			byte[] packedKey = BaseUtil.longToBytes(packedFile.getPackedKey());
			byte[] key = new byte[md5.length + 8];
			System.arraycopy(packedKey, 0, key, 0, 8);
			System.arraycopy(md5, 0, key, 8, md5.length);
			return key;
		}

	}

	class CacheClean implements Runnable {

		@Override
		public void run() {
			File file = new File(options.getCacheRootPath());
			if (file.exists() && file.isDirectory()) {
				needRecovery = file.getUsableSpace() < MIN_USABLESPACE;
				if (options.isFreeCache() && file.getUsableSpace() < options.getMinUsableSpace()) {
					try {
						doCacheRecovery();
					} catch (Throwable e) {
						logger.error("CacheClean error：{}", e.getLocalizedMessage(), e);
					}
				}
			}
		}
	}

	class PackedCacheRecord implements Runnable {

		@Override
		public void run() {
			while (true) {
				try {
					Collection<PackedFile> caches = packedFileQueue.take();
					globalIndex.batchPut(caches);
					packedIndex.batchPut(caches, false);
				} catch (InterruptedException e) {
					logger.debug("PackedCacheRecord interrupted and exit");
					break;
				} catch (Throwable e) {
					logger.error("PackedCacheRecord error:{}", e.getLocalizedMessage(), e);
				}
			}
		}
	}

	class AutoCacheClean implements Runnable {

		@Override
		public void run() {
			logger.info("Auto cache clean thread start");
			while (true) {
				try {
					int n = 0;
					ReleaseList list = releaseListQuery.take();
					if (list.getKeys() != null) {
						for (String key : list.getKeys()) {
							if (key != null) {
								if (LruFileCache.this.removeCacheFile(key)) {
									autoClearBigNum.getAndIncrement();
									clearBigNum.getAndIncrement();
								} else {
									failClearBigNum.getAndIncrement();
								}
							}
						}

					}
					if (list.getPackedKeys() != null) {
						for (Long key : list.getPackedKeys()) {
							if (key != null) {
								if (LruFileCache.this.removePackedFile(key)) {
									autoClearSmallNum.getAndIncrement();
									clearSmallNum.getAndIncrement();
								} else {
									failClearSmallNum.getAndIncrement();
								}
							}
						}
					}
				} catch (InterruptedException e) {
					break;
				}
			}
			logger.info("Auto cache clean thread stop");
		}
	}
}
