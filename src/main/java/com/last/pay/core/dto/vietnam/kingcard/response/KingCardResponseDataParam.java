package com.last.pay.core.dto.vietnam.kingcard.response;

public class KingCardResponseDataParam {
	
	private long user_id;
	
	private long account_id;
	
	private int amount;
	
	private int freeze_amount;
	
	private String pin;
	
	private String seri;
	
	private String type;
	
	private int stat;
	
	private String description;
	
	private int fee_amount;
	
	private int fee_display;
	
	private String updated_at;
	
	private String created_at;
	
	private long id;

	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public long getAccount_id() {
		return account_id;
	}

	public void setAccount_id(long account_id) {
		this.account_id = account_id;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getFreeze_amount() {
		return freeze_amount;
	}

	public void setFreeze_amount(int freeze_amount) {
		this.freeze_amount = freeze_amount;
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

	public int getStat() {
		return stat;
	}

	public void setStat(int stat) {
		this.stat = stat;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getFee_amount() {
		return fee_amount;
	}

	public void setFee_amount(int fee_amount) {
		this.fee_amount = fee_amount;
	}

	public int getFee_display() {
		return fee_display;
	}

	public void setFee_display(int fee_display) {
		this.fee_display = fee_display;
	}

	public String getUpdated_at() {
		return updated_at;
	}

	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("{")
			.append("\"user_id\":\"").append(user_id).append("\",")
			.append("\"account_id\":\"").append(account_id).append("\",")
			.append("\"amount\":\"").append(amount).append("\",")
			.append("\"freeze_amount\":\"").append(freeze_amount).append("\",")
			.append("\"pin\":\"").append(pin).append("\",")
			.append("\"seri\":\"").append(seri).append("\",")
			.append("\"type\":\"").append(type).append("\",")
			.append("\"stat\":\"").append(stat).append("\",")
			.append("\"description\":\"").append(description).append("\",")
			.append("\"fee_amount\":\"").append(fee_amount).append("\",")
			.append("\"fee_display\":\"").append(fee_display).append("\",")
			.append("\"updated_at\":\"").append(updated_at).append("\",")
			.append("\"created_at\":\"").append(created_at).append("\",")
			.append("\"id\":\"").append(id).append("\"")
			.append("}");
		return sb.toString();
	}
	

}
