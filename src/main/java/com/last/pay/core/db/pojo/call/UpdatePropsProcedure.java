package com.last.pay.core.db.pojo.call;

public class UpdatePropsProcedure {
	
	private Integer iUserID;
	
	private Integer iPropID;
	
	private Integer iAmount;
	
	private Integer iType;
	
	private String iRelatedInfo;
	
	private Integer errorCode;

	public Integer getiUserID() {
		return iUserID;
	}

	public void setiUserID(Integer iUserID) {
		this.iUserID = iUserID;
	}

	public Integer getiPropID() {
		return iPropID;
	}

	public void setiPropID(Integer iPropID) {
		this.iPropID = iPropID;
	}

	public Integer getiAmount() {
		return iAmount;
	}

	public void setiAmount(Integer iAmount) {
		this.iAmount = iAmount;
	}

	
	public Integer getiType() {
		return iType;
	}

	public void setiType(Integer iType) {
		this.iType = iType;
	}

	public String getiRelatedInfo() {
		return iRelatedInfo;
	}

	public void setiRelatedInfo(String iRelatedInfo) {
		this.iRelatedInfo = iRelatedInfo;
	}

	public Integer getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(Integer errorCode) {
		this.errorCode = errorCode;
	}

	@Override
	public String toString() {
		return "UpdatePropsProcedure [iUserID=" + iUserID + ", iPropID=" + iPropID + ", iAmount=" + iAmount + ", iType="
				+ iType + ", iRelatedInfo=" + iRelatedInfo + ", errorCode=" + errorCode + "]";
	}

}
