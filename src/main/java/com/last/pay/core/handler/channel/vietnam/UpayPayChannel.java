package com.last.pay.core.handler.channel.vietnam;

import java.util.Objects;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.last.pay.base.CodeMsg;
import com.last.pay.base.CodeMsgType;
import com.last.pay.base.common.constants.Constants;
import com.last.pay.base.common.constants.Constants.PayChannelConstants;
import com.last.pay.core.db.pojo.web.PayOrder;
import com.last.pay.core.dto.vietnam.upay.request.UPayVietnamReqeustParams;
import com.last.pay.core.dto.vietnam.upay.response.UPayVietnamResponseParam;
import com.last.pay.core.component.RequestResponseFactory;
import com.last.pay.core.component.sys.SystemConfiguration;
import com.last.pay.core.component.third.VietnamPayConfiguration;
import com.last.pay.util.RetryUtil;

@Component
public class UpayPayChannel implements PayChannelHandler{
	
	private static final Log logger = LogFactory.getLog(UpayPayChannel.class);
	
	@Autowired
	private VietnamPayConfiguration vietnamPayConfiguration;
	@Autowired
	private SystemConfiguration systemConfiguration;
	@Autowired
	private RequestResponseFactory requestResponseFactory;
	@Autowired
	private RestTemplate restTemplate;
	@Override
	public int getPayChannel() {
		return PayChannelConstants.UPAY_CHANNEL;
	}

	@Override
	public CodeMsg<?> doProcessRequest(PayOrder payOrder, String cardNum, String cardPin, String providerCode) {
		logger.info("越南点卡支付渠道为 -> uPay");
		if(systemConfiguration.getSystemEnv() == Constants.ENV_TEST) {
			payOrder.setThird_order_num(requestResponseFactory.buildRequestId());
	    	logger.info("錯誤碼："+CodeMsgType.SUCCESS.getCode()+"，錯誤信息："+CodeMsgType.SUCCESS.getMsg());
	    	return CodeMsg.common(CodeMsgType.SUCCESS);
		}
		return doProcessUpayRequest(payOrder, cardNum, cardPin, providerCode);
	}
	/**
	 *  upay 支付渠道
	 * @param payOrder
	 * @param cardNum
	 * @param cardPin
	 * @param money
	 * @param providerCode
	 * @return
	 */
	private CodeMsg<?> doProcessUpayRequest(PayOrder payOrder,String cardNum,String cardPin,String providerCode){
		
		UPayVietnamReqeustParams uPayVietnamParams = requestResponseFactory.builderUPayVietnamParams(payOrder.getOrderNum(), payOrder.getPointName(), cardNum, cardPin, providerCode, payOrder.getRealMoney());
		payOrder.setErrorThirdNum(payOrder.getOrderNum()); //uPay 充值错误没有订单号
		String url = vietnamPayConfiguration.getUPayUrl();
		String buildUrl = buildUrl(url,uPayVietnamParams);
		logger.info("UPay request:" + buildUrl);
		int reTryCount = systemConfiguration.getReTry();
		UPayVietnamResponseParam responseParam = RetryUtil.retryPayOrder(payOrder, reTryCount, ()-> restTemplate.getForObject(buildUrl, UPayVietnamResponseParam.class), "调用UPay越南支付接口失败");
    	if(Objects.nonNull(responseParam)) {
    		if(responseParam.getResult() == 200) { // 支付成功，待回调
    			payOrder.setRealMoney(Float.valueOf(responseParam.getPrice()/100));
    			payOrder.setThird_order_num(responseParam.getSerialNumber());
    			return CodeMsg.success(CodeMsgType.SUCCESS);
    		} else {
    			payOrder.setErrorInfo("UPay 支付失败,卡号:"+cardNum+",卡密:"+cardPin+",金额:"+payOrder.getRealMoney()+",错误码"+responseParam.getResult()+",错误信息："+responseParam.getError());
    			return CodeMsg.failure((int)responseParam.getResult(),responseParam.getError());
    		}
    	}else {
    		logger.error("UPay 支付失败,响应信息为空");
    		payOrder.setErrorInfo("UPay 支付失败,响应信息为空");
    		return CodeMsg.failure(CodeMsgType.ERR_TIMEOUT);
    	}
	}
	
	private String buildUrl(String url,UPayVietnamReqeustParams uPayVietnamParams) {
		StringBuffer sb = new StringBuffer();
		sb.append(url);
		sb.append("?appKey=").append(uPayVietnamParams.getAppKey())
			.append("&cardNum=").append(uPayVietnamParams.getCardNum())
			.append("&uid=").append(uPayVietnamParams.getUid())
			.append("&cpOrderId=").append(uPayVietnamParams.getCpOrderId())
			.append("&chKey=").append(uPayVietnamParams.getChKey())
			.append("&cardId=").append(uPayVietnamParams.getCardId())
			.append("&vendor=").append(uPayVietnamParams.getVendor())
			.append("&equipmentType=").append(uPayVietnamParams.getEquipmentType())
			.append("&goodsKey=").append(uPayVietnamParams.getGoodsKey())
			.append("&extra=").append(uPayVietnamParams.getExtra())
			.append("&cardPrintAmount=").append(uPayVietnamParams.getCardPrintAmount());
		return sb.toString();
	}
}
