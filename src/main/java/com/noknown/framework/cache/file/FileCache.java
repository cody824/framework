package com.noknown.framework.cache.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.List;

/**
 * 文件缓存服务，支持小文件打包等功能
 * @author guodong
 */
public interface FileCache {

	/**
	 * 文件是否存在
	 * @param key 缓存key
	 * @return 是否存在
	 */
	boolean exist(String key);

	/**
	 * 文件是否存在打包文件中
	 * @param key 缓存key
	 * @return 是否存在
	 */
	boolean existPacked(String key);

	/**
	 * cache是否在写入
	 * @param key 缓存key
	 * @return 是否在写入
	 */
	boolean writeing(String key);

	/**
	 * 根据md5值获取文件cache路径
	 * @param key 缓存key
	 * @return cache文件路径
	 */
	String getCacheFilePath(String key);

	/**
	 *
	 * 获取缓存文件
	 * @param key 缓存key
	 * @return 缓存文件对象
	 */
	File getCacheFile(String key);

	/**
	 * 增加缓存文件
	 *
	 * @param key         缓存key
	 * @param inputStream 输入流
	 * @return 是否成功
	 * @throws IOException IO错误
	 */
	boolean addCacheFile(String key, InputStream inputStream) throws IOException;

	/**
	 * 创建文件准备协议
	 *
	 * @param key  缓存key
	 * @param size 文件大小
	 * @return 文件
	 * @throws IOException IO错误
	 */
	File openCacheForWrite(String key, long size) throws IOException;

	/**
	 * buffer写入文件
	 * @param key   缓存key
	 * @param buffer    数据buf
	 * @throws IOException IO错误
	 */
	void writeCache(String key, ByteBuffer buffer) throws IOException;

	/**
	 * buffer写入一部分文件
	 * @param key   缓存key
	 * @param seek  文件偏移
	 * @param buffer    数据buf
	 * @throws IOException  IO错误
	 */
	void writeCache(String key, long seek, ByteBuffer buffer) throws IOException;

	/**
	 * buffer写入一部分文件
	 *
	 * @param key    缓存key
	 * @param seek   文件偏移
	 * @param buffer 数据buf
	 * @throws IOException IO错误
	 */
	void writeCacheFile(String key, long seek, ByteBuffer buffer) throws IOException;

	/**
	 * 读取打包文件
	 * @param key 缓存key
	 * @return 文件内容
	 * @throws IOException  IO错误
	 */
	byte[] readPacekedFile(String key) throws IOException;

	/**
	 * 读取打包文件
	 * @param packedFile 小文件包
	 * @return 文件内容
	 * @throws IOException  IO错误
	 */
	byte[] readPacekedFile(PackedFile packedFile) throws IOException;

	/**
	 * 读取packedKey对应的文件包
	 *
	 * @param packedKey 文件包的key
	 * @return 文件包内容
	 * @throws IOException  IO错误
	 */
	byte[] readPaceked(long packedKey) throws IOException;

	/**
	 * 写入文件包cache
	 * @param buffer    数据buf
	 * @param caches    小文件包
	 * @throws IOException  IO错误
	 * @throws InterruptedException 中断异常
	 */
	void writePackedCache(ByteBuffer buffer, Collection<PackedFile> caches) throws IOException, InterruptedException;

	/**
	 * 关闭cache写入
	 * @param key   缓存key
	 * @param success   是否成功
	 */
	void closeWriteCache(String key, boolean success);

	/**
	 * 关闭cache写入
	 *
	 * @param key     缓存key
	 * @param success 是否成功
	 */
	void closeWriteCacheFile(String key, boolean success);

	/**
	 * 关闭cache写入
	 *
	 * @param key     缓存key
	 * @param targetKey 目标缓存key
	 */
	void closeWriteCacheFile(String key, String targetKey);

	/**
	 * 移除缓存文件
	 * @param key   缓存key
	 * @return 是否成功
	 */
	boolean removeCacheFile(String key);

	/**
	 * 移除缓存文件包
	 *
	 * @param packedKey 缓存包key
	 * @return 是否成功
	 */
	boolean removePackedFile(long packedKey);

	/**
	 * 异步删除cache文件
	 *
	 * @param keys cache文件key列表
	 */
	void releaseCacheFile(List<String> keys);

	/**
	 * 异步删除缓存文件包
	 *
	 * @param packedKeys 文件包key列表
	 */
	void releasePackedFile(List<Long> packedKeys);


	/**
	 * 移除所有缓存文件
	 */
	void removeAllCacheFiles();

	/**
	 * 清理大文件数量
	 *
	 * @return 个数
	 */
	long getClearBigNum();

	/**
	 * 清理小文件包数量
	 *
	 * @return 个数
	 */
	long getClearSmallNum();

	/**
	 * 自动清理的大文件数
	 *
	 * @return 个数
	 */
	long getAutoClearBigNum();

	/**
	 * 自动清理的小文件数
	 *
	 * @return 个数
	 */
	long getAutoClearSmallNum();

	/**
	 * 清理失败的大文件数
	 *
	 * @return 个数
	 */
	long getFailClearBigNum();

	/**
	 * 清理失败的小文件数
	 *
	 * @return 个数
	 */
	long getFailClearSmallNum();

	/**
	 * 清空所有
	 */
	void clearAll();
}
