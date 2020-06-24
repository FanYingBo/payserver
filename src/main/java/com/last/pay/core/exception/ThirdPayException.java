package com.last.pay.core.exception;


public class ThirdPayException extends AbstractException{

	private static final long serialVersionUID = 1L;
	
	public ThirdPayException(Integer code,String message) {
		super(code, message);
	}
	public ThirdPayException(Integer code,String message,Object data) {
		super(code,message,data);
	}
}
