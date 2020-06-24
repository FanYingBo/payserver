package com.last.pay.cache;

public class RedisCacheManager implements CacheManager{
	
	private String keyStart;
	private Boolean useCache;
	
	public void setKeystart(String keyStart) {
		this.keyStart = keyStart;
	}
	
	public String getKeyStart() {
		return this.keyStart;
	}
	public String formatKey(String key) {
		return getKeyStart() +":"+ key;
	}

	public Boolean getUseCache() {
		return useCache;
	}

	public void setUseCache(Boolean useCache) {
		this.useCache = useCache;
	}

	@Override
	public void setValue(String key, String value, int expiredSeconds) {
		
	}

	@Override
	public String getValue(String key) {
		return null;
	}

	@Override
	public long ttl(String key) {
		return 0;
	}

}
