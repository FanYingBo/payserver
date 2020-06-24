package com.last.pay.core.handler.channel.vietnam;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.last.pay.base.CodeMsg;
import com.last.pay.base.CodeMsgType;
import com.last.pay.core.db.pojo.web.PayOrder;
import com.last.pay.core.exception.ThirdPayException;

@Component
public class PayChannelDelegate {
	
	@Autowired
	private PayChannelAdapter payChannelAdapter;
	
	public CodeMsg<?> doProcessRequest(PayOrder payOrder, String cardNum, String cardPin, String providerCode){
		PayChannelHandler matchingPayChannel = payChannelAdapter.matchingPayChannel(payOrder);
		if(Objects.isNull(matchingPayChannel)) {
			throw new ThirdPayException(CodeMsgType.ERR_SYS.getCode(),CodeMsgType.ERR_SYS.getMsg());
		}
		return matchingPayChannel.doProcessRequest(payOrder, cardNum, cardPin, providerCode);
	}

}
