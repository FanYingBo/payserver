package com.last.pay.core.dto.ios.response;

import java.util.Date;

public class IOSInAppParams {
	
	private String quantity;
	
	private String product_id;
	
	private String transaction_id;
	
	private String original_transaction_id;
	
	private Date purchase_date;
	
	private Long purchase_date_ms;
	
	private Date original_purchase_date;
	
	private Long original_purchase_date_ms;
	
	private Boolean is_trial_period;

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getProduct_id() {
		return product_id;
	}

	public void setProduct_id(String product_id) {
		this.product_id = product_id;
	}

	public String getTransaction_id() {
		return transaction_id;
	}

	public void setTransaction_id(String transaction_id) {
		this.transaction_id = transaction_id;
	}

	public String getOriginal_transaction_id() {
		return original_transaction_id;
	}

	public void setOriginal_transaction_id(String original_transaction_id) {
		this.original_transaction_id = original_transaction_id;
	}

	public Date getPurchase_date() {
		return purchase_date;
	}

	public void setPurchase_date(Date purchase_date) {
		this.purchase_date = purchase_date;
	}

	public Long getPurchase_date_ms() {
		return purchase_date_ms;
	}

	public void setPurchase_date_ms(Long purchase_date_ms) {
		this.purchase_date_ms = purchase_date_ms;
	}

	public Boolean getIs_trial_period() {
		return is_trial_period;
	}

	public void setIs_trial_period(Boolean is_trial_period) {
		this.is_trial_period = is_trial_period;
	}

	public Date getOriginal_purchase_date() {
		return original_purchase_date;
	}

	public void setOriginal_purchase_date(Date original_purchase_date) {
		this.original_purchase_date = original_purchase_date;
	}

	public Long getOriginal_purchase_date_ms() {
		return original_purchase_date_ms;
	}

	public void setOriginal_purchase_date_ms(Long original_purchase_date_ms) {
		this.original_purchase_date_ms = original_purchase_date_ms;
	}

}
