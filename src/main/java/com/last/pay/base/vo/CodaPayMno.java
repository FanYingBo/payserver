package com.last.pay.base.vo;

public class CodaPayMno {
	
	private String name;
	
	private String code;
	
	private String payCentCode;
	
	/**最低***/
	private float mixLimit;
	/**最高***/
	private float maxLimit;
	
	public CodaPayMno(String name, String code, float mixLimit, float maxLimit,String payCentCode) {
		this.name = name;
		this.code = code;
		this.mixLimit = mixLimit;
		this.maxLimit = maxLimit;
		this.payCentCode = payCentCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public float getMixLimit() {
		return mixLimit;
	}

	public void setMixLimit(float mixLimit) {
		this.mixLimit = mixLimit;
	}

	public float getMaxLimit() {
		return maxLimit;
	}

	public void setMaxLimit(float maxLimit) {
		this.maxLimit = maxLimit;
	}

	public String getPayCentCode() {
		return payCentCode;
	}

	public void setPayCentCode(String payCentCode) {
		this.payCentCode = payCentCode;
	}

}
