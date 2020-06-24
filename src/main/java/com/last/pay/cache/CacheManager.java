package com.last.pay.cache;

public interface CacheManager {
	
	public void setValue(String key, String value,int expiredSeconds);
	/**
	 * 获取字符串的值
	 * @param key
	 * @return
	 */
	public String getValue(String key);
	/**
	 * 过期时间
	 * @param key
	 * @return
	 */
	public long ttl(String key);
	
	

}
