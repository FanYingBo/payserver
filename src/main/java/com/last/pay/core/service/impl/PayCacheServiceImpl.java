package com.last.pay.core.service.impl;



import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import com.coda.airtime.api.util.CodaSoapHandler;
import com.last.pay.base.CodeMsg;
import com.last.pay.base.CodeMsgType;
import com.last.pay.base.common.constants.Constants;
import com.last.pay.base.common.constants.Constants.CurrencyConstants;
import com.last.pay.base.common.constants.Constants.PayStatusConstants;
import com.last.pay.base.common.constants.Constants.PayTypeConstants;
import com.last.pay.base.common.constants.Constants.PaycentErrorCode;
import com.last.pay.cache.IncrCacheManager;
import com.last.pay.cache.command.PayCallBackCommand;
import com.last.pay.core.db.pojo.log.PayErrorLog;
import com.last.pay.core.db.pojo.user.UserIfm;
import com.last.pay.core.db.pojo.web.PayOrder;
import com.last.pay.core.db.pojo.web.PayPoint;
import com.last.pay.core.dto.vietnam.kingcard.KingCardCallBackParam;
import com.last.pay.core.dto.vietnam.kingcard.KingCardOrder;
import com.last.pay.core.dto.vietnam.kingcard.KingCardTxn;
import com.last.pay.core.component.PayConfigManager;
import com.last.pay.core.component.RequestHandlerAdapter;
import com.last.pay.core.component.sys.SystemConfiguration;
import com.last.pay.core.component.third.CodaPayConfiguration;
import com.last.pay.core.component.third.PaycentConfiguraion;
import com.last.pay.core.component.third.VietnamPayConfiguration;
import com.last.pay.core.exception.ParamsException;
import com.last.pay.core.handler.impl.IOSPayRequestHandler;
import com.last.pay.core.handler.iter.PayRequestHandler;
import com.last.pay.core.service.ILogService;
import com.last.pay.core.service.IPayCacheService;
import com.last.pay.core.service.IPayErrorLogService;
import com.last.pay.core.service.IPayOrderQueryService;
import com.last.pay.core.service.IPayOrderService;
import com.last.pay.core.service.ITranslationService;
import com.last.pay.util.InternationalUtil;
import com.last.pay.util.HashUtils;
import com.last.pay.util.HttpUtil;
import com.last.pay.util.MatchUtils;
import com.last.pay.util.PayUtil;
import com.last.pay.util.SerializableUtil;

@Service
public class PayCacheServiceImpl implements IPayCacheService{
	
	private static Log logger = LogFactory.getLog(PayCacheServiceImpl.class);
	
	@Autowired
	private IPayOrderService payOrderService;
	@Autowired
	private CodaSoapHandler codaSoapHandler;
	@Autowired
	private CodaPayConfiguration codaPayConfiguration;
	@Autowired
	private ITranslationService translationService;
	@Autowired
	private IPayOrderQueryService payOrderQueryService;
	@Autowired
	private IOSPayRequestHandler iosPayRequestHandler;
	@Autowired
	private VietnamPayConfiguration vietnamPayConfiguration;
	@Autowired
	private SystemConfiguration systemConfiguration;
	@Autowired
	private IPayErrorLogService payErrorLogService;
	@Autowired
	private PaycentConfiguraion paycentConfiguraion;
	@Autowired
	private RequestHandlerAdapter requestHandlerAdapter;
	
	@Resource
	private ILogService logService;
	
	@Resource
	private IncrCacheManager incrCacheManager;
	
	@Resource
	private PayConfigManager payConfigManager;
	
