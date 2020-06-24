package com.last.pay.core.dto.huawei.request;

public class HuaWeiPurchaseInfoRedis {
	
	private String originalJson;
	
	private String payOrderId;
	
	private String productId;
	
	private String purchaseToken;
	
	private String orderId;

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getPurchaseToken() {
		return purchaseToken;
	}

	public void setPurchaseToken(String purchaseToken) {
		this.purchaseToken = purchaseToken;
	}

	public String getOriginalJson() {
		return originalJson;
	}

	public void setOriginalJson(String originalJson) {
		this.originalJson = originalJson;
	}

	

	public String getPayOrderId() {
		return payOrderId;
	}

	public void setPayOrderId(String payOrderId) {
		this.payOrderId = payOrderId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	@Override
	public String toString() {
		return originalJson != null ? originalJson:"HuaWeiPurchaseInfoRedis [originalJson=" + originalJson + ", payOrderId=" + payOrderId + ", productId="
				+ productId + ", purchaseToken=" + purchaseToken + ", orderId=" + orderId + "]";
	}
	
	
	
}
