package com.last.pay.core.component.third;

import java.util.List;

import com.last.pay.base.common.constants.Constants.CurrencyConstants;

public class MyanmarCodaPayConfiguration {
	private String currency = CurrencyConstants.Myanmar;
	
	private List<String> lang;
	
	private int pay_channel;
	
	private float rate;
	
	private String api_key;

	public List<String> getLang() {
		return lang;
	}

	public MyanmarCodaPayConfiguration setLang(List<String> lang) {
		this.lang = lang;
		return this;
	}


	public int getPay_channel() {
		return pay_channel;
	}

	public MyanmarCodaPayConfiguration setPay_channel(int pay_channel) {
		this.pay_channel = pay_channel;
		return this;
	}

	public String getCurrency() {
		return currency;
	}

	public MyanmarCodaPayConfiguration setCurrency(String currency) {
		this.currency = currency;
		return this;
	}

	public float getRate() {
		return rate;
	}

	public MyanmarCodaPayConfiguration setRate(float rate) {
		this.rate = rate;
		return this;
	}

	public String getApi_key() {
		return api_key;
	}

	public MyanmarCodaPayConfiguration setApi_key(String api_key) {
		this.api_key = api_key;
		return this;
	}
	
}
