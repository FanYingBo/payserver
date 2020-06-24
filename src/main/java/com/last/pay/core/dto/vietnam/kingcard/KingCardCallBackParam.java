package com.last.pay.core.dto.vietnam.kingcard;

public class KingCardCallBackParam {
	
	private KingCardOrder order;
	
	private KingCardTxn txn;
	
	private String sign;

	public KingCardOrder getOrder() {
		return order;
	}

	public void setOrder(KingCardOrder order) {
		this.order = order;
	}

	public KingCardTxn getTxn() {
		return txn;
	}

	public void setTxn(KingCardTxn txn) {
		this.txn = txn;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("{")
			.append("\"order\":").append(order == null ? "null":order).append(",")
			.append("\"txn\":").append(txn == null ? "null":txn)
			.append("}");
		return sb.toString();
	}
	
}
