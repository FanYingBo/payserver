package com.last.pay.core.component.load;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.last.pay.base.common.constants.Constants.CurrencyConstants;
import com.last.pay.base.common.constants.Constants.PayPointTypeConstants;
import com.last.pay.core.db.pojo.web.PayPoint;
import com.last.pay.core.db.mapper.webdb.PayPointMapper;
/**
 * WebDB.PayPoint
 * @author Administrator
 *
 */
@Component
public class PayPointConfiguration {
	
	private Map<String , PayPoint> payPointMap = new HashMap<String, PayPoint>();
	/***不同货币的金币计费点***/
	private Map<String ,TreeMap<Float, PayPoint>> currencyPayPoint = new TreeMap<>();
	
	@Autowired
	public PayPointMapper payPointMapper;
	
	@PostConstruct
	public void loadConfig() {
		
		List<PayPoint> payPointList = payPointMapper.getAllPayPoints();
		
		for (PayPoint payPoint : payPointList) {
			payPointMap.put(payPoint.getName(), payPoint);
			if(payPoint.getType() == PayPointTypeConstants.GOLD) {
				if(payPoint.getUsd() > 0) {
					TreeMap<Float, PayPoint> map = currencyPayPoint.get(CurrencyConstants.USA);
					if(map == null) {
						map = new TreeMap<Float, PayPoint>();
						currencyPayPoint.put(CurrencyConstants.USA, map);
					}
					map.put(payPoint.getUsd(), payPoint);
				}
				if(payPoint.getCny() > 0) {
					TreeMap<Float, PayPoint> map = currencyPayPoint.get(CurrencyConstants.China);
					if(map == null) {
						map = new TreeMap<Float, PayPoint>();
						currencyPayPoint.put(CurrencyConstants.China, map);
					}
					map.put(payPoint.getCny().floatValue(), payPoint);
				}
				if(payPoint.getVnd() > 0) {
					TreeMap<Float, PayPoint> map = currencyPayPoint.get(CurrencyConstants.Vietnam);
					if(map == null) {
						map = new TreeMap<Float, PayPoint>();
						currencyPayPoint.put(CurrencyConstants.Vietnam, map);
					}
					map.put(payPoint.getVnd().floatValue(), payPoint);
				}
				if(payPoint.getMmk() > 0) {
					TreeMap<Float, PayPoint> map = currencyPayPoint.get(CurrencyConstants.Myanmar);
					if(map == null) {
						map = new TreeMap<Float, PayPoint>();
						currencyPayPoint.put(CurrencyConstants.Myanmar, map);
					}
					map.put(payPoint.getMmk().floatValue(), payPoint);
				}
			}			
		}
	}

	public Map<String, PayPoint> getPayPointMap() {
		return payPointMap;
	}


	public Map<String, TreeMap<Float, PayPoint>> getCurrencyPayPoint() {
		return currencyPayPoint;
	}

	
}
