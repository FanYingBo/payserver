package com.last.pay.core.db.mapper.gamedb;


import org.apache.ibatis.annotations.Param;

import com.last.pay.core.db.pojo.game.PropConfig;

public interface PropConfigMapper {
	
	PropConfig getPropConfigByName(@Param("name") String name);

	PropConfig getPropConfigById(@Param("id") Integer id);
}
