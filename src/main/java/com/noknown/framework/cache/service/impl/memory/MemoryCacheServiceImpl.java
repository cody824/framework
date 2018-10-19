package com.noknown.framework.cache.service.impl.memory;

import com.noknown.framework.cache.service.CacheService;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author guodong
 * @date 2018/10/19
 */
public class MemoryCacheServiceImpl implements CacheService {

	private Map<String, Object> objectMap = new ConcurrentHashMap<>(500);

	private Map<String, Date> expireTimeMap = new ConcurrentHashMap<>(500);

	@Override
	public boolean set(String key, Object obj, Date expireTime) {
		objectMap.put(key, obj);
		if (expireTime != null) {
			expireTimeMap.put(key, expireTime);
		}
		return true;
	}

	@Override
	public Object get(String key) {
		if (expireTimeMap.containsKey(key)) {
			Date now = new Date();
			if (now.after(expireTimeMap.get((key)))) {
				objectMap.remove(key);
				expireTimeMap.remove(key);
			}
		}
		return objectMap.get(key);
	}

	@Override
	public boolean delete(String key) {
		if (objectMap.containsKey(key)) {
			objectMap.remove(key);
			expireTimeMap.remove(key);
			return true;
		}
		return false;
	}
}
