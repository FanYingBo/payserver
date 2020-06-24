package com.last.pay.core.compute;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.last.pay.base.CodeMsg;
import com.last.pay.base.CodeMsgType;
import com.last.pay.base.common.constants.Constants.CurrencyConstants;
import com.last.pay.base.vo.CountryCurrency;
import com.last.pay.core.db.pojo.web.PayOrder;
import com.last.pay.core.component.third.VietnamPayConfiguration;
import com.last.pay.core.exception.ParamsException;
/**
 * 	越南盾转化为美元
 * @author Administrator
 *
 */
@Component("vietnamPayBuilder")
public class VietnamPayBuilder implements ExchangePayParamBuilder{
	
	@Autowired
	private VietnamPayConfiguration vietnamPayConfiguration;


	@Override
	public String getCurrency() {
		return CurrencyConstants.Vietnam;
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
