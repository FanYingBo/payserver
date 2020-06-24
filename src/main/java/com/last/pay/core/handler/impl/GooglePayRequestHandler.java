package com.last.pay.core.handler.impl;

import java.util.List;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.last.pay.base.CodeMsg;
import com.last.pay.base.CodeMsgType;
import com.last.pay.base.common.constants.Constants;
import com.last.pay.base.common.constants.Constants.CurrencyConstants;
import com.last.pay.base.common.constants.Constants.PayStatusConstants;
import com.last.pay.base.common.constants.Constants.PayTypeConstants;
import com.last.pay.core.db.pojo.web.PayOrder;
import com.last.pay.core.db.pojo.web.ReplacementOrder;
import com.last.pay.core.dto.google.response.PurchaseInfoRet;
import com.last.pay.core.handler.GeneralPayRequestHandler;
import com.last.pay.core.handler.common.PayRequestChecker;
import com.last.pay.core.component.PayConfigManager;
import com.last.pay.core.component.sys.SystemConfiguration;
import com.last.pay.core.component.third.GoogleAuth2Configuration;
import com.last.pay.core.dto.google.request.PurchaseInfo;
import com.last.pay.core.service.IPayOrderQueryService;
import com.last.pay.util.RetryUtil;
import com.last.pay.util.SerializableUtil;

@Component
public class GooglePayRequestHandler extends GeneralPayRequestHandler{

	private static Log logger = LogFactory.getLog(GooglePayRequestHandler.class);
	@Autowired
	private GoogleAuth2Configuration googleAuth2Configuration;
	
	@Autowired
	private GoogleCredential googleCredential;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Value("${pay.interface.retry.count}")
	private Integer reTry;
	
	@Autowired
	private SystemConfiguration systemConfiguration;
	@Autowired
	private PayConfigManager payConfigManager;
	@Autowired
	private IPayOrderQueryService payOrderQueryService;
	@Autowired
	private PayRequestChecker payRequestChecker;
	
	@Override
	public String getType() {
		return String.valueOf(PayTypeConstants.GOOGLE);
	}

	@Override
	public boolean isBackEnd() {
		return Boolean.TRUE;
	}

	@Override
	public boolean isNeedCallBack() {
		return Boolean.FALSE;
	}

	@Override
	public CodeMsg<?> doProcessRequest(PayOrder payOrder, HttpServletRequest request) {
		String purchaseToken = "";
		String productId = "";
		String packageName = "";
		String orderId = "";
		if(Objects.isNull(request)) {
			if(Objects.nonNull(payOrder)) {
				PurchaseInfo purchaseInfo = payOrder.getGooglePurchaseInfo();
				if(Objects.nonNull(purchaseInfo)) {
					purchaseToken = purchaseInfo.getPurchaseToken();
					productId = purchaseInfo.getProductId();
					packageName = purchaseInfo.getPackageName();
					orderId = purchaseInfo.getOrderId();
					logger.info("Redis移动端回調， purchaseInfo："+purchaseInfo);
				}else {
					logger.error("移动端 purchaseInfo为空");
					return CodeMsg.failure(CodeMsgType.ERR_GOOGLE_PAY_PARAM_PURCHASE);
				}
			}else {
				logger.error("订单参数错误："+payOrder);
				return CodeMsg.failure(CodeMsgType.ERR_GOOGLE_PAY_PARAM_PURCHASE);
			}
		} else {
			String purchaseInfoJson = request.getParameter("purchaseInfo");
			if(StringUtils.isBlank(purchaseInfoJson)) {
				logger.error("移动端 purchaseInfo为空");
				return CodeMsg.failure(CodeMsgType.ERR_GOOGLE_PAY_PARAM_PURCHASE);
			}
			logger.info("移动端 purchaseInfo: "+purchaseInfoJson);
			JSONObject jsonObject = JSON.parseObject(purchaseInfoJson);
			purchaseToken = jsonObject.getString("purchaseToken");
			productId = jsonObject.getString("productId");
			packageName = jsonObject.getString("packageName");
			orderId = jsonObject.getString("orderId");
		}
		if(StringUtils.isBlank(purchaseToken) || StringUtils.isBlank(productId) || StringUtils.isBlank(packageName)) {
			payOrder.setThird_order_num(orderId);
			payOrder.setErrorInfo("Google支付的参数【purchaseInfo】不合法");
			return CodeMsg.failure(CodeMsgType.ERR_GOOGLE_PAY_PARAM_PURCHASE);
		}
		payRequestChecker.checkPurchasePointName(payOrder, productId);
		payOrder.setErrorThirdNum(orderId);
		payOrder.setRealCurrency(CurrencyConstants.USA);
		Object[] existMoney = payConfigManager.getPayPointExistMoney(payOrder.getPointName());
		if(Float.valueOf(existMoney[1].toString()) > 0) {
			payOrder.setRealCurrency(existMoney[0].toString());
			payOrder.setRealMoney(Float.valueOf(existMoney[1].toString()));
		} else {
			logger.error("Google支付付费点【"+ payOrder.getPointName() +"】金额未配置，请检查数据库付费点配置");
			return CodeMsg.failure(CodeMsgType.ERR_ORDER_MONEY);
		}
		if(Constants.ENV_TEST == systemConfiguration.getSystemEnv()) {
			payOrder.setThird_order_num("GPA.3327-4475-7531-85809");
			payOrder.setStatus(1);
			return CodeMsg.success(CodeMsgType.SUCCESS);
		} 
		PurchaseInfoRet purchaseInfo = null;
		CodeMsg<String> accessCode = generateAccessToken(payOrder);
		if(accessCode.getCode() == CodeMsgType.SUCCESS.getCode()) {
			CodeMsg<PurchaseInfoRet> invokGoogleApiQueryResult = invokGoogleApiQuery(packageName, productId, purchaseToken, accessCode.getData(), payOrder);
			if(invokGoogleApiQueryResult.getCode() == CodeMsgType.SUCCESS.getCode()) {
				purchaseInfo = invokGoogleApiQueryResult.getData();
				if(Objects.nonNull(purchaseInfo)) {
					payOrder.setThird_order_num(purchaseInfo.getOrderId());
					logger.info("Google Pay 订单查询成功，purchaseInfo: "+purchaseInfo);
					if( purchaseInfo.getPurchaseState() == CodeMsgType.SUCCESS.getCode()) {
						payOrder.setStatus(PayStatusConstants.SUCCESS_ORDER);
						return CodeMsg.success(CodeMsgType.SUCCESS);
					} else {
						payOrder.setStatus(PayStatusConstants.FAILURE_ORDER);
						payOrder.setErrorInfo("Google Pay 订单为失败订单 ");
						return CodeMsg.failure(CodeMsgType.ERR_GOOGLE_PAY_FAILURE);
					}
				}else {
					payOrder.setStatus(PayStatusConstants.FAILURE_ORDER);
					payOrder.setErrorInfo("Google Pay 订单查询失败，失败信息， "+invokGoogleApiQueryResult.getMsg());
					return invokGoogleApiQueryResult;
				}
			}else {
				logger.error("Google Pay 订单查询失败，查询结果信息为空 ");
				payOrder.setErrorInfo("Google Pay 订单查询失败，查询结果信息为空 ");
				return CodeMsg.failure(CodeMsgType.ERR_GOOGLE_PAY_API);
			}
		}else {
			logger.error("生成access_token失败，access_token 为 null");
			payOrder.setErrorInfo("生成access_token失败，access_token 为 null");
			return CodeMsg.failure(CodeMsgType.ERR_GOOGLE_ACCESS_TOKEY);	
		}
	}
	/**
	 *	 调用google api
	 * @param packageName
	 * @param productId
	 * @param purchaseToken
	 * @param accessToken
	 * @param payOrder
	 * @return
	 */
	private CodeMsg<PurchaseInfoRet> invokGoogleApiQuery(String packageName,String productId, String purchaseToken, String accessToken,PayOrder payOrder){
		String verifyUrl = googleAuth2Configuration.getVerifyUrl();
		String url = buildUrl(verifyUrl,packageName,productId,purchaseToken,accessToken);
		int reTryCount = reTry.intValue();
		PurchaseInfoRet purchaseInfo = null;
		if(Objects.nonNull(accessToken)) {
			logger.info("Google purchase verify url: "+url);
			purchaseInfo = RetryUtil.retryPayOrder(payOrder, reTryCount,()-> restTemplate.getForObject(url, PurchaseInfoRet.class), "Google Pay API 调用失败");
			if(Objects.nonNull(purchaseInfo)) {
				return CodeMsg.common(CodeMsgType.SUCCESS,purchaseInfo);
			}else {
				return CodeMsg.failure(CodeMsgType.ERR_GOOGLE_PAY_API);
			}
		} else {
			return CodeMsg.failure(CodeMsgType.ERR_GOOGLE_ACCESS_TOKEY);
		}
		
	}

