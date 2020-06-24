package com.last.pay.core.db.pojo.log;

import java.util.Date;

public class WarheadPoolLog {
	
	private Long id;
	
	private Integer userId;
	
	private Integer type;
	
	private Long personPool;
	
	private Long publicPool;
	
	private Float money;
	
	private Integer add_personPool;
	
	private Integer add_publicPool;
	
	private Date log_date;

	public WarheadPoolLog() {
		
	}
	
	public WarheadPoolLog(Integer userId, Integer type, Long personPool, Long publicPool, Float money,
			Integer add_personPool, Integer add_publicPool, Date log_date) {
		super();
		this.userId = userId;
		this.type = type;
		this.personPool = personPool;
		this.publicPool = publicPool;
		this.money = money;
		this.add_personPool = add_personPool;
		this.add_publicPool = add_publicPool;
		this.log_date = log_date;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Long getPersonPool() {
		return personPool;
	}

	public void setPersonPool(Long personPool) {
		this.personPool = personPool;
	}

	public Long getPublicPool() {
		return publicPool;
	}

	public void setPublicPool(Long publicPool) {
		this.publicPool = publicPool;
	}

	public Float getMoney() {
		return money;
	}

	public void setMoney(Float money) {
		this.money = money;
	}

	public Integer getAdd_personPool() {
		return add_personPool;
	}

	public void setAdd_personPool(Integer add_personPool) {
		this.add_personPool = add_personPool;
	}

	public Integer getAdd_publicPool() {
		return add_publicPool;
	}

	public void setAdd_publicPool(Integer add_publicPool) {
		this.add_publicPool = add_publicPool;
	}

	public Date getLog_date() {
		return log_date;
	}

	public void setLog_date(Date log_date) {
		this.log_date = log_date;
	}

	@Override
	public String toString() {
		return "WarheadPoolLog [id=" + id + ", userId=" + userId + ", type=" + type + ", personPool=" + personPool
				+ ", publicPool=" + publicPool + ", money=" + money + ", add_personPool=" + add_personPool
				+ ", add_publicPool=" + add_publicPool + ", log_date=" + log_date + "]";
	}
	

}
