package com.last.pay.core.component.load;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.last.pay.core.db.pojo.web.PayAward;
import com.last.pay.core.db.mapper.webdb.PayAwardMapper;

@Component
public class PayAwardConfiguration {
	
	private Map<String,List<PayAward>> payAwardMap = new HashMap<>();
	
	@Autowired
	private PayAwardMapper payAwardMapper;
	
	private final String pointNameUserTypeSeparat = "_";
	
	@PostConstruct
	public void initConfig() {
		List<PayAward> payAwards = payAwardMapper.getPayAwards();
		for(PayAward payAward : payAwards) {
			String pointName = payAward.getPointName();
			if(StringUtils.isBlank(pointName)) {
				continue;
			}
			Integer userType = payAward.getUserType();
			List<PayAward> userTypeAwards = payAwardMap.get(pointName+pointNameUserTypeSeparat+userType.intValue());
			if(userTypeAwards != null) {
				userTypeAwards.add(payAward);
			}else {
				userTypeAwards = new ArrayList<>();
				userTypeAwards.add(payAward);
				payAwardMap.put(pointName+pointNameUserTypeSeparat+userType.intValue(), userTypeAwards);
			}
		}
	}
	
	public Map<Integer,Integer> loadPayAwardPropAmount(String pointName,int userType,Map<Integer,Integer> propMap){
		List<PayAward> payAwards= getPayAward(pointName, userType);
		if(Objects.nonNull(payAwards)) {
			for(int i = 0;i < payAwards.size();i++) {
				PayAward payAward = payAwards.get(i);
				Integer propId = payAward.getPropId();
				Integer amount = payAward.getAmount();
				Integer have = propMap.get(propId);
				if(Objects.nonNull(have)) {
					have = have.intValue() + amount.intValue();
					propMap.put(propId, have);
				}else {
					propMap.put(propId, amount);
				}
			}
		}
		return propMap;
	}
	
	
	public List<PayAward> getPayAward(String pointName,int userType){
		return payAwardMap.get(formatKey(pointName, userType));
	}
	
	private String formatKey(String pointName,int userType) {
		if(Objects.isNull(pointName)) {
			return "";
		}
		return pointName+pointNameUserTypeSeparat+userType;
	}
}
