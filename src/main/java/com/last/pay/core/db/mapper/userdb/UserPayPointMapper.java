package com.last.pay.core.db.mapper.userdb;


import org.apache.ibatis.annotations.Param;

import com.last.pay.core.db.pojo.user.UserPayPoint;


public interface UserPayPointMapper {
	
	public UserPayPoint getUserPayPointsByUserIdAndPoint(@Param("userId") Integer userId,@Param("pointName") String pointName);
	
	public void insertUserPayPoint(UserPayPoint userPayPoint);
	
	public void updateUserPayPoint(@Param("userId")int userId , @Param("pointName")String pointName);

}