	@Override
	public CodeMsg<?> handleRedisCachePay(PayCallBackCommand payCallBack) {
		if(Objects.nonNull(payCallBack.getOrderNum())) {
			PayOrder payOrderHave = payOrderService.getPayOrder(payCallBack.getOrderNum().trim());
			if(Objects.nonNull(payOrderHave) && payOrderHave.getStatus() == PayStatusConstants.SUCCESS_ORDER) {
				logger.error("订单已被处理："+payCallBack.getOrderNum());
				return CodeMsg.failure(CodeMsgType.ERR_PAYORDER_HAD_HANDLED);
			}
		}
		PayOrder payOrder = createPayOrder(payCallBack.getUserId(), payCallBack.getPlatform(), payCallBack.getPayType(), 
				payCallBack.getChannel(), payCallBack.getPointName(), payCallBack.getOrderNum(),payCallBack.getIp(),payCallBack.getDynamicId());
		
		/***支付类型的Redis支付权限控制****/
		if(Objects.nonNull(payOrder.getPayType()) 
				&& !PayUtil.redisCachePay(payOrder.getPayType())) {
			logger.error("支付類型【"+payOrder.getPayType()+"】不合法，当前类型无法通过Redis完成订单");
			return CodeMsg.failure(CodeMsgType.ERR_PAYTYPE);
		}
		if(StringUtils.isBlank(payCallBack.getPurchaseInfo())) {  // 后台补单
			try {
				if(payOrder.getPayType() == PayTypeConstants.GOOGLE || payOrder.getPayType() == PayTypeConstants.HUAWEI_INTERNATIONAL) {
					payOrder.setRealCurrency(CurrencyConstants.USA);
					payOrder.setThird_order_num("");
				} else if(payOrder.getPayType() == PayTypeConstants.HUAWEI_INLAND){
					payOrder.setRealCurrency(CurrencyConstants.China);
					payOrder.setThird_order_num("");
				} else {
					/****如果是缅甸的短信和waveMoney 越南点卡 直接从数据库获取****/
					payOrder = payOrderService.getPayOrder(payOrder.getOrderNum().trim());
				}
				if(Objects.isNull(payOrder)) {
					logger.error("未查找到订单号信息："+payOrder.getOrderNum());
					return CodeMsg.failure(CodeMsgType.ERR_SYS_PARAMS);
				}
				CodeMsg<?> orderTransStatus = queryThirdOrderTransStatus(payOrder);
				if(orderTransStatus.getCode() != CodeMsgType.SUCCESS.getCode()){
					logger.error("订单"+payOrder+"补单失败，错误码："+orderTransStatus.getCode()+"，错误信息："+orderTransStatus.getMsg());
					payOrder.setErrorInfo("错误码："+orderTransStatus.getCode()+"，错误信息："+orderTransStatus.getMsg());
					payOrder.setErrorThirdNum(payOrder.getThird_order_num());
					logService.addNormalPayErrorLog(payOrder, null);
					return orderTransStatus;
				}
				PayPoint payPoint = payOrderService.checkAndBindPayPointStatus(payOrder);
				if(Objects.isNull(payOrder.getRealMoney())) {
					Object[] existMoney = payConfigManager.getPayPointExistMoney(payPoint.getName());
					if(Float.valueOf(existMoney[1].toString()) > 0) {
						payOrder.setRealCurrency(existMoney[0].toString());
						payOrder.setRealMoney(Float.valueOf(existMoney[1].toString()));
					} else {
						logger.error("补单未找到付费点【"+ payOrder.getPointName() +"】金额配置，请检查数据库付费点配置");
						return CodeMsg.failure(CodeMsgType.ERR_ORDER_MONEY);
					}
				}
				payOrderService.bindUserNickName(payOrder);
				return payOrderService.addPayOrder(payOrder, payPoint);
			} catch (Exception e) {
				return InternationalUtil.handleException(e,payCallBack.getCurrency(),translationService);
			}
		} else {
			try {
				if(payOrder.getPayType() == PayTypeConstants.IOS) {
					logger.info("######## 开始处理IOS的订单请求 ########");
					UserIfm user = payOrderService.bindUserNickName(payOrder);
					payOrder.setIosPurchaseInfo(payCallBack.getPurchaseInfo());
					CodeMsg<?> processCodeMsg = iosPayRequestHandler.processMultiOrder(payOrder);
					if(processCodeMsg.getCode().intValue() == CodeMsgType.SUCCESS.getCode()) {
						List<PayOrder> payOrders = payOrder.getPayOrders();
						for(PayOrder payOrderIos : payOrders) {
							try {
								PayPoint payPoint = payOrderService.checkAndBindPayPointStatus(payOrderIos);
								List<PayOrder> payOrderByThirdNum = payOrderQueryService.getPayOrderByThirdNum(payOrderIos.getThird_order_num());
								if(Objects.nonNull(payOrderByThirdNum) && payOrderByThirdNum.size() > 0) {
									PayOrder payOrderExist = payOrderByThirdNum.get(0);
									if(payOrderExist.getStatus() != 1 && payOrderExist.getPointName().equals(payOrderIos.getPointName())) { // 完成的订单
										payOrderService.addPayOrder(payOrderExist, payPoint);
									} else {
										logger.error("订单处理失败，IOS订单号与数据库中付费点不匹配或者订单已到账，订单号："+payOrderIos.getThird_order_num()+"，IOS付费点："+payOrderIos.getPointName()+"数据库付费点："+payOrderExist.getPointName());
										payOrderIos.setOrderNum(payOrderExist.getOrderNum());
										payOrderIos.setNickName(payOrder.getNickName());
										payOrderIos.setRealCurrency(CurrencyConstants.USA);
										payOrderIos.setRealMoney(payConfigManager.getPayPointMoney(payPoint, payOrderIos.getRealCurrency()));
										payOrderIos.setErrorInfo("订单处理失败，IOS订单号与数据库中付费点不匹配或者订单已到账");
										payOrderIos.setErrorThirdNum(payOrderIos.getThird_order_num());
										logService.addNormalPayErrorLog(payOrderIos, null);
									}
								}else {
									String orderNum = PayUtil.createOrderNum(payOrderIos.getPayType(), incrCacheManager.incrOrder());
									payOrderIos.setOrderNum(orderNum);
									payOrderIos.setUser(user);
									payOrderIos.setNickName(payOrder.getNickName());
									payOrderIos.setRealCurrency(CurrencyConstants.USA);
									payOrderIos.setRealMoney(0f);
									Object[] existMoney = payConfigManager.getPayPointExistMoney(payPoint.getName());
									if(Float.valueOf(existMoney[1].toString()) > 0) {
										payOrderIos.setRealCurrency(existMoney[0].toString());
										payOrderIos.setRealMoney(Float.valueOf(existMoney[1].toString()));
									} else {
										logger.error("IOS支付付费点【"+ payOrder.getPointName() +"】金额未配置，请检查数据库付费点配置");
										return CodeMsg.failure(CodeMsgType.ERR_ORDER_MONEY);
									}
									payOrderService.addPayOrder(payOrderIos, payPoint);
								}
							}catch (Exception e) {
								CodeMsg<?> handleException = InternationalUtil.handleException(e);
								logger.error("IOS订单号为："+payOrderIos.getThird_order_num()+"，订单处理失败，错误码："+handleException.getCode()+"错误信息："+handleException.getMsg());
							}
						}
					}
					logger.info("######## 停止处理IOS的订单请求 ########");
					return processCodeMsg;
				}else if(payOrder.getPayType() == PayTypeConstants.GOOGLE 
						|| payOrder.getPayType() == PayTypeConstants.HUAWEI_INTERNATIONAL 
						|| payOrder.getPayType() == PayTypeConstants.HUAWEI_INLAND){
					payOrder.setPurchaseInfo(payCallBack.getPurchaseInfo());
					return payOrderService.thirdPayChannel(payOrder,null);
				}else {
					return CodeMsg.failure(CodeMsgType.ERR_SYS);
				}
			} catch (Exception e) {
				CodeMsg<?> handleException = InternationalUtil.handleException(e,payCallBack.getCurrency(),translationService);
				logger.info("订单处理失败，错误码："+handleException.getCode()+"，错误信息："+handleException.getMsg());
				return handleException;
			}
		}
	}
	
