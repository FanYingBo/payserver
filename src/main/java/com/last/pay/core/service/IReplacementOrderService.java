package com.last.pay.core.service;

import java.util.List;

import com.last.pay.core.db.pojo.web.ReplacementOrder;

public interface IReplacementOrderService {

	public void addReplacementOrder(ReplacementOrder replacementOrder,String cardNum);
	
	public List<ReplacementOrder> getAllNotSuccesOrder();
	
	public void updateReplacementOrder(ReplacementOrder replacementOrder, boolean success);
	
	public List<ReplacementOrder> getReplacementOrderByFullText(String cardNum); 
}
