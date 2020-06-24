package com.last.pay.core.handler.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

import com.alibaba.fastjson.JSONObject;
import com.last.pay.base.CodeMsg;
import com.last.pay.base.CodeMsgType;
import com.last.pay.base.common.constants.Constants;
import com.last.pay.base.common.constants.Constants.CurrencyConstants;
import com.last.pay.base.common.constants.Constants.PayTypeConstants;
import com.last.pay.core.db.pojo.web.PayOrder;
import com.last.pay.core.db.pojo.web.ReplacementOrder;
import com.last.pay.core.dto.ios.response.IOSInAppParams;
import com.last.pay.core.dto.ios.response.IOSReceipt;
import com.last.pay.core.dto.ios.response.IOSReceiptParams;
import com.last.pay.core.handler.GeneralPayRequestHandler;
import com.last.pay.core.component.sys.SystemConfiguration;
import com.last.pay.core.component.third.IOSPayConfiguration;
import com.last.pay.util.RetryUtil;
import com.last.pay.util.SerializableUtil;

@Component
public class IOSPayRequestHandler extends GeneralPayRequestHandler{
	
	private static final Log logger = LogFactory.getLog(IOSPayRequestHandler.class);
	
	private static Map<Integer,CodeMsgType> responseMap = new HashMap<Integer,CodeMsgType>();

	@Autowired
	private IOSPayConfiguration iosPayConfiguration;
	@Autowired
	private SystemConfiguration systemConfiguration;
	@Autowired
	private RestTemplate restTemplate;
	@Value("${pay.interface.retry.count}")
	private Integer reTry;
	@Override
	public CodeMsg<?> doProcessRequest(PayOrder payOrder, HttpServletRequest request) {
		return CodeMsg.failure(CodeMsgType.ERR_SYS);
	}

