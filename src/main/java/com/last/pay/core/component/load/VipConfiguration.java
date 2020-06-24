package com.last.pay.core.component.load;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.last.pay.core.db.pojo.game.ParamConfig;
import com.last.pay.core.db.pojo.game.VipConfig;
import com.last.pay.core.db.mapper.gamedb.ParamConfigMapper;
import com.last.pay.core.db.mapper.gamedb.VipConfigMapper;

/**
 * GameDB.VipConfig
 * @author Administrator
 *
 */
@Component
public class VipConfiguration {
	
	private static final Log logger = LogFactory.getLog(VipConfiguration.class);
	
	@Autowired
	private VipConfigMapper vipConfigMapper;
	@Autowired
	private ParamConfigMapper paramConfigMapper;
	
	private TreeMap<Integer , VipConfig> vipLevelMap = new TreeMap<Integer, VipConfig>();
	
	private Map<String, Integer[]> currencyVipIntegralMap = new HashMap<String, Integer[]>();
	
	@PostConstruct
	public void loadVipConfig() {
		
		List<VipConfig> vipConfigs = vipConfigMapper.getVipConfigs();
		
		for (VipConfig vipConfig : vipConfigs) {
			vipLevelMap.put(vipConfig.getIntegral(), vipConfig);
		}
		
		ParamConfig currencyIntegral = paramConfigMapper.getParamConfigByKey("currency_integral");
		if(currencyIntegral == null) {
			logger.error("请配置货币积分参数：currency_integral ！");
		}
		
		JSONObject currencyJson = JSONObject.parseObject(currencyIntegral.getValue());
		
		for (String currency : currencyJson.keySet()) {
			currencyVipIntegralMap.put(currency, currencyJson.getJSONArray(currency).toArray(new Integer[0]));
		}
	}

	public TreeMap<Integer, VipConfig> getVipLevelMap() {
		return vipLevelMap;
	}

	
	public Map<String, Integer[]> getCurrencyVipIntegralMap() {
		return currencyVipIntegralMap;
	}

	
}
