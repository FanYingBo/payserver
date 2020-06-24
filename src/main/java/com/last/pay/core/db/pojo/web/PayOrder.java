package com.last.pay.core.db.pojo.web;

import java.util.Date;
import java.util.List;

import com.last.pay.base.common.constants.Constants.PayStatusConstants;
import com.last.pay.core.db.pojo.user.UserIfm;
import com.last.pay.core.dto.huawei.request.HuaWeiPurchaseInfoRedis;
import com.last.pay.core.dto.google.request.PurchaseInfo;

public class PayOrder{
	
	/**订单号**/
	private String orderNum;
	
	/**玩家ID***/
	private Integer userId;
	
	private String nickName;
	
	private String pointName;
	
	private Integer payType;
	/***0:新的订单  1:成功 2:失败****/
	private Integer status;
	
	private Integer platform;
	
	private Integer channel;

	private String ip;
	
	private String third_order_num;
	
	private Date order_date;
	
	private Date success_date;
	
	private String operator;
	
	private String Note;
	
	private Float realMoney;
	
	private String realCurrency;
	
	private UserIfm user;
	
	private PurchaseInfo googlePurchaseInfo;
	
	private String iosPurchaseInfo;
	
	private HuaWeiPurchaseInfoRedis huaWeiPurchaseInfo;
	
	private String purchaseInfo;
	
	// 记错误订单号（或者事务ID），错误日志
	private String errorThirdNum;
	// 记错误信息
	private String errorInfo;
	
	private List<PayOrder> payOrders;
	
	//话费卡积分
	private Integer ticketsIntegral;
	
	private Float beforeInfullAmount;
	
	private int beforeVipIntegral;
	
	private int vipIntegral;
	
	//付费点与第三方订单是否一致
	private boolean isUniformPointName = Boolean.TRUE;
	
	private Integer dynamicId;
	
	private DynamicPayPoint dynamicPayPoint;

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

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
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

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public void setNewOrder() {
		this.status = PayStatusConstants.NEW_ORDER;
	}
	
	public void setSuccessOrder() {
		this.status = PayStatusConstants.SUCCESS_ORDER;
	}
	
	public void setFailureOrder() {
		this.status = PayStatusConstants.FAILURE_ORDER;
	}
	
	public void setDealingOrder() {
		this.status = PayStatusConstants.DEALING_ORDER;
	}
	
	public Integer getPlatform() {
		return platform;
	}

	public void setPlatform(Integer platform) {
		this.platform = platform;
	}
	
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getThird_order_num() {
		return third_order_num;
	}

	public void setThird_order_num(String third_order_num) {
		this.third_order_num = third_order_num;
	}

	public Date getOrder_date() {
		return order_date;
	}

	public void setOrder_date(Date order_date) {
		this.order_date = order_date;
	}

	public Date getSuccess_date() {
		return success_date;
	}

