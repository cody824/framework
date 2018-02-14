package com.noknown.framework.cache.service;

import java.util.Date;

/**
 * cache服务接口
 * @author guodong
 *
 */
public interface CacheService {

	/**
	 * 设置key
	 * @param key   key
	 * @param obj   value
	 * @param expireTime    过期时间
	 * @return 是否成功
	 */
	boolean set(String key, Object obj, Date expireTime);

	/**
	 * 获取对象
	 * @param key   key
	 * @return 对象
	 */
	Object get(String key);
	
	/**
	 * 删除key对应的对象
	 * @param key   key
	 * @return 是否成功
	 */
	boolean delete(String key);

	
}
