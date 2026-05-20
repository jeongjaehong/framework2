/**
 * @(#)Redis.java
 */
package framework.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import framework.config.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 * Redis 캐시 구현체 (http://redis.io/)
 * Jedis 4.x/5.x JedisPool API 사용
 */
public class Redis extends AbstractCache {
	/**
	 * 싱글톤 객체
	 */
	private static Redis _uniqueInstance;

	/**
	 * 타임아웃 값 (ms)
	 */
	private static final int _TIMEOUT = 500;

	/**
	 * 캐시 클라이언트 Pool
	 */
	private final JedisPool _pool;

	/**
	 * 생성자, 외부에서 객체를 인스턴스화 할 수 없음
	 */
	private Redis() {
		String servers = null;
		if (Configuration.getInstance().containsKey("redis.servers")) {
			servers = Configuration.getInstance().getString("redis.servers");
		} else {
			throw new RuntimeException("redis의 호스트정보가 설정되었습니다.");
		}
		String[] parts = _parsePrimaryAddress(servers);
		String host = parts[0];
		int port = Integer.parseInt(parts[1]);
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		_pool = new JedisPool(poolConfig, host, port, _TIMEOUT);
	}

	/**
	 * 객체의 인스턴스를 리턴해준다.
	 *
	 * @return Redis 객체의 인스턴스
	 */
	public synchronized static Redis getInstance() {
		if (_uniqueInstance == null) {
			_uniqueInstance = new Redis();
		}
		return _uniqueInstance;
	}

	@Override
	public void set(String key, Object value, int seconds) {
		set(_serialize(key), _serialize(value), seconds);
	}

	public void set(byte[] key, byte[] value, int seconds) {
		try (Jedis jedis = _pool.getResource()) {
			jedis.setex(key, seconds, value);
		} catch (JedisConnectionException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Object get(String key) {
		return get(_serialize(key));
	}

	public Object get(byte[] key) {
		try (Jedis jedis = _pool.getResource()) {
			return _deserialize(jedis.get(key));
		} catch (JedisConnectionException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Map<String, Object> get(String[] keys) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		for (String key : keys) {
			resultMap.put(key, get(key));
		}
		return resultMap;
	}

	@Override
	public long incr(String key, int by) {
		return incr(_serialize(key), by);
	}

	public long incr(byte[] key, int by) {
		try (Jedis jedis = _pool.getResource()) {
			return jedis.incrBy(key, by);
		} catch (JedisConnectionException e) {
			return -1;
		}
	}

	@Override
	public long decr(String key, int by) {
		return decr(_serialize(key), by);
	}

	public long decr(byte[] key, int by) {
		try (Jedis jedis = _pool.getResource()) {
			return jedis.decrBy(key, by);
		} catch (JedisConnectionException e) {
			return -1;
		}
	}

	@Override
	public void delete(String key) {
		try (Jedis jedis = _pool.getResource()) {
			jedis.del(key);
		} catch (JedisConnectionException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void clear() {
		try (Jedis jedis = _pool.getResource()) {
			jedis.flushAll();
		} catch (JedisConnectionException e) {
			throw new RuntimeException(e);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////Private 메소드

	/**
	 * 문자열에서 첫 번째 redis 호스트 주소를 파싱하여 반환한다.
	 * @param str 공백/쉼표로 구분된 주소문자열
	 * @return [host, port] 배열
	 */
	private String[] _parsePrimaryAddress(String str) {
		if (str == null || "".equals(str.trim())) {
			throw new IllegalArgumentException("redis의 호스트정보가 설정되었습니다.");
		}
		for (String addr : str.split("(?:\\s|,)+")) {
			if ("".equals(addr)) {
				continue;
			}
			int sep = addr.lastIndexOf(':');
			if (sep < 1) {
				throw new IllegalArgumentException("주소형식이 잘못되었습니다. 형식=>호스트:포트");
			}
			return new String[] { addr.substring(0, sep), addr.substring(sep + 1) };
		}
		throw new IllegalArgumentException("redis의 호스트정보가 설정되었습니다.");
	}

	/**
	 * 객체를 바이트배열로 직렬화 한다.
	 * @param obj 직렬화할 객체
	 * @return 바이트배열
	 */
	public byte[] _serialize(Object obj) {
		ObjectOutputStream oos = null;
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(obj);
			byte[] bytes = baos.toByteArray();
			return bytes;
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * 바이트배열을 객체로 역직렬화 한다.
	 * @param bytes 바이트배열
	 * @return 역직렬화된 객체
	 */
	public Object _deserialize(byte[] bytes) {
		ByteArrayInputStream bais = null;
		try {
			bais = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(bais);
			return ois.readObject();
		} catch (Exception e) {
		}
		return null;
	}
}
