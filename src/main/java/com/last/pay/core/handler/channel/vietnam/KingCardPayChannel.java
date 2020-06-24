package com.last.pay.core.handler.channel.vietnam;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.last.pay.base.CodeMsg;
import com.last.pay.base.CodeMsgType;
import com.last.pay.base.common.constants.Constants;
import com.last.pay.base.common.constants.Constants.PayChannelConstants;
import com.last.pay.base.common.constants.VietnamCardConstants.VietnamCardProvider;
import com.last.pay.base.common.constants.VietnamCardConstants.VietnamKingCardErrorConstans;
import com.last.pay.core.db.pojo.web.PayOrder;
import com.last.pay.core.dto.vietnam.kingcard.request.KingCardRequestParam;
import com.last.pay.core.dto.vietnam.kingcard.response.KingCardResponseParam;
import com.last.pay.core.component.RequestResponseFactory;
import com.last.pay.core.component.sys.SystemConfiguration;
import com.last.pay.core.component.third.VietnamPayConfiguration;
import com.last.pay.util.HashUtils;
import com.last.pay.util.RetryUtil;

@Component
public class KingCardPayChannel implements PayChannelHandler{
	
	private static final Log logger = LogFactory.getLog(KingCardPayChannel.class);

	private static Map<String,String> kingCardTelcoMap = new HashMap<>();

	@Autowired
	private VietnamPayConfiguration vietnamPayConfiguration;
	@Autowired
	private SystemConfiguration systemConfiguration;
	@Autowired
	private RequestResponseFactory requestResponseFactory;
	@Autowired
	private RestTemplate restTemplate;
	
	private static final String callbackUri = "pay/vietnamkcret";
	@Override
	public int getPayChannel() {
		return PayChannelConstants.KING_CARD_CHANNEL;
	}

