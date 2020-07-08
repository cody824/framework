package com.noknown.framework.cache.file;

/**
 * 文件缓存服务配置
 * @author guodong
 */
public class FileCacheOptions {
	/**
	 * the file cache root path
	 */
	private String cacheRootPath;

	/**
	 * file cache min usableSpace : byte
	 */
	private long minUsableSpace;

	/**
	 * file cache count
	 */
	private int maxFileCount;

	/**
	 * file cache max size: byte
	 */
	private long maxCacheSize;

	/**
	 * do free if need
	 */
	private boolean isFreeCache = true;

	/**
	 * 清除比例
	 */
	private float cleanRadio;

	/**
	 * 是否支持packed
	 */
	private boolean supportPacked;

	public String getCacheRootPath() {
		return cacheRootPath;
	}

	public FileCacheOptions setCacheRootPath(String cacheRootPath) {
		this.cacheRootPath = cacheRootPath;
		return this;
	}

	public long getMinUsableSpace() {
		return minUsableSpace;
	}

	public FileCacheOptions setMinUsableSpace(long minUsableSpace) {
		this.minUsableSpace = minUsableSpace;
		return this;
	}

	public int getMaxFileCount() {
		return maxFileCount;
	}

	public FileCacheOptions setMaxFileCount(int maxFileCount) {
		this.maxFileCount = maxFileCount;
		return this;
	}

	public long getMaxCacheSize() {
		return maxCacheSize;
	}

	public FileCacheOptions setMaxCacheSize(long maxCacheSize) {
		this.maxCacheSize = maxCacheSize;
		return this;
	}

	public boolean isFreeCache() {
		return isFreeCache;
	}

	public FileCacheOptions setFreeCache(boolean isFreeCache) {
		this.isFreeCache = isFreeCache;
		return this;
	}

	public float getCleanRadio() {
		return cleanRadio;
	}

	public FileCacheOptions setCleanRadio(float cleanRadio) {
		this.cleanRadio = cleanRadio;
		return this;
	}

	public boolean isSupportPacked() {
		return supportPacked;
	}

	public FileCacheOptions setSupportPacked(boolean supportPacked) {
		this.supportPacked = supportPacked;
		return this;
	}
}
