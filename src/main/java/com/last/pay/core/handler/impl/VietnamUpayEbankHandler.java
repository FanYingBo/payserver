package com.last.pay.core.handler.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.last.pay.base.CodeMsg;
import com.last.pay.base.CodeMsgType;
import com.last.pay.base.common.constants.Constants.CurrencyConstants;
import com.last.pay.base.common.constants.Constants.PayTypeConstants;
import com.last.pay.core.db.pojo.web.PayOrder;
import com.last.pay.core.db.pojo.web.ReplacementOrder;
import com.last.pay.core.dto.vietnam.upay.response.UpayVietnamEbankResponse;
import com.last.pay.core.handler.GeneralPayRequestHandler;
import com.last.pay.core.component.PayConfigManager;
import com.last.pay.core.component.third.VietnamPayConfiguration;
import com.last.pay.util.RetryUtil;
import com.last.pay.util.SerializableUtil;

@Component
public class VietnamUpayEbankHandler extends GeneralPayRequestHandler {
	
	private static final Log logger = LogFactory.getLog(VietnamUpayEbankHandler.class);
	
	private static Map<String,String> responseMap = new HashMap<>();
	
	@Autowired
	private VietnamPayConfiguration vietnamPayConfiguration;
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private PayConfigManager payConfigManager;
	
