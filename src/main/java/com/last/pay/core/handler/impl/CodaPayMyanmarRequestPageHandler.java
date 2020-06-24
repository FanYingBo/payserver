package com.last.pay.core.handler.impl;

import java.util.Date;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.last.pay.base.CodeMsg;
import com.last.pay.base.CodeMsgType;
import com.last.pay.base.common.constants.Constants;
import com.last.pay.base.common.constants.CodaPayConstants.CodaPayTypeConstants;
import com.last.pay.base.common.constants.Constants.CurrencyConstants;
import com.last.pay.base.common.constants.Constants.PayStatusConstants;
import com.last.pay.base.common.constants.Constants.PayTypeConstants;
import com.last.pay.core.db.pojo.web.PayOrder;
import com.last.pay.core.db.pojo.web.ReplacementOrder;
import com.last.pay.core.dto.coda.response.CodaPayTransParam;
import com.last.pay.core.handler.GeneralPayRequestHandler;
import com.last.pay.core.handler.common.PayRequestChecker;
import com.last.pay.core.component.RequestHandlerAdapter;
import com.last.pay.core.component.sys.SystemConfiguration;
import com.last.pay.core.component.third.CodaPayConfiguration;
import com.last.pay.core.compute.ExchangePayParamBuilder;
import com.last.pay.util.CodaPayUtil;

@Component("codaPayRequestPageHandler")
public class CodaPayMyanmarRequestPageHandler extends  GeneralPayRequestHandler{
	
	private static final Log logger = LogFactory.getLog(CodaPayMyanmarRequestPageHandler.class);
	
	@Autowired
	private RequestHandlerAdapter reqeuestHandlerAdapter;
	@Autowired
	private CodaPayConfiguration codePayConfiguration;
	@Autowired
	private SystemConfiguration systemConfiguration;
	@Autowired
	private PayRequestChecker payRequestChecker;
	@Override
	public String getType() {
		return "5,6";
	}

