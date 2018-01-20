package com.noknown.framework.cache.service.impl.memcached;

import java.util.Date;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.noknown.framework.cache.service.CacheService;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.exception.MemcachedException;

public class XMemcachedCacheServiceImpl implements CacheService {
	
	public final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private MemcachedClient memcachedClient;
	
	@Value("${cache.prefix:noknown}")
	private String prefix;

	@Override
	public boolean set(String key, Object obj, Date expireTime) {
		long exp = 0;
		if (expireTime != null) {
			exp = ( expireTime.getTime() - System.currentTimeMillis())/1000;
		}
		exp = exp < 0? 0 : exp;
		try {
			return memcachedClient.set(prefix + key, (int) exp, obj);
		} catch (TimeoutException | InterruptedException | MemcachedException e) {
			logger.warn(e.getLocalizedMessage());
			return false;
		}
	}

	@Override
	public Object get(String key) {
		try {
			return memcachedClient.get(prefix + key);
		} catch (TimeoutException | InterruptedException | MemcachedException e) {
			logger.warn(e.getLocalizedMessage());
			return null;
		}
	}

	@Override
	public boolean delete(String key) {
		try {
			return memcachedClient.delete(prefix + key);
		} catch (TimeoutException | InterruptedException | MemcachedException e) {
			logger.warn(e.getLocalizedMessage());
			return false;
		}
	}

}
