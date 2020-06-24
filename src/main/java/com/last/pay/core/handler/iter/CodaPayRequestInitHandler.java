package com.last.pay.core.handler.iter;

import com.last.pay.base.CodeMsg;
import com.last.pay.base.CodeMsgType;
import com.last.pay.base.common.constants.CodaPayConstants.CodaErrorCodeConstants;
import com.last.pay.core.db.pojo.web.PayOrder;
/**
 * Coda Pay
 * @author Administrator
 *
 */
public interface CodaPayRequestInitHandler {
	/**
	 *  初始化事务
	 * @param payOrder
	 * @param mnoId
	 * @return
	 */
	public default CodeMsg<?> initTnxId(PayOrder payOrder,String mnoId) {
		return CodeMsg.success(CodeMsgType.ERR_SYS);
	}

	/**
	 * 获取CodaPay错误信息
	 * @param code
	 * @return
	 */
	public default CodeMsgType getCodeMsgType(int code) {
		return CodaErrorCodeConstants.responseMap.get(code);
	}
}
