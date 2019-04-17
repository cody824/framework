package com.noknown.framework.fss.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 文件服务
 *
 * @author guodong
 */
public interface FileStoreService {

	/**
	 * 把数据保存到key中
	 *
	 * @param data 数据
	 * @param key  文件key
	 * @return 文件访问url
	 * @throws IOException IO异常
	 */
	String put(final byte[] data, String key) throws IOException;

	/**
	 * 把输入流保存到key中
	 * @param inputStream   输入流
	 * @param key           文件key
	 * @return 文件访问url
	 * @throws IOException IO异常
	 */
	String put(InputStream inputStream, String key) throws IOException;

	/**
	 * 把指定文件保存到key中
	 * @param path  文件路径
	 * @param key   文件key
	 * @return 文件访问url
	 * @throws IOException IO异常
	 */
	String put(String path, String key) throws IOException;

	/**
	 * 保存文件到给定路径
	 * @param path  文件路径
	 * @param key   文件可以
	 * @throws IOException IO异常
	 */
	void get(String path, String key) throws IOException;

	/**
	 * 获取文件输出到输出流中
	 * @param outputStream  输出流
	 * @param key   文件key
	 * @throws IOException IO异常
	 */
	void get(OutputStream outputStream, String key) throws IOException;

	/**
	 * 删除文件
	 * @param key 文件key
	 * @throws IOException IO异常
	 */
	void del(String key) throws IOException;

	/**
	 * 拷贝文件
	 *
	 * @param srcKey    源key
	 * @param targetKey 目标key
	 * @return 文件url
	 * @throws IOException IO异常
	 */
	String copy(String srcKey, String targetKey) throws IOException;

	/**
	 * 是否存在
	 *
	 * @param key 文件key
	 * @return 是否存在
	 */
	boolean exist(String key);

}
