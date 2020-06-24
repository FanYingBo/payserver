package com.last.pay.core.handler.channel.vietnam;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.last.pay.base.CodeMsg;
import com.last.pay.base.CodeMsgType;
import com.last.pay.base.common.constants.Constants;
import com.last.pay.base.common.constants.Constants.PayChannelConstants;
import com.last.pay.base.common.constants.VietnamCardConstants.VietnamCardProvider;
import com.last.pay.core.db.pojo.web.PayOrder;
import com.last.pay.core.db.pojo.web.ReplacementOrder;
import com.last.pay.core.dto.vietnam.old.request.VietnamRequestParams;
import com.last.pay.core.dto.vietnam.old.response.VietnamResponseParams;
import com.last.pay.core.component.RequestResponseFactory;
import com.last.pay.core.component.sys.SystemConfiguration;
import com.last.pay.core.component.third.VietnamPayConfiguration;
import com.last.pay.core.service.IReplacementOrderService;
import com.last.pay.util.RetryUtil;
import com.last.pay.util.SerializableUtil;

@Component
public class OriginPayChannel implements PayChannelHandler{
	
	private static final Log logger = LogFactory.getLog(OriginPayChannel.class);
	
	private static Map<String,CodeMsgType> responseMap = new HashMap<String,CodeMsgType>();

	@Autowired
	private VietnamPayConfiguration vietnamPayConfiguration;
	@Autowired
	private SystemConfiguration systemConfiguration;
	@Autowired
	private RequestResponseFactory requestResponseFactory;
	@Autowired
	private IReplacementOrderService replacementOrderService;
	@Autowired
	private RestTemplate restTemplate;
	@Override
	public int getPayChannel() {
		return PayChannelConstants.OLD_CHANNEL;
	}

	@Override
	public CodeMsg<?> doProcessRequest(PayOrder payOrder, String cardNum, String cardPin, String providerCode) {
		logger.info("越南点卡支付渠道为 -> 旧渠道");
		if(VietnamCardProvider.FPTGATE.equalsIgnoreCase(providerCode.trim())) {
			providerCode = VietnamCardProvider.GATE;
		}
		if(systemConfiguration.getSystemEnv() == Constants.ENV_TEST) {
			payOrder.setThird_order_num(requestResponseFactory.buildRequestId());
	    	logger.info("錯誤碼："+CodeMsgType.SUCCESS.getCode()+"，錯誤信息："+CodeMsgType.SUCCESS.getMsg());
	    	return CodeMsg.common(CodeMsgType.SUCCESS);
		}
		return doProcessRequestOrigin(payOrder, cardNum, cardPin, providerCode);
	}
	
	/**
	 * 越南点卡支付渠道
	 * @param payOrder
	 * @param cardNum
	 * @param cardPin
	 * @param money
	 * @param providerCode
	 * @return
	 */
	private CodeMsg<?> doProcessRequestOrigin(PayOrder payOrder,String cardNum,String cardPin,String providerCode){
		VietnamRequestParams transRequest = requestResponseFactory.builderVietnamPay(null, cardPin, cardNum, providerCode, payOrder.getRealMoney());
		if(payOrder != null) {
			payOrder.setThird_order_num(transRequest.getRequestId());
		}
    	CodeMsg<VietnamResponseParams> queryResponseCode = doTransRequest(payOrder,transRequest);
//    	return queryResponseCode;
		if(queryResponseCode.getCode().intValue() == CodeMsgType.SUCCESS.getCode()) {
			VietnamResponseParams vietnamResponseParams = queryResponseCode.getData();
			VietnamRequestParams queryVietnamRequestParams = requestResponseFactory.builderVietnamPay(transRequest.getRequestId(),cardPin, cardNum, providerCode, payOrder.getRealMoney());
			payOrder.setRealMoney(Float.valueOf(vietnamResponseParams.getCardPrintAmount()));
			CodeMsg<VietnamResponseParams> doQueryRequest = doQueryRequest(payOrder, queryVietnamRequestParams);
			if(doQueryRequest.getCode() == CodeMsgType.SUCCESS.getCode()) {
				String cardAmount = doQueryRequest.getData().getQueryResult().getCardAmount();
				if(Integer.parseInt(cardAmount) == 0) { // 如果卡面金额为0
					payOrder.setErrorThirdNum(transRequest.getRequestId());
					payOrder.setErrorInfo("越南点卡查询失败,卡号:"+cardNum+"，卡密:"+cardPin+",提供商:"+providerCode+"，查找到交易的卡面金额为0");
					return CodeMsg.failure(CodeMsgType.ERR_QUERY_CARD_AMOUNT_IS_ZERO);
				} else {
					payOrder.setRealMoney(Float.valueOf(cardAmount));
					return CodeMsg.common(CodeMsgType.SUCCESS,doQueryRequest);
				}
			} else {
				payOrder.setErrorThirdNum(transRequest.getRequestId());
				payOrder.setErrorInfo("越南点卡查询失败,卡号:"+cardNum+"，卡密:"+cardPin+",提供商:"+providerCode+"，错误码:"+doQueryRequest.getCode()+",错误信息:"+doQueryRequest.getMsg());
				return CodeMsg.failure(doQueryRequest.getCode(),doQueryRequest.getMsg());
			}
		}else {
			queryResponseCode.setData(null);
			payOrder.setErrorInfo("越南点卡支付失败,卡号:"+transRequest.getCardSerial()+",卡密:"+transRequest.getCardPin()+",提供商:"+transRequest.getProviderCode()+",错误信息:"+queryResponseCode.getMsg());
			return queryResponseCode;
		}
	}
	
