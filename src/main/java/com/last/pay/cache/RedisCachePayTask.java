package com.last.pay.cache;

import java.util.concurrent.ExecutorService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.last.pay.cache.command.PayCallBackCommand;
import com.last.pay.core.service.IPayCacheService;

@Component
public class RedisCachePayTask implements Runnable{
	
	private final static Log logger = LogFactory.getLog(PayCacheHandle.class);
	@Autowired
	private IPayCacheService payCacheService;
	@Autowired
	private GMQueneManager queneManager;
	@Autowired
	private ExecutorService mainExecutor;
	
	@Override
	public void run() {
		while(true){
			try {
				PayCallBackCommand popPayCallbackCommand = queneManager.popPayCallbackCommand();
				logger.info("正在处理redis回调中的订单，"+popPayCallbackCommand);
				mainExecutor.execute(new RedisCacheHandleTask(popPayCallbackCommand,payCacheService));
			}catch(Exception e){
				logger.error("Redis缓存支付回调获取失败", e);
				e.printStackTrace();
			}
		}
	}

}
