package com.last.pay.core.dto.vietnam.momo;

public class MomoReceiptReqeust {
	
	private String requestTime;
	
	private String momo_transId;
	
	private String money;
	/**用于区分不同的游戏**/
	private String message;
	
	private String phone;
	
	private String type;
	
	private String authKey;

	public String getRequestTime() {
		return requestTime;
	}

	public void setRequestTime(String requestTime) {
		this.requestTime = requestTime;
	}

	public String getMomo_transId() {
		return momo_transId;
	}

	public void setMomo_transId(String momo_transId) {
		this.momo_transId = momo_transId;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAuthKey() {
		return authKey;
	}

	public void setAuthKey(String authKey) {
		this.authKey = authKey;
	}

	@Override
	public String toString() {
		StringBuffer sbuf = new StringBuffer();
		sbuf.append("{")
			.append("\"requestTime\":\"").append(requestTime).append("\",")
			.append("\"momo_transId\":\"").append(momo_transId).append("\",")
			.append("\"money\":\"").append(money).append("\",")
			.append("\"message\":\"").append(message).append("\",")
			.append("\"phone\":\"").append(phone).append("\",")
			.append("\"type\":\"").append(type).append("\",")
			.append("\"authKey\":\"").append(authKey).append("\"")
			.append("}");
		return sbuf.toString();
	}

	
}
