package com.last.pay.core.db.mapper.userdb;

import org.apache.ibatis.annotations.Param;

import com.last.pay.core.db.pojo.user.UserControl;

public interface UserControlMapper {
	
	public UserControl getUserControl(@Param("userId") Integer userId);

	/**
	 * @param userId
	 * @param warHeadIntegral 增加的弹头积分
	 * @param ticketIntegral 增加的话费积分
	 */
	public void updateUserControl(UserControl userControl);

}