	/**
	 * 	发送交易请求
	 * @param VietnamRequestParams
	 * @return
	 */
	private CodeMsg<VietnamResponseParams> doTransRequest(PayOrder payOrder,VietnamRequestParams transRequest) {
		payOrder.setErrorThirdNum(transRequest.getRequestId());
		String url = vietnamPayConfiguration.getTransUrl();
		if(StringUtils.isBlank(url)) {
			logger.error("越南交易的url地址未配置");
			return CodeMsg.failure(CodeMsgType.ERR_SYS_CONFIG);
		}
		logger.info("trans url:"+url);
		String VietnamRequestParamsStr = SerializableUtil.objectToJsonStr(transRequest);
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
	    MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
	    map.add("request", VietnamRequestParamsStr);
	    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
	    ResponseEntity<VietnamResponseParams> postForObject  = null;
	    int reTryCount = systemConfiguration.getReTry();
    	while(reTryCount > 0) {
  	    	try {
  	    		if(logger.isInfoEnabled()) {
  	    			logger.info("trans request："+VietnamRequestParamsStr);
  	    		}
  		    	postForObject= restTemplate.postForEntity(url,request,  VietnamResponseParams.class);
  		    	break;
  			} catch (Exception e) {
  				reTryCount--;
  				if(reTryCount <= 0 ) {
  					logger.error("调用越南支付接口失败，失败原因："+e.getMessage()+"，重試 "+systemConfiguration.getReTry()+" 次后仍然失敗");
  					payOrder.setErrorInfo("越南点卡支付失败,卡号："+transRequest.getCardSerial()+",卡密:"+transRequest.getCardPin()+"失败原因:"+e.getMessage());
  					return CodeMsg.failure(CodeMsgType.ERR_SYS);
  				}else {
  					if(e.getMessage() != null && e.getMessage().indexOf("504") >= 0) {
  						logger.error("调用越南支付接口为超时订单，不再重试...记录补单表");
  						payOrder.setErrorInfo("调用越南支付接口失败," + e.getMessage());
  						replacementOrderService.addReplacementOrder(ReplacementOrder.createReplaceOrerByPayOrder(payOrder),transRequest.getCardSerial());
  						return CodeMsg.failure(CodeMsgType.ERR_TIMEOUT);
  					}else {
  						logger.error("调用越南支付接口失败，失败原因："+e.getMessage()+"，正在重試.....");
  					}
  				}
  			}
  	    }
  	    VietnamResponseParams VietnamResponseParams = postForObject.getBody();
  	    if(logger.isInfoEnabled()) {
  	    	logger.info("trans response："+SerializableUtil.objectToJsonStr(VietnamResponseParams));
  	    }
  	    CodeMsgType codeMsgType = getCodeMsgType(VietnamResponseParams.getStatus());
	    if(codeMsgType != null) {
	    	logger.info("錯誤碼："+codeMsgType.getCode()+"，錯誤信息："+codeMsgType.getMsg());
	    	if(codeMsgType.getCode() == CodeMsgType.ERR_TRANSACTION_PENDING.getCode()) { // 99 进入补单表
	    		payOrder.setErrorInfo("錯誤碼："+codeMsgType.getCode()+"，錯誤信息："+codeMsgType.getMsg());
	    		replacementOrderService.addReplacementOrder(ReplacementOrder.createReplaceOrerByPayOrder(payOrder),transRequest.getCardSerial());
	    	}
	    	return CodeMsg.common(codeMsgType,VietnamResponseParams);
	    }else {
		    int code = Integer.parseInt(VietnamResponseParams.getStatus());
		    String message = VietnamResponseParams.getMessage();
		    logger.info("錯誤碼："+code+"，錯誤信息："+message);
		    return CodeMsg.common(code, message, VietnamResponseParams);
	    }
	}
	/**
	 * 	查询交易
	 * @param VietnamRequestParamsStr
	 * @return
	 */
	private CodeMsg<VietnamResponseParams> doQueryRequest(PayOrder payOrder, VietnamRequestParams vietnamRequestParams){
		String VietnamRequestParamsStr = SerializableUtil.objectToJsonStr(vietnamRequestParams);
		String url = vietnamPayConfiguration.getQueryUrl();
		if(StringUtils.isBlank(url)) {
			logger.error("越南交易的url地址未配置");
			return CodeMsg.failure(CodeMsgType.ERR_SYS_CONFIG);
		}
		logger.info("query url:"+url);
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
	    MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
	    map.add("request", VietnamRequestParamsStr);
	    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
	    int reTryCount = systemConfiguration.getReTry();
		VietnamResponseParams VietnamResponseParams = RetryUtil.retryPayOrder(payOrder, reTryCount, ()->restTemplate.postForObject(url,request,  VietnamResponseParams.class), "调用越南查询接口失败");
		if(Objects.nonNull(VietnamResponseParams)) {
			if(logger.isInfoEnabled()) {
		  	    logger.info("query response："+SerializableUtil.objectToJsonStr(VietnamResponseParams));
		  	}
			if(getCodeMsgType(VietnamResponseParams.getStatus()) != null) {
				return CodeMsg.common(getCodeMsgType(VietnamResponseParams.getStatus()),VietnamResponseParams);
			}else {
				return CodeMsg.common(Integer.parseInt(VietnamResponseParams.getStatus()),VietnamResponseParams.getMessage(),VietnamResponseParams);
			}
		}else {
			return CodeMsg.common(CodeMsgType.ERR_SYS);
		}
	}
	
