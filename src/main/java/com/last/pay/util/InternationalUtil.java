package com.last.pay.util;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.last.pay.base.CodeMsg;
import com.last.pay.base.CodeMsgType;
import com.last.pay.base.common.constants.Constants;
import com.last.pay.base.common.constants.Constants.CurrencyConstants;
import com.last.pay.core.db.pojo.game.International;
import com.last.pay.core.exception.AbstractException;
import com.last.pay.core.exception.ParamsException;
import com.last.pay.core.exception.RedisOptException;
import com.last.pay.core.exception.SqlQueryException;
import com.last.pay.core.exception.ThirdPayException;
import com.last.pay.core.service.ITranslationService;
/**
 * 	统一异常处理 ，返回错误码
 * @author Administrator
 *
 */
public class InternationalUtil {
	
	/**
	 * 不带国际化的异常处理
	 * @param e
	 * @return
	 */
	public static CodeMsg<?> handleException(Exception e){
		if (e instanceof ParamsException){
			ParamsException paramsException = (ParamsException) e;
			return CodeMsg.failure(paramsException.getErrorCode().getCode(), paramsException.getMessage(),e);	
		} else if(e instanceof RedisOptException){
			return CodeMsg.failure(CodeMsgType.ERR_REDIS, e);
		} else if(e instanceof SqlQueryException){
			return CodeMsg.failure(CodeMsgType.ERR_DB, e);
		} else if(e instanceof ThirdPayException) {
			ThirdPayException thirdPayException = (ThirdPayException) e;
			return CodeMsg.failure(thirdPayException.getCode(),thirdPayException.getMessage(),e);	
		}else {
			return CodeMsg.failure(CodeMsgType.ERR_CORE_SYS, e);
		}
	}
	
	public static CodeMsg<?> handleRetCode(String currency, CodeMsg<?> retCode, ITranslationService transactionService){
		String realCurrency = currency;
		if(StringUtils.isBlank(realCurrency)) {
			realCurrency = CurrencyConstants.USA;
		}
		if(retCode.getData() != null && retCode.getData() instanceof Exception) {
			Exception exec = (Exception)retCode.getData();
			retCode.setData(null);
			return handleException(exec, currency, transactionService);
		} else {
			International international = transactionService.getInternationalByErrorCode(retCode.getCode());
			String internationalMsg = getInternationalMsg(currency, international);
			if(Objects.nonNull(internationalMsg)) {
				retCode.setMsg(internationalMsg);
				
			}
			return retCode;
		}
	}
	
	
	public static CodeMsg<?> handleException(Exception e,String currency,ITranslationService translationService){
		if(e instanceof AbstractException) {
			return handleSysException((AbstractException)e, currency, translationService);
		}else {
			International international = translationService.getInternationalByErrorCode(CodeMsgType.ERR_CORE_SYS.getCode());
			if(Objects.isNull(international)) {
				return CodeMsg.failure(CodeMsgType.ERR_CORE_SYS);
			}else {
				return CodeMsg.failure(CodeMsgType.ERR_CORE_SYS.getCode(), getInternationalMsg(currency,international));
			}
		}
	}
	
	/**
	 * 	异常处理
	 * @param e
	 * @param currency
	 * @param transactionService
	 * @return
	 */
	public static CodeMsg<?> handleSysException(AbstractException e,String currency,ITranslationService transactionService){
		String internationalMsg = "";
		if (e instanceof ParamsException){
			ParamsException paramsException = (ParamsException) e;
			International international = transactionService.getInternationalByErrorCode(paramsException.getErrorCode().getCode());
			if(Objects.isNull(international)) {
				internationalMsg = paramsException.getMessage();
			}else {
				internationalMsg = getInternationalMsg(currency,international);
			}
		} else if(e instanceof RedisOptException){
			International international = transactionService.getInternationalByErrorCode(CodeMsgType.ERR_REDIS.getCode());
			if(Objects.isNull(international)) {
				internationalMsg = CodeMsgType.ERR_REDIS.getMsg();
			}else {
				internationalMsg = getInternationalMsg(currency,international);
			}
		} else if(e instanceof SqlQueryException){
			International international = transactionService.getInternationalByErrorCode(CodeMsgType.ERR_DB.getCode());
			if(Objects.isNull(international)) {
				internationalMsg = CodeMsgType.ERR_DB.getMsg();
			}else {
				internationalMsg = getInternationalMsg(currency,international);
			}
		} else {
			ThirdPayException thirdPayException = (ThirdPayException) e;
			International international = transactionService.getInternationalByErrorCode(thirdPayException.getCode());
			if(Objects.isNull(international)) {
				internationalMsg = thirdPayException.getMessage();
			}else {
				internationalMsg = getInternationalMsg(currency,international);
			}
		}
		if(internationalMsg.indexOf("%d") >= 0) {
			internationalMsg = String.format(internationalMsg, e.getData());
		}
		return CodeMsg.failure(e.getCode(), internationalMsg);
	}
	/**
	 * 	返回不同语言的错误信息
	 * @param currency
	 * @param international
	 * @return
	 */
	public static String getInternationalMsg(String currency,International international) {
		if(Objects.nonNull(international)) {
			if(StringUtils.isBlank(currency)) {
				return StringUtils.isBlank(international.getEnValue()) ? (StringUtils.isBlank(international.getEnValue()) ? international.getCnValue():international.getEnValue()):international.getEnValue();
			}else if(Constants.CurrencyConstants.Myanmar.equals(currency)) {
				return StringUtils.isBlank(international.getMmValue()) ? (StringUtils.isBlank(international.getEnValue()) ? international.getCnValue():international.getEnValue()):international.getMmValue();
			}else if(Constants.CurrencyConstants.Vietnam.equals(currency)) {
				return StringUtils.isBlank(international.getViValue()) ? (StringUtils.isBlank(international.getEnValue()) ? international.getCnValue():international.getEnValue()):international.getViValue();
			}
			return StringUtils.isBlank(international.getEnValue()) ? international.getCnValue():international.getEnValue();
		}else {
			return null;
		}
	}
}
