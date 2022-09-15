/**
 * @(#)EhCache.java
 */
package framework.cache;

import java.util.HashMap;
import java.util.Map;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * �⺻ ĳ�� ����ü (http://ehcache.org/)
 */
public class EhCache extends AbstractCache {

	/**
	 * �̱��� ��ü
	 */
	private static EhCache _uniqueInstance;

	/**
	 * ĳ�� �Ŵ���
	 */
	private final CacheManager _cacheManager;

	/**
	 * ĳ�� ������Ʈ
	 */
	private final net.sf.ehcache.Cache _cache;

	/**
	 * �⺻ ĳ�� �̸�
	 */
	private static final String _CACHE_NAME = "framework2";

	/**
	 * ������, �ܺο��� ��ü�� �ν��Ͻ�ȭ �� �� ������ ����
	 */
	private EhCache() {
		_cacheManager = CacheManager.create();
		_cacheManager.addCache(_CACHE_NAME);
		_cache = _cacheManager.getCache(_CACHE_NAME);
	}

	/**
	 * ��ü�� �ν��Ͻ��� �������ش�.
	 *
	 * @return EhCache ��ü�� �ν��Ͻ�
	 */
	public synchronized static EhCache getInstance() {
		if (_uniqueInstance == null) {
			_uniqueInstance = new EhCache();
		}
		return _uniqueInstance;
	}

	@Override
	public void set(String key, Object value, int seconds) {
		Element e = new Element(key, value);
		e.setTimeToLive(seconds);
		_cache.put(e);
	}

	@Override
	public Object get(String key) {
		Element e = _cache.get(key);
		return (e == null) ? null : e.getObjectValue();
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
		Element e = _cache.get(key);
		if (e == null) {
			return -1;
		}
		long newValue = ((Number) e.getObjectValue()).longValue() + by;
		Element newE = new Element(key, newValue);
		newE.setTimeToLive(e.getTimeToLive());
		_cache.put(newE);
		return newValue;
	}

	@Override
	public synchronized long decr(String key, int by) {
		Element e = _cache.get(key);
		if (e == null) {
			return -1;
		}
		long newValue = ((Number) e.getObjectValue()).longValue() - by;
		Element newE = new Element(key, newValue);
		newE.setTimeToLive(e.getTimeToLive());
		_cache.put(newE);
		return newValue;
	}

	@Override
	public void delete(String key) {
		_cache.remove(key);
	}

	@Override
	public void clear() {
		_cache.removeAll();
	}
}
