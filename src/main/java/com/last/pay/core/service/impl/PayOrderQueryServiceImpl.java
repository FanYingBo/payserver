package com.last.pay.core.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.last.pay.core.db.pojo.web.PayOrder;
import com.last.pay.core.db.mapper.webdb.PayOrderMapper;
import com.last.pay.core.exception.SqlQueryException;
import com.last.pay.core.service.IPayOrderQueryService;

@Service
public class PayOrderQueryServiceImpl implements IPayOrderQueryService{
	
	@Autowired
	private PayOrderMapper payOrderMapper;

	@Override
	public List<PayOrder> getPayOrderByThirdNumSuccess(String thirdOrderNum) {
		try {
			return payOrderMapper.getPayOrderByThirdOrderNumSuccess(thirdOrderNum);
		} catch (Exception e) {
			throw new SqlQueryException(e.getMessage());
		}
	}

	@Override
	public List<PayOrder> getPayOrderByThirdNumFail(String thirdOrderNum) {
		try {
			return payOrderMapper.getPayOrderByThirdOrderNumFail(thirdOrderNum);
		} catch (Exception e) {
			throw new SqlQueryException(e.getMessage());
		}
	}

	@Override
	public List<PayOrder> getPayOrderByThirdNum(String thirdOrderNum) {
		try {
			return payOrderMapper.getPayOrderByThirdNum(thirdOrderNum);
		} catch (Exception e) {
			throw new SqlQueryException(e.getMessage());
		}
	}

	@Override
	public List<PayOrder> getPayOrderByFullText(int payType, String text, int status) {
		try {
			return payOrderMapper.getPayOrderByFullText(payType, text, status);
		}catch(Exception e){
			throw new SqlQueryException(e.getMessage());
		}
	}
	
	
}
