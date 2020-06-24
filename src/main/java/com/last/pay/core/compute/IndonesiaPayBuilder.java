package com.last.pay.core.compute;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import com.last.pay.base.CodeMsg;
import com.last.pay.base.CodeMsgType;
import com.last.pay.base.common.constants.Constants.CurrencyConstants;
import com.last.pay.base.vo.CountryCurrency;
import com.last.pay.core.db.pojo.web.PayOrder;
import com.last.pay.core.component.third.PaycentConfiguraion;

public class IndonesiaPayBuilder implements ExchangePayParamBuilder{
	
	private String currency = "IDR";
	
	@Autowired
	private PaycentConfiguraion paycentConfiguraion;
	
	private Float rate;

	@Override
	public String getCurrency() {
		return currency;
	}

	@Override
	public CountryCurrency getCountryCurrency() {
		return CurrencyConstants.country.get(getCurrency());
	}

	@Override
	public Map<String, Object> buildCodaParamMap(String orderId, HttpServletRequest request) {
		return null;
	}

	@Override
	public CodeMsg<?> getTranscationParam(PayOrder payOrder, String orderId, String subPayType, String url, String mnoId) {
		return null;
	}

	@Override
	public CodeMsg<?> notifyTransStatus(long txnId) {
		return CodeMsg.success(CodeMsgType.SUCCESS);
	}

}
