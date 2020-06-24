package com.last.pay.core.db.mapper.gamedb;

import org.apache.ibatis.annotations.Param;

import com.last.pay.core.db.pojo.game.ParamConfig;


public interface ParamConfigMapper {
	
	public ParamConfig getParamConfigByKey(@Param("key") String key);
}
