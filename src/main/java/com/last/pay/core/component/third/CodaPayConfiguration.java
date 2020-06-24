package com.last.pay.core.component.third;


public class CodaPayConfiguration {
	
	private String sandboxUrl;
	
	private String productionUrl;
	
	private String soapSandboxUrl;
	
	private String soapSandboxWsdlUrl;
	
	private String soapProductUrl;
	
	private String soapProductWsdlUrl;
	
	private String iframeSandboxUrl;
	
	private String iframeProductUrl;
	
	private int timeout;

	private int retryInterval;
	
	private int retryDelay;
	
	private Integer callBackCheckMD5;
	
	public String getSoapSandboxUrl() {
		return soapSandboxUrl;
	}

	public CodaPayConfiguration setSoapSandboxUrl(String soapSandboxUrl) {
		this.soapSandboxUrl = soapSandboxUrl;
		return this;
	}

	public String getSoapSandboxWsdlUrl() {
		return soapSandboxWsdlUrl;
	}

	public CodaPayConfiguration setSoapSandboxWsdlUrl(String soapSandboxWsdlUrl) {
		this.soapSandboxWsdlUrl = soapSandboxWsdlUrl;
		return this;
	}

	public String getSoapProductUrl() {
		return soapProductUrl;
	}

	public CodaPayConfiguration setSoapProductUrl(String soapProductUrl) {
		this.soapProductUrl = soapProductUrl;
		return this;
	}

	public String getSoapProductWsdlUrl() {
		return soapProductWsdlUrl;
	}

	public CodaPayConfiguration setSoapProductWsdlUrl(String soapProductWsdlUrl) {
		this.soapProductWsdlUrl = soapProductWsdlUrl;
		return this;
	}

	public String getSandboxUrl() {
		return sandboxUrl;
	}

	public CodaPayConfiguration setSandboxUrl(String sandboxUrl) {
		this.sandboxUrl = sandboxUrl;
		return this;
	}

	public String getProductionUrl() {
		return productionUrl;
	}

	public CodaPayConfiguration setProductionUrl(String productionUrl) {
		this.productionUrl = productionUrl;
		return this;
	}

	public String getIframeSandboxUrl() {
		return iframeSandboxUrl;
	}

	public CodaPayConfiguration setIframeSandboxUrl(String iframeSandboxUrl) {
		this.iframeSandboxUrl = iframeSandboxUrl;
		return this;
	}

	public String getIframeProductUrl() {
		return iframeProductUrl;
	}

	public CodaPayConfiguration setIframeProductUrl(String iframeProductUrl) {
		this.iframeProductUrl = iframeProductUrl;
		return this;
	}

	public int getTimeout() {
		return timeout;
	}

	public CodaPayConfiguration setTimeout(int timeout) {
		this.timeout = timeout;
		return this;
	}

	public int getRetryInterval() {
		return retryInterval;
	}

	public CodaPayConfiguration setRetryInterval(int retryInterval) {
		this.retryInterval = retryInterval;
		return this;
	}

	public int getRetryDelay() {
		return retryDelay;
	}

	public CodaPayConfiguration setRetryDelay(int retryDelay) {
		this.retryDelay = retryDelay;
		return this;
	}

	public Integer getCallBackCheckMD5() {
		return callBackCheckMD5;
	}

	public CodaPayConfiguration setCallBackCheckMD5(Integer callBackCheckMD5) {
		this.callBackCheckMD5 = callBackCheckMD5;
		return this;
	}
	
	

}
