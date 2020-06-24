package com.last.pay.core.service.impl;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import com.last.pay.base.CodeMsg;
import com.last.pay.base.CodeMsgType;
import com.last.pay.core.db.pojo.web.PayOrder;
import com.last.pay.core.component.PayConfigManager;
import com.last.pay.core.dto.vietnam.momo.MomoReceiptReqeust;
import com.last.pay.core.dto.vietnam.momo.MomoUpdateInfoRequest;
import com.last.pay.core.service.IMOMORequestService;
import com.last.pay.core.service.IPayOrderQueryService;
import com.last.pay.core.service.IPayOrderService;

@Service
public class MOMORequestServiceImpl implements IMOMORequestService {
	
	private static final Log logger = LogFactory.getLog(MOMORequestServiceImpl.class);
	@Autowired
	private PayConfigManager payConfigManager;
	@Autowired
	private IPayOrderService payOrderService;
	@Autowired
	private IPayOrderQueryService payOrderQueryService;
	
	@Override
	public CodeMsg<?> handleVietnamMoMoCallBack(MomoReceiptReqeust momoReceiptReqeust) {
		if(Objects.isNull(momoReceiptReqeust)){
			logger.error("越南MoMo请求信息为空");
			return CodeMsg.failure(CodeMsgType.ERR_SYS_PARAMS.getCode(),"越南MoMo请求信息为空");
		}
		logger.info("");
		if(StringUtils.isBlank(momoReceiptReqeust.getMomo_transId())) {
			logger.error("越南MoMo请求的momo_transId为空");
			return CodeMsg.failure(CodeMsgType.ERR_SYS_PARAMS.getCode(),"越南MoMo交易ID为空");
		}
		if(StringUtils.isBlank(momoReceiptReqeust.getRequestTime())) {
			logger.error("越南MoMo请求的requestTime为空");
			return CodeMsg.failure(CodeMsgType.ERR_SYS_PARAMS.getCode(),"越南MoMo请求的交易时间为空");
		}
		if(StringUtils.isBlank(momoReceiptReqeust.getMessage())) {
			logger.error("越南MoMo请求的message为空");
			return CodeMsg.failure(CodeMsgType.ERR_SYS_PARAMS.getCode(),"越南MoMo请求的交易时间为空");
		}
		if(StringUtils.isBlank(momoReceiptReqeust.getAuthKey())) {
			logger.error("越南MoMo请求的authKey为空");
			return CodeMsg.failure(CodeMsgType.ERR_SYS_PARAMS.getCode(),"越南MoMo请求的验证签名为空");
		}
		if(StringUtils.isBlank(momoReceiptReqeust.getMoney())) {
			logger.error("越南MoMo请求的money为空");
			return CodeMsg.failure(CodeMsgType.ERR_SYS_PARAMS.getCode(),"越南MoMo请求的转账金额为空");
		}
		if(md5Verify(momoReceiptReqeust)) {
			String momo_transId = momoReceiptReqeust.getMomo_transId();
			List<PayOrder> payOrderByThirdNum = payOrderQueryService.getPayOrderByThirdNumSuccess(momo_transId);
			if(Objects.isNull(payOrderByThirdNum) || payOrderByThirdNum.isEmpty()) {
				
				return CodeMsg.success(CodeMsgType.SUCCESS);
			}else {
				logger.error("越南MoMo订单【"+momo_transId+"】已经支付成功");
				return CodeMsg.failure(CodeMsgType.ERR_PAYORDER_HAD_HANDLED);
			}
		}else {
			return CodeMsg.failure(CodeMsgType.ERR_SYS_PARAMS,"越南MoMo请求的MD5验证失败");
		}
	}

	@Override
	public CodeMsg<?> updateMoMoUserInfo(MomoUpdateInfoRequest momoUpdateInfoRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	
	private boolean md5Verify(MomoReceiptReqeust momoReceiptReqeust) {
		String requestTime = momoReceiptReqeust.getRequestTime();
		String money = momoReceiptReqeust.getMoney();
		String message = momoReceiptReqeust.getMessage();
		String phone = momoReceiptReqeust.getPhone();
		String momoSecretKey = payConfigManager.getMomoSecretKey();
		StringBuffer sbuf = new StringBuffer();
		sbuf.append(requestTime).append("|")
			.append(message).append("|")
			.append(money).append("|")
			.append(phone).append("|")
			.append(momoSecretKey);
		logger.info("MoMo authKey:" + momoSecretKey);
		String md5DigestAsHex = DigestUtils.md5DigestAsHex(sbuf.toString().getBytes());
		if(md5DigestAsHex.equals(momoReceiptReqeust.getAuthKey())) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
}
