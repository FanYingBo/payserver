package com.last.pay.core.handler.common;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.last.pay.base.CodeMsg;
import com.last.pay.base.CodeMsgType;
import com.last.pay.base.common.constants.CodaPayConstants;
import com.last.pay.base.common.constants.PayParamConstants;
import com.last.pay.base.common.constants.Constants.PayChannelConstants;
import com.last.pay.base.common.constants.Constants.PayPlatformConstants;
import com.last.pay.base.vo.CodaPayMno;
import com.last.pay.cache.ConfigManager;
import com.last.pay.cache.PayCacheManager;
import com.last.pay.core.db.pojo.web.PayOrder;
import com.last.pay.core.component.PayConfigManager;
import com.last.pay.core.component.load.PayParamConfiguration;

@Component
public class PayRequestChecker {
	
	private static final Log logger = LogFactory.getLog(PayRequestChecker.class);
	
	private static int DEFAULT_LIMITTIME = 10;
	
	private static ThreadLocal<Integer> payChannelLocal = new ThreadLocal<>();
	@Autowired
	private PayCacheManager payCacheManager;
	@Autowired
	private ConfigManager configManager;
	@Autowired
	private PayParamConfiguration configLoadComponent;
	@Autowired
	private PayConfigManager payConfigManager;
	
	public long checkCardLimitTimes(int userId) {
		long remainTime = payCacheManager.remainTime(userId);
		if(remainTime < 0) {
			int limitTime = DEFAULT_LIMITTIME;
			try {
				String limit = configLoadComponent.getPayParam(PayParamConstants.pay_card_limit_time);
				limitTime = Integer.parseInt(limit);
			} catch (Exception e) {
				logger.error("解析 "+PayParamConstants.pay_card_limit_time+" 参数失败，"+e.getMessage());
			}
			payCacheManager.setVietnamCardNum(userId, "1", limitTime);
			return -1;
		}
		return remainTime;
	}
	
	
	/***
	 * 	 矫正支付渠道
	 * @param payOrder
	 * @return
	 */
	public int correctPayChannel(PayOrder payOrder) {
		int webChannel = 0;
		int inappChannel = 0;
		try {
			String paramValue = configManager.getParamValue(PayChannelConstants.pay_channel_param);
			JSONObject payChannelJson = JSONObject.parseObject(paramValue);
			webChannel = payChannelJson.getIntValue(PayChannelConstants.WEB_CHANNEL);
			inappChannel = payChannelJson.getIntValue(PayChannelConstants.INAPP_CHANNEL);
		}catch (Exception e) {
			logger.warn("获取pay_channel参数失败，请检查数据配置是否正确，将使用旧渠道完成支付");
			webChannel = PayChannelConstants.OLD_CHANNEL;
			inappChannel = PayChannelConstants.OLD_CHANNEL;
		}
		int payChannel = 0;
		if(payOrder.getPlatform() == PayPlatformConstants.WEB_OLD_CHANNEL) { // 网页支付
			payChannel = webChannel;
			logger.info("越南点卡支付方式为 -> web");
			if(payChannel == PayChannelConstants.UPAY_CHANNEL) {
				payOrder.setPlatform(PayPlatformConstants.WEB_UPAY);
			}else if(payChannel == PayChannelConstants.KING_CARD_CHANNEL) {
				payOrder.setPlatform(PayPlatformConstants.WEB_KING_CARD);
			}else if(payChannel == PayChannelConstants.BANG_LANG_CHANNEL){
				payOrder.setPlatform(PayPlatformConstants.WEB_BANG_LANG);
			}
		}else {// 游戏内支付
			payChannel = inappChannel;
			logger.info("越南点卡支付方式为 -> inapp");
			if(payChannel == PayChannelConstants.UPAY_CHANNEL) {
				payOrder.setPlatform(PayPlatformConstants.IN_GAEM_UPAY);
			}else if(payChannel == PayChannelConstants.KING_CARD_CHANNEL){
				payOrder.setPlatform(PayPlatformConstants.IN_GAME_KING_CARD);
			}else if(payChannel == PayChannelConstants.BANG_LANG_CHANNEL){
				payOrder.setPlatform(PayPlatformConstants.IN_GAME_BANG_LANG);
			}
		}
		setVitnamPayChannel(payChannel);
		return payChannel;
	}
	
	/**
	 * 通道名称	最低($)	最高($)
		MPT		0.19	25      103
Telenor Myanmar	0.03	31.25	101 
		Ooredoo	0.01	125		102 
		Mytel	0.03	31.25	104 
	Wave Money	0.01	125		
	 */
	public CodeMsg<?> checkRealMoneyLimit(PayOrder payOrder,String mnoId){
		CodaPayMno codaPayMno = getCodaPayMno(mnoId);
		if(Objects.isNull(codaPayMno)) {
			return CodeMsg.success(CodeMsgType.SUCCESS);
		}
		float usd = payConfigManager.getPointMoneyExchangeUSD(payOrder.getPointName());
		if(usd >= codaPayMno.getMixLimit() && usd <= codaPayMno.getMaxLimit()) {
			return CodeMsg.success(CodeMsgType.SUCCESS);
		}
		logger.error("付费点金额超出了所选电信运营商支付范围，无法支付，金额："+usd+"，运营商："+mnoId+"，范围："+codaPayMno.getMixLimit()+"~"+codaPayMno.getMaxLimit());
		return CodeMsg.failure(CodeMsgType.ERR_CODAPAY_MNO_LIMIT.getCode(),"金额超出此支付范围，无法支付！");
	}
	
	/**
	 *  获取运营商限额信息
	 * @param mnoId
	 * @return
	 */
	private CodaPayMno getCodaPayMno(String mnoId) {
		return CodaPayConstants.codaPayMnos.get(mnoId);
	}
	
	public void setVitnamPayChannel(int payChannel) {
		payChannelLocal.set(payChannel);
	}
	
	public int getVietnamPaychannel() {
		return payChannelLocal.get();
	}
	/**
	 *  检查付费点
	 *  {@link HuaWeiInternationalHandler}
	 *  {@link HuaWeiInlandHandler}
	 * @param payOrder
	 * @param newPointName
	 */
	public void checkPurchasePointName(PayOrder payOrder, String newPointName) {
		if(StringUtils.isBlank(payOrder.getPointName()) || !payOrder.getPointName().equals(newPointName)) {
			logger.warn("支付的付费点【"+newPointName+"】信息与订单号【"+payOrder.getOrderNum()+"】付费点【"+payOrder.getPointName()+"】信息不一致");
			payOrder.setUniformPointName(false);
			payOrder.setPointName(newPointName);
		}
	}
	
}
