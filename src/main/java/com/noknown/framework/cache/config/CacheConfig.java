package com.noknown.framework.cache.config;

import com.noknown.framework.cache.service.CacheService;
import com.noknown.framework.cache.service.impl.memcached.XMemcachedCacheServiceImpl;
import com.noknown.framework.cache.service.impl.memory.MemoryCacheServiceImpl;
import com.noknown.framework.cache.service.impl.redis.JedisCacheServiceImpl;
import com.noknown.framework.common.util.StringUtil;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.utils.XMemcachedClientFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author guodong
 */
@Configuration
@PropertySource(value = "${spring.config.custom-path:classpath:}conf/${spring.profiles.active}/cache.properties", ignoreResourceNotFound = true)
public class CacheConfig {

	public final Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${cache.global.type:memory}")
	private String cacheType;

	@Bean
	public JedisPool redisPoolFactory(@Value("${redis.config.open:false}") boolean connect,
	                                  RedisProperties redisProperties) {
		JedisPool jedisPool = null;
		if (connect) {
			JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
			jedisPoolConfig.setMaxIdle(redisProperties.getJedis().getPool().getMaxIdle());
			jedisPoolConfig.setMaxWaitMillis(redisProperties.getJedis().getPool().getMaxWait().toMillis());
			if (StringUtil.isNotBlank(redisProperties.getPassword())) {
				jedisPool = new JedisPool(jedisPoolConfig, redisProperties.getHost(), redisProperties.getPort(), (int) redisProperties.getTimeout().getSeconds(), redisProperties.getPassword());
			} else {
				jedisPool = new JedisPool(jedisPoolConfig, redisProperties.getHost(), redisProperties.getPort(), (int) redisProperties.getTimeout().getSeconds());

			}
		}
		return jedisPool;
	}

	@Bean
	public MemcachedClient connectMemcachedClient(@Value("${memcached.config.open:false}") boolean connect,
	                                              @Value("${memcached.config.servers:memcached.soulinfo.com:11211}") String servers) {
		MemcachedClient client = null;
		if (connect) {
			XMemcachedClientFactoryBean factoryBean = new XMemcachedClientFactoryBean();
			factoryBean.setServers(servers);
			try {
				client = (MemcachedClient) factoryBean.getObject();
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e.getLocalizedMessage());
			}
		}
		return client;
	}

	@Bean
	public CacheService connectionCache() {
		CacheService cacheService;
		switch (cacheType) {
			case "jedis":
				cacheService = new JedisCacheServiceImpl();
				break;
			case "xmemcached":
				cacheService = new XMemcachedCacheServiceImpl();
				break;
			case "memory":
				cacheService = new MemoryCacheServiceImpl();
				break;
			default:
				cacheService = new MemoryCacheServiceImpl();
				break;
		}

		return cacheService;
	}
}