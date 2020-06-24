package com.last.pay.core.component.load;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.last.pay.base.common.constants.Constants.CurrencyConstants;
import com.last.pay.core.db.pojo.web.CurrencyExchange;
import com.last.pay.core.db.pojo.web.PayParam;
import com.last.pay.core.db.mapper.webdb.CurrencyExchangeMapper;
import com.last.pay.core.db.mapper.webdb.PayParamMapper;
/**
 * WebDB.pay_param  WebDB.exchange_rate
 * @author Administrator
 *
 */
@Component
public class PayParamConfiguration {
	
	private static final Log logger = LogFactory.getLog(PayParamConfiguration.class);
	
	private Map<String,String> payParamMap = new HashMap<>();
	
	@Autowired
	private PayParamMapper payParamMapper; 
	@Autowired
	private CurrencyExchangeMapper currencyExchangeMapper;
	
	private List<CurrencyExchange> currencyExchangeList = new ArrayList<CurrencyExchange>();
	
	@PostConstruct
	public void loadConfig() {
		logger.info("**************开始加载数据库参数************");
		initRate();
		initPayParam();
	}
	private void initPayParam() {
		payParamMap.clear();
		List<PayParam> payParams = payParamMapper.getPayParams();
		Iterator<PayParam> iterator = payParams.iterator();
		while(iterator.hasNext()) {
			PayParam payParam = iterator.next();
			logger.info("[key]: "+formatPrint(payParam.getParamKey())+"[value]:" + payParam.getParamValue());
			payParamMap.put(payParam.getParamKey(), payParam.getParamValue());
		}
		
	}

	public void initRate() {
		List<CurrencyExchange> currencyExchanges = currencyExchangeMapper.getCurrencyExchanges();
		for(CurrencyExchange currencyExchange: currencyExchanges) {
			String primitiveCurrency = currencyExchange.getPrimitiveCurrency();
			String exchangeCurrency = currencyExchange.getExchangeCurrency();
			if(CurrencyConstants.USA.equalsIgnoreCase(primitiveCurrency)) {
				logger.info("[currency]: "+formatPrint(exchangeCurrency)+"[rate]:" + currencyExchange.getRate());
			}else {
				logger.warn("[currency]: "+formatPrint(exchangeCurrency)+"[rate]:" + currencyExchange.getRate());
			}
		}
		currencyExchangeList.clear();
		currencyExchangeList.addAll(currencyExchanges);
	}
	
	public String getPayParam(String paramKey) {
		return payParamMap.get(paramKey);
	}
	
	public float getUSDRate(String realCurrency) {
		Iterator<CurrencyExchange> iterator = currencyExchangeList.iterator();
		while(iterator.hasNext()) {
			CurrencyExchange currencyExchange = iterator.next();
			if(CurrencyConstants.USA.equalsIgnoreCase(currencyExchange.getPrimitiveCurrency()) && realCurrency.equals(currencyExchange.getExchangeCurrency())) {
				return currencyExchange.getRate().floatValue();
			}
		}
		return 0;
	}
	
	public float getCurrencyRate(String unitCurrency , String targetCurrent) {
		for (CurrencyExchange currencyExchange : currencyExchangeList) {
			if(currencyExchange.getPrimitiveCurrency().equalsIgnoreCase(unitCurrency) && currencyExchange.getExchangeCurrency().equalsIgnoreCase(targetCurrent)) {
				return currencyExchange.getRate().floatValue();
			}else if(currencyExchange.getPrimitiveCurrency().equalsIgnoreCase(targetCurrent) && currencyExchange.getExchangeCurrency().equalsIgnoreCase(unitCurrency)) {
				return 1/currencyExchange.getRate().floatValue();
			}
		}
		return 0;
	}
	
	public float exchangeCurrencyToUSD(String currency , float money) {
		return getCurrencyRate(currency, CurrencyConstants.USA)*money;
	}
	
	private String formatPrint(String key) {
		int length = key.length();
		if(length < 32) {
			for(int i = 0;i < 32 - length;i++) {
				key += " ";
			}
			return key;
		}
		return key;
	}
}
