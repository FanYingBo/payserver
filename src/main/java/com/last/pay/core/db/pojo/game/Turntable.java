package com.last.pay.core.db.pojo.game;

public class Turntable {
	
	private Integer position;
	
	private Integer type;
	
	private Float amount;
	
	private Integer propId;
	
	private Float radio;

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}


	public Float getAmount() {
		return amount;
	}

	public void setAmount(Float amount) {
		this.amount = amount;
	}

	public Integer getPropId() {
		return propId;
	}

	public void setPropId(Integer propId) {
		this.propId = propId;
	}

	public Float getRadio() {
		return radio;
	}

	public void setRadio(Float radio) {
		this.radio = radio;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("{\"turntable\":")
			.append("{")
			.append("\"position\":").append(position).append(",")
			.append("\"type\":").append(type).append(",")
			.append("\"amount\":").append(amount).append(",")
			.append("\"propId\":").append(propId).append(",")
			.append("\"radio\":").append(radio).append(",")
			.append("}}");
		return sb.toString();
	}
	
}
