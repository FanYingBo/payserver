package com.last.pay.cache.replacement;

import java.util.Date;
import java.util.Objects;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.last.pay.base.CodeMsg;
import com.last.pay.base.CodeMsgType;
import com.last.pay.base.common.constants.Constants.ReplacementOrderConstants;
import com.last.pay.core.db.pojo.user.UserIfm;
import com.last.pay.core.db.pojo.web.PayOrder;
import com.last.pay.core.db.pojo.web.PayPoint;
import com.last.pay.core.db.pojo.web.ReplacementOrder;
import com.last.pay.core.component.RequestHandlerAdapter;
import com.last.pay.core.handler.iter.PayRequestHandler;
import com.last.pay.core.service.IPayOrderService;
import com.last.pay.core.service.IReplacementOrderService;
/**
 * {@link ReplacementOrderConstant#NOT_SUCCESS}
 * {@link ReplacementOrderConstant#TRANS_ERROR}
 * {@link ReplacementOrderConstant#SUCCESS_ORDER}
 * @author Administrator
 *
 */
public class ReplacementOrderTask {
	
	private static final Log logger = LogFactory.getLog(ReplacementOrderTask.class);
	
	private RequestHandlerAdapter requestHandlerAdapter;
	
	private IPayOrderService payOrderService;
	
	private IReplacementOrderService replacementOrderService;
	
	private ReplacementOrder replacementOrder;
	

	public ReplacementOrderTask(RequestHandlerAdapter requestHandlerAdapter, IPayOrderService payOrderService,
			ReplacementOrder replacementOrders,IReplacementOrderService replacementOrderService) {
		this.requestHandlerAdapter = requestHandlerAdapter;
		this.payOrderService = payOrderService;
		this.replacementOrder = replacementOrders;
		this.replacementOrderService = replacementOrderService;
	}

	public void run() {
		try {
			if(Objects.nonNull(replacementOrder)) {
				PayRequestHandler requestHandler = requestHandlerAdapter.matchingHandler(String.valueOf(replacementOrder.getPayType()));
				if(Objects.nonNull(requestHandler)) {
					CodeMsg<?> queryRequest = requestHandler.queryRequest(replacementOrder);
					Integer optTimes = replacementOrder.getOptTimes();
					if(Objects.nonNull(queryRequest)) {
						if(queryRequest.getCode() == CodeMsgType.SUCCESS.getCode()) { // 成功 则更新状态为 1
							PayOrder payOrder = (PayOrder)queryRequest.getData();
							payOrderService.bindUserNickName(payOrder);
							PayPoint payPoint = payOrderService.checkAndBindPayPointStatus(payOrder);
							CodeMsg<?> codeMsg = payOrderService.addPayOrder(payOrder, payPoint);
							if(codeMsg.getCode() == CodeMsgType.SUCCESS.getCode()) {
								replacementOrder.setStatus(ReplacementOrderConstants.SUCCESS_ORDER);
								replacementOrder.setSuccessDate(new Date());
								replacementOrder.setOptTimes(++optTimes);
								replacementOrder.setReplaceDate(new Date());
								replacementOrderService.updateReplacementOrder(replacementOrder, true);
								return;
							}
						} else { // 查询返回的是失败，更新补单状态为2，不再执行
							updateErrorReplacement(ReplacementOrderConstants.TRANS_ERROR,queryRequest);
							return;
						} 
					}
				}
			}
		} catch (Exception e) {
			logger.error("訂單處理失敗",e);
			updateErrorReplacement(ReplacementOrderConstants.NOT_SUCCESS, null);
		}
	}

	public void updateErrorReplacement(int status,CodeMsg<?> codeMsg) {
		replacementOrder.setStatus(status);
		Integer optTimes = replacementOrder.getOptTimes();
		replacementOrder.setOptTimes(++optTimes);
		replacementOrder.setReplaceDate(new Date());
		if(Objects.nonNull(codeMsg)) {
			replacementOrder.setErrorDesc("错误码:"+codeMsg.getCode()+",错误信息:"+codeMsg.getMsg());
		}
		replacementOrderService.updateReplacementOrder(replacementOrder, false);
	}
	
}
