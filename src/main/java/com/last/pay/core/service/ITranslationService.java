package com.last.pay.core.service;

import com.last.pay.core.db.pojo.game.International;

public interface ITranslationService {
	
	public International getInternationalByErrorCode(Integer code);
	
	public International getinternationalByPrimaryKey(String key);

}
