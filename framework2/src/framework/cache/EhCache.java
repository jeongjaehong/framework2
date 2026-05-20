/**
 * @(#)EhCache.java
 */
package framework.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;

/**
 * 기본 캐시 구현체 - Caffeine 기반 (https://github.com/ben-manes/caffeine)
 */
public class EhCache extends AbstractCache {

	/**
	 * 싱글톤 객체
	 */
	private static EhCache _uniqueInstance;

	/**
	 * 캐시 엔트리 (값 + TTL 보관)
	 */
	private static class CacheEntry {
		final Object value;
		final long expiryNanos;

		CacheEntry(Object value, int seconds) {
			this.value = value;
			this.expiryNanos = TimeUnit.SECONDS.toNanos(seconds);
		}
	}

	/**
	 * Caffeine 캐시
	 */
	private final Cache<String, CacheEntry> _cache;

	/**
	 * 생성자, 외부에서 객체를 인스턴스화 할 수 없음
	 */
	private EhCache() {
		_cache = Caffeine.newBuilder()
			.expireAfter(new Expiry<String, CacheEntry>() {
				@Override
				public long expireAfterCreate(String key, CacheEntry entry, long currentTime) {
					return entry.expiryNanos;
				}
				@Override
				public long expireAfterUpdate(String key, CacheEntry entry, long currentTime, long currentDuration) {
					return entry.expiryNanos;
				}
				@Override
				public long expireAfterRead(String key, CacheEntry entry, long currentTime, long currentDuration) {
					return currentDuration;
				}
			})
			.build();
	}

	/**
	 * 객체의 인스턴스를 리턴해준다.
	 *
	 * @return EhCache 객체의 인스턴스
	 */
	public synchronized static EhCache getInstance() {
		if (_uniqueInstance == null) {
			_uniqueInstance = new EhCache();
		}
		return _uniqueInstance;
	}

	@Override
	public void set(String key, Object value, int seconds) {
		_cache.put(key, new CacheEntry(value, seconds));
	}

	@Override
	public Object get(String key) {
		CacheEntry entry = _cache.getIfPresent(key);
		return (entry == null) ? null : entry.value;
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
	public synchronized long incr(String key, int by) {
		CacheEntry entry = _cache.getIfPresent(key);
		if (entry == null) {
			return -1;
		}
		long newValue = ((Number) entry.value).longValue() + by;
		_cache.put(key, new CacheEntry(newValue, (int) TimeUnit.NANOSECONDS.toSeconds(entry.expiryNanos)));
		return newValue;
	}

	@Override
	public synchronized long decr(String key, int by) {
		CacheEntry entry = _cache.getIfPresent(key);
		if (entry == null) {
			return -1;
		}
		long newValue = ((Number) entry.value).longValue() - by;
		_cache.put(key, new CacheEntry(newValue, (int) TimeUnit.NANOSECONDS.toSeconds(entry.expiryNanos)));
		return newValue;
	}

	@Override
	public void delete(String key) {
		_cache.invalidate(key);
	}

	@Override
	public void clear() {
		_cache.invalidateAll();
	}
}
