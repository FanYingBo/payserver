package com.last.pay.core.component;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.last.pay.core.handler.GeneralPayRequestHandler;
import com.last.pay.core.compute.ExchangePayParamBuilder;
/**
 * 對不同的支付類型進行適配handler
 * @author Administrator
 *
 */
@Component
public class RequestHandlerAdapter {
	
	private Map<String,GeneralPayRequestHandler> handlerMap = new ConcurrentHashMap<>();
	 
	private Map<String,ExchangePayParamBuilder> computeMap = new ConcurrentHashMap<>();
	
	
	@Autowired
	private ApplicationContext applicationContext;
	
	@PostConstruct
	public void initHandlers() {
		Map<String, ExchangePayParamBuilder> computBeanNames = applicationContext.getBeansOfType(ExchangePayParamBuilder.class);
		Iterator<Entry<String, ExchangePayParamBuilder>> computIterator = computBeanNames.entrySet().iterator();
		while(computIterator.hasNext()) {
			Entry<String, ExchangePayParamBuilder> next = computIterator.next();
			ExchangePayParamBuilder compute = next.getValue();
			register(compute.getCurrency(), compute);
		}
		
		Map<String, GeneralPayRequestHandler> handlerBeanNames = applicationContext.getBeansOfType(GeneralPayRequestHandler.class);
		Iterator<Entry<String, GeneralPayRequestHandler>> handlerIterator = handlerBeanNames.entrySet().iterator();
		while(handlerIterator.hasNext()) {
			Entry<String, GeneralPayRequestHandler> next = handlerIterator.next();
			GeneralPayRequestHandler handler = next.getValue();
			String typesStr = handler.getType();
			String[] types = typesStr.split(",");
			for(String type:types) {
				register(type, handler);
			}
		}
	}
	/**
	 * 注冊handler
	 * @param type
	 * @param requestHandler
	 */
	public void register(String type,GeneralPayRequestHandler requestHandler) {
		handlerMap.put(type, requestHandler);
	}
	
	/**
	 * 注冊compute
	 * @param type
	 * @param compute
	 */
	public void register(String type,ExchangePayParamBuilder compute) {
		computeMap.put(type, compute);
	}
	/**
	 * 適配獲取handler
	 * @param type
	 * 
	 */
	public GeneralPayRequestHandler matchingHandler(String type) {
		return handlerMap.get(type);
	}
	
	/**
	 * 	適配獲取computer
	 * @param type
	 * 
	 */
	public ExchangePayParamBuilder matchingBuilder(String currency) {
		return computeMap.get(currency);
	}

}
