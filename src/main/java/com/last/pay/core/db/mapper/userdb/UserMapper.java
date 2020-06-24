package com.last.pay.core.db.mapper.userdb;

import org.apache.ibatis.annotations.Param;

import com.last.pay.core.db.pojo.user.UserIfm;

public interface UserMapper {
	/**
	 * 获取用户信息
	 * @param userId
	 * @return
	 */
	public UserIfm getUserByUserId(@Param("userId")Integer userId);
	
	/**
	 * @param userId
	 * @param infullAmount 充值金额USD
	 */
	public void updateUserInfullAmount(@Param("userId")int userId, @Param("infullAmount")float infullAmount);
	
}
