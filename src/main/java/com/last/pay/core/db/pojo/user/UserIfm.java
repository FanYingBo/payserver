package com.last.pay.core.db.pojo.user;

public class UserIfm {
	//ID
	private Integer userId;
	
	private String nickName;
	//金币
	private Long score;
	//钻石
	private Long diamond;
	//等级/经验
	private Long experience;
	//VIP等级/积分
	private Integer vip;
	//总充值
	private Float infullAmount;
	//解锁炮倍
	private Integer lockCannon;
	//总玩分
	private Long playScore;
	//总赢分
	private Long winScore;
	//总玩时长
	private Long playTime;
	//vip积分
	private Integer vipIntegral;
	
	private Integer userType;
	
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public Long getScore() {
		return score;
	}
	public void setScore(Long score) {
		this.score = score;
	}
	public Long getDiamond() {
		return diamond;
	}
	public void setDiamond(Long diamond) {
		this.diamond = diamond;
	}
	public Long getExperience() {
		return experience;
	}
	public void setExperience(Long experience) {
		this.experience = experience;
	}
	public Integer getVip() {
		return vip;
	}
	public void setVip(Integer vip) {
		this.vip = vip;
	}
	
	public float getInfullAmount() {
		return infullAmount;
	}
	public void setInfullAmount(float infullAmount) {
		this.infullAmount = infullAmount;
	}
	public Integer getLockCannon() {
		return lockCannon;
	}
	public void setLockCannon(Integer lockCannon) {
		this.lockCannon = lockCannon;
	}
	public Long getPlayScore() {
		return playScore;
	}
	public void setPlayScore(Long playScore) {
		this.playScore = playScore;
	}
	public Long getWinScore() {
		return winScore;
	}
	public void setWinScore(Long winScore) {
		this.winScore = winScore;
	}
	public Long getPlayTime() {
		return playTime;
	}
	public void setPlayTime(Long playTime) {
		this.playTime = playTime;
	}
	public Integer getVipIntegral() {
		return vipIntegral;
	}
	public void setVipIntegral(Integer vipIntegral) {
		this.vipIntegral = vipIntegral;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	
	public void setInfullAmount(Float infullAmount) {
		this.infullAmount = infullAmount;
	}

	public Integer getUserType() {
		return userType;
	}
	public void setUserType(Integer userType) {
		this.userType = userType;
	}
	@Override
	public String toString() {
		return "UserIfm [userId=" + userId + ", nickName=" + nickName + ", score=" + score + ", diamond=" + diamond
				+ ", experience=" + experience + ", vip=" + vip + ", infullAmount=" + infullAmount + ", lockCannon="
				+ lockCannon + ", playScore=" + playScore + ", winScore=" + winScore + ", playTime=" + playTime
				+ ", vipIntegral=" + vipIntegral + "]";
	}
	
}
