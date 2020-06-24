package com.last.pay.core.handler.channel.vietnam;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.last.pay.core.db.pojo.web.PayOrder;
import com.last.pay.core.handler.common.PayRequestChecker;

@Component
public class PayChannelAdapter {
	
	@Autowired
	private ApplicationContext applicationContext;
	
	private Map<Integer,PayChannelHandler> payChannelHandlerMap = new HashMap<Integer, PayChannelHandler>();
	@Autowired
	private PayRequestChecker payRequestChecker;
	@PostConstruct
	public void initPayChannelHandle() {
		Map<String, PayChannelHandler> payChannelMap = applicationContext.getBeansOfType(PayChannelHandler.class);
		Iterator<Entry<String, PayChannelHandler>> iterator = payChannelMap.entrySet().iterator();
		while(iterator.hasNext()) {
			Entry<String, PayChannelHandler> entry = iterator.next();
			PayChannelHandler vietnamPayChannelHandler = entry.getValue();
			payChannelHandlerMap.put(vietnamPayChannelHandler.getPayChannel(), vietnamPayChannelHandler);
		}
	}
	
	
	public PayChannelHandler matchingPayChannel(PayOrder payOrder) {
		int payChannel = payRequestChecker.correctPayChannel(payOrder);
		return payChannelHandlerMap.get(payChannel);
	}

}
