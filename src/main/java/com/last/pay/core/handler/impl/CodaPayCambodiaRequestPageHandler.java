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
import com.last.pay.core.component.RequestHandlerAdapter;
import com.last.pay.core.component.sys.SystemConfiguration;
import com.last.pay.core.component.third.CodaPayConfiguration;
import com.last.pay.core.compute.ExchangePayParamBuilder;
import com.last.pay.util.CodaPayUtil;

@Component
public class CodaPayCambodiaRequestPageHandler extends GeneralPayRequestHandler{
	
	private static final Log logger = LogFactory.getLog(CodaPayCambodiaRequestPageHandler.class);
	@Autowired
	private RequestHandlerAdapter reqeuestHandlerAdapter;
	@Autowired
	private SystemConfiguration systemConfiguration;
	@Autowired
	private CodaPayConfiguration codePayConfiguration;
	@Override
	public CodeMsg<?> doProcessRequest(PayOrder payOrder, HttpServletRequest request) {
		/****CodaPay 当且仅当页面支付时status为0，已创建未入库****/
		if(Objects.nonNull(payOrder) && payOrder.getStatus() == 0) {
			logger.info("柬埔寨CodaPay页面支付请求处理中...订单号："+payOrder.getOrderNum());
			return CodeMsg.success(CodeMsgType.SUCCESS);
		}
		return CodeMsg.failure(CodeMsgType.ERR_SYS);
	}

	
	
	@Override
	public CodeMsg<?> initTnxId(PayOrder payOrder, String mnoId) {
		String subPayType = "";
		payOrder.setRealCurrency(CurrencyConstants.USA);
		if(payOrder.getPayType() == PayTypeConstants.Cambodia_CODAPAY_SMS) {
			subPayType = CodaPayTypeConstants.Cambodia_CODAPAY_SMS;
			if(StringUtils.isBlank(mnoId)){
				logger.error("柬埔寨CodaPay支付类型为："+payOrder.getPayType()+" 的运营商编号为空");
				return CodeMsg.failure(CodeMsgType.ERR_CODA_PAY_PARAM_MNO);
			}
		}else if(payOrder.getPayType() == PayTypeConstants.Cambodia_CODAPAY_Wing) {
			subPayType = CodaPayTypeConstants.Cambodia_CODAPAY_Wing;
		}
		String iframeUrl = null;
		if(systemConfiguration.getSystemEnv() == Constants.ENV_SANDBOX || systemConfiguration.getSystemEnv() == Constants.ENV_TEST) {
			iframeUrl = codePayConfiguration.getIframeSandboxUrl();
		}else if(systemConfiguration.getSystemEnv() == Constants.ENV_PRODUCT) {
			iframeUrl = codePayConfiguration.getIframeProductUrl();
		}
		ExchangePayParamBuilder matchingBuilder = reqeuestHandlerAdapter.matchingBuilder(CurrencyConstants.Cambodia);
		String orderId = CodaPayUtil.generateOrderId();
		payOrder.setThird_order_num(orderId);
		payOrder.setOrder_date(new Date());
		CodeMsg<?> codeMsgParam = matchingBuilder.getTranscationParam(payOrder,orderId ,subPayType,iframeUrl,mnoId);
		CodeMsgType codeMsgType = getCodeMsgType(codeMsgParam.getCode());
		if(Objects.nonNull(codeMsgType)) {
			if(codeMsgType.getCode() == CodeMsgType.SUCCESS.getCode()) {
				CodaPayTransParam transParam = (CodaPayTransParam)codeMsgParam.getData();
				logger.info("柬埔寨CodaPay开启事务成功.....事务ID："+transParam.getTxnId());
				payOrder.setNote("{\"txnId\":"+transParam.getTxnId()+"}");
				payOrder.setStatus(PayStatusConstants.NEW_ORDER);
				return CodeMsg.common(codeMsgType,transParam);
			} else {
				logger.error("柬埔寨CodaPay开启事务失败，错误码："+codeMsgType.getCode()+"，错误信息："+codeMsgType.getMsg());
				payOrder.setErrorInfo("柬埔寨CodaPay开启事务失败，错误码："+codeMsgType.getCode()+"，错误信息："+codeMsgType.getMsg());
				return CodeMsg.common(codeMsgType);
			}
		} else {
			logger.error("柬埔寨CodaPay开启事务失败，错误码："+codeMsgParam.getCode()+"，错误信息："+codeMsgParam.getMsg());
			payOrder.setErrorInfo("柬埔寨CodaPay开启事务失败，错误码："+codeMsgParam.getCode()+"，错误信息："+codeMsgParam.getMsg());
			return CodeMsg.failure((int)codeMsgParam.getCode(),codeMsgParam.getMsg());
		}
	}



	@Override
	public boolean confirmIfExistPayOrder(PayOrder payOrder) {
		return false;
	}

	@Override
	public boolean confirmIfExistSuccessPayOrder(PayOrder payOrder, HttpServletRequest httpServletRequest) {
		return false;
	}

	@Override
	public boolean isSuccessPayOrder(ReplacementOrder replacementOrder) {
		return false;
	}

	@Override
	public String getType() {
		return "10,11";
	}

	@Override
	public boolean isBackEnd() {
		return Boolean.FALSE;
	}

	@Override
	public boolean isNeedCallBack() {
		return Boolean.TRUE;
	}
	
}
