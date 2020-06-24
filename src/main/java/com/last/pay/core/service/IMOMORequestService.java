package com.last.pay.core.service;

import com.last.pay.base.CodeMsg;
import com.last.pay.core.dto.vietnam.momo.MomoReceiptReqeust;
import com.last.pay.core.dto.vietnam.momo.MomoUpdateInfoRequest;

public interface IMOMORequestService {
	
	/**
	 * 
	 * @return
	 */
	public CodeMsg<?> handleVietnamMoMoCallBack(MomoReceiptReqeust momoReceiptReqeust);
	
	public CodeMsg<?> updateMoMoUserInfo(MomoUpdateInfoRequest momoUpdateInfoRequest);

}
