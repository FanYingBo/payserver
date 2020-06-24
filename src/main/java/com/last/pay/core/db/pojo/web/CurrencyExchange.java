package com.last.pay.core.db.pojo.web;

import java.math.BigDecimal;

public class CurrencyExchange {
	
	private String primitiveCurrency;
	
	private String exchangeCurrency;
	
	private BigDecimal rate;

	public String getPrimitiveCurrency() {
		return primitiveCurrency;
	}

	public void setPrimitiveCurrency(String primitiveCurrency) {
		this.primitiveCurrency = primitiveCurrency;
	}

	public String getExchangeCurrency() {
		return exchangeCurrency;
	}

	public void setExchangeCurrency(String exchangeCurrency) {
		this.exchangeCurrency = exchangeCurrency;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}
	
	
}
