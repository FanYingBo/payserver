package com.last.pay.cache;

import java.util.concurrent.atomic.AtomicLong;

public class IncrCacheManager {
	
	private AtomicLong incrNum = new AtomicLong(0);
	
	
	public long incrOrder() {
		return incrNum.getAndIncrement();
	}
	
	public void updateWarHeadPool(int publicWarHead) {
		
	}
	
	public long getWarHeadPool() {
		return 0l;
	}
}
