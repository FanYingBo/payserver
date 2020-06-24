package com.last.pay.core.db.pojo.game;


public class PropConfig {
	
	/**道具ID*/
	private Integer ID;
	
	/**道具名称*/
	private String Name;
	
	/**类型*/
	private Integer Type;
	
	/**子类型*/
	private Integer SubType;
	
	/**单价*/
	private Integer BuyUnitPrice;
	
	/**卖价*/
	private Integer SellPrice;
	
	/**是否允许赠送 "true-是","false-否"*/
	private boolean Present;
	
	/**属性1*/
	private Integer Attr1;
	
	/**属性2*/
	private Integer Attr2;

	public Integer getID() {
		return ID;
	}

	public void setID(Integer iD) {
		ID = iD;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public Integer getType() {
		return Type;
	}

	public void setType(Integer type) {
		Type = type;
	}

	public Integer getSubType() {
		return SubType;
	}

	public void setSubType(Integer subType) {
		SubType = subType;
	}

	public Integer getBuyUnitPrice() {
		return BuyUnitPrice;
	}

	public void setBuyUnitPrice(Integer buyUnitPrice) {
		BuyUnitPrice = buyUnitPrice;
	}

	public Integer getSellPrice() {
		return SellPrice;
	}

	public void setSellPrice(Integer sellPrice) {
		SellPrice = sellPrice;
	}

	public boolean isPresent() {
		return Present;
	}

	public void setPresent(boolean present) {
		Present = present;
	}

	public Integer getAttr1() {
		return Attr1;
	}

	public void setAttr1(Integer attr1) {
		Attr1 = attr1;
	}

	public Integer getAttr2() {
		return Attr2;
	}

	public void setAttr2(Integer attr2) {
		Attr2 = attr2;
	}

	@Override
	public String toString() {
		return "PropConfig [ID=" + ID + ", Name=" + Name + ", Type=" + Type + ", SubType=" + SubType + ", BuyUnitPrice="
				+ BuyUnitPrice + ", SellPrice=" + SellPrice + ", Present=" + Present + ", Attr1=" + Attr1 + ", Attr2="
				+ Attr2 + "]";
	}
	
}
