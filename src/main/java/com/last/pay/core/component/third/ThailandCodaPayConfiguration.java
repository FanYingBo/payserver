package com.last.pay.core.component.third;


public class ThailandCodaPayConfiguration {
	
	private String thailandApikey;
	
	private String currency;

	public String getThailandApikey() {
		return thailandApikey;
	}

	public ThailandCodaPayConfiguration setThailandApikey(String thailandApikey) {
		this.thailandApikey = thailandApikey;
		return this;
	}

	public String getCurrency() {
		return currency;
	}

	public ThailandCodaPayConfiguration setCurrency(String currency) {
		this.currency = currency;
		return this;
	}
	

}
