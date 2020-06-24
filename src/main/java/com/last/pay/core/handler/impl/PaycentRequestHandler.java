package com.last.pay.core.handler.impl;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bellotec.paycent.api.merchant.Pay;
import com.last.pay.base.CodeMsg;
import com.last.pay.base.CodeMsgType;
import com.last.pay.base.common.constants.CodaPayConstants;
import com.last.pay.base.common.constants.Constants.CurrencyConstants;
import com.last.pay.base.common.constants.Constants.LanguageConstants;
import com.last.pay.base.common.constants.Constants.PayStatusConstants;
import com.last.pay.base.vo.CodaPayMno;
import com.last.pay.core.db.pojo.web.PayOrder;
import com.last.pay.core.db.pojo.web.ReplacementOrder;
import com.last.pay.core.handler.GeneralPayRequestHandler;
import com.last.pay.core.handler.common.PayRequestChecker;
import com.last.pay.core.component.PayConfigManager;
import com.last.pay.core.component.third.PaycentConfiguraion;
import com.last.pay.util.HashUtils;

@Component
public class PaycentRequestHandler extends GeneralPayRequestHandler{
	
	private static final Log logger = LogFactory.getLog(PaycentRequestHandler.class);
	
	private static final String initUri = "aggregatePay_init.action";
	private static final String executeUri = "aggregatePay_execute.action";
	private static final String queryUri = "aggregatePay_queryOrder.action";
	private static final String webUri = "webpayment.action";
	
	private static final String notifyUri = "pay/paycentnotify";
	private static final String returnUri = "pay/paycentreturnpage";
	
	private String payType = "8";
	@Autowired
	private PaycentConfiguraion paycentConfiguraion;
	@Autowired
	private PayConfigManager payConfigManager;
	@Autowired
	private PayRequestChecker payRequestChecker;
	@Override
	public String getType() {
		return payType;
	}

	@Override
	public boolean isBackEnd() {
		return Boolean.FALSE;
	}

