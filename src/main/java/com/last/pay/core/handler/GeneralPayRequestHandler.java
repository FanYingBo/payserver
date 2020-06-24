package com.last.pay.core.handler;

import javax.servlet.http.HttpServletRequest;

import com.last.pay.base.CodeMsg;
import com.last.pay.base.CodeMsgType;
import com.last.pay.core.db.pojo.web.PayOrder;
import com.last.pay.core.db.pojo.web.ReplacementOrder;
import com.last.pay.core.handler.iter.CodaPayRequestInitHandler;
import com.last.pay.core.handler.iter.PayOrderVerifyHandler;
import com.last.pay.core.handler.iter.PayRequestHandler;
/**
 * 订单处理框架
 * @author Administrator
 *
 */
public abstract class GeneralPayRequestHandler implements PayRequestHandler, PayOrderVerifyHandler, CodaPayRequestInitHandler{
	
	
	/**
	 * 如果订单未成功，将执行该程序
	 * @param payOrder
	 * @param request
	 * @return
	 */
	public abstract CodeMsg<?> doProcessRequest(PayOrder payOrder,HttpServletRequest request);
	/**
	 * 检查第三方订单是否存在已经成功的订单
	 * @param payOrder
	 * @return
	 */
	public abstract boolean confirmIfExistPayOrder(PayOrder payOrder);
	/**
	 * 	检查第三方订单是否存在已经成功的订单（其他非订单号条件:例如卡号）
	 * @param payOrder
	 * @param httpServletRequest
	 * @return
	 */
	public abstract boolean confirmIfExistSuccessPayOrder(PayOrder payOrder, HttpServletRequest httpServletRequest);
	/**
	 * 检查订单中的补单号是否存在或者已经成功 （验证是否需要补单）
	 * @param replacementOrder
	 * @return
	 */
	public abstract boolean isSuccessPayOrder(ReplacementOrder replacementOrder);
	/**
	 * 补单查询
	 * @param replacementOrder
	 * @return
	 */
	public CodeMsg<?> doQueryRequest(ReplacementOrder replacementOrder){
		return CodeMsg.success(CodeMsgType.SUCCESS);
	}
	
	@Override
	public final CodeMsg<?> processRequest(PayOrder payOrder, HttpServletRequest request) {
		boolean existThirdPayOrder = isExistThirdPayOrder(payOrder);
		if(existThirdPayOrder) {
			return CodeMsg.failure(CodeMsgType.ERR_PAYORDER_HAD_HANDLED);
		}
		boolean haveSuccessPayOrder = isHaveSuccessPayOrder(payOrder, request);
		if(haveSuccessPayOrder) {
			return CodeMsg.failure(CodeMsgType.ERR_PAY_FAILED);
		}
		return doProcessRequest(payOrder,request);
	}
	
	@Override
	public CodeMsg<?> queryRequest(ReplacementOrder replacementOrder) {
		boolean isSuccessPayOrder = isSuccessPayOrder(replacementOrder);
		if(isSuccessPayOrder) {
			return CodeMsg.failure(CodeMsgType.ERR_PAYORDER_HAD_HANDLED);
		} 
		return doQueryRequest(replacementOrder);
	}
	
	@Override
	public final boolean isExistThirdPayOrder(PayOrder payOrder) {
		return confirmIfExistPayOrder(payOrder);
	}
	
	@Override
	public final boolean isHaveSuccessPayOrder(PayOrder payOrder, HttpServletRequest httpServletRequest) {
		return confirmIfExistSuccessPayOrder(payOrder, httpServletRequest);
	}
	@Override
	public abstract String getType() ;

	@Override
	public abstract boolean isBackEnd();

	@Override
	public abstract boolean isNeedCallBack();
	
}
