package com.last.pay.core.service;



import javax.servlet.http.HttpServletRequest;

import com.last.pay.base.CodeMsg;
import com.last.pay.core.db.pojo.user.UserIfm;
import com.last.pay.core.db.pojo.web.PayOrder;
import com.last.pay.core.db.pojo.web.PayPoint;

public interface IPayOrderService {
	
	/**
	 *	第三方充值渠道
	 * @param payOrder
	 * @param type
	 * @param request
	 * @return
	 */
	public CodeMsg<?> thirdPayChannel(PayOrder payOrder,HttpServletRequest request);
	
	/***
	 * 	并添加订单
	 * @param payOrder
	 * @param cardNum
	 * @param cardPin
	 * @param providerCode
	 * @param cardPrintAmount
	 * @return
	 */
	public CodeMsg<?> addPayOrder(PayOrder payOrder,PayPoint payPoint);
	
	/**
	 * codaPay 网页支付请求
	 * @param payOrder
	 * @param request
	 * @return
	 */
	public CodeMsg<?> initCodaTranscation(PayOrder payOrder,String payType,HttpServletRequest request);
	
	/**
	 * 更新玩家和世界的弹头数量
	 * @param payOrder
	 * @return
	 */
	public void updateWarHeadInfo(PayOrder payOrder, UserIfm user);
	/**
	 * 判断请求是否合法(back end or page)
	 * @param request
	 * @return
	 */
	public boolean isLegalRequest(PayOrder payOrder);
	
	/**
	 * 	订单是否存在
	 * @param payOrder
	 * @return
	 */
	public boolean isHavePayOrder(PayOrder payOrder);
	/**
	 * 订单信息入库
	 * @param payOrder
	 */
	public void addOrderSuccess(PayOrder payOrder);
	
	/**
	 * 检查付费点状态
	 * @return
	 */
	public PayPoint checkAndBindPayPointStatus(PayOrder payOrder);
	
	/**
	 * 绑定玩家信息
	 * @param payOrder
	 * @return
	 */
	public UserIfm bindUserNickName(PayOrder payOrder);
	
	/**
	 * 	查找PayOrder
	 * @param orderNum
	 * @return
	 */
	PayOrder getPayOrder(String orderNum);
	
	/**
	 * 获取订单
	 * @param thirdOrderNum
	 * @return
	 */
	public PayOrder getPayOrderByThirdOrderNum(String thirdOrderNum);
}