	/**
	 * 生成access_token
	 * @param payOrder
	 * @return
	 */
	private CodeMsg<String> generateAccessToken(PayOrder payOrder) {
		int reTryCount = reTry.intValue();
		String accessTokey = RetryUtil.retryPayOrder(payOrder, reTryCount, ()->{
			return getGoogleAccessTokey();
		}, "Google生成access_token失败");
		if(Objects.isNull(accessTokey)) {
			return CodeMsg.common(CodeMsgType.ERR_GOOGLE_ACCESS_TOKEY);
		}
		logger.info("Google生成access_token成功，"+accessTokey);
		return CodeMsg.common(CodeMsgType.SUCCESS.getCode(),"生成access_token成功", accessTokey);
	}
	
	private String getGoogleAccessTokey(){
		try {
			googleCredential.refreshToken();
			return googleCredential.getAccessToken();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private String buildUrl(String verifyUrl,String packageName,String productId,String token,String accessToken) {
		StringBuilder url = new StringBuilder(verifyUrl);
		if(!verifyUrl.endsWith("/")) {
			url.append("/");
		}
		url.append("applications/").append(packageName)
				.append("/purchases/")
				.append("products/")
				.append(productId)
				.append("/tokens/")
				.append(token)
				.append("?access_token=")
				.append(accessToken);
		return url.toString();
	}
	
	@Override
	public boolean confirmIfExistPayOrder(PayOrder payOrder) {
		PurchaseInfo purchaseInfo = SerializableUtil.jsonStrToObject(payOrder.getPurchaseInfo(), PurchaseInfo.class);
		purchaseInfo.setOriginalJson(payOrder.getPurchaseInfo());
		payOrder.setGooglePurchaseInfo(purchaseInfo);
		if(StringUtils.isNotBlank(purchaseInfo.getOrderId())) {
			List<PayOrder> payOrderByThirdNum = payOrderQueryService.getPayOrderByThirdNum(purchaseInfo.getOrderId());
			if(Objects.nonNull(payOrderByThirdNum) && payOrderByThirdNum.size() > 0) {
				logger.error("订单已被处理，Google订单号："+ purchaseInfo.getOrderId()+"，已经成功购买过该付费点");
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}

	@Override
	public boolean isSuccessPayOrder(ReplacementOrder replacementOrder) {
		return Boolean.FALSE;
	}

	@Override
	public boolean confirmIfExistSuccessPayOrder(PayOrder payOrder, HttpServletRequest httpServletRequest) {
		// TODO Auto-generated method stub
		return false;
	}

}
