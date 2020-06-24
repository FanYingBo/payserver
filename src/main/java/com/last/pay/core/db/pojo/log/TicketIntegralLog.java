package com.last.pay.core.db.pojo.log;

import java.util.Date;

public class TicketIntegralLog {
	
	private Long id;
	
	private Integer userId;
	
	private Integer beforeIntegral;
	
	private Integer integral;
	
	private Integer type;
	
	private Date logDate;
	
	public TicketIntegralLog() {
		
	}

	public TicketIntegralLog(Integer userId, Integer beforeIntegral, Integer integral, Integer type, Date logDate) {
		super();
		this.userId = userId;
		this.beforeIntegral = beforeIntegral;
		this.integral = integral;
		this.type = type;
		this.logDate = logDate;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getBeforeIntegral() {
		return beforeIntegral;
	}

	public void setBeforeIntegral(Integer beforeIntegral) {
		this.beforeIntegral = beforeIntegral;
	}

	public Integer getIntegral() {
		return integral;
	}

	public void setIntegral(Integer integral) {
		this.integral = integral;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Date getLogDate() {
		return logDate;
	}

	public void setLogDate(Date logDate) {
		this.logDate = logDate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	

}
