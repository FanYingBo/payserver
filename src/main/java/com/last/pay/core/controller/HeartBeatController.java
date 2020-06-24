package com.last.pay.core.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HeartBeatController {
	
	private static final Log logger = LogFactory.getLog(HeartBeatController.class);
	@RequestMapping("/heartbeat")
	@ResponseBody
	public Integer heartBeat() {
		logger.info("######### 心跳检测 ###########");
		return 1;
	}
}
