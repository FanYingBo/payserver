package com.last.pay.core.exception;

import com.last.pay.base.CodeMsgType;

public class ParamsException extends AbstractException{
	
	private static final long serialVersionUID = 1L;
	
	private CodeMsgType codeMsgType;
	
	public ParamsException(CodeMsgType codeMsgType) {
		super(codeMsgType.getCode(),codeMsgType.getMsg());
		this.codeMsgType = codeMsgType;
	}
	
	public ParamsException(CodeMsgType codeMsgType,String message) {
		super(codeMsgType.getCode(), message);
		this.codeMsgType = codeMsgType;
	}

	@Override
	public String getMessage() {
		return super.getMessage();
	}

	public CodeMsgType getErrorCode() {
		return codeMsgType;
	}

	@Override
	public Integer getCode() {
		// TODO Auto-generated method stub
		return codeMsgType.getCode();
	}
}
