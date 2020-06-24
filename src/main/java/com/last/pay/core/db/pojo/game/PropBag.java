package com.last.pay.core.db.pojo.game;


public class PropBag {
	
	/**禮包ID*/
	private Integer BagID;
	
	/**道具ID*/
	private Integer PropID;
	
	/**顺序*/
	private Integer Sort;
	
	/**数量*/
	private Integer Amount;

	public Integer getBagID() {
		return BagID;
	}

	public void setBagID(Integer bagID) {
		BagID = bagID;
	}

	public Integer getPropID() {
		return PropID;
	}

	public void setPropID(Integer propID) {
		PropID = propID;
	}

	public Integer getSort() {
		return Sort;
	}

	public void setSort(Integer sort) {
		Sort = sort;
	}

	public Integer getAmount() {
		return Amount;
	}

	public void setAmount(Integer amount) {
		Amount = amount;
	}

	public PropBag(Integer bagID, Integer propID, Integer sort, Integer amount) {
		super();
		BagID = bagID;
		PropID = propID;
		Sort = sort;
		Amount = amount;
	}

	public PropBag() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "PropBag [BagID=" + BagID + ", PropID=" + PropID + ", Sort=" + Sort + ", Amount=" + Amount + "]";
	}
	
	
}
