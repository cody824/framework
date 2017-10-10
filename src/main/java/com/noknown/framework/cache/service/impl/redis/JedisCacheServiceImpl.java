package com.noknown.framework.cache.service.impl.redis;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.noknown.framework.cache.service.CacheService;
import com.noknown.framework.cache.util.redis.RedisUtil;

public class JedisCacheServiceImpl implements CacheService{

	@Autowired
	private RedisUtil redisUtil;

	@Value("${cache.prefix:noknown}")
	private String prefix;

	@Override
	public boolean set(String key, Object obj, Date expireTime) {
		//设置值
		String ok = redisUtil.setObject(prefix + ":" + key, obj);
		if(!"OK".equals(ok)){
			return false;
		}

		//永不过期
		if(expireTime == null){
			return true;
		}

		//过期时间
		long success = redisUtil.expireAt(prefix + ":" + key, expireTime);
		if(success > 0){
			return true;
		}else{
			redisUtil.del(key);
			return false;
		}
	}

	@Override
	public Object get(String key) {
		return redisUtil.getObject(prefix + ":" + key);
	}

	@Override
	public boolean delete(String key) {
		long success = redisUtil.del(prefix + ":" + key);
		return success > 0;
	}
}