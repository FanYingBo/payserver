package com.last.pay.base;

import java.util.ArrayList;
import java.util.List;
/**
 * 可以记录PayOrder表的异常信息
 * @author Administrator
 *
 */
public class SignExceptionOrder {

	public static List<Integer> exceptionCodeContainer = new ArrayList<>();
	
	static {
		exceptionCodeContainer.add(CodeMsgType.ERR_TRANSACTION_PENDING.getCode());
		exceptionCodeContainer.add(CodeMsgType.ERR_TIMEOUT.getCode());
	}
	
	public static boolean containError(int code) {
		return exceptionCodeContainer.contains(code);
	}
}
