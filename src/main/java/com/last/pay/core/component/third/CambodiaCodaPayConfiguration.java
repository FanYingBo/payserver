package com.last.pay.core.component.third;

public class CambodiaCodaPayConfiguration {
	
	private String api_key;
	
	private String currency;

	public String getApi_key() {
		return api_key;
	}

	public CambodiaCodaPayConfiguration setApi_key(String api_key) {
		this.api_key = api_key;
		return this;
	}

	public String getCurrency() {
		return currency;
	}

	public CambodiaCodaPayConfiguration setCurrency(String currency) {
		this.currency = currency;
		return this;
	}

}