	@Override
	public CodeMsg<?> handlePayCallBack(HttpServletRequest request) {
		String txnId = request.getParameter("TxnId");
		String orderId = request.getParameter("OrderId");
		String resultCode = request.getParameter("ResultCode");
		String checksum = request.getParameter("Checksum");
		String totalPrice = request.getParameter("totalprice");
		if(Objects.isNull(txnId)) {
			logger.error("CodaPay回调信息处理失败，参数TxnId为空");
			return CodeMsg.failure(CodeMsgType.ERR_PAYORDER_NUM.getCode(),"txnId为空");
		}
		if(Objects.isNull(orderId)) {
			logger.error("CodaPay回调信息处理失败，参数OrderId为空");
			return CodeMsg.failure(CodeMsgType.ERR_PAYORDER_NUM.getCode(),"orderId为空");
		}
		if(!StringUtils.isNumeric(resultCode)) {
			logger.error("CodaPay回调信息处理失败，参数ResultCode不合法");
			return CodeMsg.failure(CodeMsgType.ERR_PAYORDER_NUM.getCode(),"resultCode为空");
		}
		if(Objects.isNull(checksum)) {
			logger.error("CodaPay回调信息处理失败，参数Checksum不合法");
			return CodeMsg.failure(CodeMsgType.ERR_PAYORDER_NUM.getCode(),"checksum为空");
		}
		String apiKey = payConfigManager.getMMCodaPayApiKey();
		List<PayOrder> payOrders = null;
		PayOrder payOrder = null;
		try {
			if(codaPayConfiguration.getCallBackCheckMD5() == Constants.CALLBACKCHECK_FALSE) {
				payOrders = payOrderQueryService.getPayOrderByThirdNumFail(orderId);
				if(Objects.nonNull(payOrders) && !payOrders.isEmpty()) {
					payOrder = payOrders.get(0);
					if(MatchUtils.isNumberStr(totalPrice)) {
						payOrder.setRealMoney(Float.valueOf(totalPrice));
					}
					PayPoint payPoint = payOrderService.checkAndBindPayPointStatus(payOrder);
					payOrderService.bindUserNickName(payOrder);
					return payOrderService.addPayOrder(payOrder, payPoint);
				}else {
					logger.error("订单回调处理失败,未查找到订单或者订单已到账,回调信息:{\"txnId\":\""+txnId+"\",\"apiKey\":\""+apiKey+"\",\"orderId\":\""+orderId+"\",\"resultCode\":\""+resultCode+"\",\"checksum\":\""+checksum+"\"}");
					return CodeMsg.failure(CodeMsgType.ERR_PAYORDER_NUM);
				}
			} else {
				// 验证订单信息是否被更改, 这里决定是否信任第三方的支付验证信息
				boolean validated = codaSoapHandler.validateChecksum(txnId, apiKey, orderId, resultCode, checksum);
				if (validated && Integer.parseInt(resultCode) == 0) {
					payOrders = payOrderQueryService.getPayOrderByThirdNumFail(orderId);
					if(Objects.nonNull(payOrders) && !payOrders.isEmpty()) {
						payOrder = payOrders.get(0);
						PayPoint payPoint = payOrderService.checkAndBindPayPointStatus(payOrder);
						payOrderService.bindUserNickName(payOrder);
						return payOrderService.addPayOrder(payOrder, payPoint);
					}else {
						logger.error("订单回调处理失败,未查找到订单或者订单已到账,回调信息:{\"txnId\":\""+txnId+"\",\"apiKey\":\""+apiKey+"\",\"orderId\":\""+orderId+"\",\"resultCode\":\""+resultCode+"\",\"checksum\":\""+checksum+"\"}");
						return CodeMsg.failure(CodeMsgType.ERR_PAYORDER_NUM);
					}
				} else {
					payOrders = payOrderQueryService.getPayOrderByThirdNumFail(orderId);
					if(Objects.nonNull(payOrders)&& !payOrders.isEmpty()) {
						payOrder = payOrders.get(0);
						payOrder.setErrorThirdNum(orderId);
						payOrder.setErrorInfo("订单回调验证失败，回调信息:{\"txnId\":\""+txnId+"\",\"apiKey\":\""+apiKey+"\",\"orderId\":\""+orderId+"\",\"resultCode\":\""+resultCode+"\",\"checksum\":\""+checksum+"\"}");
						logService.addNormalPayErrorLog(payOrder, null);
					}
					logger.error("订单回调未验证通过,{\"txnId\":\""+txnId+"\",\"apiKey\":\""+apiKey+"\",\"orderId\":\""+orderId+"\",\"resultCode\":\""+resultCode+"\",\"checksum\":\""+checksum+"\"}");
					return CodeMsg.failure(CodeMsgType.ERR_CODACALL_BACK_PARAM);
				}
			}
		}catch (Exception e) {
			logger.error("订单回调处理失败,{\"txnId\":\""+txnId+"\",\"apiKey\":\""+apiKey+"\",\"orderId\":\""+orderId+"\",\"resultCode\":\""+resultCode+"\",\"checksum\":\""+checksum+"\"},错误信息："+e.getMessage());
			return InternationalUtil.handleException(e,Objects.isNull(payOrder) ? CurrencyConstants.USA : payOrder.getRealCurrency(),translationService);
		}
	}
	