	public void setSuccess_date(Date success_date) {
		this.success_date = success_date;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getNote() {
		return Note;
	}

	public void setNote(String note) {
		Note = note;
	}

	public void setChannel(Integer channel) {
		this.channel = channel;
	}
	
	public Integer getChannel() {
		return channel;
	}

	public Float getRealMoney() {
		return realMoney;
	}

	public void setRealMoney(Float realMoney) {
		this.realMoney = realMoney;
	}

	public String getRealCurrency() {
		return realCurrency;
	}

	public void setRealCurrency(String realCurrency) {
		this.realCurrency = realCurrency;
	}

	public PurchaseInfo getGooglePurchaseInfo() {
		return googlePurchaseInfo;
	}

	public void setGooglePurchaseInfo(PurchaseInfo googlePurchaseInfo) {
		this.googlePurchaseInfo = googlePurchaseInfo;
	}

	public String getErrorThirdNum() {
		return errorThirdNum;
	}

	public void setErrorThirdNum(String errorThirdNum) {
		this.errorThirdNum = errorThirdNum;
	}

	public String getErrorInfo() {
		return errorInfo;
	}

	public void setErrorInfo(String errorInfo) {
		this.errorInfo = errorInfo;
	}

	public UserIfm getUser() {
		return user;
	}

	public void setUser(UserIfm user) {
		this.user = user;
	}

	public List<PayOrder> getPayOrders() {
		return payOrders;
	}

	public void setPayOrders(List<PayOrder> payOrders) {
		this.payOrders = payOrders;
	}
	
	public Integer getTicketsIntegral() {
		return ticketsIntegral;
	}
	public void setTicketsIntegral(Integer ticketsIntegral) {
		this.ticketsIntegral = ticketsIntegral;
	}
	
	
	public Float getBeforeInfullAmount() {
		return beforeInfullAmount;
	}

	public void setBeforeInfullAmount(Float beforeInfullAmount) {
		this.beforeInfullAmount = beforeInfullAmount;
	}

	public String getIosPurchaseInfo() {
		return iosPurchaseInfo;
	}

	public void setIosPurchaseInfo(String iosPurchaseInfo) {
		this.iosPurchaseInfo = iosPurchaseInfo;
	}

	

	public boolean isUniformPointName() {
		return isUniformPointName;
	}

	public void setUniformPointName(boolean isUniformPointName) {
		this.isUniformPointName = isUniformPointName;
	}

	public HuaWeiPurchaseInfoRedis getHuaWeiPurchaseInfo() {
		return huaWeiPurchaseInfo;
	}

	public void setHuaWeiPurchaseInfo(HuaWeiPurchaseInfoRedis huaWeiPurchaseInfo) {
		this.huaWeiPurchaseInfo = huaWeiPurchaseInfo;
	}

	public int getBeforeVipIntegral() {
		return beforeVipIntegral;
	}

	public void setBeforeVipIntegral(int beforeVipIntegral) {
		this.beforeVipIntegral = beforeVipIntegral;
	}

	public int getVipIntegral() {
		return vipIntegral;
	}

	public void setVipIntegral(int vipIntegral) {
		this.vipIntegral = vipIntegral;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"orderNum\":\"")
				.append(orderNum)
				.append("\",\"userId\":\"")
				.append(userId)
				.append("\",\"nickName\":\"")
				.append(nickName)
				.append("\",\"pointName\":\"")
				.append(pointName)
				.append("\",\"payType\":\"")
				.append(payType)
				.append("\",\"status\":\"")
				.append(status)
				.append("\",\"platform\":\"")
				.append(platform)
				.append("\",\"channel\":\"")
				.append(channel)
				.append("\",\"ip\":\"")
				.append(ip)
				.append("\",\"third_order_num\":\"")
				.append(third_order_num)
				.append("\",\"order_date\":\"")
				.append(order_date)
				.append("\",\"success_date\":\"")
				.append(success_date)
				.append("\",\"operator\":\"")
				.append(operator)
				.append("\",\"Note\":\"")
				.append(Note)
				.append("\",\"realMoney\":\"")
				.append(realMoney)
				.append("\",\"realCurrency\":\"")
				.append(realCurrency)
				.append("\",\"isUniformPointName\":\"")
				.append(isUniformPointName)
				.append("\"}");
		return sb.toString();
	}

	public String getPurchaseInfo() {
		return purchaseInfo;
	}

	public void setPurchaseInfo(String purchaseInfo) {
		this.purchaseInfo = purchaseInfo;
	}

	public Integer getDynamicId() {
		return dynamicId;
	}

	public void setDynamicId(Integer dynamicId) {
		this.dynamicId = dynamicId;
	}

	public DynamicPayPoint getDynamicPayPoint() {
		return dynamicPayPoint;
	}

	public void setDynamicPayPoint(DynamicPayPoint dynamicPayPoint) {
		this.dynamicPayPoint = dynamicPayPoint;
	}
	
	

}