	@Override
	public boolean confirmIfExistPayOrder(PayOrder payOrder) {
		// TODO Auto-generated method stub
		return Boolean.FALSE;
	}
	/**
	 * 由于IOS可能会产生大量订单，对其进行单独处理
	 * @param payOrder
	 * @return
	 */
	public CodeMsg<?> processMultiOrder(PayOrder payOrder){
		String receiptData = payOrder.getIosPurchaseInfo();
		String url = iosPayConfiguration.getProductUrl();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("receipt-data", receiptData);
		HttpEntity<String> httpEntity = new HttpEntity<String>(jsonObject.toJSONString(),headers);
		CodeMsg<IOSReceipt> iosReceiptCode = doProcess(url, httpEntity);
		if(iosReceiptCode.getCode() == CodeMsgType.SUCCESS.getCode()) {
			IOSReceipt iosReceipt = iosReceiptCode.getData();
			List<PayOrder> payOrders = new ArrayList<PayOrder>();
			if(Objects.nonNull(iosReceipt)) {
				int status = iosReceipt.getStatus();
				if(status == 21007 || "Sandbox".equals(iosReceipt.getEnvironment())) {  // 沙箱环境订单发送到生产环境
					logger.info("######## IOS订单信息为沙箱环境订单 ########");
					url = iosPayConfiguration.getSandboxUrl();
					iosReceiptCode = doProcess(url, httpEntity);
					iosReceipt = iosReceiptCode.getData();
					if(Objects.nonNull(iosReceipt)) {
						return doVerify(payOrder, payOrders, iosReceipt);
					}else {
						logger.error("IOS获取到的凭据响应信息为空");
						return CodeMsg.failure(CodeMsgType.ERR_IOS_RECEIPT_EMPTY);
					}
				}else {
					logger.info("######## IOS订单信息为正式环境订单 ########");
					return doVerify(payOrder, payOrders, iosReceipt);
				}
			}else {
				logger.error("IOS获取到的凭据响应信息为空");
				return CodeMsg.failure(CodeMsgType.ERR_IOS_RECEIPT_EMPTY);
			}
		} else {
			return iosReceiptCode;
		}
		
	}
	/**
	 * 处理不同环境的订单
	 * @param url
	 * @param httpEntity
	 * @return
	 */
	public CodeMsg<IOSReceipt> doProcess(String url,HttpEntity<String> httpEntity) {
		IOSReceipt iosReceipt = null;
		try {
			int retyTimes = reTry.intValue();
			byte[] response = RetryUtil.retryPayOrder(null, retyTimes, () -> restTemplate.postForObject(url, httpEntity, byte[].class) , "IOS支付验证请求失败");
			if(Objects.nonNull(response)) {
				String jsonResponse = new String(response);
				logger.info("IOS支付验证响应信息: " + jsonResponse);
				iosReceipt = SerializableUtil.jsonStrToObject(jsonResponse, IOSReceipt.class);
				return CodeMsg.common(CodeMsgType.SUCCESS,iosReceipt);
			} 
			return CodeMsg.common(CodeMsgType.ERR_IOS_INTERFACE_QUERY,iosReceipt);
		} catch (Exception e) {
			logger.error("访问IOS订单查询接口失败，失败原因："+e.getMessage());
			return CodeMsg.failure(CodeMsgType.ERR_IOS_INTERFACE_QUERY);
		}
	}
	/**
	 * 解析订单
	 * @param iosReceipt
	 * @return
	 */
	public CodeMsg<?> doVerify(PayOrder payOrder,List<PayOrder> payOrders,IOSReceipt iosReceipt){
		int env = systemConfiguration.getSystemEnv();
		IOSReceiptParams iosReceiptParams = iosReceipt.getReceipt();
		if(Objects.nonNull(iosReceiptParams) && iosReceipt.getStatus() == CodeMsgType.SUCCESS.getCode()) {
			List<IOSInAppParams> in_app = iosReceiptParams.getIn_app();
			String application_version = iosReceiptParams.getApplication_version();
			String environment = iosReceipt.getEnvironment();
			if("Sandbox".equals(environment)) {
				if(env == Constants.ENV_PRODUCT) {
					logger.error("错误订单：IOS沙箱环境的订单发至了生产环境");
					return CodeMsg.failure(CodeMsgType.ERR_IOS_RECEIPT_SEND_SANDBOX);
				}
			}else {
				if(env == Constants.ENV_SANDBOX) {
					logger.error("错误订单：IOS生产环境的订单发至了测试环境环境");
					return CodeMsg.failure(CodeMsgType.ERR_IOS_RECEIPT_SEND_PRODUCT);
				}
			}
			String note = "{\"bundle_id\":\""+iosReceiptParams.getBundle_id()+"\",\"application_version\":\""+application_version+"\",\"environment\":\""+environment+"\"}";
			if(in_app.isEmpty()) {
				logger.error("IOS凭据应用内购买信息为空");
				return CodeMsg.failure(CodeMsgType.ERR_IOS_IN_APP_DATA);
			} else {
				in_app.stream().forEach(inAppParam -> {
					PayOrder payOrderInApp = new PayOrder();
					payOrderInApp.setNote(note);
					payOrderInApp.setChannel(payOrder.getChannel());
					payOrderInApp.setPlatform(payOrder.getPlatform());
					payOrderInApp.setIp(payOrder.getIp());
					payOrderInApp.setPayType(payOrder.getPayType());
					payOrderInApp.setRealCurrency(CurrencyConstants.USA);
					payOrderInApp.setUserId(payOrder.getUserId());
					payOrderInApp.setPointName(inAppParam.getProduct_id());
					payOrderInApp.setThird_order_num(inAppParam.getTransaction_id());
					payOrders.add(payOrderInApp);
				});
				payOrder.setPayOrders(payOrders);
				logger.info("IOS凭据所有的内购信息:"+SerializableUtil.objectToJsonStr(payOrders));
				return CodeMsg.success(CodeMsgType.SUCCESS);
			}
		}else {
			logger.error("IOS收到的凭据信息为空，或支付未成功");
			if(!responseMap.containsKey(iosReceipt.getStatus())) {
				return CodeMsg.failure(iosReceipt.getStatus(),"IOS支付出错了");
			}
			return CodeMsg.failure(responseMap.get(iosReceipt.getStatus()));
		}
	}
	@Override
	public boolean isSuccessPayOrder(ReplacementOrder replacementOrder) {
		return Boolean.FALSE;
	}

	@Override
	public String getType() {
		return String.valueOf(PayTypeConstants.IOS);
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
	public boolean confirmIfExistSuccessPayOrder(PayOrder payOrder, HttpServletRequest httpServletRequest) {
		// TODO Auto-generated method stub
		return false;
	}

}
