package com.last.pay.core.dto.vietnam.kingcard.response;

public class KingCardResponseParam {
	
	private int code;
	
	private int count;
	
	private String message;
	
	private KingCardResponseDataParam data;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public KingCardResponseDataParam getData() {
		return data;
	}

	public void setData(KingCardResponseDataParam data) {
		this.data = data;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("{")
			.append("\"code\":\"").append(code).append("\",")
			.append("\"count\":\"").append(count).append("\",")
			.append("\"message\":\"").append(message).append("\",")
			.append("\"data\":").append(data)
			.append("}");
		return sb.toString();
	}
	
	
}
