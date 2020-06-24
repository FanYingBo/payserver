package com.last.pay.core.db.pojo.game;


public class VipConfig {
	
	/***vip 等级*/
	private Integer Level;
	
	/**积分*/
	private Integer Integral;
	
	private Float ForceSendScoreRatio;

	public Integer getLevel() {
		return Level;
	}

	public void setLevel(Integer level) {
		Level = level;
	}

	public Integer getIntegral() {
		return Integral;
	}

	public void setIntegral(Integer integral) {
		Integral = integral;
	}

	public Float getForceSendScoreRatio() {
		return ForceSendScoreRatio;
	}

	public void setForceSendScoreRatio(Float forceSendScoreRatio) {
		ForceSendScoreRatio = forceSendScoreRatio;
	}

	@Override
	public String toString() {
		return "VipConfig [Level=" + Level + ", Integral=" + Integral + ", ForceSendScoreRatio=" + ForceSendScoreRatio
				+ "]";
	}

	
	
}
