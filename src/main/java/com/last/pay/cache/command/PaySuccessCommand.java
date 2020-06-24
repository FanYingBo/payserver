package com.last.pay.cache.command;

import java.util.Map;

public class PaySuccessCommand {
	
	private int userId;
	private int vip;
	private int upgradeVip;
	private String pointName;
	private int vipIntegral;
	private int position;
	private int toll;
	private int dynamicType;
	
	
	private Map<Integer,Integer> upgradeProps;
	private Map<Integer,Integer> props;
	
	private int multipleTurntablePosition;
	private int goldTurntablePosition;
	
	public int getVip() {
		return vip;
	}
	public void setVip(int vip) {
		this.vip = vip;
	}
	public int isUpgradeVip() {
		return upgradeVip;
	}
	public void setUpgradeVip(int upgradeVip) {
		this.upgradeVip = upgradeVip;
	}
	public int getVipIntegral() {
		return vipIntegral;
	}
	public void setVipIntegral(int vipIntegral) {
		this.vipIntegral = vipIntegral;
	}
	public Map<Integer, Integer> getUpgradeProps() {
		return upgradeProps;
	}
	public void setUpgradeProps(Map<Integer, Integer> upgradeProps) {
		this.upgradeProps = upgradeProps;
	}
	public Map<Integer, Integer> getProps() {
		return props;
	}
	public void setProps(Map<Integer, Integer> props) {
		this.props = props;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getPointName() {
		return pointName;
	}
	public void setPointName(String pointName) {
		this.pointName = pointName;
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	public int getToll() {
		return toll;
	}
	public void setToll(int toll) {
		this.toll = toll;
	}
	
	public int getDynamicType() {
		return dynamicType;
	}
	public void setDynamicType(int dynamicType) {
		this.dynamicType = dynamicType;
	}
	public int getMultipleTurntablePosition() {
		return multipleTurntablePosition;
	}
	public void setMultipleTurntablePosition(int multipleTurntablePosition) {
		this.multipleTurntablePosition = multipleTurntablePosition;
	}
	public int getGoldTurntablePosition() {
		return goldTurntablePosition;
	}
	public void setGoldTurntablePosition(int goldTurntablePosition) {
		this.goldTurntablePosition = goldTurntablePosition;
	}

	
}