	public CodeMsgType getCodeMsgType(String status) {
		return responseMap.get(status);
	}
	
	static {
		responseMap.put("00",CodeMsgType.SUCCESS);
		responseMap.put("-1",CodeMsgType.ERR_SYS);
		responseMap.put("01",CodeMsgType.ERR_PARTNERID);
		responseMap.put("02",CodeMsgType.ERR_PROVIDER);
		responseMap.put("03",CodeMsgType.ERR_CORE_SYS);
		responseMap.put("04",CodeMsgType.ERR_REQUEST_PARAMS);
		responseMap.put("05",CodeMsgType.ERR_REQUEST_ID);
		responseMap.put("06",CodeMsgType.ERR_CARD_NUM);
		responseMap.put("07",CodeMsgType.ERR_CARD_PIN);
		responseMap.put("08",CodeMsgType.ERR_PROVIDER_LOCKED);
		responseMap.put("09",CodeMsgType.ERR_PAY_FAILED);
		responseMap.put("24",CodeMsgType.ERR_CARD);
		responseMap.put("25",CodeMsgType.ERR_REQUEST);
		responseMap.put("26",CodeMsgType.ERR_CARD_AMOUNT);
		responseMap.put("36",CodeMsgType.ERR_TRANSACTION);
		responseMap.put("99",CodeMsgType.ERR_TRANSACTION_PENDING);
		responseMap.put("30",CodeMsgType.ERR_CARD_PRINT_AMOUNT);
	
	}
}
