package com.last.pay.core.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.last.pay.base.CodeMsgType;
import com.last.pay.base.common.constants.Constants;
import com.last.pay.core.db.pojo.game.International;
import com.last.pay.core.dto.vietnam.kingcard.KingCardCallBackParam;
import com.last.pay.core.component.PayConfigManager;
import com.last.pay.core.service.IPayCacheService;
import com.last.pay.core.service.ITranslationService;
import com.last.pay.util.HashUtils;

@Controller
public class CallBackController {
	
	private static final Log logger = LogFactory.getLog(CallBackController.class);

	@Autowired
	private IPayCacheService payCacheService;
	@Autowired
	private ITranslationService translationService;
	@Autowired
	private PayConfigManager payConfigManager;
	
	@RequestMapping("/backend")
	@ResponseBody
	public String backEndCallBack(HttpServletRequest servletRequest){
		logger.info("######## 开始处理CodaPay的回调订单 ########");
		payCacheService.handlePayCallBack(servletRequest);
		logger.info("######## 结束处理CodaPay的回调订单 ########");
		return "{\"Response\":\"ResultCode=0\"}";
	}
	
	@RequestMapping("/successpage")
	public String callBackPage(String TxnId,String OrderId,Model model){
		International international = translationService.getinternationalByPrimaryKey(Constants.formatInternationalKey(CodeMsgType.SUCCESS.getCode()));
		if(Objects.nonNull(international)) {
			model.addAttribute("success", StringUtils.isBlank(international.getMmValue()) ? "Recharge Success":international.getMmValue());
		}else {
			model.addAttribute("success", "Recharge Success");
		}
		return "pay/success";
	}
	@RequestMapping("/upaybackend")
	@ResponseBody
	public String uPaycallBack(HttpServletRequest servletRequest) {
		logger.info("######## 开始处理uPay的回调订单 ########");
		payCacheService.handleUPayCallBack(servletRequest);
		logger.info("######## 结束处理uPay的回调订单 ########");
		return "SUCCESS";
	}
	
	@RequestMapping("/paycentnotify")
	@ResponseBody
	public String paycentCallBack(HttpServletRequest servletRequest) {
		logger.info("######## 开始处理paycent的回调订单 ########");
		payCacheService.handlePayCentCallBack(servletRequest);
		logger.info("######## 结束处理paycent的回调订单 ########");
		return "success";
	}
	@RequestMapping("/paycentreturnpage")
	public String paycentReturnPage() {
		return "pay/success";
	}
	
	@RequestMapping("/vietnampayret")
	@ResponseBody
	public String vietnamPayCallBack(HttpServletRequest request) {
		logger.info("######## 开始处理越南的回调订单 ########");
		payCacheService.handleVietnamCallBack(request);
		logger.info("######## 结束处理越南的回调订单 ########");
		return "success";
	}
	@RequestMapping("/vietnamkcret")
	@ResponseBody
	public String vitnamKingCardCallBack(HttpServletRequest request,@RequestBody KingCardCallBackParam kingCardCallBackParam) {
		logger.info("######## 开始处理KingCard的回调订单 ########");
		payCacheService.handleVietnamKingCardCallBack(kingCardCallBackParam, request);
		logger.info("######## 结束处理KingCard的回调订单 ########");
		return  "{\"err_code\": \"0\", \"message\": \"success\"}";
	}
	@RequestMapping("/vietnamblret")
	@ResponseBody
	public String vitnamBangLangCardCallback(@RequestParam Map<String, Object> paramMap) {
		logger.info("######## 开始处理BangLang的回调订单 ########");
		payCacheService.handleVietnamBangLangCallBack(paramMap);
		logger.info("######## 结束处理BangLang的回调订单 ########");
		return "success";
	}
	@RequestMapping("/vietnamkcret-test")
	@ResponseBody
	public String vitnamKingCardCallBack(HttpServletRequest servletRequest) {
		logger.info("######## 开始处理KingCard的回调订单 ########");
		BufferedReader bufferedReader;
		try {
			bufferedReader = servletRequest.getReader();
			JsonObject retData = new JsonParser().parse(bufferedReader).getAsJsonObject();
			String sign = retData.get("sign").getAsString();
			logger.info("回调的签名信息："+ sign);
			retData.remove("sign");
			String signData = retData.toString();
			logger.info("回调信息：" + signData);
			String newdataToSign = StringEscapeUtils.escapeJava(signData);
			String dataToSign1 = newdataToSign.replaceAll("\\\\\"", "\"");
			String dataToSign2 = dataToSign1.replaceAll("/", "\\\\\\/");
			String hmac256 = HashUtils.getHMAC256(dataToSign2, payConfigManager.getVietnamKingCardSecretKey());
			logger.info("回调信息的签名：" + hmac256);
			if(sign != null) {
				if(hmac256.equalsIgnoreCase(sign)) {
					KingCardCallBackParam kingCardCallBackParam = new Gson().fromJson(signData, KingCardCallBackParam.class);
					payCacheService.handleVietnamKingCardCallBack(kingCardCallBackParam,servletRequest);
				}else {
					logger.error("KingCard签名信息验证失败，无法处理");
				}
			}else {
				logger.error("KingCard签名信息为空，无法处理");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		logger.info("######## 结束处理KingCard的回调订单 ########");
		return  "{\"err_code\": \"0\", \"message\": \"success\"}";
	}
}
