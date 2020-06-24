package com.last.pay.core.dto.vietnam.old.request;
/**
 * {"partnerId":1001,"partner_username":"dev_test","cardPin":"1234567890","cardSerial":"12345678900","requestId":"
 *1001_1528094181945","remark":"Thanh toan","providerCode":"VTT","cardPrintAmount":10000}
 * 调用越南充值接口的请求参数
 * @author Administrator
 *
 */
public class VietnamRequestParams {
	
	private String partnerId;
	
	private String partner_username;
	
	private String cardPin;
	
	private String cardSerial;
	/***partnerId_yyyyMMddhhmmss_random 1000_20190219041305001****/
	private String requestId;
	
	private String remark;
	
	private String providerCode;
	
	private String cardPrintAmount;

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

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getProviderCode() {
		return providerCode;
	}

	public void setProviderCode(String providerCode) {
		this.providerCode = providerCode;
	}

	public String getCardPrintAmount() {
		return cardPrintAmount;
	}

	public void setCardPrintAmount(String cardPrintAmount) {
		this.cardPrintAmount = cardPrintAmount;
	}

	@Override
	public String toString() {
		return "RequestParams [partnerId=" + partnerId + ", partner_username=" + partner_username + ", cardPin="
				+ cardPin + ", cardSerial=" + cardSerial + ", requestId=" + requestId + ", remark=" + remark
				+ ", providerCode=" + providerCode + ", cardPrintAmount=" + cardPrintAmount + "]";
	}

}
