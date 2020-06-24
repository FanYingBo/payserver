package com.last.pay.core.service;

import java.util.List;

import com.last.pay.core.db.pojo.log.PayErrorLog;

public interface IPayErrorLogService {
	/**
	 * 	查询错误订单信息
	 * @param orderNum
	 * @return
	 */
	public List<PayErrorLog> getPayErrorLogs(String orderNum);
	
	/**
	 *  	添加错误日志
	 * @param payErrorLog
	 */
	public void addPayErrorLog(PayErrorLog payErrorLog);
}