	@Override
	public CodeMsg<?> doProcessRequest(PayOrder payOrder, String cardNum, String cardPin, String providerCode) {
		logger.info("越南点卡支付渠道为 -> KingCard");
		if(kingCardTelcoMap.get(providerCode) == null) {
			return CodeMsg.failure(CodeMsgType.ERR_PROVIDER.getCode(),"KingCard 不支持该["+providerCode+"]供应商");
		}
		if(systemConfiguration.getSystemEnv() == Constants.ENV_TEST) {
			// 第三方订单号为必填，无回调就没有第三方订单号
			payOrder.setThird_order_num(payOrder.getOrderNum());
	    	logger.info("錯誤碼："+CodeMsgType.SUCCESS.getCode()+"，錯誤信息："+CodeMsgType.SUCCESS.getMsg());
	    	return CodeMsg.common(CodeMsgType.SUCCESS);
		}
		return doProcessKingCardRequest(payOrder, cardNum, cardPin, providerCode);
	}
	/**
	 * KingCard 支付渠道
	 * @param payOrder
	 * @param cardNum
	 * @param cardPin
	 * @param money
	 * @param providerCode
	 * @return
	 */
	private CodeMsg<?> doProcessKingCardRequest(PayOrder payOrder,String cardNum,String cardPin,String providerCode){
		String url = vietnamPayConfiguration.getKingCardProductUrl();
		String notifyUrl = vietnamPayConfiguration.getKingCardProductNotifyUrl();
		if(systemConfiguration.getSystemEnv() == Constants.ENV_SANDBOX) {
			url = vietnamPayConfiguration.getKingCardSandboxUrl();
			notifyUrl = vietnamPayConfiguration.getKingCardSandboxNotifyUrl();
		}
		if(notifyUrl.endsWith("/")) {
			notifyUrl += callbackUri;
		}else {
			notifyUrl += "/"+callbackUri;
		}
		KingCardRequestParam cardVietnamParams = requestResponseFactory.builderKingCardVietnamParams(payOrder.getOrderNum(),  cardNum, cardPin, kingCardTelcoMap.get(providerCode), payOrder.getRealMoney(), notifyUrl);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		String jsonParam = JSONObject.toJSONString(cardVietnamParams);
		HttpEntity<String> httpEntity = new HttpEntity<>(jsonParam, httpHeaders);
		
		String finalUrl = formatKingCardUrl(url, jsonParam);
		
		logger.info("KingCard url: "+ finalUrl);
		logger.info("KingCard request: "+ cardVietnamParams);
		int reTryCount = systemConfiguration.getReTry();
		// 重试获取结果
		KingCardResponseParam kingCardResponse = RetryUtil.retryPayOrder(payOrder, reTryCount, 
					() -> restTemplate.postForObject(finalUrl, httpEntity, KingCardResponseParam.class), "调用KingCard越南支付接口失败");
		if(Objects.isNull(kingCardResponse)) { // 响应的信息为空，只有可能是因为未连接，即响应超时
			logger.error("调用KingCard越南支付接口失败，响应信息为空");
			return CodeMsg.success(CodeMsgType.ERR_TIMEOUT);
		}
		logger.info("KingCard response: " + kingCardResponse);
		if(kingCardResponse.getCode() == 0) { // 成功
			payOrder.setThird_order_num(String.valueOf(kingCardResponse.getData().getId()));
			return CodeMsg.success(CodeMsgType.SUCCESS);
		} else {
			CodeMsgType codeMsgType = VietnamKingCardErrorConstans.errorCodeMap.get(kingCardResponse.getCode());
			if(Objects.isNull(codeMsgType)) {
				logger.error("KingCard越南支付失败，错误码："+kingCardResponse.getCode()+"，错误信息："+kingCardResponse.getMessage());
				return CodeMsg.failure(kingCardResponse.getCode(),kingCardResponse.getMessage());
			}else {
				logger.error("KingCard越南支付失败，错误码："+codeMsgType.getCode()+"，错误信息："+codeMsgType.getMsg());
				return CodeMsg.failure(codeMsgType);
			}
		}
	}
	/**
	 *  格式化URL
	 * @param url
	 * @param clientId
	 * @param secretKey
	 * @param jsonParam
	 * @return
	 */
	private String formatKingCardUrl(String url,String jsonParam) {
		StringBuffer stringBuffer = new StringBuffer(url);
		int lastSprit = stringBuffer.lastIndexOf("/");
		if(stringBuffer.lastIndexOf("/") == url.length() - 1) {
			stringBuffer = new StringBuffer(stringBuffer.substring(0, lastSprit));
			stringBuffer.append(vietnamPayConfiguration.getKingCardTransUri());
		}else {
			stringBuffer.append(vietnamPayConfiguration.getKingCardTransUri());
		}
		stringBuffer.append("?jwt=").append(getJwtToken(jsonParam));
		return stringBuffer.toString();
	}
	/**
	 * 创建token信息
	 * {@link #doProcessKingCardRequest(PayOrder, String, String, String)}
	 * @param clientId
	 * @param secretKey
	 * @param jsonParam
	 * @return
	 */
	private String getJwtToken(String jsonParam) {
		long now = System.currentTimeMillis();
		String tokey = JWT.create()
					.withJWTId(HashUtils.getBase6432IV())
					.withIssuedAt(new Date(now - 60 * 1000))
					.withNotBefore(new Date(now - 60 * 1000))
					.withExpiresAt(new Date(now + 60 * 1000))
					.withClaim("form_params", jsonParam)
					.withIssuer(vietnamPayConfiguration.getKingCardApiKey())
					.sign(Algorithm.HMAC256(vietnamPayConfiguration.getKingCardSecretKey()));
		return tokey;
	}
	
	static {
		kingCardTelcoMap.put(VietnamCardProvider.viettel, VietnamCardProvider.KingCard_VTT);
		kingCardTelcoMap.put(VietnamCardProvider.mobifone, VietnamCardProvider.KingCard_VMS);
		kingCardTelcoMap.put(VietnamCardProvider.vinaphone, VietnamCardProvider.KingCard_VNP);
	}
}
