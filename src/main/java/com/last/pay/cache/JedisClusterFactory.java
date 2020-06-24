package com.last.pay.cache;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.FactoryBean;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

public class JedisClusterFactory implements FactoryBean<JedisCluster>{
	
	
	private GenericObjectPoolConfig<Jedis> genericObjectPoolConfig ;
	private Integer maxRedirections;
	private Integer timeOut;
	private Set<HostAndPort> hosts = new HashSet<>();
	
	public JedisClusterFactory(String address) {
		if(StringUtils.isBlank(address)) {
			throw new NullPointerException();
		}
		String[] addresss = address.split(",");
		Set<HostAndPort> hosts = new HashSet<>();
		for(String hostAndPort : addresss) {
			String[] hostPort = hostAndPort.split(":");
			hosts.add(new HostAndPort(hostPort[0], Integer.parseInt(hostPort[1])));
		}
	}
	@Override
	public JedisCluster getObject() throws Exception {
		return new JedisCluster(hosts, timeOut, maxRedirections, genericObjectPoolConfig);
	}

	@Override
	public Class<?> getObjectType() {
		return JedisCluster.class;
	}

	public GenericObjectPoolConfig<Jedis> getGenericObjectPoolConfig() {
		return genericObjectPoolConfig;
	}

	public void setGenericObjectPoolConfig(GenericObjectPoolConfig<Jedis> genericObjectPoolConfig) {
		this.genericObjectPoolConfig = genericObjectPoolConfig;
	}


	public Integer getMaxRedirections() {
		return maxRedirections;
	}

	public void setMaxRedirections(Integer maxRedirections) {
		this.maxRedirections = maxRedirections;
	}

	public Integer getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(Integer timeOut) {
		this.timeOut = timeOut;
	}
	

}
