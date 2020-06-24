package com.last.pay.base;

public class CodeMsg<T> {
	
	private Integer code;
	
	private String msg;
	
	private T data;
	
	public CodeMsg(Integer code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public CodeMsg(Integer code, String msg, T data) {
		this.code = code;
		this.msg = msg;
		this.data = data;
	}
	
	public static <T> CodeMsg<T> common(CodeMsgType codeMsgType){
		return common(codeMsgType.getCode(),codeMsgType.getMsg(),null);
	}
	
	public static <T> CodeMsg<T> common(CodeMsgType codeMsgType,T data){
		return common(codeMsgType.getCode(),codeMsgType.getMsg(),data);
	}
	
	public static <T> CodeMsg<T> success(CodeMsgType codeMsgType){
		return common(codeMsgType.getCode(),codeMsgType.getMsg(),null);
	}
	public static <T> CodeMsg<T> success(String msg){
		return common(CodeMsgType.SUCCESS.getCode(),msg,null);
	}

	public static <T> CodeMsg<T> common(Integer code, String msg, T data){
		return new CodeMsg<T>(code, msg, data);
	}
	
	public static <T> CodeMsg<T> failure(CodeMsgType codeMsgType){
		return common(codeMsgType.getCode(), codeMsgType.getMsg(), null);
	}
	
	public static <T> CodeMsg<T> failure(CodeMsgType codeMsgType,T data){
		return common(codeMsgType.getCode(), codeMsgType.getMsg(), data);
	}
	
	public static <T> CodeMsg<T> failure(Integer code, String msg){
		return common(code, msg , null);
	}
	public static <T> CodeMsg<T> failure(Integer code, String msg,T data){
		return common(code, msg , data);
	}
	public Integer getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	@Override
	public String toString() {
		return "{\"code\":" + code + ", \"msg\":" + msg + ", \"data\":" + data + "}";
	}
	
}
