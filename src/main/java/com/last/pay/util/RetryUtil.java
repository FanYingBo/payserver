package com.last.pay.util;

import java.util.Objects;
import java.util.function.Supplier;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import com.last.pay.core.db.pojo.web.PayOrder;


public class RetryUtil {
	
	private static final Log logger = LogFactory.getLog(RetryUtil.class);
	
	public static <T> T retryPayOrder(PayOrder payOrder,int retyTimes,Supplier<T> supplier,String errorMsg) {
		T t = null;
		int tmpTimes = retyTimes;
		while(tmpTimes > 0) {
			try {
				t = supplier.get();
				break;
			} catch (Exception e) {
				tmpTimes --;
				if(tmpTimes <= 0) {
					logger.error(errorMsg+"，失败原因："+e.getMessage()+"，重試 "+retyTimes+" 次后仍然失敗");
					if(Objects.nonNull(payOrder)) {
						payOrder.setErrorInfo(errorMsg+"，失败原因："+e.getMessage());
					}
				}else {
					if(checkSpecialError(e, errorMsg)) { // 400 和 403 异常不在重试
						return t;
					}else {
						logger.error(errorMsg+"，失败原因："+e.getMessage()+"，正在重試.....");
					}
				}
			}
		}
		return t;
	}
	/**
	 * 
	 * @param e
	 * @return
	 */
	private static boolean checkStatus(HttpClientErrorException e,String errorMsg) {
		logger.error(errorMsg+"，失败原因："+e.getStatusCode().value()+" "+e.getStatusText()+" "+e.getResponseBodyAsString()+"，不再重试");
		return HttpStatus.BAD_REQUEST.value() == e.getStatusCode().value() ||  HttpStatus.FORBIDDEN.value() == e.getStatusCode().value();
	}
	/**
	 * 1.400 错误请求 
	 * 2.403 禁止访问
	 * @param errorMsg
	 * @return
	 */
	private static boolean checkSpecialError(Exception e,String errorMsg) {
		if(e instanceof HttpClientErrorException) {
			return checkStatus((HttpClientErrorException)e, errorMsg);
		} else {
			return Objects.isNull(e.getMessage()) || e.getMessage().indexOf("400") >= 0 || e.getMessage().indexOf("403") >= 0;
		}
		
	}
}
