package com.last.pay.cache;

import org.springframework.stereotype.Component;

import com.last.pay.cache.mode.TempPayInfo;

@Component
public class UserPayCacheManager {
	
	
	public TempPayInfo getUserSlotsPointInfo(int userId){
		return new TempPayInfo();
	}
	
	public TempPayInfo getUserMapPointInfo(int userId){
		return new TempPayInfo();
	}
	
	public TempPayInfo setUserMapPointInfo(int userId, int toll, int position){
		return new TempPayInfo();
	}
}
