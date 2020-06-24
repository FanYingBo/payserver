package com.last.pay.core.dto.vietnam.upay.response;

public class UPayVietnamResponseParam {
	
	private long result;
	
	private String error;
	
	private String errorUrl;
	
	private String errorDescr;
	
	private String orderId;
	
	private String serialNumber;
	
	private String payTime;
	
	private int price;
	
	private String resultE;
	
	private String hash;

	public long getResult() {
		return result;
	}

	public void setResult(long result) {
		this.result = result;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getErrorUrl() {
		return errorUrl;
	}

	public void setErrorUrl(String errorUrl) {
		this.errorUrl = errorUrl;
	}

	public String getErrorDescr() {
		return errorDescr;
	}

	public void setErrorDescr(String errorDescr) {
		this.errorDescr = errorDescr;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getPayTime() {
		return payTime;
	}

	public void setPayTime(String payTime) {
		this.payTime = payTime;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public String getResultE() {
		return resultE;
	}

	public void setResultE(String resultE) {
		this.resultE = resultE;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	@Override
	public String toString() {
		StringBuffer sbuf = new StringBuffer();
		sbuf.append("{\"result\":\"")
			.append(result)
			.append("\",\"error\":\"")
			.append(error)
			.append("\",\"errorUrl\":\"")
			.append(errorUrl)
			.append("\",\"errorDescr\":\"")
			.append(errorDescr)
			.append("\",\"orderId\":\"")
			.append(orderId)
			.append("\",\"serialNumber\":\"")
			.append(serialNumber)
			.append("\",\"payTime\":\"")
			.append(payTime)
			.append("\",\"price\":\"")
			.append(price)
			.append("\",\"resultE\":\"")
			.append(resultE)
			.append("\",\"hash\":\"")
			.append(hash)
			.append("\"}");
		return sbuf.toString();
	}
}
