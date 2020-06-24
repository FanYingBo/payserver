package com.last.pay.core.compute;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.last.pay.base.CodeMsg;
import com.last.pay.base.CodeMsgType;
import com.last.pay.base.common.constants.Constants.CurrencyConstants;
import com.last.pay.base.vo.CountryCurrency;
import com.last.pay.core.db.pojo.web.PayOrder;
import com.last.pay.core.exception.ParamsException;

@Component("defaultPayBuilder")
public class DefaultPayBuilder implements ExchangePayParamBuilder{

	@Override
	public String getCurrency() {
		return CurrencyConstants.USA;
	}

	@Override
	public Map<String, Object> buildCodaParamMap(String orderId,HttpServletRequest request) {
		throw new ParamsException(CodeMsgType.ERR_ORDER_CURRENCY);
	}

	@Override
	public CountryCurrency getCountryCurrency() {
		return CurrencyConstants.country.get(getCurrency());
	}

	@Override
	public CodeMsg<?> getTranscationParam(PayOrder payOrder,String orderId,String subPayType,String url,String mnoId) {
		throw new ParamsException(CodeMsgType.ERR_ORDER_CURRENCY);
	}

	@Override
	public CodeMsg<?> notifyTransStatus(long txnId) {
		throw new ParamsException(CodeMsgType.ERR_ORDER_CURRENCY);
	}
	
}
