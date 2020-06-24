package com.last.pay.core.handler.iter;

import javax.servlet.http.HttpServletRequest;

import com.last.pay.core.db.pojo.web.PayOrder;

public interface PayOrderVerifyHandler {
	/**
	 * 	验证第三方订单（订单号验证）
	 * @return
	 */
	public default boolean isExistThirdPayOrder(PayOrder payOrder) {
		return Boolean.FALSE;
	}
	/**
	 * 	检查成功订单是否存在（其他条件验证）
	 * @param payOrder
	 * @param httpServletRequest
	 * @return
	 */
	public default boolean isHaveSuccessPayOrder(PayOrder payOrder, HttpServletRequest httpServletRequest){
		return Boolean.FALSE;
	}

}
