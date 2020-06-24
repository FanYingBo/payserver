package com.last.pay.core.component.third;

public class PaycentConfiguraion {
	
	private String merchant;
	
	private String secret;
	
	private String sandboxUrl;
	
	private String productUrl;
	
	private String returnUrl;
	
	private String notifyUrl;

	public String getMerchant() {
		return merchant;
	}

	public PaycentConfiguraion setMerchant(String merchant) {
		this.merchant = merchant;
		return this;
	}

	public String getSecret() {
		return secret;
	}

	public PaycentConfiguraion setSecret(String secret) {
		this.secret = secret;
		return this;
	}

	public String getSandboxUrl() {
		return sandboxUrl;
	}

	public PaycentConfiguraion setSandboxUrl(String sandboxUrl) {
		this.sandboxUrl = sandboxUrl;
		return this;
	}

	public String getProductUrl() {
		return productUrl;
	}

	public PaycentConfiguraion setProductUrl(String productUrl) {
		this.productUrl = productUrl;
		return this;
	}

	public String getReturnUrl() {
		return returnUrl;
	}

	public PaycentConfiguraion setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
		return this;
	}

	public String getNotifyUrl() {
		return notifyUrl;
	}

	public PaycentConfiguraion setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
		return this;
	}
	
}
