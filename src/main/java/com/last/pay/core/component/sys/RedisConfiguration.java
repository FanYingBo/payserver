package com.last.pay.core.component.sys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:config/redis.properties")
public class RedisConfiguration {

	@Value("${address}")
	private String address;
	@Value("${useCache}")
	private Boolean useCache;
	@Value("${key}")
	private String key;
	@Value("${timeout}")
	private Integer timeout;
	@Value("${MaxIdle}")
	private Integer MaxIdle;
	@Value("${MinIdle}")
	private Integer MinIdle;
	@Value("${MaxWait}")
	private Integer MaxWait;
	@Value("${MaxTotal}")
	private Integer MaxTotal;
	@Value("${MaxRedirections}")
	private Integer MaxRedirections;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Boolean getUseCache() {
		return useCache;
	}

	public void setUseCache(Boolean useCache) {
		this.useCache = useCache;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Integer getTimeout() {
		return timeout;
	}

	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}

	public Integer getMaxIdle() {
		return MaxIdle;
	}

	public void setMaxIdle(Integer maxIdle) {
		MaxIdle = maxIdle;
	}

	public Integer getMinIdle() {
		return MinIdle;
	}

	public void setMinIdle(Integer minIdle) {
		MinIdle = minIdle;
	}

	public Integer getMaxWait() {
		return MaxWait;
	}

	public void setMaxWait(Integer maxWait) {
		MaxWait = maxWait;
	}

	public Integer getMaxTotal() {
		return MaxTotal;
	}

	public void setMaxTotal(Integer maxTotal) {
		MaxTotal = maxTotal;
	}

	public Integer getMaxRedirections() {
		return MaxRedirections;
	}

	public void setMaxRedirections(Integer maxRedirections) {
		MaxRedirections = maxRedirections;
	}
	
	
}
