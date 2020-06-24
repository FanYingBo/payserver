package com.last.pay.core.dto.coda.response;

public class CodaPayTransParam {
	
	private String txnId;
	
	private String url;
	
	private String type="3";
	
	private String browser_type = "mobile-web";

	public String getTxnId() {
		return txnId;
	}

	public void setTxnId(String txnId) {
		this.txnId = txnId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getBrowser_type() {
		return browser_type;
	}

	public void setBrowser_type(String browser_type) {
		this.browser_type = browser_type;
	}
	
	
}
