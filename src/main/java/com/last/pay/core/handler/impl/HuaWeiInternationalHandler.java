package com.last.pay.core.handler.impl;

import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;
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
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;
import com.last.pay.base.CodeMsg;
import com.last.pay.base.CodeMsgType;
import com.last.pay.base.common.constants.Constants.CurrencyConstants;
import com.last.pay.base.common.constants.Constants.PayTypeConstants;
import com.last.pay.core.db.pojo.web.PayOrder;
import com.last.pay.core.db.pojo.web.ReplacementOrder;
import com.last.pay.core.dto.huawei.request.HuaWeiPurchaseInfoRedis;
import com.last.pay.core.dto.huawei.response.HuaWeiPurchaseInfoRet;
import com.last.pay.core.dto.huawei.response.HuaWeiPurchaseTokenData;
import com.last.pay.core.handler.GeneralPayRequestHandler;
import com.last.pay.core.handler.common.HuaWeiRequestTool;
import com.last.pay.core.handler.common.PayRequestChecker;
import com.last.pay.core.component.PayConfigManager;
import com.last.pay.core.component.third.HuaWeiPayConfiguration;
import com.last.pay.core.service.IPayOrderQueryService;
import com.last.pay.util.EncryptUtil;
import com.last.pay.util.RetryUtil;
import com.last.pay.util.SerializableUtil;

@Component
public class HuaWeiInternationalHandler extends GeneralPayRequestHandler {
	
	private static final Log logger = LogFactory.getLog(HuaWeiInternationalHandler.class);
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private HuaWeiPayConfiguration huaWeiPayConfiguration;
	@Resource
	private PayConfigManager payConfigManager;
	@Autowired
	private PayRequestChecker payRequestChecker;
	
	@Value("${pay.interface.retry.count}")
	private Integer reTry;
	
	private RSAPublicKey pubKey;
	@Autowired
	private HuaWeiRequestTool huaWeiRequestTool;
	@Autowired
	private IPayOrderQueryService payOrderQueryService;
	@Override
	public CodeMsg<?> doProcessRequest(PayOrder payOrder, HttpServletRequest request) {
		HuaWeiPurchaseInfoRedis huaWeiPurchaseInfo = payOrder.getHuaWeiPurchaseInfo();
		String orderId = "";
		String purchaseToken = "";
		String productId = "";
		if(Objects.nonNull(request)) {
			String purchaseInfo = request.getParameter("purchaseInfo");
			if(StringUtils.isNoneBlank(purchaseInfo)) {
				JSONObject purchaseInfoJob = JSONObject.parseObject(purchaseInfo);
				orderId = purchaseInfoJob.getString("orderId");
				purchaseToken = purchaseInfoJob.getString("purchaseToken");
				productId = purchaseInfoJob.getString("productId");
			}else {
				return CodeMsg.failure(CodeMsgType.ERR_HUAWEI_PRODUCTID);
			}
		}else {
			orderId = huaWeiPurchaseInfo.getOrderId();
			purchaseToken = huaWeiPurchaseInfo.getPurchaseToken();
			productId = huaWeiPurchaseInfo.getProductId();
		}
		payOrder.setThird_order_num(orderId == null ? "":orderId);
		payOrder.setRealCurrency(CurrencyConstants.USA);
		if(StringUtils.isBlank(productId)) {
			return CodeMsg.failure(CodeMsgType.ERR_HUAWEI_PRODUCTID);
		}
		if(StringUtils.isBlank(purchaseToken)) {
			return CodeMsg.failure(CodeMsgType.ERR_HUAWEI_PURCHASETOKEN);
		}
		
		payRequestChecker.checkPurchasePointName(payOrder, productId);
		
		return doprocessRequest0(payOrder, orderId, productId, purchaseToken);
	}
	
