package com.last.pay.base.vo;

public class CountryCurrency {
	
	private String country;
	
	private String currency;
	
	private short countryCode;
	
	private short currencyCode;
	

	public CountryCurrency(String country, String currency, short countryCode, short currencyCode) {
		this.country = country;
		this.currency = currency;
		this.countryCode = countryCode;
		this.currencyCode = currencyCode;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public short getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(short countryCode) {
		this.countryCode = countryCode;
	}

	public short getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(short currencyCode) {
		this.currencyCode = currencyCode;
	}
	

}
