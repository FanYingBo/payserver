package com.last.pay.core.component;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import com.last.pay.core.dto.vietnam.kingcard.request.KingCardRequestParam;
import com.last.pay.core.dto.vietnam.old.request.VietnamRequestParams;
import com.last.pay.core.dto.vietnam.old.response.VietnamResponseParams;
import com.last.pay.core.dto.vietnam.upay.request.UPayVietnamReqeustParams;
import com.last.pay.core.component.third.VietnamPayConfiguration;
import com.last.pay.util.SerializableUtil;

@Component
public class RequestResponseFactory {
	
	@Autowired
	private VietnamPayConfiguration vietnamPayConfiguration;

	/**
	 * 越南請求參數處理创建请求参数
	 * @param cardPin 卡PIN
	 * @param cardSerial 卡序列号
	 * @param providerCode 卡供应商代码
	 * @param cardPrintAmount 卡片面额
	 * @return
	 */
	public VietnamRequestParams builderVietnamPay(String requestId,String cardPin ,String cardSerial,@Nullable String providerCode,Float cardPrintAmount) {
		VietnamRequestParams VietnamRequestParams = new VietnamRequestParams();
		VietnamRequestParams.setPartnerId(vietnamPayConfiguration.getPartnerId());
		VietnamRequestParams.setPartner_username(vietnamPayConfiguration.getPartner_username());
		VietnamRequestParams.setCardPin(cardPin);
		VietnamRequestParams.setCardSerial(cardSerial);
		VietnamRequestParams.setProviderCode(providerCode);
		VietnamRequestParams.setCardPrintAmount(String.valueOf(cardPrintAmount));
		if(requestId == null) {
			requestId = buildRequestId();
		}
		VietnamRequestParams.setRequestId(requestId);
		return VietnamRequestParams;
	}
	/**
	 * 解析響應參數
	 * @param json
	 * @return
	 */
	public VietnamResponseParams parseVietnamPay(String json) {
		return SerializableUtil.jsonStrToObject(json, VietnamResponseParams.class);
	}
	/**
	 * 
	 * @return
	 */
	public String buildRequestId() {
		long currentTimeMillis = System.currentTimeMillis();
		String time = String.valueOf(currentTimeMillis);
		String stamps = time.substring(time.length()-5);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
		String format = simpleDateFormat.format(new Date());
		return vietnamPayConfiguration.getPartnerId()+"_"+format+stamps;
	}
	/**
	 * UPay越南請求參數處理创建请求参数
	 * @param orderNum 
	 * @param payPoint
	 * @param cardPin
	 * @param cardSerial
	 * @param providerCode
	 * @param cardPrintAmount
	 * @return
	 */
	public UPayVietnamReqeustParams builderUPayVietnamParams(String orderNum,String payPoint,String cardSerial,String cardPin,@Nullable String providerCode,Float cardPrintAmount) {
		UPayVietnamReqeustParams uPayVietnamReqeustParams = new UPayVietnamReqeustParams();
		uPayVietnamReqeustParams.setAppKey(vietnamPayConfiguration.getUPayAppKey());
		uPayVietnamReqeustParams.setCardId(cardSerial);
		uPayVietnamReqeustParams.setCardNum(cardPin);
		uPayVietnamReqeustParams.setCardPrintAmount(cardPrintAmount.intValue());
		uPayVietnamReqeustParams.setUid("300001515");
		uPayVietnamReqeustParams.setEquipmentType("0");
		uPayVietnamReqeustParams.setVendor("vietnam"+providerCode);
		uPayVietnamReqeustParams.setCpOrderId(orderNum);
		uPayVietnamReqeustParams.setChKey("default");
		uPayVietnamReqeustParams.setExtra("");
		uPayVietnamReqeustParams.setGoodsKey("GSK300");
		return uPayVietnamReqeustParams;
	}
	/**
	 * KingCard 请求参数
	 * @param orderNum 订单号
	 * @param cardSerial 卡序列号
	 * @param cardPin 卡PIN
	 * @param providerCode
	 * @param cardPrintAmount
	 * @return
	 */
	public KingCardRequestParam builderKingCardVietnamParams(String orderNum, String cardSerial,String cardPin,String providerCode,Float cardPrintAmount,String notifyUrl) {
		KingCardRequestParam kingCardRequestParam = new KingCardRequestParam();
		kingCardRequestParam.setAmount(cardPrintAmount.intValue());
		kingCardRequestParam.setSerial(cardSerial);
		kingCardRequestParam.setCode(cardPin);
		kingCardRequestParam.setTelco(providerCode);
		kingCardRequestParam.setWebhooks(notifyUrl);
		kingCardRequestParam.setMrc_order_id(orderNum);
		return kingCardRequestParam;
	}
	
	

}
