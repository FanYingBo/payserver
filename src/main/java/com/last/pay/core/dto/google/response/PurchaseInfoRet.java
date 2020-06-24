package com.last.pay.core.dto.google.response;

public class PurchaseInfoRet {
	
	private String kind;
	
	private long purchaseTimeMillis;
	
	private int purchaseState;
	
	private int consumptionState;
	
	private String developerPayload;
	
	private String orderId;
	
	private int purchaseType;
	
	private int acknowledgementState;

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public long getPurchaseTimeMillis() {
		return purchaseTimeMillis;
	}

	public void setPurchaseTimeMillis(long purchaseTimeMillis) {
		this.purchaseTimeMillis = purchaseTimeMillis;
	}

	public int getPurchaseState() {
		return purchaseState;
	}

	public void setPurchaseState(int purchaseState) {
		this.purchaseState = purchaseState;
	}

	public int getConsumptionState() {
		return consumptionState;
	}

	public void setConsumptionState(int consumptionState) {
		this.consumptionState = consumptionState;
	}

	public String getDeveloperPayload() {
		return developerPayload;
	}

	public void setDeveloperPayload(String developerPayload) {
		this.developerPayload = developerPayload;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public int getPurchaseType() {
		return purchaseType;
	}

	public void setPurchaseType(int purchaseType) {
		this.purchaseType = purchaseType;
	}

	public int getAcknowledgementState() {
		return acknowledgementState;
	}

	public void setAcknowledgementState(int acknowledgementState) {
		this.acknowledgementState = acknowledgementState;
	}

	@Override
	public String toString() {
		return "{\"kind\":" + kind + ", \"purchaseTimeMillis\":" + purchaseTimeMillis + ", \"purchaseState\":"
				+ purchaseState + ", \"consumptionState\":" + consumptionState + ", \"developerPayload\":" + developerPayload
				+ ", \"orderId\":" + orderId + ", \"purchaseType\":" + purchaseType + ", \"acknowledgementState\":"
				+ acknowledgementState + "}";
	}
	
	

}
