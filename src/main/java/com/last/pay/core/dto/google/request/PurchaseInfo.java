package com.last.pay.core.dto.google.request;

public class PurchaseInfo {
	
	private String originalJson;
	
	private String purchaseToken;
	
	private String productId;
	
	private String packageName;
	
	private String orderId;

	public String getPurchaseToken() {
		return purchaseToken;
	}

	public void setPurchaseToken(String purchaseToken) {
		this.purchaseToken = purchaseToken;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getOriginalJson() {
		return originalJson;
	}

	public void setOriginalJson(String originalJson) {
		this.originalJson = originalJson;
	}

	@Override
	public String toString() {
		return originalJson;
	}
	
	
}
