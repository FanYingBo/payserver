package com.last.pay.core.handler.impl;

import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.last.pay.base.CodeMsg;
import com.last.pay.base.CodeMsgType;
import com.last.pay.base.common.constants.Constants;
import com.last.pay.base.common.constants.CodaPayConstants.CodaPayProfileVouchersDetails;
import com.last.pay.base.common.constants.CodaPayConstants.CodaPayRequestParam;
import com.last.pay.base.common.constants.Constants.CurrencyConstants;
import com.last.pay.base.common.constants.Constants.PayTypeConstants;
import com.last.pay.core.db.pojo.web.PayOrder;
import com.last.pay.core.db.pojo.web.ReplacementOrder;
import com.last.pay.core.dto.coda.response.CodaPayReponseParam;
import com.last.pay.core.handler.GeneralPayRequestHandler;
import com.last.pay.core.handler.common.PayRequestChecker;
import com.last.pay.core.component.RequestHandlerAdapter;
import com.last.pay.core.component.sys.SystemConfiguration;
import com.last.pay.core.component.third.CodaPayConfiguration;
import com.last.pay.core.compute.ExchangePayParamBuilder;
import com.last.pay.util.CodaPayUtil;
import com.last.pay.util.RetryUtil;
import com.last.pay.util.SerializableUtil;

@Component
public class CodaPayRequestEasyPointHandle extends GeneralPayRequestHandler{
	
	private static final Log logger = LogFactory.getLog(CodaPayRequestEasyPointHandle.class);
	@Autowired
	private RequestHandlerAdapter reqeuestHandlerAdapter;
	@Autowired
	private CodaPayConfiguration codePayConfiguration;

	@Value("${pay.interface.retry.count}")
	private Integer reTry;
	
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private SystemConfiguration systemConfiguration;
	@Autowired
	private PayRequestChecker payRequestChecker;
	@Override
	public CodeMsg<?> doProcessRequest(PayOrder payOrder, HttpServletRequest request) {
		payOrder.setRealCurrency(CurrencyConstants.Myanmar);
		if(Objects.isNull(request)) {
			logger.error("请求参数错误，请求信息为空（非HTTP请求订单）");
			return CodeMsg.failure(CodeMsgType.ERR_SYS_PARAMS);
		}
		String cardNum = request.getParameter("cardNum");
		String cardPin = request.getParameter("cardPin");
		if (Objects.isNull(cardNum)) {
			logger.error("请求处理失败，cardNum 为空！");
			return CodeMsg.failure(CodeMsgType.ERR_CODA_PAY_VOUCHERS_CARDNUM);
		}
		if(Objects.isNull(cardPin)) {
			logger.error("请求处理失败，cardPin 为空！");
			return CodeMsg.failure(CodeMsgType.ERR_CODA_PAY_VOUCHERS_CARDPIN);
		}
		long limitTimes = 0;
		if((limitTimes = payRequestChecker.checkCardLimitTimes(payOrder.getUserId())) > 0) {
			logger.warn("缅甸点卡卡号重复输入，玩家ID：" + payOrder.getUserId());
			return CodeMsg.common(CodeMsgType.ERR_CARD_REPEAT_INPUT, limitTimes);
		}
		return doProcessRequest0(payOrder, request);
	}

