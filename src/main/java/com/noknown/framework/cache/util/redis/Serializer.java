package com.noknown.framework.cache.util.redis;

import java.io.IOException;

/**
 * 对象序列化接口
 * @author winterlau
 */
public interface Serializer {

	/**
	 * 序列化名称
	 *
	 * @return 名称
	 */
	String name();

	/**
	 * 对象序列化
	 *
	 * @param obj 对象
	 * @return 字节数组
	 * @throws IOException IO异常
	 */
	byte[] serialize(Object obj) throws IOException;

	/**
	 * 反序列化
	 *
	 * @param bytes 字节数组
	 * @return 对象
	 * @throws IOException IO异常
	 */
	Object deserialize(byte[] bytes) throws IOException;
	
}
