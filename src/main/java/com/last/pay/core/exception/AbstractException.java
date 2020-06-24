package com.last.pay.core.exception;

public abstract class AbstractException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;

	private Integer code;
	
	private Object data;
	
	public AbstractException() {
		super();
	}
	
	public AbstractException(String message) {
		super(message);
	}
	
	public AbstractException(Integer code,String message) {
		super(message);
		this.code = code;
	}
	
	public AbstractException(Integer code,String message, Object data) {
		super(message);
		this.code = code;
		this.data = data;
	}
	
	public Integer getCode() {
		return code;
	}

	public Object getData() {
		return data;
	}

}
