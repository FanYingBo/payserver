package com.last.pay.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class PayCacheManager {

	@Autowired
	private CacheManager cacheManager;
	
	public void setVietnamCardNum(int userId,String value,int expiredSeconds) {
		String key = formatKey(userId);
		cacheManager.setValue(key, value, expiredSeconds);
	}
	
	public String getVietnamCardNum(int userId) {
		String formatKey = formatKey(userId);
		return cacheManager.getValue(formatKey);
	}
	
	public long remainTime(int userId) {
		String formatKey = formatKey(userId);
		return cacheManager.ttl(formatKey);
	}
	
	private String formatKey(int userId) {
		return String.format("",userId);
	}
}
