package com.last.pay.core.db.mapper.webdb;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.last.pay.core.db.pojo.web.ReplacementOrder;

public interface ReplacementOrderMapper {

	public int insertReplacementOrder(ReplacementOrder replacementOrder);
	
	public List<ReplacementOrder> getReplacementOrdersByStatus(@Param("status") int status);
	
	public int updateReplacementOrderSuccess(ReplacementOrder replacementOrder);
	
	public int updateReplacementOrderFailt(ReplacementOrder replacementOrder);
	
	public List<ReplacementOrder> getReplacementOrderByFulltextIndex(@Param("payType") int payType, @Param("cardNum") String cardNum);
	
}