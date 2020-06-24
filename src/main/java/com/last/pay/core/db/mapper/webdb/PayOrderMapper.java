package com.last.pay.core.db.mapper.webdb;


import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.last.pay.core.db.pojo.web.PayOrder;

public interface PayOrderMapper {
	
	public void insertPayOrder(PayOrder payOrder);
	
	public void updatePayOrder(PayOrder payOrder);
	
	public PayOrder getPayOrdersByPointName(@Param("userId") int userId,@Param("pointName") String pointName);
	
	public PayOrder getPayOrdersByDynamicId(@Param("userId") int userId,@Param("dynamicId") int dynamicId);
	
	public PayOrder getPayOrder(@Param("orderNum") String orderNum);
	
	public List<PayOrder> getPayOrderByThirdOrderNumFail(@Param("thirdOrderNum") String thirdOrderNum);

	public void updatePayOrderThirdNum(@Param("orderNum") String orderNum,@Param("thirdOrderNum") String thirdOrderNum);
	
	public List<PayOrder> getPayOrderByThirdOrderNumSuccess(@Param("thirdOrderNum") String thirdOrderNum);
	
	public List<PayOrder> getPayOrderByThirdNum(@Param("thirdOrderNum") String thirdOrderNum);
	
	public List<PayOrder> getPayOrderByFullText(@Param("payType")int payType,@Param("text") String text,@Param("status") int status);
}
