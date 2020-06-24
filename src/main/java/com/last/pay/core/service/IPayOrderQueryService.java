package com.last.pay.core.service;

import java.util.List;

import com.last.pay.core.db.pojo.web.PayOrder;

public interface IPayOrderQueryService {
	
	public List<PayOrder> getPayOrderByThirdNum(String orderNum);
	
	public List<PayOrder> getPayOrderByThirdNumSuccess(String orderNum);
	
	public List<PayOrder> getPayOrderByThirdNumFail(String orderNum);
	/**
	 * PayOrder_Note_Index(Note)
	 * @return
	 */
	public List<PayOrder> getPayOrderByFullText(int payType,String text,int status);

}
