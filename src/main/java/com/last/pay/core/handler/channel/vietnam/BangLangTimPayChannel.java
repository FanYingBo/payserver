package com.last.pay.core.handler.channel.vietnam;

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
import org.springframework.util.DigestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.last.pay.base.CodeMsg;
import com.last.pay.base.CodeMsgType;
import com.last.pay.base.common.constants.Constants;
import com.last.pay.base.common.constants.Constants.PayChannelConstants;
import com.last.pay.base.common.constants.VietnamCardConstants.VietnamCardProvider;
import com.last.pay.core.db.pojo.web.PayOrder;
import com.last.pay.core.dto.vietnam.banglang.response.BangLangCardResponse;
import com.last.pay.core.component.sys.SystemConfiguration;
import com.last.pay.core.component.third.VietnamPayConfiguration;
import com.last.pay.util.RetryUtil;

@Component
public class BangLangTimPayChannel implements PayChannelHandler{
	
	private static final Log logger = LogFactory.getLog(BangLangTimPayChannel.class);
	
	private static Map<String,String> bangLangCardType = new HashMap<>();
	
	private static Map<Integer,CodeMsgType> responseMap = new HashMap<Integer,CodeMsgType>();

	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private SystemConfiguration systemConfiguration;
	@Autowired
	private VietnamPayConfiguration vietnamPayConfiguration;

	@Override
	public int getPayChannel() {
		return PayChannelConstants.BANG_LANG_CHANNEL;
	}

	@Override
	public CodeMsg<?> doProcessRequest(PayOrder payOrder, String cardNum, String cardPin, String providerCode) {
		logger.info("越南点卡支付渠道为 -> BangLang");
		if((providerCode = bangLangCardType.get(providerCode)) == null) {
			return CodeMsg.failure(CodeMsgType.ERR_PROVIDER.getCode(),"BangLang 不支持该["+providerCode+"]供应商");
		}
		if(systemConfiguration.getSystemEnv() == Constants.ENV_TEST) {
			// 第三方订单号为必填，无回调就没有第三方订单号
			payOrder.setThird_order_num(payOrder.getOrderNum());
	    	logger.info("錯誤碼："+CodeMsgType.SUCCESS.getCode()+"，錯誤信息："+CodeMsgType.SUCCESS.getMsg());
	    	return CodeMsg.common(CodeMsgType.SUCCESS);
		}
		return doProcessBangLangReqeust(payOrder, cardNum, cardPin, providerCode);
	}
	
	private CodeMsg<?> doProcessBangLangReqeust(PayOrder payOrder, String cardNum, String cardPin, String providerCode){
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		httpHeaders.add("User-Agent", "Chrome/54.0.2840.99");
		MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
		String apiKey = vietnamPayConfiguration.getBangLangApiKey();
		paramMap.add("api_key", apiKey);
		paramMap.add("card_seri", cardNum);
		paramMap.add("card_code", cardPin);
		paramMap.add("request_id", payOrder.getOrderNum());
		paramMap.add("card_amount", String.valueOf(payOrder.getRealMoney().intValue()));
		paramMap.add("card_type", providerCode);
		paramMap.add("signature", computeMD5(apiKey, payOrder.getRealMoney().intValue(), cardPin, cardNum));
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(paramMap, httpHeaders);
		String bangLangUrl = vietnamPayConfiguration.getBangLangUrl();
		logger.info("BangLang url: " + bangLangUrl);
		logger.info("BangLang request: "+ paramMap);
		int retryTimes = systemConfiguration.getReTry();
		
		BangLangCardResponse bangLangCardResponse = RetryUtil.retryPayOrder(payOrder, retryTimes, 
				() -> restTemplate.postForObject(bangLangUrl, request, BangLangCardResponse.class), "调用BangLang越南支付接口失败");
		
		if(Objects.isNull(bangLangCardResponse)) {
			logger.error("BangLang越南支付失败，响应信息为空");
			payOrder.setErrorInfo("BangLang越南支付失败，响应信息为空");
			return CodeMsg.failure(CodeMsgType.ERR_SYS);
		}
		logger.info("BangLang response: " + bangLangCardResponse);
		
		if(bangLangCardResponse.getStatus() == 0) {
			payOrder.setThird_order_num(bangLangCardResponse.getTran_id());
			return CodeMsg.success(CodeMsgType.SUCCESS);
		}else {
			logger.error("BangLang越南支付失败，错误码："+bangLangCardResponse.getStatus()+"，错误信息："+bangLangCardResponse.getMessage());
			payOrder.setErrorInfo("BangLang越南支付失败，错误码："+bangLangCardResponse.getStatus()+"，错误信息："+bangLangCardResponse.getMessage());
			CodeMsgType response = getCodeMsgType(bangLangCardResponse.getStatus());
			if(response != null) {
				return CodeMsg.failure(response);
			}
			return CodeMsg.failure(bangLangCardResponse.getStatus(),bangLangCardResponse.getMessage());
		}
		
	}
	/**
	 * 計算MD5
	 * @param api_key
	 * @param card_amount
	 * @param card_code
	 * @param card_seri
	 * @return
	 */
	private static String computeMD5(String api_key,int card_amount,String card_code,String card_seri) {
		String content = api_key + card_amount + card_code + card_seri;
		return DigestUtils.md5DigestAsHex(content.getBytes());
	}
	
	public CodeMsgType getCodeMsgType(int status) {
		return responseMap.get(status);
	}
	
	static {
		responseMap.put(0,CodeMsgType.SUCCESS);
		responseMap.put(-1,CodeMsgType.ERR_REQUEST); //(缺少参数 - 未知错误)
		responseMap.put(-2,CodeMsgType.ERR_CARD);// (状态错误, 例如，卡密为13-15个字)
		responseMap.put(-3,CodeMsgType.ERR_REQUEST);//(符号错误)
		responseMap.put(-10,CodeMsgType.ERR_CORE_SYS);// (系统API维护)
		responseMap.put(-5,CodeMsgType.ERR_CORE_SYS);//(卡未充值, 重新检查卡 - (系统正忙))
		responseMap.put(-6,CodeMsgType.ERR_CORE_SYS);// USER 卡已满暂无法运行
		responseMap.put(-98,CodeMsgType.ERR_CARD);//(卡已重复使用)
		
		bangLangCardType.put(VietnamCardProvider.viettel, VietnamCardProvider.BangLang_VTT);
		bangLangCardType.put(VietnamCardProvider.mobifone, VietnamCardProvider.BangLang_VMS);
		bangLangCardType.put(VietnamCardProvider.vinaphone, VietnamCardProvider.BangLang_VNP);
	}
}
