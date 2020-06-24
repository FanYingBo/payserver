package com.last.pay.core.db.mapper.gamedb;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.last.pay.core.db.pojo.game.International;


public interface InternationalMapper {

	public International getInternationalByPrimaryKey(@Param("key") String key);
	
	public List<International> getAllInternational();
}
