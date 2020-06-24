package com.last.pay.core.component.load;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.last.pay.core.db.pojo.game.ParamConfig;
import com.last.pay.core.db.mapper.gamedb.ParamConfigMapper;

/**
 *  GameDB.ParamConfig
 * @author Administrator
 *
 */
@Component
public class ParamConfiguration {
	
	private static final Log logger = LogFactory.getLog(ParamConfiguration.class);
	
	private String warHeadIntegralRadio;
	
	private String ticketIntegralRadio;
	@Autowired
	private ParamConfigMapper paramConfigMapper;
	
	@PostConstruct
	public void loadConfig() {
		ParamConfig warHeadParamConfig = paramConfigMapper.getParamConfigByKey("warHeadIntegralRadio");
		if(warHeadParamConfig == null) {
			logger.error("请配置弹头参数：warHeadIntegralRadio ！");
		}else {
			warHeadIntegralRadio = warHeadParamConfig.getValue();
		}
		
		ParamConfig ticketIntegralRadioConfig = paramConfigMapper.getParamConfigByKey("ticketIntegralRadio");
		if(ticketIntegralRadioConfig == null) {
			logger.error("请配置奖券积分参数：ticketIntegralRadio ！");
		}else {
			ticketIntegralRadio = ticketIntegralRadioConfig.getValue();
		}
	}

	public String getWarHeadIntegralRadio() {
		return warHeadIntegralRadio;
	}

	public String getTicketIntegralRadio() {
		return ticketIntegralRadio;
	}
	
	
	
}
