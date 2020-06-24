package com.last.pay.core.dto.ios.response;

public class IOSReceipt {
	
	private IOSReceiptParams receipt;

	private int status;
	
	private String environment;

	public IOSReceiptParams getReceipt() {
		return receipt;
	}

	public void setReceipt(IOSReceiptParams receipt) {
		this.receipt = receipt;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}
	
}
