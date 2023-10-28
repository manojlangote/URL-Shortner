package com.url.shortner.app.db;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class RedisDBServiceImpl {
	static JedisPool pool = null;

	public RedisDBServiceImpl() {
	}

	public static JedisPool getInstance() {
		if (pool == null)
			pool = new JedisPool("localhost", 6379);
		return pool;
	}

	/*
	 * public static void main(String[] args) { JedisPool redisDBInstance =
	 * RedisDBServiceImpl.getInstance(); RedisDBServiceImpl red = new
	 * RedisDBServiceImpl();
	 * 
	 * try (Jedis jedis = redisDBInstance.getResource()) { jedis.set("name",
	 * "Manoj"); System.out.println(jedis.get("name"));
	 * System.out.println(jedis.get("name1"));
	 * 
	 * Map<String, String> hash = new HashMap<>(); ; hash.put("shortUrl", "John");
	 * jedis.hmset("user-session:123", hash);
	 * System.out.println(jedis.hgetAll("user-session:123"));
	 * 
	 * System.out.println(jedis.ping());
	 * 
	 * } catch (Exception e) { e.printStackTrace(); }
	 * 
	 * }
	 */

	public Map<String, String> getData(String key) {
		JedisPool redisDBInstance = RedisDBServiceImpl.getInstance();
		try (Jedis jedis = redisDBInstance.getResource()) {
			return jedis.hgetAll(key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String setData(String key, Map<String, String> keyValueMap) {
		JedisPool redisDBInstance = RedisDBServiceImpl.getInstance();
		try (Jedis jedis = redisDBInstance.getResource()) {
			// Later on check if key already exist before set.
			return jedis.hmset(key, keyValueMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public long getKeyCount() {
		JedisPool redisDBInstance = RedisDBServiceImpl.getInstance();
		try (Jedis jedis = redisDBInstance.getResource()) {
			return jedis.dbSize();
		} catch (Exception e) {
			return 0;
		}
	}

	public String pingRedis() {
		JedisPool redisDBInstance = RedisDBServiceImpl.getInstance();
		try (Jedis jedis = redisDBInstance.getResource()) {
			return jedis.ping();
		} catch (Exception e) {
			return null;
		}
	}

}
