package com.last.pay.core.handler.impl;

import java.util.Collections;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;
import com.last.pay.base.CodeMsg;
import com.last.pay.base.CodeMsgType;
import com.last.pay.base.common.constants.Constants.CurrencyConstants;
import com.last.pay.base.common.constants.Constants.PayChannelConstants;
import com.last.pay.base.common.constants.VietnamCardConstants.VietnamCardPrintAmount;
import com.last.pay.core.db.pojo.web.PayOrder;
import com.last.pay.core.db.pojo.web.ReplacementOrder;
import com.last.pay.core.dto.vietnam.old.request.VietnamRequestParams;
import com.last.pay.core.dto.vietnam.old.response.VietnamResponseParams;
import com.last.pay.core.handler.GeneralPayRequestHandler;
import com.last.pay.core.handler.common.PayRequestChecker;
import com.last.pay.core.component.PayConfigManager;
import com.last.pay.core.component.RequestResponseFactory;
import com.last.pay.core.component.third.VietnamPayConfiguration;
import com.last.pay.core.handler.channel.vietnam.OriginPayChannel;
import com.last.pay.core.handler.channel.vietnam.PayChannelDelegate;
import com.last.pay.util.MatchUtils;
import com.last.pay.util.RetryUtil;
import com.last.pay.util.SerializableUtil;
/**
 * 需注意点：
 *   查询交易时返回的卡面金额可能为0
 * @author Administrator
 *
 */
@Component("vietnamPayRequetHandler")
public class VietnamPayRequetHandler extends  GeneralPayRequestHandler{
	
	private static final Log logger = LogFactory.getLog(VietnamPayRequetHandler.class);
	@Autowired
	private OriginPayChannel originPayChannel;
	@Autowired
	private RequestResponseFactory requestResponseFactory;
	@Autowired
	private VietnamPayConfiguration vietnamPayConfiguration;
	@Value("${pay.interface.retry.count}")
	private Integer reTry;
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private PayRequestChecker payRequestChecker;
	@Autowired
	private PayConfigManager payConfigManager;
	
	private String reg = "^[A-Za-z0-9]+$";

	private String payType = "3";
	@Autowired
	private PayChannelDelegate payChannelDelegate;
	
	@Override
	public CodeMsg<?> doProcessRequest(PayOrder payOrder, HttpServletRequest request) {
		payOrder.setRealCurrency(CurrencyConstants.Vietnam);
		if(Objects.isNull(request)) {
			logger.warn("请求参数错误，请求信息为空（非HTTP请求订单或是后台补单）");
			return CodeMsg.failure(CodeMsgType.SUCCESS);
		}
		String cardNum = request.getParameter("cardNum");
		String cardPin = request.getParameter("cardPin");
		String money = request.getParameter("money");
		String providerCode = request.getParameter("providerCode");
		if(StringUtils.isBlank(cardNum) || !cardNum.matches(reg)) {
			return CodeMsg.failure(CodeMsgType.ERR_CARD_NUM.getCode(),"卡號不合法字或者为空");
		}
		if(StringUtils.isBlank(cardPin) || !cardPin.matches(reg)) {
			return CodeMsg.failure(CodeMsgType.ERR_CARD_PIN.getCode(),"卡PIN不合法或者为空");
		}
		if(!MatchUtils.isNumberStr(money) || Float.valueOf(money).intValue() == 0) {
			float payPointMoney = payConfigManager.getPayPointMoney(payOrder.getPointName(), payOrder.getRealCurrency());
			if(payPointMoney > 0) {
				payOrder.setRealMoney(payPointMoney);
			} else {
				logger.error("越南点卡支付付费点【"+ payOrder.getPointName() +"】【VND】金额未配置，请检查数据库付费点配置");
				return CodeMsg.failure(CodeMsgType.ERR_ORDER_MONEY);
			}
		}else {
			payOrder.setRealMoney(Float.valueOf(money));
		}
		if(StringUtils.isBlank(providerCode) || !VietnamCardPrintAmount.cardPrintAmountMap.containsKey(providerCode)) {
			return CodeMsg.failure(CodeMsgType.ERR_PROVIDER);
		}
		if(Collections.binarySearch(VietnamCardPrintAmount.cardPrintAmountMap.get(providerCode), payOrder.getRealMoney().intValue()) < 0) {
			logger.error("越南点卡支付提供商【"+providerCode+"】不支持该面额【"+payOrder.getRealMoney()+"】");
			return CodeMsg.failure(CodeMsgType.ERR_CARD_AMOUNT);
		}
		long limitTimes = 0;
		if((limitTimes = payRequestChecker.checkCardLimitTimes(payOrder.getUserId())) > 0) {
			logger.warn("越南点卡卡号重复输入，玩家ID：" + payOrder.getUserId());
			return CodeMsg.common(CodeMsgType.ERR_CARD_REPEAT_INPUT,limitTimes);
		}
		StringBuilder sb = new StringBuilder("");
		sb.append("{\"cardNum\":")
			.append("\""+cardNum+"\",")
			.append("\"cardPin\":")
			.append("\""+cardPin+"\",")
			.append("\"providerCode\":")
			.append("\""+providerCode+"\",")
			.append("\"cardPrintAmount\":")
			.append("\""+payOrder.getRealMoney()+"\"}");
		payOrder.setNote(sb.toString());
		
		return payChannelDelegate.doProcessRequest(payOrder, cardNum, cardPin, providerCode);
	}

