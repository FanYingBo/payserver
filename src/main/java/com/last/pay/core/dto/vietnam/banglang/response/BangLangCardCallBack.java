package com.last.pay.core.dto.vietnam.banglang.response;

public class BangLangCardCallBack {
	
	private int status;
	
	private String tran_id;
	
	private String amount;
	
	private String real_amount;
	
	private String card_seri;
	
	private String card_code;
	
	private String request_id;
	
	private String message;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getTran_id() {
		return tran_id;
	}

	public void setTran_id(String tran_id) {
		this.tran_id = tran_id;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getReal_amount() {
		return real_amount;
	}

	public void setReal_amount(String real_amount) {
		this.real_amount = real_amount;
	}

	public String getCard_seri() {
		return card_seri;
	}

	public void setCard_seri(String card_seri) {
		this.card_seri = card_seri;
	}

	public String getCard_code() {
		return card_code;
	}

	public void setCard_code(String card_code) {
		this.card_code = card_code;
	}

	public String getRequest_id() {
		return request_id;
	}

	public void setRequest_id(String request_id) {
		this.request_id = request_id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		StringBuffer sbuBuffer = new StringBuffer();
		sbuBuffer.append("{")
			.append("\"status\":").append(status).append(",")
			.append("\"tran_id\":\"").append(tran_id).append("\",")
			.append("\"amount\":\"").append(amount).append("\",")
			.append("\"real_amount\":\"").append(real_amount).append("\",")
			.append("\"card_seri\":\"").append(card_seri).append("\",")
			.append("\"card_code\":\"").append(card_code).append("\",")
			.append("\"request_id\":\"").append(request_id).append("\",")
			.append("\"message\":\"").append(message).append("\"}");
		return  sbuBuffer.toString();
	}

}
