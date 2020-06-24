package com.last.pay.core.dto.coda.response;

import java.util.Map;

import com.last.pay.util.SerializableUtil;

public class CodaPayReponseParam {
	
	private int result_code;
	
	private String result_desc;
	
	private long txn_id;
	
	private int txn_timeout;
	
	private int total_price;
	
	private String pay_instructions;
	
	private Map<String,String> profile;

	public int getResult_code() {
		return result_code;
	}

	public void setResult_code(int result_code) {
		this.result_code = result_code;
	}

	public String getResult_desc() {
		return result_desc;
	}

	public void setResult_desc(String result_desc) {
		this.result_desc = result_desc;
	}

	public long getTxn_id() {
		return txn_id;
	}

	public void setTxn_id(long txn_id) {
		this.txn_id = txn_id;
	}

	public int getTxn_timeout() {
		return txn_timeout;
	}

	public void setTxn_timeout(int txn_timeout) {
		this.txn_timeout = txn_timeout;
	}

	public int getTotal_price() {
		return total_price;
	}

	public void setTotal_price(int total_price) {
		this.total_price = total_price;
	}

	public String getPay_instructions() {
		return pay_instructions;
	}

	public void setPay_instructions(String pay_instructions) {
		this.pay_instructions = pay_instructions;
	}

	public Map<String, String> getProfile() {
		return profile;
	}

	public void setProfile(Map<String, String> profile) {
		this.profile = profile;
	}

	@Override
	public String toString() {
		String profileStr = SerializableUtil.objectToJsonStr(profile);
		StringBuilder sb = new StringBuilder();
		sb.append("{\"result_code\":")
			.append("\""+result_code+"\",")
			.append("\"result_desc\":")
			.append("\""+result_desc+"\",")
			.append("\"txn_id\":")
			.append("\""+txn_id+"\",")
			.append("\"txn_timeout\":")
			.append("\""+txn_timeout+"\",")
			.append("\"total_price\":")
			.append("\""+total_price+"\",")
			.append("\"pay_instructions\":")
			.append("\""+pay_instructions+"\",")
			.append("\"profile\":")
			.append("\""+profileStr+"\"}");
		return sb.toString();
	}
	
}
