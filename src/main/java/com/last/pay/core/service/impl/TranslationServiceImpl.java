package com.last.pay.core.service.impl;

import java.util.Objects;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.last.pay.base.common.constants.Constants;
import com.last.pay.core.db.pojo.game.International;
import com.last.pay.core.component.PayConfigManager;
import com.last.pay.core.db.mapper.gamedb.InternationalMapper;
import com.last.pay.core.service.ITranslationService;

@Service
public class TranslationServiceImpl implements ITranslationService{
	
	private static final Log logger = LogFactory.getLog(TranslationServiceImpl.class);
	
	@Autowired
	private InternationalMapper internationalMapper;
	@Autowired
	private PayConfigManager payConfigManager;

	@Override
	public International getInternationalByErrorCode(Integer code) {
		String key = Constants.formatInternationalKey(code);
		try {
			return getinternationalByPrimaryKey(key);
		} catch (Exception e) {
			logger.error("查找错误码的国际化语言失败：" + key+"失败原因："+e.getMessage());
			return null;
		}
	}

	@Override
	public International getinternationalByPrimaryKey(String key) {
		try {
			if(Objects.isNull(payConfigManager.getInternationalByKey(key))) {
				International internatonal = internationalMapper.getInternationalByPrimaryKey(key);
				if(Objects.isNull(internatonal)) {
					logger.warn("未找到国际化参数：" + key);
					return null;
				}
				payConfigManager.addInternational(internatonal);
				return internatonal;
			}
			return payConfigManager.getInternationalByKey(key);
		} catch (Exception e) {
			logger.error("查找错误码的国际化语言失败：" + key+"失败原因："+e.getMessage());
			return null;
		}
	}

}