	/**
	 * 	查询交易
	 * @param VietnamRequestParamsStr
	 * @return
	 */
	private CodeMsg<VietnamResponseParams> doQueryRequest(VietnamRequestParams vietnamRequestParams){
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
	    int reTryCount = reTry.intValue();
	    VietnamResponseParams VietnamResponseParams = RetryUtil.retryPayOrder(null, reTryCount, ()-> restTemplate.postForObject(url,request,  VietnamResponseParams.class), "调用越南查询接口失败");
		if(Objects.nonNull(VietnamResponseParams)) {
			if(logger.isInfoEnabled()) {
		  	    logger.info("query response："+SerializableUtil.objectToJsonStr(VietnamResponseParams));
		  	}
			if(originPayChannel.getCodeMsgType(VietnamResponseParams.getStatus()) != null) {
				return CodeMsg.common(originPayChannel.getCodeMsgType(VietnamResponseParams.getStatus()),VietnamResponseParams);
			}else {
				return CodeMsg.common(Integer.parseInt(VietnamResponseParams.getStatus()),VietnamResponseParams.getMessage(),VietnamResponseParams);
			}
		}else {
			return CodeMsg.common(CodeMsgType.ERR_SYS);
		}
	}

	public String getType() {
		return payType;
	}
	
	@Override
	public boolean isBackEnd() {
		return Boolean.TRUE;
	}

	@Override
	public boolean isNeedCallBack() {
		int payChannel = payRequestChecker.getVietnamPaychannel();
		if(payChannel == PayChannelConstants.UPAY_CHANNEL 
				|| payChannel == PayChannelConstants.KING_CARD_CHANNEL
				|| payChannel == PayChannelConstants.BANG_LANG_CHANNEL) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	@Deprecated
	@Override
	public CodeMsg<?> queryRequest(ReplacementOrder replacementOrder) {
		try {
			JSONObject jsonObject = JSONObject.parseObject(replacementOrder.getErrorRemark());
			String cardNum = jsonObject.getString("cardNum");
			String cardPin = jsonObject.getString("cardPin");
			String providerCode = jsonObject.getString("providerCode");
			Float cardPrintAmount = jsonObject.getFloat("cardPrintAmount");
			VietnamRequestParams builderVietnamPay = requestResponseFactory.builderVietnamPay(replacementOrder.getThirdOrderNum(), cardPin, cardNum, providerCode, cardPrintAmount);
			CodeMsg<VietnamResponseParams> responseParams = doQueryRequest(builderVietnamPay);
			PayOrder payOrder = replacementOrder.builderPayOrderByReplacementOrder();
			if(responseParams.getCode() == CodeMsgType.SUCCESS.getCode()) {
				VietnamResponseParams responseParam= responseParams.getData();
				String cardAmount = responseParam.getQueryResult().getCardAmount();
				if(Integer.parseInt(cardAmount) != 0) {
					payOrder.setRealMoney(Float.valueOf(cardAmount));
				}else {
					payOrder.setErrorThirdNum(replacementOrder.getThirdOrderNum());
					payOrder.setErrorInfo("越南点卡查询失败,卡号:"+cardNum+"，卡密:"+cardPin+",提供商:"+providerCode+"，查找到交易的卡面金额为0");
					return CodeMsg.failure(CodeMsgType.ERR_QUERY_CARD_AMOUNT_IS_ZERO);
				}
				return CodeMsg.common(responseParams.getCode(),responseParams.getMsg(),payOrder);
			}else {
				return responseParams;
			}
		} catch (Exception e) {
			logger.error("越南支付记录的备注信息不合法,"+replacementOrder.getErrorRemark());
			return CodeMsg.failure(CodeMsgType.ERR_SYS);
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
	public boolean confirmIfExistSuccessPayOrder(PayOrder payOrder, HttpServletRequest httpServletRequest) {
		return Boolean.FALSE;
	}
	
}
