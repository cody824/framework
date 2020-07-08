package com.noknown.framework.kv;

import org.apache.commons.io.FileUtils;
import org.rocksdb.*;
import org.rocksdb.util.SizeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @author guodong
 */
public class BaseRocksDb {

	protected final Charset charset;
	private final Logger logger = LoggerFactory.getLogger(BaseRocksDb.class);
	protected RocksDB db;
	private Options options;
	private BlockBasedTableConfig blockBasedTableConfig;
	private Filter bloomFilter;
	private String dbPath;
	private boolean useBloomFilter;
	private CompressionType compressionType;

	private int prefix;

	public BaseRocksDb(String dbPath) {
		this.dbPath = dbPath;
		this.charset = Charset.forName("utf-8");
	}

	public BaseRocksDb(String dbPath, String charset) {
		this.dbPath = dbPath;
		this.charset = Charset.forName(charset);
	}

	/**
	 * 使用默认配置初始化db
	 *
	 * @param useBloomFilter 是否使用bloomFilter
	 *                       如果都是get请求则使用，如果都是替代器查询，不使用
	 * @param prefix
	 * @throws RocksDBException RocksDB.open异常
	 */
	public void initDb(boolean useBloomFilter, int prefix, CompressionType compressionType) throws RocksDBException {
		options = new Options();
		this.useBloomFilter = useBloomFilter;
		this.prefix = prefix;
		this.compressionType = compressionType;
		final BlockBasedTableConfig blockBasedTableConfig = new BlockBasedTableConfig();
		blockBasedTableConfig.setBlockCacheSize(16 * SizeUnit.MB)
				.setBlockRestartInterval(10)
				.setCacheIndexAndFilterBlocks(true)
				.setPinL0FilterAndIndexBlocksInCache(true)
				.setHashIndexAllowCollision(false)
				.setBlockCacheCompressedSize(64 * SizeUnit.KB)
				.setBlockCacheCompressedNumShardBits(10);
		if (useBloomFilter) {
			bloomFilter = new BloomFilter(10);
			blockBasedTableConfig.setFilter(bloomFilter);
		}
		options.setCreateIfMissing(true)
				.setLevelCompactionDynamicLevelBytes(true)
				.setWriteBufferSize(16 * SizeUnit.MB)
				.setMaxWriteBufferNumber(3)
				.setMaxBackgroundCompactions(10)
				.setMaxBackgroundFlushes(5)
				.setTableFormatConfig(blockBasedTableConfig)
				.setCompressionType(compressionType)
				.setCompactionStyle(CompactionStyle.UNIVERSAL);
		if (prefix > 0) {
			options.useFixedLengthPrefixExtractor(prefix);
		}
		db = RocksDB.open(options, dbPath);
	}


	public boolean exist(String key) throws RocksDBException {
		byte[] value = db.get(key.getBytes());
		return value != null;
	}

	public void delete(String key) throws RocksDBException {
		db.delete(key.getBytes());
	}

	public void putString(String key, String value) throws RocksDBException {
		db.put(key.getBytes(), value.getBytes());
	}

	public String getString(String key) throws RocksDBException {
		String ret = null;
		byte[] bv = db.get(key.getBytes());
		if (bv != null) {
			ret = new String(bv);
		}
		return ret;
	}

	public void delete(List<String> keys) throws RocksDBException {
		try (final WriteOptions writeOpt = new WriteOptions()) {
			try (final WriteBatch batch = new WriteBatch()) {
				for (String key : keys) {
					batch.remove(key.getBytes());
				}
				db.write(writeOpt, batch);
			}
		}
	}

	public void clearAll() throws RocksDBException {
		File file = new File(dbPath);
		close();
		try {
			FileUtils.deleteDirectory(file);
		} catch (IOException e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		initDb(this.useBloomFilter, this.prefix, this.compressionType);
	}

	public void close() {
		if (db != null) {
			db.close();
		}
		if (bloomFilter != null) {
			bloomFilter.close();
		}
		if (options != null) {
			options.close();
		}
	}


	@Override
	public String toString() {
		return "BaseRocksDb{" +
				"charset=" + charset +
				", db=" + db +
				", options=" + options +
				", blockBasedTableConfig=" + blockBasedTableConfig +
				", bloomFilter=" + bloomFilter +
				", dbPath='" + dbPath + '\'' +
				", useBloomFilter=" + useBloomFilter +
				", prefix=" + prefix +
				'}';
	}
}
