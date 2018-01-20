package com.noknown.framework.cache.util.redis;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * 
 * @author cody
 *
 */
@Component
public class RedisUtil {
	
	@Autowired
	private JedisPool jedisPool;
	
	@Value("${redis.util.serializer:fst}")
	private String serializer;
	
	private Serializer g_ser;

	private static final Logger logger = LoggerFactory.getLogger(RedisUtil.class);

	public static final String R_OK = "OK";

	public static final Long R_1L = 1l;

	/**
	 * 从连接池中获取jedis连接
	 */
	public Jedis getJedis() {
		return jedisPool.getResource();
	}

	/**
	 * 从连接池中释放jedis
	 */
	public void close(Jedis jedis) {
		if (jedis != null) {
			try {
				jedisPool.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 判断事物执行后返回结果列表是否为期望的结果列表
	 * 
	 * @param results
	 *            事务执行结果
	 * @param expects
	 *            期望的结果
	 * @return
	 */
	public static boolean expect(List<Object> results, Object... expects) {
		try {
			if (results != null && expects != null && results.size() == expects.length) {
				for (int i = 0; i < expects.length; i++) {
					Object result = results.get(i);
					if (result == null || !result.equals(expects[i])) {
						return false;
					}
				}
				return true;
			}
		} catch (Exception e) {
			logger.error("对比失败:" + results + " @ " + Arrays.toString(expects), e);
		}
		return false;
	}

	/**
	 * 构造函数
	 * 
	 * @throws Exception
	 */
	@PostConstruct
	public void init() throws Exception {
		
		// 序列化
		if (serializer == null || serializer.isEmpty()) {
			g_ser = new JavaSerializer();
			logger.warn("RedisUtil constructor param serializer is not set.");
		} else {
			switch (serializer) {
			case "java":
				g_ser = new JavaSerializer();
				break;
			case "fst":
				g_ser = new FSTSerializer();
				break;
			default:
				g_ser = new JavaSerializer();
			}
			logger.info("RedisUtil serializer is " + g_ser.name());
		}

		
	}

	/**
	 * set 存储对象
	 * 
	 * @param key
	 * @param value
	 * @return 返回OK表示执行成功；返回null表示exception
	 */
	public String setObject(String key, Object value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.set(key.getBytes(), g_ser.serialize(value));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			jedis.close();
		}
		return null;
	}

	/**
	 * get 获取Object对象
	 * 
	 * @param key
	 * @return Object
	 * @throws Exception
	 */
	public Object getObject(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			byte[] b = jedis.get(key.getBytes());
			return g_ser.deserialize(b);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			jedis.close();
		}
		return null;
	}

	/**
	 * 存储单个值
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean set(String key, String value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.set(key, value);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			jedis.close();
		}
		return true;
	}

	/**
	 * 存储多个值
	 * 
	 * @param keyvalues
	 * @return
	 */
	public boolean mset(String... keyvalues) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.mset(keyvalues);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			jedis.close();
		}
		return true;
	}

	/**
	 * 获取单个值
	 * 
	 * @param key
	 * @return
	 */
	public String get(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.get(key);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			jedis.close();
		}
	}

	/**
	 * 获取多个值
	 * 
	 * @param keys
	 * @return
	 */
	public List<String> mget(String... keys) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.mget(keys);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			jedis.close();
		}
	}

	/**
	 * 删除单个
	 * 
	 * @param key
	 * @return 实际删除个数(返回-1表示exception)
	 */
	public long del(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.del(key.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			jedis.close();
		}
		return -1;
	}

	/**
	 * 删除多个
	 * 
	 * @param keys
	 * @return 实际删除个数(返回-1表示exception)
	 */
	public long del(String[] keys) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.del(keys);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			jedis.close();
		}
		return -1;
	}

	/**
	 * check 查看是否存在指定的缓存
	 * 
	 * @param key
	 * @return
	 */
	public Boolean check(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();

			return jedis.exists(key);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			jedis.close();
		}
	}

	/**
	 * 获取hash表全部数据
	 * 
	 * @param key
	 * @throws Exception
	 */
	public Map<String, String> hgetall(String key) {
		Jedis jedis = null;
		Map<String, String> map = new HashMap<String, String>();
		try {
			jedis = jedisPool.getResource();
			map = jedis.hgetAll(key);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			jedis.close();
		}
		return map;
	}

	/**
	 * hset 存储值
	 * 
	 * @param key
	 * @param field
	 * @param value
	 *            --> String
	 * @return
	 * @throws Exception
	 */
	public boolean hset(String key, String field, String value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.hset(key, field, value);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			jedis.close();
		}
		return true;
	}

	/**
	 * hget 获取String值
	 * 
	 * @param key
	 * @param field
	 * @return String
	 * @throws Exception
	 */
	public String hgetStr(String key, String field) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hget(key, field);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			jedis.close();
		}
	}

	/**
	 * hcheck 查看字典中，是否存在指定的缓存
	 * 
	 * @param key
	 * @param field
	 * @return
	 */
	public Boolean hcheck(String key, String field) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.hexists(key.getBytes(), field.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			jedis.close();
		}
	}

	/**
	 * 删除缓存中指定字典中的单个对象
	 * 
	 * @param key
	 * @param field
	 * @throws Exception
	 */
	public void hdel(String key, String field) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.hdel(key, field);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			jedis.close();
		}
	}

	/**
	 * List操作，lpush
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public long lpush(String key, String value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			long count = jedis.lpush(key, value);
			return count;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		} finally {
			jedis.close();
		}
	}

	/**
	 * List操作，rpush
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public long rpush(String key, String value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			long count = jedis.rpush(key, value);
			return count;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		} finally {
			jedis.close();
		}
	}

	/**
	 * List操作，lrange
	 * 
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public List<String> lrange(String key, long start, long end) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.lrange(key, start, end);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			jedis.close();
		}
	}

	/**
	 * List操作，lrem
	 * 
	 * @param key
	 * @return
	 */
	public long lrem(String key, String value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.lrem(key, 0, value);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		} finally {
			jedis.close();
		}
	}

	/**
	 * List操作，llen
	 * 
	 * @param key
	 * @return
	 */
	public long llen(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.llen(key);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		} finally {
			jedis.close();
		}
	}

	/**
	 * 有序集合操作，zadd，新增一个 返回实际新增的个数（即1个） 返回-1说明操作失败
	 * 
	 * @param key
	 * @param score
	 * @param member
	 * @return
	 */
	public long zadd(String key, double score, Object member) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			if (member instanceof String) {
				return jedis.zadd(key, score, (String) member);
			} else {
				return jedis.zadd(key.getBytes(), score, g_ser.serialize(member));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		} finally {
			jedis.close();
		}
	}

	/**
	 * 有序集合操作，zadd，新增一个 返回实际新增的个数（即1个） 返回-1说明操作失败
	 * 
	 * @param key
	 * @param score
	 * @param member
	 * @return
	 */
	public long zadd(String key, double score, String member) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zadd(key, score, member);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		} finally {
			jedis.close();
		}
	}

	/**
	 * 有序集合操作，zrem，删除一个 返回实际被删除的个数（即1个） 返回-1说明操作失败
	 * 
	 * @param key
	 * @param member
	 * @return
	 */
	public long zrem(String key, String member) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrem(key, member);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		} finally {
			jedis.close();
		}
	}

	/**
	 * 有序集合操作，zcard，获取集合内成员数量 key不存在，返回0 返回-1，说明操作失败
	 * 
	 * @param key
	 * @return
	 */
	public long zcard(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zcard(key);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		} finally {
			jedis.close();
		}
	}

	/**
	 * 有序集合操作，zscore 返回 member 成员的 score 值 如果 member 元素不是有序集 key 的成员，或 key
	 * 不存在，返回 null
	 * 
	 * @param key
	 * @param member
	 * @return
	 */
	public Double zscore(String key, String member) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zscore(key, member);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			jedis.close();
		}
	}

	/**
	 * 有序集合操作，zrange 返回索引在start和stop之间的成员列表 当start = 0 & stop = -1时，返回全部
	 * 
	 * @param key
	 * @return
	 */
	public Set<Object> zrangeRetObject(String key, long start, long stop) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			Set<byte[]> values = jedis.zrange(key.getBytes(), start, stop);
			Set<Object> rets = new HashSet<Object>();
			for (byte[] v : values) {
				rets.add(g_ser.deserialize(v));
			}
			return rets;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			jedis.close();
		}
	}

	/**
	 * 有序集合操作，zrange 返回索引在start和stop之间的成员列表 当start = 0 & stop = -1时，返回全部
	 * 
	 * @param key
	 * @return
	 */
	public Set<String> zrange(String key, long start, long stop) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrange(key, start, stop);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			jedis.close();
		}
	}

	/**
	 * 有序集合操作，zrange 返回索引在start和stop之间的成员列表 当start = 0 & stop = -1时，返回全部
	 * 
	 * @param key
	 * @return
	 */
	public Set<String> zrevrange(String key, long start, long stop) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.zrevrange(key, start, stop);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			jedis.close();
		}
	}

	/**
	 * 自然增长
	 * 
	 * @param key
	 * @return
	 */
	public long incr(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.incr(key);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		} finally {
			jedis.close();
		}
	}

	/**
	 * 自然增长的当前值
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public long currentIncr(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			String current = jedis.get(key);
			if (current != null) {
				return new Long(current);
			} else {
				return -1;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		} finally {
			jedis.close();
		}
	}

	/**
	 * 设置过期时间(时间段)
	 * 
	 * @param key
	 * @param seconds
	 * @return 成功设置返回1；当key不存在或者不能为key设置生存时间时返回0；exception返回-1
	 */
	public long expire(String key, int seconds) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.expire(key, seconds);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			jedis.close();
		}
		return -1;
	}

	/**
	 * 设置过期时间(具体时间点)
	 * 
	 * @param key
	 * @param date
	 * @return 成功设置返回1；当key不存在或者不能为key设置生存时间时返回0；exception返回-1
	 */
	public long expireAt(String key, Date date) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.expireAt(key, date.getTime() / 1000);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			jedis.close();
		}
		return -1;
	}

	/**
	 * 查询剩余过期时间
	 * 
	 * @param key
	 * @return >= 0 ： 正常 -1 : exception -2 : 已过期
	 */
	public long ttl(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.ttl(key);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			jedis.close();
		}
		return -1;
	}

	/**
	 * set 存储值
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public boolean sadd(String key, String... values) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.sadd(key, values);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			jedis.close();
		}
		return true;
	}

	/**
	 * set 存储值
	 * 
	 * @param key
	 * @param value
	 *            --> String
	 * @return
	 * @throws Exception
	 */
	public boolean sadd(String key, Object value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			if (value instanceof String) {
				jedis.sadd(key, (String) value);
			} else {
				jedis.sadd(key.getBytes(), g_ser.serialize(value));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			jedis.close();
		}
		return true;
	}

	public boolean srem(String key, String... values) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.srem(key, values);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			jedis.close();
		}
		return true;
	}

	/**
	 * set 值获取
	 * 
	 * @param key
	 * @return String
	 * @throws Exception
	 */
	public Set<String> smembers(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.smembers(key);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			jedis.close();
		}
	}

	public Set<Object> smembersRetObject(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			Set<byte[]> values = jedis.smembers(key.getBytes());
			Set<Object> rets = new HashSet<Object>();
			for (byte[] v : values) {
				rets.add(g_ser.deserialize(v));
			}
			return rets;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			jedis.close();
		}
	}

	public boolean sismember(String key, String member) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.sismember(key, member);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			jedis.close();
		}
	}

	public long scard(String key) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			long ret = jedis.scard(key.getBytes());
			return ret;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		} finally {
			jedis.close();
		}
	}
}