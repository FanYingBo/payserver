package com.last.pay.cache;

import com.last.pay.cache.command.ForceCommand;
import com.last.pay.cache.command.PayCallBackCommand;
import com.last.pay.cache.command.PaySuccessCommand;
/**
 * Redis 队列推送
 * 	样板代码（真实实现在缓存模块中）
 * @param forceCommand
 */
public class GMQueneManager {
	
	
	public void pushForceCommand(ForceCommand forceCommand){
		
	}
	
	public void pushPaySuccess(int userId, PaySuccessCommand paySuccessCommand){
		
	}
	
	public PayCallBackCommand popPayCallbackCommand(){
		return new PayCallBackCommand();
	}
	
	public void pushPayCallback(PayCallBackCommand payCallBackCommand){
		
	}
	
}