	@Override
	public CodeMsg<?> handleUPayCallBack(HttpServletRequest request) {
		String amount = request.getParameter("amount");
		String cpOrderId = request.getParameter("cpOrderId");
		String tradeId = request.getParameter("tradeId");
		String result = request.getParameter("result");
		int realMoney = 0;
		if(StringUtils.isNumeric(amount)) {
			realMoney = Integer.parseInt(amount);
		}else {
			logger.error("UPay回调信息处理失败，参数amount为空");
			return CodeMsg.failure(CodeMsgType.ERR_CARD_AMOUNT.getCode(),"回调返回的金额不合法");
		}
		if(Objects.isNull(cpOrderId)) {
			logger.error("UPay回调信息处理失败，参数cpOrderId为空");
			return CodeMsg.failure(CodeMsgType.ERR_PAYORDER_NUM.getCode(),"订单号为空");
		}
		if(Objects.isNull(tradeId)) {
			logger.error("UPay回调信息处理失败，参数tradeId为空");
			return CodeMsg.failure(CodeMsgType.ERR_PAYORDER_NUM.getCode(),"第三方订单号为空");
		}
		if(!StringUtils.isNumeric(result)) {
			logger.error("UPay回调信息处理失败，参数result不合法");
			return CodeMsg.failure(CodeMsgType.ERR_PAYORDER_NUM.getCode(),"请求结果不合法");
		}
		int resultCode = Integer.parseInt(result);
		if(resultCode != 200) {
			logger.error("UPay回调信息处理失败，支付未成功，第三方订单号："+tradeId);
			return CodeMsg.failure(CodeMsgType.ERR_PAY_FAILED);
		}
		if(checkAndGetParam(request)) {
			List<PayOrder> payOrders = null;
			PayOrder payOrder = null;
			try {
				payOrders = payOrderQueryService.getPayOrderByThirdNum(tradeId);
				if(Objects.isNull(payOrders) || payOrders.isEmpty()) {
					logger.warn("UPay回调信息处理失败，参数tradeId不合法，未查找到订单信息:"+tradeId+"，将查找错误日志");
//					return CodeMsg.failure(CodeMsgType.ERR_PAYORDER_NUM.getCode(),"订单号不合法,未查找到订单信息:"+tradeId);
					List<PayErrorLog> payErrorLogs = payErrorLogService.getPayErrorLogs(cpOrderId);
					if(Objects.isNull(payErrorLogs) || payErrorLogs.isEmpty()) {
						logger.error("UPay回调信息处理失败，参数cpOrderId不合法，未查找到订单信息:"+cpOrderId);
						return CodeMsg.failure(CodeMsgType.ERR_PAYORDER_NUM.getCode(),"订单号不合法,未查找到订单信息:"+cpOrderId);
					}else {
						PayErrorLog payErrorLog = payErrorLogs.get(0);
						payOrder = createPayOrder(payErrorLog.getUserId(), 
								payErrorLog.getPlatform(), 
								payErrorLog.getPayType(), 
								payErrorLog.getChannel(), 
								payErrorLog.getPointName(), 
								payErrorLog.getOrderNum(),
								payErrorLog.getIp(),payErrorLog.getDynamicId());
						payOrder.setRealCurrency(CurrencyConstants.Vietnam);
					}
				}else {
					payOrder = payOrders.get(0);
				}
				if(payOrder.getStatus() == PayStatusConstants.SUCCESS_ORDER) {
					logger.error("Upay订单已被处理："+payOrder.getOrderNum());
					return CodeMsg.failure(CodeMsgType.ERR_PAYORDER_HAD_HANDLED);
				}else {
					payOrder.setThird_order_num(tradeId);
					payOrder.setRealMoney(Float.valueOf(realMoney/100));
					PayPoint payPoint = payOrderService.checkAndBindPayPointStatus(payOrder);
					payOrderService.bindUserNickName(payOrder);
					return payOrderService.addPayOrder(payOrder, payPoint);
				}
			} catch (Exception e) {
				logger.error("UPay回调订单处理失败,第三方订单号："+tradeId+",错误信息："+e.getMessage());
				return InternationalUtil.handleException(e,Objects.isNull(payOrder) ? CurrencyConstants.USA : payOrder.getRealCurrency(),translationService);
			}
		} else {
			logger.error("******UPay回调订单处理失败,订单未验证通过");
			return CodeMsg.failure(CodeMsgType.ERR_SYS_PARAMS.getCode(),"MD5未验证通过");
		}
	}
	
