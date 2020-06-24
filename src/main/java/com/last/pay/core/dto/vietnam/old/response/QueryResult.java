package com.last.pay.core.dto.vietnam.old.response;

public class QueryResult {
	
	private String partnerId;
	
	private String requestId;
	
	private String status;
	
	private String cardAmount;
	
	private String cardPrintAmount;
	
	public String getPartnerId() {
		return partnerId;
	}
	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCardAmount() {
		return cardAmount;
	}
	public void setCardAmount(String cardAmount) {
		this.cardAmount = cardAmount;
	}
	public String getCardPrintAmount() {
		return cardPrintAmount;
	}
	public void setCardPrintAmount(String cardPrintAmount) {
		this.cardPrintAmount = cardPrintAmount;
	}
	@Override
	public String toString() {
		return "QueryResult [partnerId=" + partnerId + ", requestId=" + requestId + ", status=" + status
				+ ", cardAmount=" + cardAmount + ", cardPrintAmount=" + cardPrintAmount + "]";
	}

}
