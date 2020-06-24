package com.last.pay.core.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.last.pay.base.CodeMsg;
import com.last.pay.base.CodeMsgType;
import com.last.pay.core.dto.vietnam.momo.MomoReceiptReqeust;
import com.last.pay.core.service.IMOMORequestService;

@RestController
public class VietnamMoMoController {
	
	@Autowired
	private IMOMORequestService momoRequestService;
	
	@GetMapping("/momo-receipt")
	public String receiptCallBack(MomoReceiptReqeust momoReceiptReqeust) {
		CodeMsg<?> moMoCallBack = momoRequestService.handleVietnamMoMoCallBack(momoReceiptReqeust);
		if(moMoCallBack.getCode() == CodeMsgType.ERR_PAYORDER_HAD_HANDLED.getCode()) {
			return "{\"errorCode\":\"-4\",\"errorDescription\":\"Ma giao dich da ton tai\"}";
		}
		return "{\"errorCode\":\"0\",\"errorDescription\":\"Success\"}";
	}
	
}
