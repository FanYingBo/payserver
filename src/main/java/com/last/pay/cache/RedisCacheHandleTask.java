package com.last.pay.cache;


import java.util.Objects;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.last.pay.cache.command.PayCallBackCommand;
import com.last.pay.core.service.IPayCacheService;

public class RedisCacheHandleTask implements Runnable{
	
	private static Log logger = LogFactory.getLog(RedisCacheHandleTask.class);
	
	private PayCallBackCommand popPayCallbackCommand;
	
	private IPayCacheService payCacheService;
	
	public RedisCacheHandleTask(PayCallBackCommand popPayCallbackCommand,IPayCacheService payCacheService) {
		this.popPayCallbackCommand = popPayCallbackCommand;
		this.payCacheService = payCacheService;
	}

	@Override
	public void run() {
		try {
			if(Objects.nonNull(popPayCallbackCommand)) {
				payCacheService.handleRedisCachePay(popPayCallbackCommand);
			}
		} catch (Exception e) {
			logger.error("Redis缓存支付回调获取失败", e);
			e.printStackTrace();
		}
		
	}

}
