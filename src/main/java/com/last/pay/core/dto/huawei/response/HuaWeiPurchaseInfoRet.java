package com.last.pay.core.dto.huawei.response;

public class HuaWeiPurchaseInfoRet {
	
	private int responseCode;
	
	private String responseMessage;
	
	private String purchaseTokenData;
	
	private String dataSignature;

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public String getPurchaseTokenData() {
		return purchaseTokenData;
	}

	public void setPurchaseTokenData(String purchaseTokenData) {
		this.purchaseTokenData = purchaseTokenData;
	}

	public String getDataSignature() {
		return dataSignature;
	}

	public void setDataSignature(String dataSignature) {
		this.dataSignature = dataSignature;
	}
	
	
}
