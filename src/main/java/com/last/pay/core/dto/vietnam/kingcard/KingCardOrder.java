package com.last.pay.core.dto.vietnam.kingcard;

public class KingCardOrder {

	private Long id;
	
	private Long user_id;
	
	private String mrc_order_id;
	
	private Long txn_id;
	
	private String pin;
	
	private String seri;
	/**提供商**/
	private String type;
	
	private String tmp_amount;
	
	private String discount;
	/**1.卡已发送，2.卡成功，3.卡错误，4.卡未决**/
	private Integer stat;
	
	private String webhooks;
	
	private String logs;
	
	private String created_at;
	
	private String updated_at;


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public Long getUser_id() {
		return user_id;
	}


	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}


	public String getMrc_order_id() {
		return mrc_order_id;
	}


	public void setMrc_order_id(String mrc_order_id) {
		this.mrc_order_id = mrc_order_id;
	}


	public Long getTxn_id() {
		return txn_id;
	}


	public void setTxn_id(Long txn_id) {
		this.txn_id = txn_id;
	}


	public String getPin() {
		return pin;
	}


	public void setPin(String pin) {
		this.pin = pin;
	}


	public String getSeri() {
		return seri;
	}


	public void setSeri(String seri) {
		this.seri = seri;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getTmp_amount() {
		return tmp_amount;
	}


	public void setTmp_amount(String tmp_amount) {
		this.tmp_amount = tmp_amount;
	}


	public String getDiscount() {
		return discount;
	}


	public void setDiscount(String discount) {
		this.discount = discount;
	}


	public Integer getStat() {
		return stat;
	}


	public void setStat(Integer stat) {
		this.stat = stat;
	}


	public String getWebhooks() {
		return webhooks;
	}


	public void setWebhooks(String webhooks) {
		this.webhooks = webhooks;
	}


	public String getLogs() {
		return logs;
	}


	public void setLogs(String logs) {
		this.logs = logs;
	}


	public String getCreated_at() {
		return created_at;
	}


	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}


	public String getUpdated_at() {
		return updated_at;
	}


	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}

	/**
	 * {
	 * "id":1168446,
	 * "user_id":10460214,
	 * "mrc_order_id":"D158624778298869",
	 * "txn_id":null,
	 * "pin":"71874952560931",
	 * "seri":"59000010105357",
	 * "type":"VIETTEL",
	 * "tmp_amount":"10000.00",
	 * "discount":"35.00",
	 * "stat":3,
	 * "webhooks":"http://helptw.yuegame.com:8083/pay/vietnamkcret",
	 * "logs":null,
	 * "created_at":"2020-04-07 15:23:03",
	 * "updated_at":"2020-04-07 15:23:04"
	 * }
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("{")
			.append("\"id\":").append(id==null ? "0":id.longValue()).append(",")
			.append("\"user_id\":\"").append(user_id == null ? "0" : user_id.longValue()).append("\",")
			.append("\"mrc_order_id\":\"").append(mrc_order_id).append("\",")
			.append("\"txn_id\":").append(txn_id == null ?"null":txn_id.longValue()).append(",")
			.append("\"pin\":\"").append(pin).append("\",")
			.append("\"seri\":\"").append(seri).append("\",")
			.append("\"type\":\"").append(type).append("\",")
			.append("\"tmp_amount\":\"").append(tmp_amount).append("\",")
			.append("\"discount\":\"").append(discount).append("\",")
			.append("\"stat\":").append(stat == null ? "null":stat.intValue()).append(",")
			.append("\"webhooks\":\"").append(webhooks).append("\",")
			.append("\"logs\":").append(logs == null ?"null":logs).append(",")
			.append("\"created_at\":\"").append(created_at).append("\",")
			.append("\"updated_at\":\"").append(updated_at).append("\"")
			.append("}");
		return sb.toString();
	}
	
	
}
