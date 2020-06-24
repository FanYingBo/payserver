package com.last.pay.core.component.third;


public class IOSPayConfiguration {
	
	private String sandboxUrl;
	
	private String productUrl;

	public String getSandboxUrl() {
		return sandboxUrl;
	}

	public IOSPayConfiguration setSandboxUrl(String sandboxUrl) {
		this.sandboxUrl = sandboxUrl;
		return this;
	}

	public String getProductUrl() {
		return productUrl;
	}

	public IOSPayConfiguration setProductUrl(String productUrl) {
		this.productUrl = productUrl;
		return this;
	}
	
	
}
