package com.last.pay.core.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.last.pay.core.db.mapper.logdb.PayErrorLogMapper;
import com.last.pay.core.db.pojo.log.PayErrorLog;
import com.last.pay.core.exception.SqlQueryException;
import com.last.pay.core.service.IPayErrorLogService;

@Service
public class PayErrorLogServiceImpl implements IPayErrorLogService{
	
	@Autowired
	private PayErrorLogMapper payErrorLogMapper;

	@Override
	public List<PayErrorLog> getPayErrorLogs(String orderNum) {
		try {
			return payErrorLogMapper.getPayErrorLogByOrderNum(orderNum);
		} catch (Exception e) {
			throw new SqlQueryException(e.getMessage());
		}
	}

	@Override
	public void addPayErrorLog(PayErrorLog payErrorLog) {
		payErrorLogMapper.insertPayUserLog(payErrorLog);
	}

}
