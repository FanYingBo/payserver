package com.last.pay.core.db.pojo.log;

import java.util.Date;

public class PayUserLog {
	
	private Integer id;
	
	private Integer userId;
	
	private String pointName;
	
	private Long beforeGold;
	
	private Long beforeDiamond;
	
	private Long beforeExperience;
	
	private Long beforePlayTime;
	
	private Integer beforeCannon;
	
	private Integer beforeVipIntegral;
	
	private String orderNum;
	
	private Date logDate;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getPointName() {
		return pointName;
	}

	public void setPointName(String pointName) {
		this.pointName = pointName;
	}

	public Long getBeforeGold() {
		return beforeGold;
	}

	public void setBeforeGold(Long beforeGold) {
		this.beforeGold = beforeGold;
	}

	public Long getBeforeDiamond() {
		return beforeDiamond;
	}

	public void setBeforeDiamond(Long beforeDiamond) {
		this.beforeDiamond = beforeDiamond;
	}

	public Long getBeforeExperience() {
		return beforeExperience;
	}

	public void setBeforeExperience(Long beforeExperience) {
		this.beforeExperience = beforeExperience;
	}

	public Long getBeforePlayTime() {
		return beforePlayTime;
	}

	public void setBeforePlayTime(Long beforePlayTime) {
		this.beforePlayTime = beforePlayTime;
	}

	public Integer getBeforeCannon() {
		return beforeCannon;
	}

	public void setBeforeCannon(Integer beforeCannon) {
		this.beforeCannon = beforeCannon;
	}

	public Integer getBeforeVipIntegral() {
		return beforeVipIntegral;
	}

	public void setBeforeVipIntegral(Integer beforeVipIntegral) {
		this.beforeVipIntegral = beforeVipIntegral;
	}

	public String getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	public Date getLogDate() {
		return logDate;
	}

	public void setLogDate(Date logDate) {
		this.logDate = logDate;
	}
	
	
	
}
