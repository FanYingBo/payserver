package com.last.pay.core.service.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.last.pay.base.common.constants.Constants.PayStatusConstants;
import com.last.pay.base.common.constants.Constants.PayTypeConstants;
import com.last.pay.base.common.constants.Constants.ReplacementOrderConstants;
import com.last.pay.core.db.pojo.web.PayOrder;
import com.last.pay.core.db.pojo.web.ReplacementOrder;
import com.last.pay.core.db.mapper.webdb.ReplacementOrderMapper;
import com.last.pay.core.service.IPayOrderQueryService;
import com.last.pay.core.service.IReplacementOrderService;

@Service
public class ReplacementOrderServiceImpl implements IReplacementOrderService{
	
	private static final Log logger = LogFactory.getLog(ReplacementOrderServiceImpl.class);
	
	@Autowired
	private IPayOrderQueryService payOrderQueryService;
	
	@Autowired
	private ReplacementOrderMapper replacementOrderMapper;

	@Override
	public void addReplacementOrder(ReplacementOrder replacementOrder, String cardNum) {
		try {
			if(replacementOrder.getPayType() == PayTypeConstants.Vietnam_CARD) {
				List<PayOrder> payOrderSuccess = payOrderQueryService.getPayOrderByFullText(replacementOrder.getPayType(), cardNum, PayStatusConstants.SUCCESS_ORDER);
				List<ReplacementOrder> replacementOrders = getReplacementOrderByFullText(cardNum);
				if((payOrderSuccess == null || payOrderSuccess.isEmpty()) && (replacementOrders == null || replacementOrders.isEmpty())) {
					replacementOrderMapper.insertReplacementOrder(replacementOrder);
				} else {
					if(payOrderSuccess != null && payOrderSuccess.size() > 0){
						logger.warn("卡号["+cardNum+"]存在订单记录："+payOrderSuccess+"，无须添加补单列表");
					}else {
						logger.warn("卡号["+cardNum+"]存在补单记录："+replacementOrders+"，无须添加补单列表");
					}
				}
			}else {
				replacementOrderMapper.insertReplacementOrder(replacementOrder);
			}
		} catch (Exception e) {
			logger.error("记录补单表失败,"+replacementOrder,e);
		}
	}

	@Override
	public List<ReplacementOrder> getAllNotSuccesOrder() {
		try {
			return replacementOrderMapper.getReplacementOrdersByStatus(ReplacementOrderConstants.NOT_SUCCESS);
		} catch (Exception e) {
			logger.error("查询补单表失败，"+e.getMessage());
			return null;
		}
	}

	@Override
	public void updateReplacementOrder(ReplacementOrder replacementOrder, boolean success) {
		try {
			logger.info("更新补单信息,"+replacementOrder);
			if(success) {
				replacementOrderMapper.updateReplacementOrderSuccess(replacementOrder);
			}else {
				replacementOrderMapper.updateReplacementOrderFailt(replacementOrder);
			}
		} catch (Exception e) {
			logger.error("更新补单表失败,"+replacementOrder,e);
		}
	}

	@Override
	public List<ReplacementOrder> getReplacementOrderByFullText(String cardNum) {
		try {
			return replacementOrderMapper.getReplacementOrderByFulltextIndex(PayTypeConstants.Vietnam_CARD, cardNum);
		} catch (Exception e) {
			logger.error("查询补单表失败，"+e.getMessage());
			return null;
		}
	}
}