	@Override
	public boolean isNeedCallBack() {
		return Boolean.TRUE;
	}
	/**
	 * 	初始化 Paycent页面参数
	 * @param payOrder
	 * @return
	 */
	@Override
	public CodeMsg<?> initTnxId(PayOrder payOrder,String mnoId){
		payOrder.setRealCurrency(CurrencyConstants.Myanmar);
		if(StringUtils.isBlank(mnoId)) {
			logger.error("Paycent支付的运营商编号为空");
			return CodeMsg.failure(CodeMsgType.ERR_PAYCENT_PAYCENT_MNO);
		}
		CodeMsg<?> realMoneyLimit = payRequestChecker.checkRealMoneyLimit(payOrder, mnoId);
		if( realMoneyLimit.getCode() != CodeMsgType.SUCCESS.getCode()) {
			return realMoneyLimit;
		}
		payOrder.setStatus(PayStatusConstants.NEW_ORDER);
		payOrder.setThird_order_num("");
		float pointMoney = payConfigManager.getPayPointMoney(payOrder.getPointName(), payOrder.getRealCurrency());
		if(pointMoney > 0) {
			payOrder.setRealMoney(pointMoney);
		} else {
			logger.error("PayCent支付付费点【"+ payOrder.getPointName() +"】【BUK】金额未配置，请检查数据库付费点配置");
			return CodeMsg.failure(CodeMsgType.ERR_ORDER_MONEY);
		}
		
		String merchantno = paycentConfiguraion.getMerchant();
		String secret = paycentConfiguraion.getSecret();
			
		String returnUrl = paycentConfiguraion.getReturnUrl();
		String notifyUrl = paycentConfiguraion.getNotifyUrl();
		String prouctUrl = paycentConfiguraion.getProductUrl();
		
		if(StringUtils.isBlank(merchantno)) {
			logger.error("Paycent商户参数获取失败，请检查配置");
			return CodeMsg.failure(CodeMsgType.ERR_PAYCENT_MERCHANT);
		}
		if(StringUtils.isBlank(returnUrl)) {
			logger.error("Paycent商户返回URL获取失败，请检查配置");
			return CodeMsg.failure(CodeMsgType.ERR_PAYCENT_RETURN_URL);
		}
		if(StringUtils.isBlank(notifyUrl)) {
			logger.error("Paycent商户回调URL获取失败，请检查配置");
			return CodeMsg.failure(CodeMsgType.ERR_PAYCENT_NOTIFY_URL);
		}
		if(StringUtils.isBlank(prouctUrl)) {
			logger.error("Paycent商户回调prouctUrl获取失败，请检查配置");
			return CodeMsg.failure(CodeMsgType.ERR_PAYCENT_PAYCENT_SERVER);
		}
		if(!returnUrl.endsWith("/")) {
			returnUrl += "/";
		}
		if(!notifyUrl.endsWith("/")) {
			notifyUrl += "/";
		}
		if(!prouctUrl.endsWith("/")) {
			prouctUrl += "/";
		}
		notifyUrl += notifyUri;
		returnUrl += returnUri;
		String initUrl = prouctUrl + initUri;
		String executeUrl = prouctUrl + executeUri;
		String queryUrl = prouctUrl + queryUri;
		int realMoneys = (int)Math.ceil(payOrder.getRealMoney()) / 50 * 50;
		payOrder.setRealMoney(Float.valueOf(realMoneys));
		TreeMap<String,Object> dataMap = new TreeMap<String, Object>();
		dataMap.put(Pay.PARAM_AMOUNT, realMoneys);
		dataMap.put(Pay.PARAM_MERCHANT_NO, merchantno);
		dataMap.put(Pay.PARAM_NOTIFY_URL, notifyUrl);
		dataMap.put(Pay.PARAM_RETURN_URL, returnUrl);
		dataMap.put(Pay.PARAM_ORDER_NO, payOrder.getOrderNum());
		
		String payPointTitle = payConfigManager.getPayPointTitle(payOrder.getPointName(),payOrder.getRealCurrency(), Boolean.TRUE);
		dataMap.put(Pay.PARAM_PRODUCT_NAME, payPointTitle);
		dataMap.put("country", CurrencyConstants.MyanmarCountry_PaycentCode);
		dataMap.put("lang", LanguageConstants.MY);
		dataMap.put("initPayUrl",initUrl);
		dataMap.put("executePayUrl",executeUrl);
		dataMap.put("orderQueryUrl",queryUrl);
		dataMap.put("resourcePath",prouctUrl);
		CodaPayMno codaPayMno = getCodaPayMno(mnoId);
		if(Objects.isNull(codaPayMno)) {
			logger.error("****Paycent运营商信息不合法，"+mnoId);
			return CodeMsg.failure(CodeMsgType.ERR_PAYCENT_PARAM);
		}
		String mnoCode =  codaPayMno != null ? codaPayMno.getPayCentCode():"";
		dataMap.put(Pay.PARAM_FRPCODE, mnoCode);
		try {
			dataMap.put("initPaymentHmac", initPayMac(merchantno, mnoCode ,secret));
			dataMap.put("executePaymentHmac", executePayMac(merchantno, mnoCode, realMoneys, payPointTitle , notifyUrl, payOrder.getOrderNum(), returnUrl, secret));
			dataMap.put("inqueryPaymentHmac", initQueryMac(merchantno, payOrder.getOrderNum(), secret));
		}catch (Exception e) {
			logger.error("****Paycent初始化Mac信息失败");
			return CodeMsg.failure(CodeMsgType.ERR_PAYCENT_PARAM);
		}
		return CodeMsg.common(CodeMsgType.SUCCESS,dataMap);
	}
	/**
	 * "merchantNo=888200000000037"
	 * @param merchantno
	 * @param secret
	 * @return
	 */
	private String initPayMac(String merchantno,String frpCode,String secret){
		TreeMap<String, Object> treeMap = new TreeMap<>();
		treeMap.put(Pay.PARAM_MERCHANT_NO, merchantno);
		treeMap.put(Pay.PARAM_FRPCODE, frpCode);
		return computeMac(treeMap,secret);
	}
	/**
	 * amount=100&merchantNo=888200000000037&notifyUrl=http://merchant.io/notifyUrl&orderNo=app12345&productName=GameDiamond&returnUrl=http://merchant.io/returnUrl
	 * amount=100&frpCode=MM-DCB-TELENOR&merchantNo=888200000000037&notifyUrl=http://merchant.io/notifyUrl&orderNo=app12345&productName=GameDiamond&returnUrl=http://merchant.io/returnUrl
	 * @param paramMap
	 * @param secret
	 * @return
	 */
	private String executePayMac(String merchantno,String frpCode,int money,String productName,String notifyUrl, String orderNo,String returnUrl,String secret) {
		TreeMap<String, Object> treeMap = new TreeMap<>();
		treeMap.put(Pay.PARAM_MERCHANT_NO, merchantno);
		treeMap.put(Pay.PARAM_AMOUNT, money);
		treeMap.put(Pay.PARAM_PRODUCT_NAME, productName);
		treeMap.put(Pay.PARAM_NOTIFY_URL, notifyUrl);
		treeMap.put(Pay.PARAM_ORDER_NO, orderNo);
		treeMap.put(Pay.PARAM_RETURN_URL, returnUrl);
		treeMap.put(Pay.PARAM_FRPCODE, frpCode);
		return computeMac(treeMap, secret);
	}
	/**
	 * merchantNo=888200000000037&orderNo=app12345
	 * @param merchantno
	 * @param orderNo
	 * @param secret
	 * @return
	 */
	private String initQueryMac(String merchantno,String orderNo,String secret) {
		TreeMap<String, Object> treeMap = new TreeMap<>();
		treeMap.put(Pay.PARAM_MERCHANT_NO, merchantno);
		treeMap.put(Pay.PARAM_ORDER_NO, orderNo);
		return computeMac(treeMap, secret);
	}
	
	private String computeMac(TreeMap<String,Object> paramMap,String secret) {
		Iterator<Entry<String, Object>> iterator = paramMap.entrySet().iterator();
		StringBuffer sbuf = new StringBuffer();
		while(iterator.hasNext()) {
			Entry<String, Object> entry = iterator.next();
			if(!StringUtils.isBlank(entry.getValue().toString())) {
				sbuf.append("&").append(entry.getKey()).append("=").append(entry.getValue());
			}
		}
		String data = sbuf.substring(1)+secret;
		return HashUtils.getSHA256(data);
	}
	
	private CodaPayMno getCodaPayMno(String mnoId) {
		return CodaPayConstants.codaPayMnos.get(mnoId);
	}

	@Override
	public CodeMsg<?> doProcessRequest(PayOrder payOrder, HttpServletRequest request) {
		if(Objects.nonNull(payOrder) && payOrder.getStatus() == 0) {
			logger.info("Paycent页面支付请求处理中...订单号："+payOrder.getOrderNum());
			return CodeMsg.success(CodeMsgType.SUCCESS);
		}
		return CodeMsg.success(CodeMsgType.ERR_PAY_FAILED);
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
		// TODO Auto-generated method stub
		return Boolean.FALSE;
	}
}
