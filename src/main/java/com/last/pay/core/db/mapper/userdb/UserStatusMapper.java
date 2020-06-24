package com.last.pay.core.db.mapper.userdb;

import org.apache.ibatis.annotations.Param;

import com.last.pay.core.db.pojo.user.UserStatus;


public interface UserStatusMapper {
	
	public UserStatus getUserStatus(@Param("userId") int userId);
	
	public int updateUseMapPoint();

}