	/**
	 * 	缅甸点卡支付处理或页面回调处理
	 * @param payOrder
	 * @param request
	 * @return
	 */
	private CodeMsg<?> doProcessRequest0(PayOrder payOrder, HttpServletRequest request) {
		ExchangePayParamBuilder matchingBuilder = reqeuestHandlerAdapter.matchingBuilder(CurrencyConstants.Myanmar);
		String orderId = CodaPayUtil.generateOrderId();
		Map<String, Object> paramMap = matchingBuilder.buildCodaParamMap(orderId, request);
		logger.info("调用CodaPay请求参数："+SerializableUtil.objectToJsonStr(paramMap));
		String initUrl = null;
		if(systemConfiguration.getSystemEnv() == Constants.ENV_SANDBOX || systemConfiguration.getSystemEnv() == Constants.ENV_TEST) {
			initUrl = codePayConfiguration.getSandboxUrl();
		}else if(systemConfiguration.getSystemEnv() == Constants.ENV_PRODUCT) {
			initUrl = codePayConfiguration.getProductionUrl();
		}
		String finalUrl = initUrl;
		payOrder.setErrorThirdNum(orderId);
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
	    HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(paramMap, headers);
	    int reTryCount = reTry.intValue();
		CodaPayReponseParam reponseParam = RetryUtil.retryPayOrder(payOrder, reTryCount, 
				() -> restTemplate.postForObject(finalUrl, requestEntity, CodaPayReponseParam.class), "调用CodaPay支付接口失败");
		if(Objects.nonNull(reponseParam)) {
			CodeMsgType codeMsgType = getCodeMsgType(reponseParam.getResult_code());
			if(Objects.nonNull(codeMsgType)) {
				if(codeMsgType.getCode() == CodeMsgType.SUCCESS.getCode()) {
					Map<String,String> profileMap = (Map<String,String>)paramMap.get(CodaPayRequestParam.PROFILE);
					StringBuffer sbNote = new StringBuffer();
					sbNote.append("{\""+CodaPayProfileVouchersDetails.VOUCHER_CODE+"\",")
							.append("\""+profileMap.get(CodaPayProfileVouchersDetails.VOUCHER_CODE)+"\",")
							.append("\""+CodaPayProfileVouchersDetails.VOUCHER_PIN+"\",")
							.append("\""+profileMap.get(CodaPayProfileVouchersDetails.VOUCHER_PIN)+"\",")
							.append("\"txn_id\":")
							.append("\""+reponseParam.getTxn_id()+"\",")
							.append("\"money\":")
							.append("\""+reponseParam.getTotal_price()+"\"}");// 事务ID可以用于控制台查找
					payOrder.setThird_order_num(orderId);
					payOrder.setNote(sbNote.toString());
					payOrder.setRealMoney(Float.valueOf(reponseParam.getTotal_price()));
					logger.info("调用CodaPay支付支付成功,响应信息："+reponseParam);
					return CodeMsg.common(codeMsgType);
				} else {
					logger.error("调用CodaPay支付接口失败，错误码："+codeMsgType.getCode()+"，错误信息："+codeMsgType.getMsg());
					payOrder.setErrorThirdNum(reponseParam.getTxn_id()+"");
					payOrder.setErrorInfo("调用CodaPay支付接口失败,失败原因: "+codeMsgType.getMsg());
					return CodeMsg.failure(codeMsgType);
				}
			} else {
				logger.error("调用CodaPay支付接口失败，错误码："+reponseParam.getResult_code()+"，错误信息："+reponseParam.getResult_desc());
				payOrder.setErrorInfo("调用CodaPay支付接口失败,错误码:"+reponseParam.getResult_code()+",失败原因: "+reponseParam.getResult_desc());
				return CodeMsg.failure(reponseParam.getResult_code(),reponseParam.getResult_desc() == null ? "未知错误，系统未录入":reponseParam.getResult_desc());
			}
		} else {
			logger.error("调用CodaPay支付接口失败，响应信息为空");
			payOrder.setErrorInfo("调用CodaPay支付接口失败,响应信息为空");
			return CodeMsg.failure(CodeMsgType.ERR_CALL_CODA_PAY);
		}
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
	public String getType() {
		return String.valueOf(PayTypeConstants.Myanmar_CODAPAY_Vouchers);
	}

	@Override
	public boolean isBackEnd() {
		return Boolean.TRUE;
	}

	@Override
	public boolean isNeedCallBack() {
		return Boolean.TRUE;
	}

	@Override
	public boolean confirmIfExistSuccessPayOrder(PayOrder payOrder, HttpServletRequest httpServletRequest) {
		return false;
	}
	
}
