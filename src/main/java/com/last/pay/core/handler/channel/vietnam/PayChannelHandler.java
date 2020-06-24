package com.last.pay.core.handler.channel.vietnam;

import com.last.pay.base.CodeMsg;
import com.last.pay.base.common.constants.Constants.PayChannelConstants;
import com.last.pay.core.db.pojo.web.PayOrder;

public interface PayChannelHandler {
	/**
	 * 点卡渠道类型
	 * {@link PayChannelConstants}
	 * @return
	 */
	public int getPayChannel();
	/**
	 * 接口调用
	 * @param payOrder 订单
	 * @param cardNum 卡号
	 * @param cardPin 卡密
	 * @param providerCode 提供商
	 * @return
	 */
	public CodeMsg<?> doProcessRequest(PayOrder payOrder,String cardNum, String cardPin, String providerCode);
}
