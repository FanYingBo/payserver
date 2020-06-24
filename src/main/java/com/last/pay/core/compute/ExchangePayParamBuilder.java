package com.last.pay.core.compute;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.last.pay.base.CodeMsg;
import com.last.pay.base.vo.CountryCurrency;
import com.last.pay.core.db.pojo.web.PayOrder;

/**
 * 	美元兑其他币种计算
 * 
 * @author Administrator
 *
 */
public interface ExchangePayParamBuilder {
	
	public String getCurrency();
	
	public CountryCurrency getCountryCurrency();
	
	public Map<String,Object> buildCodaParamMap(String orderId,HttpServletRequest request);
	
	public CodeMsg<?> getTranscationParam(PayOrder payOrder,String orderId,String subPayType,String url,String mnoId);
	@Deprecated
	public CodeMsg<?> notifyTransStatus(long txnId);
	
	
	 

}