	/**
	 * Status=101&MerchantNo=888200000000037&OrderNo=I157888523099266&Amount=1%2C504.8&TrxNo=&FrpCode=MM-DCB-OOREDOO&Cur=104&ErrorCode=10090044
	 */
	@Override
	public CodeMsg<?> handlePayCentCallBack(HttpServletRequest request) {
		String amount = request.getParameter("Amount");
		String status = request.getParameter("Status");
		String orderNo = request.getParameter("OrderNo");
		String trxNo = request.getParameter("TrxNo");
		String cur = request.getParameter("Cur");
		float money = 0;
		int retCode = 0;
		logger.info("PayCent 回调信息："+"{\"Amount\":"+amount+",\"Status\":"+status+",\"OrderNo\":\""+orderNo+"\",\"TrxNo\":"+trxNo+",\"Cur\":\""+cur+"\"}");
		if(!MatchUtils.isNumberStr(amount)) {
			logger.error("Paycent回调信息处理失败，参数Amount为空");
			return CodeMsg.failure(CodeMsgType.ERR_CARD_AMOUNT.getCode(),"回调返回的金额不合法");
		}else {
			money = Float.valueOf(amount);
		}
		if(!StringUtils.isNumeric(status)) {
			logger.error("Paycent回调信息处理失败，参数Status为空");
			return CodeMsg.failure(CodeMsgType.ERR_REQUEST_PARAMS.getCode(),"回调返回的状态不合法");
		}else {
			retCode = Integer.parseInt(status);
		}
		if(Objects.isNull(orderNo)) {
			logger.error("Paycent回调信息处理失败，参数orderNo为空");
			return CodeMsg.failure(CodeMsgType.ERR_PAYORDER_NUM.getCode(),"回调返回的订单不合法");
		}
		if(Objects.isNull(trxNo)) {
			logger.error("Paycent回调信息处理失败，参数trxNo为空");
			return CodeMsg.failure(CodeMsgType.ERR_PAYORDER_NUM.getCode(),"回调返回的第三方订单不合法");
		}
		if(StringUtils.isBlank(cur)) {
			logger.error("Paycent回调信息处理失败，参数cur为空");
			return CodeMsg.failure(CodeMsgType.ERR_PAYORDER_NUM.getCode(),"回调返回的货币符号不合法");
		}
		PayOrder payOrder = null;
		try {
			payOrder = payOrderService.getPayOrder(orderNo);
		} catch (Exception e) {
			logger.error("Paycent回调订单处理失败,订单号："+orderNo+",错误信息："+e.getMessage());
			return InternationalUtil.handleException(e,Objects.isNull(payOrder) ? CurrencyConstants.USA : payOrder.getRealCurrency(),translationService);		
		}
		if(Objects.isNull(payOrder)) {
			logger.error("Paycent回调订单处理失败，未查到到订单号"+orderNo);
			return CodeMsg.failure(CodeMsgType.ERR_PAYORDER_NUM);
		}
		if(retCode != 100) {
			// 100 成功 101 失败 102 已创建待确认 103 已取消
			CodeMsgType codeMsgType = PaycentErrorCode.callBackMap.get(retCode);
			payOrder.setRealMoney(money);
			payOrder.setThird_order_num(trxNo);
			payOrder.setRealCurrency(CurrencyConstants.Myanmar);
			payOrder.setErrorInfo("错误码："+retCode);
			if(Objects.nonNull(codeMsgType)) {
				payOrder.setErrorInfo("状态码:"+codeMsgType.getCode()+",错误信息:"+codeMsgType.getMsg());
			}
			try {
				logService.addNormalPayErrorLog(payOrder, null);
				logger.error("Paycent回调为失败订单，订单号："+orderNo+"，第三方订单号："+trxNo+"，已记录错误日志");
			}catch (Exception ee) {
				logger.error("Paycent记录错误的订单日志失败，订单号："+orderNo+"，第三方订单号："+trxNo+"，原因："+ee.getMessage());
			}
			return CodeMsg.failure(codeMsgType == null ? retCode : codeMsgType.getCode(), "订单处理失败");
		}
		try {
			if(checkPayCentParam(request)) {
				if(payOrder.getStatus() == PayStatusConstants.SUCCESS_ORDER) {
					logger.error("Paycent订单已被处理："+payOrder.getOrderNum());
					return CodeMsg.failure(CodeMsgType.ERR_PAYORDER_HAD_HANDLED);
				}else {
					payOrder.setThird_order_num(trxNo);
					payOrder.setRealCurrency(CurrencyConstants.Myanmar);
					payOrder.setRealMoney(Float.valueOf(money));
					PayPoint payPoint = payOrderService.checkAndBindPayPointStatus(payOrder);
					payOrderService.bindUserNickName(payOrder);
					return payOrderService.addPayOrder(payOrder, payPoint);
				}
			}else {
				logger.error("******Paycent回调订单处理失败,订单未验证通过");
				return CodeMsg.failure(CodeMsgType.ERR_SYS_PARAMS.getCode(),"MD5未验证通过");
			}
		} catch (Exception e) {
			logger.error("Paycent回调订单处理失败,订单号："+orderNo+",错误信息："+e.getMessage());
			return InternationalUtil.handleException(e,Objects.isNull(payOrder) ? CurrencyConstants.USA : payOrder.getRealCurrency(),translationService);
		}
	}
	
	private boolean checkPayCentParam(HttpServletRequest request) {
		List<String> paramList = new ArrayList<>()   ;
		paramList.add("Amount");
		paramList.add("Cur");
		paramList.add("DealTime");
		paramList.add("FrpCode");
		paramList.add("MerchantNo");
		paramList.add("OrderNo");
		paramList.add("PayTime");
		paramList.add("Status");
		paramList.add("TrxNo");
		paramList.add("Mp");
		paramList.add("SubscriptionId");
		return checkPayCentHash(paramList, request);
	}
	
	private boolean checkPayCentHash(List<String> paramList,HttpServletRequest request) {
		Collections.sort(paramList);
		StringBuffer sbuf = new StringBuffer();
		try {
			for(String param : paramList) {
				if(Objects.nonNull(request.getParameter(param))) {
					sbuf.append("&").append(param).append("=").append(URLDecoder.decode(request.getParameter(param),"UTF-8"));
				}
			}
		} catch (Exception e) {
			logger.error("*******参数解码失败");
		}
		String params = sbuf.substring(1);
		String hmac = request.getParameter("hmac");
		String paysecret = paycentConfiguraion.getSecret();
		if(StringUtils.isBlank(paysecret)) {
			logger.error("Paycent支付验证密钥为空，无法完成交易验证");
			throw new ParamsException(CodeMsgType.ERR_PAYCENT_SECRET);
		}
	
		if(Objects.isNull(hmac)) {
			logger.error("******Paycent hmac 为空");
			return Boolean.FALSE;
		}
		params += paysecret;
		logger.info("PayCent参数值:" + params);
		logger.info("PayCent Hmac:" + hmac);
		String sha256 = HashUtils.getSHA256(params);
		return sha256.equalsIgnoreCase(hmac);
	}
	
