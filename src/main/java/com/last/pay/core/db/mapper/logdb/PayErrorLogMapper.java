package com.last.pay.core.db.mapper.logdb;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.last.pay.core.db.pojo.log.PayErrorLog;

public interface PayErrorLogMapper {
	
	public void insertPayUserLog(PayErrorLog payErrorLog);
	
	public List<PayErrorLog> getPayErrorLogByOrderNum(@Param("orderNum") String orderNum);

}
