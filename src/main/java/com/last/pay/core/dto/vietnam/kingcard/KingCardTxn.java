package com.last.pay.core.dto.vietnam.kingcard;

public class KingCardTxn {
	
	private Long user_id;
	
	private Long account_id;
	
	private String amount;
	
	private String freeze_amount;
	
	private String pin;
	
	private String seri;
	
	private String card_type;
	
	private String gateway;
	
	private Integer type;
	
	private Integer stat; // 1.待处理，2。完成，3.Error
	
	private String description;
	
	private String fee_amount;
	
	private String fee_display;
	
	private String updated_at;
	
	private String created_at;
	
	private Long id;

	public Long getUser_id() {
		return user_id;
	}


	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}


	public Long getAccount_id() {
		return account_id;
	}


	public void setAccount_id(Long account_id) {
		this.account_id = account_id;
	}


	public String getAmount() {
		return amount;
	}


	public void setAmount(String amount) {
		this.amount = amount;
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


	public String getCard_type() {
		return card_type;
	}


	public void setCard_type(String card_type) {
		this.card_type = card_type;
	}


	public String getGateway() {
		return gateway;
	}


	public void setGateway(String gateway) {
		this.gateway = gateway;
	}


	public Integer getType() {
		return type;
	}


	public void setType(Integer type) {
		this.type = type;
	}


	public Integer getStat() {
		return stat;
	}


	public void setStat(Integer stat) {
		this.stat = stat;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
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


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getFreeze_amount() {
		return freeze_amount;
	}


	public void setFreeze_amount(String freeze_amount) {
		this.freeze_amount = freeze_amount;
	}


	public String getFee_amount() {
		return fee_amount;
	}


	public void setFee_amount(String fee_amount) {
		this.fee_amount = fee_amount;
	}


	public String getFee_display() {
		return fee_display;
	}


	public void setFee_display(String fee_display) {
		this.fee_display = fee_display;
	}


	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("{")
			.append("\"user_id\":").append(user_id == null ? "0":user_id.longValue()).append(",")
			.append("\"account_id\":").append(account_id == null ? "0":account_id.longValue()).append(",")
			.append("\"amount\":").append(amount== null ? "null":amount).append(",")
			.append("\"freeze_amount\":").append(freeze_amount == null ? "0":freeze_amount).append(",")
			.append("\"pin\":\"").append(pin == null ? "null" :pin).append("\",")
			.append("\"seri\":\"").append(seri == null ? "null" :seri).append("\",")
			.append("\"card_type\":\"").append(card_type == null ? "null" : card_type).append("\",")
			.append("\"gateway\":\"").append(gateway == null ? "null" : gateway).append("\",")
			.append("\"type\":").append(type == null ? "0": type.intValue()).append(",")
			.append("\"stat\":").append(stat == null ? "0" : stat.intValue()).append(",")
			.append("\"description\":\"").append(description == null ? "null" : description).append("\",")
			.append("\"fee_amount\":").append(fee_amount == null ? "null":fee_amount).append(",")
			.append("\"fee_display\":").append(fee_display == null ? "null" : fee_display).append(",")
			.append("\"updated_at\":\"").append(updated_at == null ? "null" : updated_at).append("\",")
			.append("\"created_at\":\"").append(created_at == null ? "null" : created_at).append("\",")
			.append("\"id\":").append(id == null ? "0":id.longValue()).append("")
			.append("}");
		return sb.toString();
	}

}
