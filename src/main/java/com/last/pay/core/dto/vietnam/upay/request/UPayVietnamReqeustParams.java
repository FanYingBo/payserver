package com.last.pay.core.dto.vietnam.upay.request;

public class UPayVietnamReqeustParams {
	/**卡PIN**/
	private String cardNum;
	
	private String appKey;
	
	private String uid;
	
	private String cpOrderId;
	
	private String chKey;
	
	private String cardId;
	
	private String vendor;
	
	private String equipmentType;
	
	private String goodsKey;
	
	private String extra;
	
	private int cardPrintAmount;

	public String getCardNum() {
		return cardNum;
	}

	public void setCardNum(String cardNum) {
		this.cardNum = cardNum;
	}

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getCpOrderId() {
		return cpOrderId;
	}

	public void setCpOrderId(String cpOrderId) {
		this.cpOrderId = cpOrderId;
	}

	public String getChKey() {
		return chKey;
	}

	public void setChKey(String chKey) {
		this.chKey = chKey;
	}

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public String getEquipmentType() {
		return equipmentType;
	}

	public void setEquipmentType(String equipmentType) {
		this.equipmentType = equipmentType;
	}

	public String getGoodsKey() {
		return goodsKey;
	}

	public void setGoodsKey(String goodsKey) {
		this.goodsKey = goodsKey;
	}

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

	
	public int getCardPrintAmount() {
		return cardPrintAmount;
	}

	public void setCardPrintAmount(int cardPrintAmount) {
		this.cardPrintAmount = cardPrintAmount;
	}

	@Override
	public String toString() {
		StringBuffer sbuf = new StringBuffer();
		sbuf.append("{\"cardNum\":\"")
			.append(cardNum)
			.append("\",\"appKey\":\"")
			.append(appKey)
			.append("\",\"uid\":\"")
			.append(uid)
			.append("\",\"cpOrderId\":\"")
			.append(cpOrderId)
			.append("\",\"chKey\":\"")
			.append(chKey)
			.append("\",\"cardId\":\"")
			.append(cardId)
			.append("\",\"vendor\":\"")
			.append(vendor)
			.append("\",\"equipmentType\":\"")
			.append(equipmentType)
			.append("\",\"goodsKey\":\"")
			.append(goodsKey)
			.append("\",\"extra\":\"")
			.append(extra)
			.append("\",\"cardPrintAmount\":\"")
			.append(cardPrintAmount)
			.append("\"}");
		return sbuf.toString();
	}
	

}
