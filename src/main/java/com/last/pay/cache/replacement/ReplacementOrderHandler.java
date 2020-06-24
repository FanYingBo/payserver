package com.last.pay.cache.replacement;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.last.pay.base.common.constants.Constants.PayStatusConstants;
import com.last.pay.base.common.constants.Constants.ReplacementOrderConstants;
import com.last.pay.core.db.pojo.web.PayOrder;
import com.last.pay.core.db.pojo.web.ReplacementOrder;
import com.last.pay.core.component.RequestHandlerAdapter;
import com.last.pay.core.service.IPayOrderService;
import com.last.pay.core.service.IReplacementOrderService;

@Component
public class ReplacementOrderHandler {
	
	private static final Log logger = LogFactory.getLog(ReplacementOrderHandler.class);
	
	@Autowired
	private IReplacementOrderService replacementOrderService;
	@Autowired
	private RequestHandlerAdapter requestHandlerAdapter;
	@Autowired
	private IPayOrderService payOrderService;
	
	private LinkedList<ReplacementOrder> allNotSuccesOrder = new java.util.LinkedList<>();
	
	private volatile boolean finish = true;
	
	@Scheduled(cron = "0/10 * * * * ?")
	public void timerReplacementOrder() {
		if(!finish) {
			logger.info("replace order is not finish !");
			return;
		}
		finish = false;
		if(allNotSuccesOrder.isEmpty()) {
			try {
				List<ReplacementOrder> tmp = replacementOrderService.getAllNotSuccesOrder();
				if(tmp != null && !tmp.isEmpty()) {
					logger.info("*******定时任务补单开始*******总个数："+tmp.size());
					allNotSuccesOrder.addAll(tmp);
				}
			} catch (Exception e) {
				logger.error("query order error", e);
			}
			if(!allNotSuccesOrder.isEmpty()) {
				try {
					while(!allNotSuccesOrder.isEmpty()) {
						ReplacementOrder replacementOrder = allNotSuccesOrder.poll();
						if(replacementOrder != null && checkExcuteTimes(replacementOrder)) {
							if(!checkPayOrderHave(replacementOrder)) {
								logger.warn("replace had success : " + replacementOrder.getOrderNum());
								replacementOrder.setStatus(ReplacementOrderConstants.SUCCESS_ORDER);
								replacementOrder.setSuccessDate(new Date());
								replacementOrder.setReplaceDate(new Date());
								replacementOrderService.updateReplacementOrder(replacementOrder, Boolean.TRUE);
							}else {
								//TODO 成功或者失败都要更新补单时间 replaceDate
								logger.info("replace start : " + replacementOrder.getOrderNum());
								new ReplacementOrderTask(requestHandlerAdapter, payOrderService, replacementOrder,replacementOrderService).run();
							}
						}
					}
				} catch (Exception e) {
					logger.error("replacement order error", e);
				}
			}
		}
		finish = true;
	}
	
	private boolean checkExcuteTimes(ReplacementOrder t) {
		if(t.getReplaceDate() == null) {
			return true;
		}
		if(t.getOptTimes() == null || t.getOptTimes() == 0) {
			return true;
		}
		long second = (System.currentTimeMillis()-t.getReplaceDate().getTime())/1000;
		//TODO 临时写死，年后通过参数控制
		if(t.getOptTimes() < 5) {
			if(second >= 60) {
				return true;
			}
		}else if(t.getOptTimes() < 10) {
			if(second >= 120) {
				return true;
			}
		}else if(t.getOptTimes() < 15) {
			if(second >= 300) {
				return true;
			}
		}else if(t.getOptTimes() < 20) {
			if(second >= 600) {
				return true;
			}
		}else if(t.getOptTimes() < 25) {
			if(second >= 3600) {
				return true;
			}
		}else {
			if(second >= 86400) {
				return true;
			}
		}
		return false;
	}
	/**
	 * 回调信息如果成功，将不再进行补单
	 * @param replacementOrder
	 * @return
	 */
	private boolean checkPayOrderHave(ReplacementOrder replacementOrder) {
		PayOrder payOrder = payOrderService.getPayOrderByThirdOrderNum(replacementOrder.getThirdOrderNum());
		if(payOrder == null || payOrder.getStatus() != PayStatusConstants.SUCCESS_ORDER) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
}
