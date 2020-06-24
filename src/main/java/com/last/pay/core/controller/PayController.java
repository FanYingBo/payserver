package com.last.pay.core.controller;

import java.util.Objects;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.last.pay.base.CodeMsg;
import com.last.pay.base.CodeMsgType;
import com.last.pay.base.common.constants.Constants;
import com.last.pay.base.common.constants.Constants.PayTypeConstants;
import com.last.pay.cache.GMQueneManager;
import com.last.pay.cache.command.PayCallBackCommand;
import com.last.pay.core.db.pojo.web.PayOrder;
import com.last.pay.core.component.PayConfigManager;
import com.last.pay.core.component.sys.SystemConfiguration;
import com.last.pay.core.service.IPayOrderService;
import com.last.pay.core.service.ITranslationService;
import com.last.pay.util.HttpUtil;
import com.last.pay.util.InternationalUtil;
import com.last.pay.util.PayUtil;
/**
 * 越南充值接口
 * @author Administrator
 *
 */
@Controller
public class PayController {

	@Autowired
	private IPayOrderService payOrderService;
	@Autowired
	private GMQueneManager queneManager;
	@Resource
	private SystemConfiguration systemConfiguration;
	@Autowired
	private PayConfigManager configManager;
	@Autowired
	private ITranslationService translationService;
	/**
	 * 充值页面
	 * @param model
	 * @param orderType
	 * @param userId
	 * @param pointName
	 * @param currency
	 * @return
	 */
	@RequestMapping("/PayManager/payOrder")
	public String payHomePage(Model model,String userId,String pointName,String currency) {
		if(systemConfiguration.getSystemEnv() == Constants.ENV_PRODUCT) {
			model.addAttribute("retData",CodeMsg.failure(404, "页面不存在"));
			return "pay/error";
		}
		model.addAttribute("userId",userId);
		model.addAttribute("pointName", pointName);
		model.addAttribute("currency", currency);
		model.addAttribute("payPoints", configManager.getAllPayPoint());
		return "pay/PayConfirmVietnam";
	}
	/**
	 * 充值处理
	 * @param request
	 * @param payOrder
	 * @param cardNum
	 * @param cardPin
	 * @param providerCode
	 * @param cardPrintAmount
	 * @return
	 */
	@RequestMapping("/PayManager/placeAnOrder")
	@ResponseBody
	public CodeMsg<?> placeAnOrder(HttpServletRequest request,PayOrder payOrder,String payType,String pointCount) {
		if (Objects.isNull(payType)) {
			return CodeMsg.failure(CodeMsgType.ERR_PAYTYPE);
		}else {
			payOrder.setPayType(Integer.parseInt(payType));
		}
		if(payOrder.getDynamicId() == null) {
			payOrder.setDynamicId(0);
		}
		if(!payOrderService.isLegalRequest(payOrder)) {
			return CodeMsg.failure(CodeMsgType.ERR_SYS_REQEUST_ADDR);
		}
		if (Objects.isNull(payOrder.getUserId())) {
			return CodeMsg.failure(CodeMsgType.ERR_USERID);
		}
		if (Objects.isNull(payOrder.getPointName())) {
			return CodeMsg.failure(CodeMsgType.ERR_ORDER_POINTNAME);
		}
		if(StringUtils.isBlank(payOrder.getIp())) {
			String ipAddress = HttpUtil.getIpAddress(request);
			payOrder.setIp(ipAddress);
		}
		CodeMsg<?> resultCode = payOrderService.thirdPayChannel(payOrder, request);
		return InternationalUtil.handleRetCode(payOrder.getRealCurrency(), resultCode, translationService);
	}
	/**
	 *  Coda pay 短信支付页面
	 * @return
	 */
	@RequestMapping("/PayManager/codaPayPage")
	public String codaPayPage(PayOrder payOrder,String payType,HttpServletRequest request,Model model) {
		CodeMsg<?> codeMsg = null;
		if(payOrderService.isLegalRequest(payOrder) 
				&& !PayUtil.checkIfCodaPay(payOrder.getPayType()) 
				&& !PayUtil.checkIfPayCent(payOrder.getPayType())) {
			
			codeMsg = CodeMsg.failure(CodeMsgType.ERR_SYS_REQEUST_ADDR);
		} else {
			codeMsg = payOrderService.initCodaTranscation(payOrder,payType,request);
		}
		if(payOrder.getDynamicId() == null) {
			payOrder.setDynamicId(0);
		}
		model.addAttribute("retData", codeMsg);
		if(codeMsg.getCode() != CodeMsgType.SUCCESS.getCode()) {
			return "pay/error";
		}
		if(payOrder.getPayType() == PayTypeConstants.PAYCENT) {
			return "pay/paycent";
		}
		return "pay/CodaPay";
	}
	
	@RequestMapping("/PayManager/redisPay")
	@ResponseBody
	public CodeMsg<?> redisPay(PayCallBackCommand command){
		queneManager.pushPayCallback(command);
		return CodeMsg.success(CodeMsgType.SUCCESS);
	}
}