	private CodeMsg<?> doprocessRequest0(PayOrder payOrder,String orderId, String productId,String purchaseToken){
		
		float pointMoney = payConfigManager.getPayPointMoney(payOrder.getPointName(), payOrder.getRealCurrency());
		if(pointMoney > 0) {
			payOrder.setRealMoney(pointMoney);
		} else {
			logger.error("HuaWei支付付费点【"+ payOrder.getPointName() +"】【USA】金额未配置，请检查数据库付费点配置");
			return CodeMsg.failure(CodeMsgType.ERR_ORDER_MONEY);
		}
		
		String url = huaWeiPayConfiguration.getChinaUrl();
		HttpHeaders headers = new HttpHeaders();
		CodeMsg<String> authHeadRet = huaWeiRequestTool.getAuthHead(huaWeiPayConfiguration.getInternationalAuthUrl(),huaWeiPayConfiguration.getInternationalSecretKey(),huaWeiPayConfiguration.getInternationalClientId());
		if(authHeadRet.getCode() == CodeMsgType.SUCCESS.getCode()) {
			headers.set("Authorization", authHeadRet.getData());
		} else {
			return authHeadRet;
		}
		
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("productId", productId);
		jsonObject.put("purchaseToken", purchaseToken);
		
		HttpEntity<String> httpEntity = new HttpEntity<>(jsonObject.toJSONString(),headers);
		int reTryCount = reTry.intValue();
		// 调用
		HuaWeiPurchaseInfoRet huaWeiPurchaseInfoRet = RetryUtil.retryPayOrder(payOrder, reTryCount, 
				() -> restTemplate.postForObject(url, httpEntity, HuaWeiPurchaseInfoRet.class), "调用HuaWei新加坡支付验证接口失败");
		
		if(Objects.nonNull(huaWeiPurchaseInfoRet)) {
			if(huaWeiPurchaseInfoRet.getResponseCode() == CodeMsgType.SUCCESS.getCode()) {
				String jsonPurchaseInfoRet = huaWeiPurchaseInfoRet.getPurchaseTokenData();
				logger.info("调用HuaWei新加坡支付验证接口成功，响应的购买信息："+jsonPurchaseInfoRet);
				logger.info("调用HuaWei新加坡支付验证接口成功，响应的签名信息："+huaWeiPurchaseInfoRet.getDataSignature());
				logger.info("调用HuaWei新加坡支付验证接口成功，公钥信息："+huaWeiPayConfiguration.getInternationalPublicKey());
				boolean verifySignData = Boolean.FALSE;
				try {
					if(Objects.isNull(pubKey)) {
						pubKey = EncryptUtil.getRSAPublicKey(huaWeiPayConfiguration.getInternationalPublicKey());
					}
					verifySignData = EncryptUtil.verifyBase64SignData(jsonPurchaseInfoRet, huaWeiPurchaseInfoRet.getDataSignature(), pubKey);
				} catch (Exception e) {
					logger.error("HuaWei新加坡支付订单【"+payOrder.getThird_order_num()+"】签名验证失败，" + e.getMessage());
					payOrder.setErrorInfo("HuaWei新加坡支付订单【"+payOrder.getThird_order_num()+"】签名验证失败");
					return CodeMsg.failure(CodeMsgType.ERR_HUAWEI_SIGNATURE);
				}
//				if(verifySignature(jsonPurchaseInfoRet, huaWeiPurchaseInfoRet.getDataSignature(), huaWeiPayConfiguration.getPublicKey())) {
				if(verifySignData) {
					HuaWeiPurchaseTokenData jsonStrToObject = SerializableUtil.jsonStrToObject(jsonPurchaseInfoRet, HuaWeiPurchaseTokenData.class);
					String retOrderId = jsonStrToObject.getOrderId();
					if(!StringUtils.isBlank(payOrder.getThird_order_num())) {
						if(!payOrder.getThird_order_num().equals(retOrderId)) {
							logger.warn("客户端返回HuaWei的新加坡支付单号与验证返回订单中的支付单号不匹配");
						}
					} else {
						logger.warn("客户端返回HuaWei的新加坡支付单号为空");
					}
					payOrder.setThird_order_num(retOrderId);
					if(jsonStrToObject.getPurchaseState() == CodeMsgType.SUCCESS.getCode()) {
						return CodeMsg.success(CodeMsgType.SUCCESS);
					} else {
						payOrder.setErrorThirdNum(retOrderId);
						if(jsonStrToObject.getPurchaseState() == 1) {
							logger.error("HuaWei新加坡订单【"+retOrderId+"】已经被取消，无法完成支付");
							payOrder.setErrorInfo(CodeMsgType.ERR_HUAWEI_PURCHASE_CANCEL.getMsg());
							return CodeMsg.failure(CodeMsgType.ERR_HUAWEI_PURCHASE_CANCEL);
						}else if(jsonStrToObject.getPurchaseState() == 2) {
							logger.error("HuaWei新加坡订单【"+retOrderId+"】已经退款，无法完成支付");
							payOrder.setErrorInfo(CodeMsgType.ERR_HUAWEI_PURCHASE_REFUND.getMsg());
							return CodeMsg.failure(CodeMsgType.ERR_HUAWEI_PURCHASE_REFUND);
						}
						logger.error("HuaWei新加坡订单【"+retOrderId+"】查询状态失败，系统异常，无法完成支付");
						payOrder.setErrorInfo("HuaWei新加坡订单【"+retOrderId+"】查询状态失败，系统异常，无法完成支付");
						return CodeMsg.failure(CodeMsgType.ERR_SYS);
					}
				} else {
					logger.error("HuaWei新加坡支付订单【"+payOrder.getThird_order_num()+"】签名验证失败");
					payOrder.setErrorInfo("HuaWei新加坡支付订单【"+payOrder.getThird_order_num()+"】签名验证失败");
					return CodeMsg.failure(CodeMsgType.ERR_HUAWEI_SIGNATURE);
				}
			} else {
				CodeMsgType codeMsgType = huaWeiRequestTool.getCodeMsgType(huaWeiPurchaseInfoRet.getResponseCode());
				if(Objects.nonNull(codeMsgType)) {
					logger.error("HuaWei新加坡订单【"+payOrder.getThird_order_num()+"】支付失败,"+huaWeiPurchaseInfoRet.getResponseMessage());
					payOrder.setErrorInfo("HuaWei订单【"+payOrder.getThird_order_num()+"】支付失败,"+huaWeiPurchaseInfoRet.getResponseMessage());
					return CodeMsg.failure(codeMsgType);
				}
				logger.error("HuaWei新加坡支付订单【"+payOrder.getThird_order_num()+"】支付失败，错误码："+huaWeiPurchaseInfoRet.getResponseCode());
				payOrder.setErrorInfo("HuaWei新加坡订单【"+payOrder.getThird_order_num()+"】支付失败");
				return CodeMsg.failure(huaWeiPurchaseInfoRet.getResponseCode(),huaWeiPurchaseInfoRet.getResponseMessage());
			}
		}else {
			logger.error("调用HuaWei新加坡支付验证【"+payOrder.getThird_order_num()+"】接口失败，响应信息为空");
			payOrder.setErrorInfo("调用HuaWei新加坡支付验证接口失败，响应信息为空，【"+payOrder.getThird_order_num()+"】支付失败");
			return CodeMsg.failure(CodeMsgType.ERR_HUAWEI_RESPONSE);
		}
		
	}
	@Override
	public boolean confirmIfExistPayOrder(PayOrder payOrder) {
		HuaWeiPurchaseInfoRedis huaWeiPurchaseInfo = SerializableUtil.jsonStrToObject(payOrder.getPurchaseInfo(),HuaWeiPurchaseInfoRedis.class);
		huaWeiPurchaseInfo.setOriginalJson(payOrder.getPurchaseInfo());
		payOrder.setHuaWeiPurchaseInfo(huaWeiPurchaseInfo);
		if(StringUtils.isNotBlank(huaWeiPurchaseInfo.getOrderId())) {
			List<PayOrder> payOrderByThirdNum = payOrderQueryService.getPayOrderByThirdNum(huaWeiPurchaseInfo.getOrderId());
			if(Objects.nonNull(payOrderByThirdNum) && payOrderByThirdNum.size() > 0) {
				logger.error("订单已被处理，HuaWei订单号："+ huaWeiPurchaseInfo.getOrderId()+"，已经成功购买过该付费点");
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
	public String getType() {
		// TODO Auto-generated method stub
		return String.valueOf(PayTypeConstants.HUAWEI_INTERNATIONAL);
	}

	@Override
	public boolean isBackEnd() {
		// TODO Auto-generated method stub
		return Boolean.TRUE;
	}

	@Override
	public boolean isNeedCallBack() {
		// TODO Auto-generated method stub
		return Boolean.FALSE;
	}

	@Override
	public boolean confirmIfExistSuccessPayOrder(PayOrder payOrder, HttpServletRequest httpServletRequest) {
		// TODO Auto-generated method stub
		return false;
	}

	
}
