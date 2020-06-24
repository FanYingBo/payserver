package com.last.pay.core.service;

import com.last.pay.core.db.pojo.log.DynamicPointLog;
import com.last.pay.core.db.pojo.user.UserIfm;
import com.last.pay.core.db.pojo.web.PayOrder;
import com.last.pay.core.db.pojo.web.PayPoint;

public interface ILogService {
	
	public void addTiketInteralLog(Integer userId, Integer beforeIntegral, Integer integral);
	
	public void addUserPayPointLog(PayOrder payOrder);
	
	public void addUserWarHeadLog(Integer userId, Integer personPool, Long publicPool, Float money,Integer add_personPool, Integer add_publicPool);
	
	public void addPayErrorLog(PayOrder payOrder,PayPoint oldPayPoint, Exception e);

	public void addPointNameChangedPayErrorLog(PayOrder payOrder,PayPoint oldPayPoint) ;

	public void addNormalPayErrorLog(PayOrder payOrder, Exception e);
	
	public void addUserPayLog(PayOrder payOrder,UserIfm userIfm);
	
	public void addDynamicPointLog(DynamicPointLog dynamicPointLog);
	
}
