package com.last.pay.cache;



import java.util.concurrent.ExecutorService;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class PayCacheHandle {
	
	private final static Log logger = LogFactory.getLog(PayCacheHandle.class);
	
	@Autowired
	private ExecutorService mainExecutor;
	@Autowired
	private RedisCachePayTask redisCachePayTask;
	
	@PostConstruct
	public void taskStart() {
		logger.info("Redis 支付回调线程启动");
		mainExecutor.execute(redisCachePayTask);
	}
	
}