	@Override
	public CodeMsg<?> doProcessRequest(PayOrder payOrder, HttpServletRequest request) {
		payOrder.setRealCurrency(CurrencyConstants.Vietnam);
		float payPointMoney = payConfigManager.getPayPointMoney(payOrder.getPointName(), payOrder.getRealCurrency());
		if(payPointMoney < 50000) {
			logger.error("越南UPay网银支付付费点【"+ payOrder.getPointName() +"】【VND】金额小于50k无法进行支付");
			return CodeMsg.failure(CodeMsgType.ERR_ORDER_MONEY);
		}
		if(payPointMoney > 0) {
			payOrder.setRealMoney(payPointMoney);
		} else {
			logger.error("越南UPay网银支付付费点【"+ payOrder.getPointName() +"】【VND】金额未配置，请检查数据库付费点配置");
			return CodeMsg.failure(CodeMsgType.ERR_ORDER_MONEY);
		}
		payOrder.setRealMoney(payPointMoney);
		String payPointTitle = payConfigManager.getPayPointTitle(payOrder.getPointName(), payOrder.getRealCurrency(), Boolean.FALSE);
		String url = builderUrl(payOrder.getOrderNum(), payOrder.getUserId(), payPointTitle,  payOrder.getRealMoney(), vietnamPayConfiguration.getUPayAppKey(), payOrder.getIp());
		logger.info("越南UPay Ebank request："+url);
		UpayVietnamEbankResponse upayVietnamCbankResponse = RetryUtil.retryPayOrder(payOrder, 
				payConfigManager.getRetryCount(), ()->restTemplate.getForObject(url, UpayVietnamEbankResponse.class), "越南UPay网银支付接口调用失败");
		
		if(Objects.isNull(upayVietnamCbankResponse)) {
			return CodeMsg.failure(CodeMsgType.ERR_CORE_SYS, "调用越南UPay网银支付失败，响应信息为空");
		}
		logger.info("越南UPay Ebank response："+SerializableUtil.objectToJsonStr(upayVietnamCbankResponse));
		if(!"00".equals(upayVietnamCbankResponse.getResponsecode())) {
			logger.error("越南UPay网银支付失败，错误码："+upayVietnamCbankResponse.getResponsecode());
			String responseMsg = getResponseMsg(upayVietnamCbankResponse.getResponsecode());
			if(responseMsg != null) {
				return CodeMsg.failure(Integer.parseInt(upayVietnamCbankResponse.getResponsecode()), responseMsg);
			}else {
				return CodeMsg.failure(Integer.parseInt(upayVietnamCbankResponse.getResponsecode()), upayVietnamCbankResponse.getDescriptionvn());
			}
		}
		payOrder.setThird_order_num(upayVietnamCbankResponse.getTranid());
		try {
			return CodeMsg.common(CodeMsgType.SUCCESS, URLDecoder.decode(upayVietnamCbankResponse.getUrl(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			return CodeMsg.common(CodeMsgType.ERR_SYS);
		}
	}

	private String builderUrl(String cpOrderId,int userId,String issuerID,Float txnAmount,String appKey,String client_ip) {
		StringBuilder sbud = new StringBuilder(vietnamPayConfiguration.getUpayEbankUrl());
		try {
			sbud.append("?cpOrderId=").append(cpOrderId)
			.append("&userName=").append(userId)
			.append("&issuerID=").append(URLEncoder.encode(issuerID,"UTF-8"))
			.append("&type_id=").append(13)
			.append("&txnAmount=").append(txnAmount.intValue())
			.append("&appKey=").append(appKey)
			.append("&goodsKey=").append("GSK313")
			.append("&extra=").append("")
			.append("&client_ip=").append(client_ip);
		} catch (Exception e) {
			logger.error("越南UPay网银支付构架URL失败，错误信息："+e.getMessage());
			return null;
		}
		return sbud.toString();
	}
	
	static {
		responseMap.put("01", "Thất bại");
		responseMap.put("02", "Chưa confirm được");// 尚未确认
		responseMap.put("03", "Đã confirm trước đó");//之前确认
		responseMap.put("04", "Giao dịch Pending");//交易待确认
		responseMap.put("05", "Sai MAC");//Mac错误
		responseMap.put("06", "Không xác định mã lỗi");//例外
		responseMap.put("07", "Giao dịch không tồn tại");//交易不存在
		responseMap.put("08", "Thông tin không đầy đủ");//字段不完整
		responseMap.put("09", "Đại lý không tồn tại");//商户不存在
		responseMap.put("10", "Sai định dạng");//虚假格式
		responseMap.put("11", "Sai thông tin");//虚假信息
		responseMap.put("12", "Ngân hàng tạm khóa hoặc không tồn tại");//银行不活跃
		responseMap.put("13", "Có lỗi");//错误
		responseMap.put("14", "Code không hợp lệ");//代码不完全
		responseMap.put("9000", "Trường hợp ngoại lệ hệ thống");//系统异常
		responseMap.put("9001", "Lặp lại Gửi theo thứ tự");//重复订单
		responseMap.put("9002", "Thương mại không tạo");//订单没有创建
		responseMap.put("9003", "Chữ ký là không chính xác");//签名不正确
		responseMap.put("9004", "Không thể xác nhận lại đơn đặt hàng");//无法再次确认订单
		responseMap.put("9011", "appKey không thể để trống");//appKey不能为空
		responseMap.put("9005", "goodsKey không thể để trống");//goodsKey不能为空
		responseMap.put("9006", "cpOrderId không thể để trống");//cpOrderId 不能为空
		responseMap.put("9007", "chKey quá dài");//chKey数据长度太长
		responseMap.put("9008", "extra quá dài");//extra 数据长度太长
		responseMap.put("9009", "cpOrderId quá dài");//cpOrderId数据长度太长
		responseMap.put("9010", "Kênh thanh toán không khớp");//支付通道不匹配,可能是没开启。
	}
	
	private String getResponseMsg(String responseCode) {
		return responseMap.get(responseCode);
	}
	@Override
	public boolean confirmIfExistPayOrder(PayOrder payOrder) {
		return Boolean.FALSE;
	}

	@Override
	public boolean confirmIfExistSuccessPayOrder(PayOrder payOrder, HttpServletRequest httpServletRequest) {
		return Boolean.FALSE;
	}

	@Override
	public boolean isSuccessPayOrder(ReplacementOrder replacementOrder) {
		return Boolean.FALSE;
	}

	@Override
	public String getType() {
		return String.valueOf(PayTypeConstants.VIETNAM_UPAY_EBANK);
	}

	@Override
	public boolean isBackEnd() {
		return Boolean.TRUE;
	}

	@Override
	public boolean isNeedCallBack() {
		return Boolean.TRUE;
	}

}
