package com.last.pay.core.service;
/**
 * 	 对redis中的订单进行处理
 * @author Administrator
 *
 */

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.last.pay.base.CodeMsg;
import com.last.pay.cache.command.PayCallBackCommand;
import com.last.pay.core.db.pojo.web.PayOrder;
import com.last.pay.core.dto.vietnam.kingcard.KingCardCallBackParam;

public interface IPayCacheService {
	/**
	 * redis 订单处理
	 * @param payCallbackCommand
	 * @return
	 */
	public CodeMsg<?> handleRedisCachePay(PayCallBackCommand payCallbackCommand);
	/**
	 * 	 回调订单处理
	 * @param payOrder
	 * @return
	 */
	public CodeMsg<?> handlePayCallBack(HttpServletRequest request);
	
	/**
	 * UPay 回调处理
	 * @param request
	 * @return
	 */
	public CodeMsg<?> handleUPayCallBack(HttpServletRequest request);

	/**
	 * Paycent 回调处理
	 * @param request
	 * @return
	 */
	public CodeMsg<?> handlePayCentCallBack(HttpServletRequest request);
	
	/**
	 * Vietnam 回调处理
	 * @param request
	 * @return
	 */
	public CodeMsg<?> handleVietnamCallBack(HttpServletRequest request);
	/**
	 * 第三方查询接口
	 * @param payOrder
	 * @return
	 */
	public CodeMsg<?> queryThirdOrderTransStatus(PayOrder payOrder); 
	
	/**
	 *  king card 回调
	 * @param httpServletRequest
	 * @return
	 */
	public CodeMsg<?> handleVietnamKingCardCallBack(KingCardCallBackParam kingCardCallBackParam,HttpServletRequest httpServletRequest);
	
	/**
	 * BangLang 回调
	 * @param bangLangCardCallBack
	 * @return
	 */
	public CodeMsg<?> handleVietnamBangLangCallBack(Map<String, Object> paramMap);
	

}
