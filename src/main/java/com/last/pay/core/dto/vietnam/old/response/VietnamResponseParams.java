package com.last.pay.core.dto.vietnam.old.response;

/**
 *{"partnerId":1000,"partner_username":"test_dev","cardPin":"","cardSerial":"","requestId":"1000_2019021904130500
 *	1","providerCode":"VTT","status":"00","cardAmount":"0","cardPrintAmount":0,"queryResult":{"partnerId":0,"reque
 *	stId":"1000_20190219041305001","status":"24","cardAmount":"0","cardPrintAmount":0},"message":"TRANSACTI
 *	ON FOUND"}
 * @author Administrator
 *
 */
public class VietnamResponseParams {
	
	private String partnerId;
	
	private String partner_username;
	
	private String cardPin;
	
	private String cardSerial;
	
	private String requestId;
	
	private String providerCode;
	
	private String status;
	
	private String cardAmount;
	
	private String cardPrintAmount;
	
	private QueryResult queryResult;
	
	private String message;

	public String getPartnerId() {
		return partnerId;
	}

	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}

	public String getPartner_username() {
		return partner_username;
	}

	public void setPartner_username(String partner_username) {
		this.partner_username = partner_username;
	}

	public String getCardPin() {
		return cardPin;
	}

	public void setCardPin(String cardPin) {
		this.cardPin = cardPin;
	}

	public String getCardSerial() {
		return cardSerial;
	}

	public void setCardSerial(String cardSerial) {
		this.cardSerial = cardSerial;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getProviderCode() {
		return providerCode;
	}

	public void setProviderCode(String providerCode) {
		this.providerCode = providerCode;
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
	
	public QueryResult getQueryResult() {
		return queryResult;
	}

	public void setQueryResult(QueryResult queryResult) {
		this.queryResult = queryResult;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "ResponseParams [partnerId=" + partnerId + ", partner_username=" + partner_username + ", cardPin="
				+ cardPin + ", cardSerial=" + cardSerial + ", requestId=" + requestId + ", providerCode=" + providerCode
				+ ", status=" + status + ", cardAmount=" + cardAmount + ", cardPrintAmount=" + cardPrintAmount
				+ ", queryResult=" + queryResult + ", message=" + message + "]";
	}
	
}
