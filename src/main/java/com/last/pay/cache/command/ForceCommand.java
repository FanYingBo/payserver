package com.last.pay.cache.command;

public class ForceCommand {
	private int userId;
	private long sendScore;
	private long loseScore;
	private long winScore;
	private long eatScore;
	private int shotCount;
	private int netScore;
	
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public long getSendScore() {
		return sendScore;
	}
	public void setSendScore(long sendScore) {
		this.sendScore = sendScore;
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
	public long getEatScore() {
		return eatScore;
	}
	public void setEatScore(long eatScore) {
		this.eatScore = eatScore;
	}
	public int getShotCount() {
		return shotCount;
	}
	public void setShotCount(int shotCount) {
		this.shotCount = shotCount;
	}
	public int getNetScore() {
		return netScore;
	}
	public void setNetScore(int netScore) {
		this.netScore = netScore;
	}
}
