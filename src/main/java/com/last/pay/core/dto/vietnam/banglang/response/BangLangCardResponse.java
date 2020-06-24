package com.last.pay.core.dto.vietnam.banglang.response;

public class BangLangCardResponse {
	
	private int status;
	
	private String message;
	
	private String tran_id;
	
	private String amount;
	
	private String real_amount;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
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

	@Override
	public String toString() {
		return "{\"status\":" + status + ", \"message\":\"" + message + "\", \"tran_id\":\"" + tran_id + "\",\"amount\":\""+amount+"\",\"real_amount\":\""+real_amount+"\"}";
	}
	
}
