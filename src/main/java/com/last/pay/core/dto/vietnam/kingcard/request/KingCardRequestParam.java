package com.last.pay.core.dto.vietnam.kingcard.request;

public class KingCardRequestParam {
	
	private String mrc_order_id;
	
	private String telco;
	
	private Integer amount;
	/**
	 * 卡Pin
	 */
	private String code;
	
	private String serial;
	
	private String webhooks;

	public String getMrc_order_id() {
		return mrc_order_id;
	}

	public void setMrc_order_id(String mrc_order_id) {
		this.mrc_order_id = mrc_order_id;
	}

	public String getTelco() {
		return telco;
	}

	public void setTelco(String telco) {
		this.telco = telco;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}

	public String getWebhooks() {
		return webhooks;
	}

	public void setWebhooks(String webhooks) {
		this.webhooks = webhooks;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("{")
			.append("\"mrc_order_id\":\"").append(mrc_order_id).append("\",")
			.append("\"telco\":\"").append(telco).append("\",")
			.append("\"amount\":\"").append(amount).append("\",")
			.append("\"code\":\"").append(code).append("\",")
			.append("\"serial\":\"").append(serial).append("\",")
			.append("\"webhooks\":\"").append(webhooks).append("\"")
			.append("}");
		return sb.toString();
	}
	
	

}
