package com.noknown.framework.cache.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.noknown.framework.cache.service.CacheService;
import com.noknown.framework.cache.service.impl.memcached.XMemcachedCacheServiceImpl;
import com.noknown.framework.cache.service.impl.redis.JedisCacheServiceImpl;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.utils.XMemcachedClientFactoryBean;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
@PropertySource(value = "${conf.protocol:classpath}:conf/${spring.profiles.active}/cache.properties", ignoreResourceNotFound = true)
public class CacheConfig {

	public final Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${cache.global.type:jedis}")
	private String cacheType;

	@Bean
	public JedisPool redisPoolFactory(@Value("${redis.config.open:true}") boolean connect,
			@Value("${spring.redis.host}") String host,
			@Value("${spring.redis.port}") int port,
			@Value("${spring.redis.timeout}") int timeout,
			@Value("${spring.redis.pool.max-idle}") int maxIdle,
			@Value("${spring.redis.pool.max-wait}") long  maxWaitMillis,
			@Value("${spring.redis.password}") String password) {
		JedisPool jedisPool = null;
		if (connect) {
			logger.info("JedisPool注入成功！！");
			logger.info("redis地址：" + host + ":" + port);
			JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
			jedisPoolConfig.setMaxIdle(maxIdle);
			jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
			jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout, password);
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
		default:
			cacheService = new JedisCacheServiceImpl();
			break;
		}

		return cacheService;
	}
}