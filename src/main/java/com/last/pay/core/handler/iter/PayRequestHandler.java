package com.last.pay.core.handler.iter;

import javax.servlet.http.HttpServletRequest;

import com.last.pay.base.CodeMsg;
import com.last.pay.base.CodeMsgType;
import com.last.pay.core.db.pojo.web.PayOrder;
import com.last.pay.core.db.pojo.web.ReplacementOrder;
import com.last.pay.core.component.RequestHandlerAdapter;
/**
 * 所有繼承 PayRequestHandler的類都會被加載，繼承類實現這兩個方法
 * 注冊{@link RequestHandlerAdapter#register(String, PayRequestHandler)}
 * 獲取{@link RequestHandlerAdapter#matchingHandler(String)}
 * @author Administrator
 *
 */
public interface PayRequestHandler {
	/**
	 * 支付類型
	 * @return
	 */
	String getType();
	/**
	 *  后端还是页面请求
	 * @return
	 */
	boolean isBackEnd();
	/**
	 *    是否需要回调
	 * @return
	 */
	boolean isNeedCallBack();
	/**
	 * 處理請求
	 * @param payOrder
	 * @param request
	 * @return
	 */
	CodeMsg<?> processRequest(PayOrder payOrder,HttpServletRequest request);
	/**
	 * 订单查询
	 * @param payOrder
	 * @return
	 */
	default CodeMsg<?> queryRequest(PayOrder payOrder){
		return CodeMsg.success(CodeMsgType.SUCCESS);
	}
	/**
	 * 	补单查询接口
	 * @return
	 */
	default CodeMsg<?> queryRequest(ReplacementOrder replacementOrder){
		return CodeMsg.success(CodeMsgType.SUCCESS);
	}

}