	/**
	 * 	初始化事务ID （短信支付）
	 * @param request
	 * @return
	 */
	@Override
	public CodeMsg<?> initTnxId(PayOrder payOrder,String mnoId) {
		String subPayType = "";
		payOrder.setRealCurrency(CurrencyConstants.Myanmar);
		if(payOrder.getPayType() == PayTypeConstants.Myanmar_CODAPAY_SMS) {
			subPayType = CodaPayTypeConstants.Myanmar_CODAPAY_SMS;
			if(StringUtils.isNotBlank(mnoId)) {
				CodeMsg<?> realMoneyLimit = payRequestChecker.checkRealMoneyLimit(payOrder, mnoId);
				if( realMoneyLimit.getCode() != CodeMsgType.SUCCESS.getCode()) {
					return realMoneyLimit;
				}
			}else {
				logger.error("缅甸CodaPay支付类型为："+payOrder.getPayType()+" 的运营商编号为空");
				return CodeMsg.failure(CodeMsgType.ERR_CODA_PAY_PARAM_MNO);
			}
		}else if(payOrder.getPayType() == PayTypeConstants.Myanmar_CODAPAY_WAVE) {
			subPayType = CodaPayTypeConstants.Myanmar_CODAPAY_WAVE;
		}
		String iframeUrl = null;
		if(systemConfiguration.getSystemEnv() == Constants.ENV_SANDBOX || systemConfiguration.getSystemEnv() == Constants.ENV_TEST) {
			iframeUrl = codePayConfiguration.getIframeSandboxUrl();
		}else if(systemConfiguration.getSystemEnv() == Constants.ENV_PRODUCT) {
			iframeUrl = codePayConfiguration.getIframeProductUrl();
		}
		ExchangePayParamBuilder matchingBuilder = reqeuestHandlerAdapter.matchingBuilder(payOrder.getRealCurrency());
		String orderId = CodaPayUtil.generateOrderId();
		payOrder.setThird_order_num(orderId);
		payOrder.setOrder_date(new Date());
		CodeMsg<?> codeMsgParam = matchingBuilder.getTranscationParam(payOrder,orderId ,subPayType,iframeUrl,mnoId);
		CodeMsgType codeMsgType = getCodeMsgType(codeMsgParam.getCode());
		if(Objects.nonNull(codeMsgType)) {
			if(codeMsgType.getCode() == CodeMsgType.SUCCESS.getCode()) {
				CodaPayTransParam transParam = (CodaPayTransParam)codeMsgParam.getData();
				logger.info("缅甸CodaPay开启事务成功.....事务ID："+transParam.getTxnId());
				payOrder.setNote("{\"txnId\":"+transParam.getTxnId()+"}");
				payOrder.setStatus(PayStatusConstants.NEW_ORDER);
				payOrder.setErrorThirdNum(transParam.getTxnId());
				return CodeMsg.common(codeMsgType,transParam);
			} else {
				logger.error("缅甸CodaPay开启事务失败，错误码："+codeMsgType.getCode()+"，错误信息："+codeMsgType.getMsg());
				payOrder.setErrorInfo("CodaPay开启事务失败，错误码："+codeMsgType.getCode()+"，错误信息："+codeMsgType.getMsg());
				return CodeMsg.common(codeMsgType);
			}
		} else {
			logger.error("缅甸CodaPay开启事务失败，错误码："+codeMsgParam.getCode()+"，错误信息："+codeMsgParam.getMsg());
			payOrder.setErrorInfo("缅甸CodaPay开启事务失败，错误码："+codeMsgParam.getCode()+"，错误信息："+codeMsgParam.getMsg());
			return CodeMsg.failure((int)codeMsgParam.getCode(),codeMsgParam.getMsg());
		}
	}
	/**
	 *     	对页面支付的进行回调验证
	 * @param txnId
	 * @param currency
	 * @return
	 */
	@Deprecated
	public CodeMsg<?> notifyTransStatus(String txnId,String realCurrency){
		ExchangePayParamBuilder matchingBuilder = reqeuestHandlerAdapter.matchingBuilder(realCurrency);
		long now = System.currentTimeMillis();
		long after = now + codePayConfiguration.getTimeout() * 1000;
		long interval = codePayConfiguration.getRetryInterval() * 1000;
		int retryDelay = codePayConfiguration.getRetryDelay() * 1000;
		long delay = retryDelay +  now;
		CodeMsg<?> notifyCode = null;
		int count = 0;
		boolean isDelay = Boolean.FALSE;
		while(now < after) {
			long current = System.currentTimeMillis();
			if(retryDelay > 0 && !isDelay) {
				if( delay > after) {
					isDelay = Boolean.TRUE;
				} else {
					if(delay - now <= 0) {
						notifyCode = doNotifyTransStatus(txnId,matchingBuilder);
						isDelay = Boolean.TRUE;
						if(notifyCode.getCode() == CodeMsgType.SUCCESS.getCode()) {
							logger.info("CodaPay交易成功，延时【"+codePayConfiguration.getRetryDelay()+"】s获取支付状态成功，事务ID："+ txnId);
							return notifyCode;
						} else {
							logger.error("CodaPay交易失败，延时【"+codePayConfiguration.getRetryDelay()+"】s获取支付状态失败，错误码"+notifyCode.getCode()+"，错误信息："+notifyCode.getMsg());
							continue;
						}
					}
				}
				now = current;
				continue;
			} else if(!isDelay) {
				notifyCode = doNotifyTransStatus(txnId,matchingBuilder);
				isDelay = Boolean.TRUE;
				if(notifyCode.getCode() == CodeMsgType.SUCCESS.getCode()) {
					logger.info("CodaPay交易回调成功，延时【"+retryDelay+"】s获取支付状态成功，事务ID："+ txnId);
					return notifyCode;
				} else {
					logger.error("CodaPay交易回调失败，延时【"+retryDelay+"】s获取支付状态失败，错误码"+notifyCode.getCode()+"，错误信息："+notifyCode.getMsg());
					continue;
				}
			}
			if(current - now >= interval) {
				count ++;
				notifyCode = doNotifyTransStatus(txnId,matchingBuilder);
				if(notifyCode.getCode() == CodeMsgType.SUCCESS.getCode()) {
					return notifyCode;
				} else {
					logger.error("CodaPay交易回调失败，正在重试第【"+count+"】次，交易ID："+txnId+"，错误码："+notifyCode.getCode()+"，错误信息："+notifyCode.getMsg());
				}
				now = current;
			}
		}
		return notifyCode;
	}
	/**
	 * 
	 * @param txnId
	 * @param matchingBuilder
	 * @return
	 */
	@Deprecated
	private CodeMsg<?> doNotifyTransStatus(String txnId,ExchangePayParamBuilder matchingBuilder){
		CodeMsg<?> responseCode = matchingBuilder.notifyTransStatus(Long.parseLong(txnId));
		CodeMsgType codeMsgType = getCodeMsgType(responseCode.getCode());
		if(Objects.nonNull(codeMsgType)) {
			if(codeMsgType.getCode() == CodeMsgType.SUCCESS.getCode()) {
				return CodeMsg.common(codeMsgType);
			}else {
				return CodeMsg.common(codeMsgType);
			}
		} else {
			return responseCode;
		}
	}
	

	@Override
	public boolean isBackEnd() {
		return Boolean.FALSE;
	}

	@Override
	public boolean isNeedCallBack() {
		return Boolean.TRUE;
	}

	@Override
	public CodeMsg<?> queryRequest(ReplacementOrder replacementOrder) {
		return null;
	}

	@Override
	public CodeMsg<?> doProcessRequest(PayOrder payOrder, HttpServletRequest request) {
		/****CodaPay 当且仅当页面支付时status为0，已创建未入库****/
		if(Objects.nonNull(payOrder) && payOrder.getStatus() == 0) {
			logger.info("缅甸CodaPay页面支付请求处理中...订单号："+payOrder.getOrderNum());
			return CodeMsg.success(CodeMsgType.SUCCESS);
		}
		return CodeMsg.failure(CodeMsgType.ERR_SYS);
	}

	@Override
	public boolean confirmIfExistPayOrder(PayOrder payOrder) {
		return Boolean.FALSE;
	}

	@Override
	public boolean isSuccessPayOrder(ReplacementOrder replacementOrder) {
		return Boolean.FALSE;
	}

	@Override
	public CodeMsg<?> doQueryRequest(ReplacementOrder replacementOrder) {
		// TODO Auto-generated method stub
		return CodeMsg.success(CodeMsgType.SUCCESS);
	}

	@Override
	public boolean confirmIfExistSuccessPayOrder(PayOrder payOrder, HttpServletRequest httpServletRequest) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
