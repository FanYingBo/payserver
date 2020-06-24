package com.last.pay.core.dto.huawei.response;

public class HuaWeiPurchaseTokenData {
	/**是否自动续订**/
	private boolean autoRenewing;
	
	private String orderId;
	
	private String packageName;
	/**0 : 消耗型商品 1 : 非消耗型商品**/
	private int kind;
	
	private long purchaseTimeMillis;
	/**0：已购买 1：已取消 2：已退款 */
	private int purchaseState;
	
	private String developerPayload;
	
	private String purchaseToken;
	
	private String developerChallenge;
	/** 0：未消費 1：已消费   */
	private int consumptionState;
	
	private int purchaseType;
	
	private String currency;
	
	private long  price;
	
	private String country;

	public boolean isAutoRenewing() {
		return autoRenewing;
	}

	public void setAutoRenewing(boolean autoRenewing) {
		this.autoRenewing = autoRenewing;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public int getKind() {
		return kind;
	}

	public void setKind(int kind) {
		this.kind = kind;
	}

	public long getPurchaseTimeMillis() {
		return purchaseTimeMillis;
	}

	public void setPurchaseTimeMillis(long purchaseTimeMillis) {
		this.purchaseTimeMillis = purchaseTimeMillis;
	}

	public int getPurchaseState() {
		return purchaseState;
	}

	public void setPurchaseState(int purchaseState) {
		this.purchaseState = purchaseState;
	}

	public String getDeveloperPayload() {
		return developerPayload;
	}

	public void setDeveloperPayload(String developerPayload) {
		this.developerPayload = developerPayload;
	}

	public String getPurchaseToken() {
		return purchaseToken;
	}

	public void setPurchaseToken(String purchaseToken) {
		this.purchaseToken = purchaseToken;
	}

	public String getDeveloperChallenge() {
		return developerChallenge;
	}

	public void setDeveloperChallenge(String developerChallenge) {
		this.developerChallenge = developerChallenge;
	}

	public int getConsumptionState() {
		return consumptionState;
	}

	public void setConsumptionState(int consumptionState) {
		this.consumptionState = consumptionState;
	}

	public int getPurchaseType() {
		return purchaseType;
	}

	public void setPurchaseType(int purchaseType) {
		this.purchaseType = purchaseType;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public long getPrice() {
		return price;
	}

	public void setPrice(long price) {
		this.price = price;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
	
	

}
