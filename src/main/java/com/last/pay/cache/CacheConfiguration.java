package com.last.pay.cache;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class CacheConfiguration {
	
	@Bean("mainExecutor")
	public ExecutorService cacheThreadExecutors() {
		return Executors.newCachedThreadPool();
	}

}
