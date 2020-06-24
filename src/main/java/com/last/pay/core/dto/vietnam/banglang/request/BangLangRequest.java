package com.last.pay.core.dto.vietnam.banglang.request;

public class BangLangRequest {
	
	private String api_key;
	
	private String card_seri;
	
	private String card_code;
	
	private String request_id;
	
	private int card_amount;
	
	private String card_type;
	
	private String signature;

	public String getApi_key() {
		return api_key;
	}

	public BangLangRequest setApi_key(String api_key) {
		this.api_key = api_key;
		return this;
	}

	public String getCard_seri() {
		return card_seri;
	}

	public BangLangRequest setCard_seri(String card_seri) {
		this.card_seri = card_seri;
		return this;
	}

	public String getCard_code() {
		return card_code;
	}

	public BangLangRequest setCard_code(String card_code) {
		this.card_code = card_code;
		return this;
	}

	public String getRequest_id() {
		return request_id;
	}

	public BangLangRequest setRequest_id(String request_id) {
		this.request_id = request_id;
		return this;
	}

	public int getCard_amount() {
		return card_amount;
	}

	public BangLangRequest setCard_amount(int card_amount) {
		this.card_amount = card_amount;
		return this;
	}

	public String getCard_type() {
		return card_type;
	}

	public BangLangRequest setCard_type(String card_type) {
		this.card_type = card_type;
		return this;
	}

	public String getSignature() {
		return signature;
	}

	public BangLangRequest setSignature(String signature) {
		this.signature = signature;
		return this;
	}	
	

}