	private boolean checkAndGetParam(HttpServletRequest request) {
		List<String> paramList = new ArrayList<>()   ;
		paramList.add("amount");
		paramList.add("cpOrderId");
		paramList.add("tradeId");
		paramList.add("uid");
		paramList.add("result");
		paramList.add("appKey");
		paramList.add("area");
		paramList.add("chKey");
		paramList.add("extra");
		paramList.add("goodsKey");
		paramList.add("op");
		paramList.add("ts");
		String test = request.getParameter("test");
		if(Objects.nonNull(test)) {
			paramList.add("test");
			if(Boolean.valueOf(test)) {
				logger.info("UPay 沙箱订单");
			}
			if(systemConfiguration.getSystemEnv() == Constants.ENV_PRODUCT) {
				logger.error("******UPay 沙箱订单 不能发送到生产环境");
				return Boolean.FALSE;
			}
		}
		return hashCheck(request, paramList);
	}
	
	
	private boolean hashCheck(HttpServletRequest request,List<String> paList) {
		Collections.sort(paList);
		StringBuffer sbuf = new StringBuffer();
		String hash = request.getParameter("hash");
		for(String param : paList) {
			try {
				sbuf.append("&"+param).append("=").append(URLEncoder.encode(request.getParameter(param), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
			}
		}
		String params = sbuf.toString().substring(1);
		params += vietnamPayConfiguration.getReceiveKey();
		logger.info("UPay参数值:" + params);
		logger.info("UPay Hash:" + hash);
		if(Objects.nonNull(hash)) {
			String md5DigestAsHex = DigestUtils.md5DigestAsHex(params.getBytes());
			return md5DigestAsHex.equalsIgnoreCase(hash);
		}else {
			return Boolean.FALSE;
		}
	}
	
	private PayOrder createPayOrder(int userId,int platform,int payType,
			int channel,String pointName,String orderNum,String ip,int dynamicId) {
		PayOrder payOrder = new PayOrder();
		payOrder.setUserId(userId);
		payOrder.setPlatform(platform);
		payOrder.setPayType(payType);
		payOrder.setChannel(channel);
		payOrder.setPointName(pointName);
		payOrder.setOrderNum(orderNum);
		payOrder.setIp(ip);
		payOrder.setDynamicId(dynamicId);
		return payOrder;
	}

	@Override
	public CodeMsg<?> handleVietnamCallBack(HttpServletRequest request) {
		String reqId = request.getParameter("reqId");
		String status = request.getParameter("status");
		String amount = request.getParameter("amount");
		String message = request.getParameter("message");
		float realMoney = 0;
		if(Objects.isNull(reqId)) {
			logger.error("越南点卡回调信息处理失败，参数reqId为空");
			return CodeMsg.failure(CodeMsgType.ERR_PAYORDER_NUM.getCode(),"回调返回的订单不合法");
		}
		if(Objects.isNull(status)) {
			logger.error("越南点卡回调信息处理失败，参数status为空");
			return CodeMsg.failure(CodeMsgType.ERR_PAYORDER_NUM.getCode(),"回调返回的状态不合法");
		}
		if(StringUtils.isBlank(amount)) {
			logger.error("越南点卡回调信息处理失败，参数amount为空");
			return CodeMsg.failure(CodeMsgType.ERR_PAYORDER_NUM.getCode(),"回调返回的货币符号不合法");
		} else {
			realMoney = Float.valueOf(amount);
		}
		try {
			PayOrder payOrder = payOrderService.getPayOrderByThirdOrderNum(reqId);
			if(Objects.nonNull(payOrder)) {
				if(payOrder.getStatus() == PayStatusConstants.SUCCESS_ORDER) {
					logger.warn("越南点卡支付["+reqId+"]已经充值到账，无须再处理回调");
					return CodeMsg.success(CodeMsgType.SUCCESS);
				} else {
					if("00".equals(status)) {
						payOrder.setRealMoney(Float.valueOf(realMoney));
						PayPoint payPoint = payOrderService.checkAndBindPayPointStatus(payOrder);
						payOrderService.bindUserNickName(payOrder);
						return payOrderService.addPayOrder(payOrder, payPoint);
					} else {
						payOrder.setErrorInfo("错误码:"+status+",错误信息:"+message);
						payOrder.setErrorThirdNum(payOrder.getThird_order_num());
						try {
							logService.addNormalPayErrorLog(payOrder, null);
							logger.error("越南点卡回调为失败订单，订单号："+reqId+"，第三方订单号："+payOrder.getThird_order_num()+"，已记录错误日志");
						}catch (Exception ee) {
							logger.error("越南点卡记录错误的订单日志失败，订单号："+reqId+"，第三方订单号："+payOrder.getThird_order_num()+"，原因："+ee.getMessage());
						}
					}
				}
			} else {
				logger.warn("越南点卡支付未查找到订单["+reqId+"]信息，无法处理回调");
				return CodeMsg.success(CodeMsgType.SUCCESS);
			}
		} catch (Exception e) {
			logger.error("越南点卡回调信息处理失败，失败原因：" + e.getMessage());
		}
		return CodeMsg.success(CodeMsgType.SUCCESS);
	}

	@Override
	public CodeMsg<?> queryThirdOrderTransStatus(PayOrder payOrder) {
		PayRequestHandler matchingHandler = requestHandlerAdapter.matchingHandler(String.valueOf(payOrder.getPayType()));
		return matchingHandler.queryRequest(payOrder);
	}

	@Override
	public CodeMsg<?> handleVietnamKingCardCallBack(KingCardCallBackParam kingCardCallBackParam, HttpServletRequest httpServletRequest) {
		String ipAddress = HttpUtil.getIpAddress(httpServletRequest);
		boolean isAccessed = payConfigManager.checkVietnamKingCardIpAddress(ipAddress);
		if(!isAccessed) {
			logger.error("越南KingCard回调错误，IP地址【"+ipAddress+"】无权限访问");
			return CodeMsg.failure(CodeMsgType.ERR_SYS);
		}
		logger.info("越南KingCard回调信息:" + kingCardCallBackParam);
		logger.info("越南KingCard回调签名:" + kingCardCallBackParam.getSign());
		KingCardOrder order = kingCardCallBackParam.getOrder();
		KingCardTxn kingCardTxn = kingCardCallBackParam.getTxn();
		if(Objects.isNull(order) ) {
			logger.error("越南KingCard回调信息处理失败，order信息为空");
			return CodeMsg.failure(CodeMsgType.ERR_REQUEST_PARAMS.getCode(),"order信息为空");
		}
		if(Objects.isNull(kingCardTxn) ) {
			logger.error("越南KingCard回调信息处理失败，txn信息为空");
			return CodeMsg.failure(CodeMsgType.ERR_REQUEST_PARAMS.getCode(),"txn信息为空");
		}
		// 自己的订单号
		String mrc_order_id = order.getMrc_order_id();
		// 第三方订单号
		Long id = order.getId();
		// 订单支付金额
		String tmp_amount = order.getTmp_amount();
		float realMoney = 0f;
		if(Objects.isNull(id)) {
			logger.error("越南KingCard回调信息处理失败，order(id)为空");
			return CodeMsg.failure(CodeMsgType.ERR_REQUEST_PARAMS.getCode(),"商户订单号为空");
		}
		if(StringUtils.isBlank(tmp_amount)) {
			logger.error("越南KingCard回调信息处理失败，tmp_amount为空");
			Integer fee_amount = Float.valueOf(kingCardTxn.getFee_amount()).intValue();
			Integer amount = Float.valueOf(kingCardTxn.getAmount()).intValue();
			if(Objects.isNull(fee_amount) || Objects.isNull(amount)) {
				return CodeMsg.failure(CodeMsgType.ERR_REQUEST_PARAMS.getCode(),"回调金额为空");
			}else {
				realMoney = fee_amount + amount;
			}
		}else {
			realMoney = Float.valueOf(tmp_amount);
		}
		if(StringUtils.isBlank(mrc_order_id)) {
			logger.error("越南KingCard回调信息处理失败，mrc_order_id为空");
			return CodeMsg.failure(CodeMsgType.ERR_REQUEST_PARAMS.getCode(),"商户订单号为空");
		}
		if(StringUtils.isBlank(kingCardCallBackParam.getSign())) {
			logger.error("越南KingCard回调信息处理失败，签名sign信息为空");
			return CodeMsg.failure(CodeMsgType.ERR_REQUEST_PARAMS.getCode(),"签名验证信息为空");
		}
		PayOrder payOrder = payOrderService.getPayOrder(mrc_order_id);
		if(Objects.isNull(payOrder)) {
			logger.error("越南KingCard回调信息处理失败,未查找到订单信息【"+mrc_order_id+"】");
			List<PayErrorLog> payErrorLogs = payErrorLogService.getPayErrorLogs(mrc_order_id);
			if(payErrorLogs == null || payErrorLogs.isEmpty()) {
				logger.warn("越南KingCard回调信息处理失败,错误订单中未查找到订单信息【"+mrc_order_id+"】");
				return CodeMsg.failure(CodeMsgType.ERR_PAYORDER_NUM.getCode(),"未查找到订单【"+mrc_order_id+"】信息");
			}
			PayErrorLog payErrorLog = payErrorLogs.get(0);
			payOrder = createPayOrder(payErrorLog.getUserId(),
								payErrorLog.getPlatform(),
								payErrorLog.getPayType(), 
								payErrorLog.getChannel(), 
								payErrorLog.getPointName(), 
								payErrorLog.getOrderNum(), 
								payErrorLog.getIp(), 
								payErrorLog.getDynamicId());
		}else {
			if(payOrder.getStatus() == PayStatusConstants.SUCCESS_ORDER) {
				logger.warn("越南点卡支付["+mrc_order_id+"]已经充值到账，无须再处理回调");
				return CodeMsg.success(CodeMsgType.SUCCESS);
			}
		}
		payOrder.setRealMoney(realMoney);
		payOrder.setThird_order_num(String.valueOf(id));
		payOrder.setRealCurrency(CurrencyConstants.Vietnam);
		
		if(order.getStat() != 2) { //1。最近提交的卡，2。成功卡，3。友卡，4。待处理卡
			logger.info("越南KingCard回调信息处理失败，交易未成功，交易状态：" + order.getStat());
			payOrder.setErrorInfo("交易状态码:"+order.getStat());
			payOrder.setErrorThirdNum(payOrder.getThird_order_num());
			try {
				logService.addNormalPayErrorLog(payOrder, null);
				logger.error("越南KingCard为失败订单，订单号："+payOrder.getOrderNum()+"，第三方订单号："+payOrder.getThird_order_num()+"，已记录错误日志");
			}catch (Exception ee) {
				logger.error("越南KingCard记录错误的订单日志失败，订单号："+payOrder.getOrderNum()+"，第三方订单号："+payOrder.getThird_order_num()+"，原因："+ee.getMessage());
			}
			return CodeMsg.failure(CodeMsgType.ERR_PAY_FAILED);
		}
		try {
			PayPoint payPoint = payOrderService.checkAndBindPayPointStatus(payOrder);
			payOrderService.bindUserNickName(payOrder);
			return payOrderService.addPayOrder(payOrder, payPoint);
		} catch (Exception e) {
			logger.error("越南KingCard回调订单处理失败,订单号："+payOrder.getOrderNum()+",错误信息："+e.getMessage());
			return InternationalUtil.handleException(e,Objects.isNull(payOrder) ? CurrencyConstants.USA : payOrder.getRealCurrency(),translationService);
		}
	}
	/**
	 * 	验证签名
	 * @param kingCardCallBackParam
	 * @return
	 */
	private boolean verifyKingCardSign(KingCardCallBackParam kingCardCallBackParam) {
		String secretKey = payConfigManager.getVietnamKingCardSecretKey();
		String hmac256 = HashUtils.getHMAC256(kingCardCallBackParam.toString(), secretKey);
		return kingCardCallBackParam.getSign().equalsIgnoreCase(hmac256);
	}

	@Override
	public CodeMsg<?> handleVietnamBangLangCallBack(Map<String, Object> paramMap) {
		String message = paramMap.get("message") == null ? "":paramMap.get("message").toString();
		String real_amount = paramMap.get("real_amount") == null ? "0":paramMap.get("real_amount").toString();
		String request_id =  paramMap.get("request_id") == null ? "":paramMap.get("request_id").toString();
		int status =  paramMap.get("status") == null ? 0 : Integer.parseInt(paramMap.get("status").toString());
		String tran_id =  paramMap.get("tran_id") == null ? "":paramMap.get("tran_id").toString();
		logger.info("越南BangLang回调信息："+SerializableUtil.objectToJsonStr(paramMap));
		if(StringUtils.isBlank(real_amount) || Float.valueOf(real_amount) < 0.0001) {
			logger.error("越南BangLang回调信息处理失败，real_amount信息为空或金额不合法");
			return CodeMsg.failure(CodeMsgType.ERR_REQUEST_PARAMS.getCode(),"real_amount信息为空");
		}
		if(StringUtils.isBlank(request_id)) {
			logger.error("越南BangLang回调信息处理失败，request_id信息为空");
			return CodeMsg.failure(CodeMsgType.ERR_REQUEST_PARAMS.getCode(),"request_id信息为空");
		}
		if(StringUtils.isBlank(tran_id)) {
			logger.error("越南BangLang回调信息处理失败，tran_id信息为空");
			return CodeMsg.failure(CodeMsgType.ERR_REQUEST_PARAMS.getCode(),"tran_id信息为空");
		}
		PayOrder payOrder = payOrderService.getPayOrder(request_id.trim());
		
		if(Objects.isNull(payOrder)) {
			logger.error("越南BangLang回调信息处理失败,未查找到订单信息【"+request_id+"】");
			List<PayErrorLog> payErrorLogs = payErrorLogService.getPayErrorLogs(request_id);
			if(payErrorLogs == null || payErrorLogs.isEmpty()) {
				logger.warn("越南BangLang回调信息处理失败,错误订单中未查找到订单信息【"+request_id+"】");
				return CodeMsg.failure(CodeMsgType.ERR_PAYORDER_NUM.getCode(),"未查找到订单【"+request_id+"】信息");
			}
			PayErrorLog payErrorLog = payErrorLogs.get(0);
			payOrder = createPayOrder(payErrorLog.getUserId(),
								payErrorLog.getPlatform(),
								payErrorLog.getPayType(), 
								payErrorLog.getChannel(), 
								payErrorLog.getPointName(), 
								payErrorLog.getOrderNum(), 
								payErrorLog.getIp(),payErrorLog.getDynamicId());
		}else {
			// 0是成功 100 是成功但面值错误
			if(status != 0 && status != 100) { 
				logger.info("越南BangLang回调信息处理失败，交易未成功，交易状态：" + status);
				payOrder.setErrorInfo("交易失败，状态码:"+ status+"，错误信息："+ message);
				payOrder.setErrorThirdNum(payOrder.getThird_order_num());
				try {
					logService.addNormalPayErrorLog(payOrder, null);
					logger.error("越南BangLang为失败订单，订单号："+payOrder.getOrderNum()+"，第三方订单号："+payOrder.getThird_order_num()+"，已记录错误日志");
				}catch (Exception ee) {
					logger.error("越南BangLang记录错误的订单日志失败，订单号："+payOrder.getOrderNum()+"，第三方订单号："+payOrder.getThird_order_num()+"，原因："+ee.getMessage());
				}
				return CodeMsg.failure(CodeMsgType.ERR_PAY_FAILED);
			}
			if(payOrder.getStatus() == PayStatusConstants.SUCCESS_ORDER) {
				logger.warn("越南BangLang点卡支付["+request_id+"]已经充值到账，无须再处理回调");
				return CodeMsg.success(CodeMsgType.SUCCESS);
			}
		}
		payOrder.setRealMoney(Float.valueOf(real_amount));
		payOrder.setThird_order_num(tran_id);
		payOrder.setRealCurrency(CurrencyConstants.Vietnam);
		try {
			PayPoint payPoint = payOrderService.checkAndBindPayPointStatus(payOrder);
			payOrderService.bindUserNickName(payOrder);
			return payOrderService.addPayOrder(payOrder, payPoint);
		} catch (Exception e) {
			logger.error("越南BangLang 回调订单处理失败,订单号："+payOrder.getOrderNum()+",错误信息："+e.getMessage());
			return InternationalUtil.handleException(e,Objects.isNull(payOrder) ? CurrencyConstants.USA : payOrder.getRealCurrency(),translationService);
		}
	}
	
}

