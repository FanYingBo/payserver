package com.last.pay.core.db.pojo.user;

public class UserControl {
	
	private int userId;
	
	private long loseScore; 
	
	private long winScore; 
	
	private long sendScore; 
	
	private long eatScore;
	
	private long netScore; 
	
	private long shotCount;
	
	private int warHeadIntegral;
	
	private int ticketIntegral;

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getWarHeadIntegral() {
		return warHeadIntegral;
	}

	public void setWarHeadIntegral(int warHeadIntegral) {
		this.warHeadIntegral = warHeadIntegral;
	}

	public int getTicketIntegral() {
		return ticketIntegral;
	}

	public void setTicketIntegral(int ticketIntegral) {
		this.ticketIntegral = ticketIntegral;
	}

	public long getLoseScore() {
		return loseScore;
	}

	public void setLoseScore(long loseScore) {
		this.loseScore = loseScore;
	}

	public long getWinScore() {
		return winScore;
	}

	public void setWinScore(long winScore) {
		this.winScore = winScore;
	}

	public long getSendScore() {
		return sendScore;
	}

	public void setSendScore(long sendScore) {
		this.sendScore = sendScore;
	}

	public long getEatScore() {
		return eatScore;
	}

	public void setEatScore(long eatScore) {
		this.eatScore = eatScore;
	}

	public long getNetScore() {
		return netScore;
	}

	public void setNetScore(long netScore) {
		this.netScore = netScore;
	}

	public long getShotCount() {
		return shotCount;
	}

	public void setShotCount(long shotCount) {
		this.shotCount = shotCount;
	}

}
