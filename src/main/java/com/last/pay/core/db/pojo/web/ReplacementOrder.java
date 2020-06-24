package com.last.pay.core.db.pojo.web;

import java.util.Date;

import com.last.pay.base.common.constants.Constants.ReplacementOrderConstants;

public class ReplacementOrder {
	
	private Integer id;
	
	private String orderNum;
	
	private String thirdOrderNum;
	
	private Integer status;
	
	private String errorRemark;
	
	private String errorDesc;
	
	private Integer userId;
	
	private String pointName;
	
	private Integer payType;
	
	private Float money;
	
	private Date logDate;
	
	private Date successDate;
	
	private String ip;
	
	private String realCurrency;
	
	private Integer channel;
	
	private Integer platform;
	
	private Integer optTimes;
	
	private Date replaceDate;

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

	public String getThirdOrderNum() {
		return thirdOrderNum;
	}

	public void setThirdOrderNum(String thirdOrderNum) {
		this.thirdOrderNum = thirdOrderNum;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getErrorRemark() {
		return errorRemark;
	}

	public void setErrorRemark(String errorRemark) {
		this.errorRemark = errorRemark;
	}

	public String getErrorDesc() {
		return errorDesc;
	}

	public void setErrorDesc(String errorDesc) {
		this.errorDesc = errorDesc;
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

	public Integer getPayType() {
		return payType;
	}

	public void setPayType(Integer payType) {
		this.payType = payType;
	}

	public Float getMoney() {
		return money;
	}

	public void setMoney(Float money) {
		this.money = money;
	}

	public Date getLogDate() {
		return logDate;
	}

	public void setLogDate(Date logDate) {
		this.logDate = logDate;
	}

	public Date getSuccessDate() {
		return successDate;
	}

	public void setSuccessDate(Date successDate) {
		this.successDate = successDate;
	}
	
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getRealCurrency() {
		return realCurrency;
	}

	public void setRealCurrency(String realCurrency) {
		this.realCurrency = realCurrency;
	}

	public Integer getChannel() {
		return channel;
	}

	public void setChannel(Integer channel) {
		this.channel = channel;
	}

	public Integer getPlatform() {
		return platform;
	}

	public void setPlatform(Integer platform) {
		this.platform = platform;
	}

	public Integer getOptTimes() {
		return optTimes;
	}

	public void setOptTimes(Integer optTimes) {
		this.optTimes = optTimes;
	}

	public static ReplacementOrder createReplaceOrerByPayOrder(PayOrder payOrder) {
		ReplacementOrder replacementOrder = new ReplacementOrder();
		replacementOrder.setUserId(payOrder.getUserId());
		replacementOrder.setOrderNum(payOrder.getOrderNum());
		replacementOrder.setThirdOrderNum(payOrder.getThird_order_num());
		replacementOrder.setErrorDesc(payOrder.getErrorInfo());
		replacementOrder.setPayType(payOrder.getPayType());
		replacementOrder.setMoney(0.f);
		replacementOrder.setPointName(payOrder.getPointName());
		replacementOrder.setErrorRemark(payOrder.getNote());
		replacementOrder.setLogDate(new Date());
		replacementOrder.setStatus(ReplacementOrderConstants.NOT_SUCCESS);
		replacementOrder.setIp(payOrder.getIp());
		replacementOrder.setRealCurrency(payOrder.getRealCurrency());
		replacementOrder.setChannel(payOrder.getChannel());
		replacementOrder.setPlatform(payOrder.getPlatform());
		return replacementOrder;
	}

	public PayOrder builderPayOrderByReplacementOrder() {
		PayOrder payOrder = new PayOrder();
		payOrder.setPointName(getPointName());
		payOrder.setOrderNum(getOrderNum());
		payOrder.setThird_order_num(getThirdOrderNum());
		payOrder.setPayType(getPayType());
		payOrder.setPointName(getPointName());
		payOrder.setOrder_date(new Date());
		payOrder.setRealCurrency(getRealCurrency());
		payOrder.setChannel(getChannel());
		payOrder.setPlatform(getPlatform());
		payOrder.setNote(getErrorRemark());
		payOrder.setIp(getIp());
		payOrder.setUserId(getUserId());
		return payOrder;
	}
	

	@Override
	public String toString() {
		return "ReplacementOrder [id=" + id + ", orderNum=" + orderNum + ", thirdOrderNum=" + thirdOrderNum
				+ ", status=" + status + ", errorRemark=" + errorRemark + ", errorDesc=" + errorDesc + ", userId="
				+ userId + ", pointName=" + pointName + ", payType=" + payType + ", money=" + money + ", logDate="
				+ logDate + ", successDate=" + successDate + ", ip=" + ip + ", realCurrency=" + realCurrency
				+ ", channel=" + channel + ", platform=" + platform + ", optTimes=" + optTimes + ", replaceDate="
				+ replaceDate + "]";
	}

	public Date getReplaceDate() {
		return replaceDate;
	}

	public void setReplaceDate(Date replaceDate) {
		this.replaceDate = replaceDate;
	}


}
