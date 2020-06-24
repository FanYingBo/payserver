package com.last.pay.core.db.pojo.log;

public class DynamicPointLog {
	
	private Integer id;
	
	private String orderNum;
	
	private Integer userId;
	
	private Integer position = 0;
	
	private Integer toll = 0;
	
	private Integer dynamicId;
	
	private Integer dynamicType;
	
	private Integer pointGold = 0;
	
	private Float multipler = 0f;
	
	private Integer userLevel;
	
	private Integer userVip;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public Integer getDynamicId() {
		return dynamicId;
	}

	public void setDynamicId(Integer dynamicId) {
		this.dynamicId = dynamicId;
	}

	public Integer getDynamicType() {
		return dynamicType;
	}

	public void setDynamicType(Integer dynamicType) {
		this.dynamicType = dynamicType;
	}


	public Integer getPointGold() {
		return pointGold;
	}

	public void setPointGold(Integer pointGold) {
		this.pointGold = pointGold;
	}

	public Float getMultipler() {
		return multipler;
	}

	public void setMultipler(Float multipler) {
		this.multipler = multipler;
	}

	public Integer getUserLevel() {
		return userLevel;
	}

	public void setUserLevel(Integer userLevel) {
		this.userLevel = userLevel;
	}

	public Integer getUserVip() {
		return userVip;
	}

	public void setUserVip(Integer userVip) {
		this.userVip = userVip;
	}

	public Integer getToll() {
		return toll;
	}

	public void setToll(Integer toll) {
		this.toll = toll;
	}
	
	

}
