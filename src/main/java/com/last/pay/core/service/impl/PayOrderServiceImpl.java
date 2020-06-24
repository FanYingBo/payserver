package com.last.pay.core.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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

import com.last.pay.base.CodeMsg;
import com.last.pay.base.CodeMsgType;
import com.last.pay.base.SignExceptionOrder;
import com.last.pay.base.common.constants.Constants;
import com.last.pay.base.common.constants.PayMapConstants;
import com.last.pay.base.common.constants.PropVipConstants;
import com.last.pay.base.common.constants.Constants.CurrencyConstants;
import com.last.pay.base.common.constants.Constants.PayStatusConstants;
import com.last.pay.base.common.constants.Constants.PropConstants;
import com.last.pay.cache.GMQueneManager;
import com.last.pay.cache.IncrCacheManager;
import com.last.pay.cache.command.ForceCommand;
import com.last.pay.cache.command.PaySuccessCommand;
import com.last.pay.cache.mode.TempPayInfo;
import com.last.pay.core.db.mapper.logdb.PayErrorLogMapper;
import com.last.pay.core.db.mapper.logdb.PayUserLogMapper;
import com.last.pay.core.db.mapper.logdb.WarheadPoolLogMapper;
import com.last.pay.core.db.pojo.game.PropBag;
import com.last.pay.core.db.pojo.log.DynamicPointLog;
import com.last.pay.core.db.pojo.user.UserControl;
import com.last.pay.core.db.pojo.user.UserIfm;
import com.last.pay.core.db.pojo.user.UserPayPoint;
import com.last.pay.core.db.pojo.user.UserStatus;
import com.last.pay.core.db.pojo.web.DynamicPayPoint;
import com.last.pay.core.db.pojo.web.PayOrder;
import com.last.pay.core.db.pojo.web.PayPoint;
import com.last.pay.core.handler.GeneralPayRequestHandler;
import com.last.pay.core.component.ActivityTimeComponent;
import com.last.pay.core.component.PayConfigManager;
import com.last.pay.core.component.RequestHandlerAdapter;
import com.last.pay.core.db.mapper.userdb.UserControlMapper;
import com.last.pay.core.db.mapper.userdb.UserDBProcedureMapper;
import com.last.pay.core.db.mapper.userdb.UserMapper;
import com.last.pay.core.db.mapper.userdb.UserPayPointMapper;
import com.last.pay.core.db.mapper.userdb.UserStatusMapper;
import com.last.pay.core.db.mapper.webdb.CurrencyExchangeMapper;
import com.last.pay.core.db.mapper.webdb.PayOrderMapper;
import com.last.pay.core.db.pojo.call.UpdatePropsProcedure;
import com.last.pay.core.exception.ParamsException;
import com.last.pay.core.exception.RedisOptException;
import com.last.pay.core.exception.SqlQueryException;
import com.last.pay.core.exception.ThirdPayException;
import com.last.pay.core.handler.impl.CodaPayMyanmarRequestPageHandler;
import com.last.pay.core.handler.iter.PayRequestHandler;
import com.last.pay.core.service.ILogService;
import com.last.pay.core.service.IPayOrderQueryService;
import com.last.pay.core.service.IPayOrderService;
import com.last.pay.core.service.ITranslationService;
import com.last.pay.util.InternationalUtil;
import com.last.pay.util.HttpUtil;
import com.last.pay.util.MathUtil;
import com.last.pay.util.PayUtil;
import com.last.pay.util.SerializableUtil;

@Service
public class PayOrderServiceImpl implements IPayOrderService {
	
	public static final Log logger = LogFactory.getLog(PayOrderServiceImpl.class);

	@Override
	public CodeMsg<?> thirdPayChannel(PayOrder payOrder, HttpServletRequest request) {
		/****生成订单号***/
		try {
			/*** Google Pay 订单号由服务端生成****/
			if(StringUtils.isBlank(payOrder.getOrderNum())) {
				String orderNum = PayUtil.createOrderNum(payOrder.getPayType(), incrCacheManager.incrOrder());
				payOrder.setOrderNum(orderNum);
			}
		} catch (Exception e) {
			logger.error("调用Redis生成订单号失败，错误原因："+e.getMessage());
			return CodeMsg.failure(CodeMsgType.ERR_REDIS.getCode(),"生成订单号失败");
		}
		try {
			/***检查该用户付费点购买次数***/
			PayPoint payPoint = checkAndBindPayPointStatus(payOrder);
			/***绑定用户昵称***/
			bindUserNickName(payOrder);
			CodeMsg<?> responseCode = null;
			try {
				/*****处理第三方订单****/
				responseCode = processReqeust(payOrder, payOrder.getPayType(), request);
			} catch (Exception e1) {
				logger.error("第三方支付请求处理失败，系统错误："+e1.getMessage());
				throw new ThirdPayException(CodeMsgType.ERR_SYS.getCode(),CodeMsgType.ERR_SYS.getMsg());
			}
			if(responseCode.getCode().intValue() != CodeMsgType.SUCCESS.getCode()) {
				logger.error("第三方支付请求处理失败，錯誤碼："+responseCode.getCode()+"，错误信息："+responseCode.getMsg());
				if(SignExceptionOrder.containError(responseCode.getCode()) && !isHavePayOrder(payOrder)) {
					/*** 过滤错误订单，记录异常信息（不建议所有的错误都记录正常的订单表）***/
					addOrderFailure(payOrder);
				}
				throw new ThirdPayException(responseCode.getCode(), responseCode.getMsg(),responseCode.getData());
			}
			/**如果订单的付费点与第三方订单的付费点不一致**/
			if(!payOrder.isUniformPointName()) {
				String orderNum = PayUtil.createOrderNum(payOrder.getPayType(), incrCacheManager.incrOrder());
				payOrder.setOrderNum(orderNum);
				payPoint = checkAndBindPayPointStatus(payOrder);
			}
			/****如果需要回调处理****/
			if(isNeedCallBack(payOrder)) {
				/*** 添加订单，待回调***/
				addOrderSuccess(payOrder);
				return CodeMsg.common(CodeMsgType.WARN_TRANS_IS_PROCESSED, responseCode.getData());
			}else {
				/****添加订单***/
				logger.info("######## 开始订单信息入库 ########");
				return addPayOrder(payOrder,payPoint);
			}
		} catch (Exception e) {
			if(Objects.nonNull(payOrder.getUser()) && (Objects.nonNull(payOrder.getErrorThirdNum()) || Objects.nonNull(payOrder.getThird_order_num()))) {
				logService.addNormalPayErrorLog(payOrder,e);
			}
			logger.error("订单處理失败，订单信息："+payOrder+"，失败原因："+e.getMessage());
			return InternationalUtil.handleException(e);
		}
	}

	/****
	 * 
	 * 訂單處理流程：
	 * 1：生成訂單號
	 * 2：生成redis指令
	 * 3：向redis推送指令
	 * 4：添加新訂單入庫
	 * 5：更新用戶道具信息
	 * 6：添加付費點購買日志
	 * 7：更新訂單狀態（）
	 */
	@Override
	public CodeMsg<?> addPayOrder(PayOrder payOrder,PayPoint payPoint) {
		List<UpdatePropsProcedure> updatePropsProcedures = new ArrayList<UpdatePropsProcedure>();
		try {
			/****構建成功指令****/
			PaySuccessCommand paySuccessCommand = buildCommand(updatePropsProcedures,payOrder,payPoint,payOrder.getUser());
			if(!isHavePayOrder(payOrder)) {
				/***新订单信息入库***/ 
				addOrderSuccess(payOrder); // 如果失敗 status = 0
			}
			/****更新用戶道具信息****/
			updateUserPorps(payOrder,updatePropsProcedures);// 如果失敗 status = 2
			/****更新弹头信息***/
			updateWarHeadInfo(payOrder, payOrder.getUser());// 如果失敗 status = 2
			/****推送成功信息***/
			pushSuccessOrder(payOrder, paySuccessCommand);  // 如果失敗 status = 2
			/***更新訂單信息狀態為成功***/
			updateOrderStatus(payOrder, PayStatusConstants.SUCCESS_ORDER);// 如果失敗 status = 2
			/****添加用户购买日志****/
			logService.addUserPayPointLog(payOrder); // 不計失敗
		} catch (Exception e) {
			// 如果是推送成功
			if(payOrder.getStatus() != null && payOrder.getStatus() == PayStatusConstants.FAILURE_ORDER) {
				/***更新訂單信息狀態為失敗***/
				updateOrderStatus(payOrder, PayStatusConstants.FAILURE_ORDER);
			}
			logService.addNormalPayErrorLog(payOrder,e);
			logger.error("订单處理失败，订单信息："+payOrder, e);
			return InternationalUtil.handleException(e);
		}
		logger.info("订单入库成功，订单信息："+payOrder);
		return CodeMsg.success(CodeMsgType.SUCCESS);
	}

	


	@Override
	public void updateWarHeadInfo(PayOrder payOrder,UserIfm user) {
		
		float realMoney = payConfigManager.exchangeCurrencyToUSD(payOrder.getRealCurrency(), payOrder.getRealMoney());
		double[] warRadio = payConfigManager.getWarHeadIntegralRadio();
		float personMoneyRate = MathUtil.multiply(warRadio[0], realMoney);
		float publicMoneyRate = MathUtil.multiply(warRadio[1], realMoney);
		int personWarHead = MathUtil.multiply(personMoneyRate, payConfigManager.getWarheadPerson());
		int publicWarHead = MathUtil.multiply(publicMoneyRate, payConfigManager.getWarheadPublic());
		int sendScore = payConfigManager.computeSendScore(payOrder.getBeforeVipIntegral(), payOrder.getVipIntegral());
		// 计算积分
		int tickets = PayUtil.computeTickets(realMoney, user.getInfullAmount(),payConfigManager.getTicketIntegralRadioValue());

		int berforPersonIntegral = 0;
		try {
			UserControl userControl = userControlMapper.getUserControl(payOrder.getUserId());
			if(Objects.nonNull(userControl)) {
				berforPersonIntegral = userControl.getWarHeadIntegral();
				int beforeIntegral = userControl.getTicketIntegral();
				if(beforeIntegral < 0) {
					tickets += -beforeIntegral;
				}
				userControl.setTicketIntegral(tickets);
				userControl.setWarHeadIntegral(berforPersonIntegral + personWarHead);
				// 强发
				ForceCommand forceCommand = PayUtil.checkUserControl(userControl, sendScore);
				userControlMapper.updateUserControl(userControl);
				gmQueneManager.pushForceCommand(forceCommand);
				logService.addTiketInteralLog(payOrder.getUserId(), beforeIntegral, tickets);
			} else {
				logger.warn("查找用户【"+payOrder.getUserId()+"】弹头信息与话费积分记录为空");
			}
		} catch (Exception e) {
			payOrder.setFailureOrder();
			logger.error("更新用户【"+payOrder.getUserId()+"】弹头信息失败" , e);
			throw new SqlQueryException("更新用户的弹头信息失败");
		}
		
		try {
			incrCacheManager.updateWarHeadPool(publicWarHead);
			logService.addUserWarHeadLog(payOrder.getUserId(), berforPersonIntegral, incrCacheManager.getWarHeadPool(), realMoney, personWarHead, publicWarHead);
		} catch (Exception e) {
			payOrder.setFailureOrder();
			logger.error("更新世界【"+payOrder.getUserId()+"】弹头信息失败，"+e.getMessage() , e);
			throw new SqlQueryException("更新世界的弹头信息失败");
		}
		
	}

	
	@Override
	public CodeMsg<?> initCodaTranscation(PayOrder payOrder,String payType,HttpServletRequest request) {
		if (Objects.isNull(payType)) {
			logger.error("订单類型為空");
			return CodeMsg.failure(CodeMsgType.ERR_PAYTYPE);
		}
		if (Objects.isNull(payOrder.getUserId())) {
			logger.error("用戶ID為空");
			return CodeMsg.failure(CodeMsgType.ERR_USERID);
		}
		if (Objects.isNull(payOrder.getPointName())) {
			logger.error("付費點為空");
			return CodeMsg.failure(CodeMsgType.ERR_ORDER_POINTNAME);
		}
		PayPoint payPoint = null;
		try {
			payPoint = checkAndBindPayPointStatus(payOrder);
		} catch (Exception e) {
			return InternationalUtil.handleException(e,CurrencyConstants.Myanmar,translaionService);
		}
		if(Objects.isNull(payPoint)){
			logger.error("付費點 " + payOrder.getPointName() + " 不存在");
			return CodeMsg.failure(CodeMsgType.ERR_ORDER_POINTNAME);
		}
		try {
			UserIfm user = bindUserNickName(payOrder);
			payOrder.setUser(user);
		} catch (Exception e) {
			return InternationalUtil.handleException(e,CurrencyConstants.Myanmar,translaionService);
		}
		try {
			/*** Google Pay 订单号由服务端生成****/
			if(StringUtils.isBlank(payOrder.getOrderNum())) {
				String orderNum = PayUtil.createOrderNum(Integer.parseInt(payType), incrCacheManager.incrOrder());
				payOrder.setOrderNum(orderNum);
			}
		} catch (Exception e) {
			logger.error("调用Redis生成订单号失败，错误原因："+e.getMessage());
			return CodeMsg.failure(CodeMsgType.ERR_REDIS.getCode(),"生成订单号失败");
		}
		String ipAddress = HttpUtil.getIpAddress(request);
		payOrder.setIp(ipAddress);
		try {
			GeneralPayRequestHandler matchingHandler = requestHandlerAdapter.matchingHandler(String.valueOf(payType));
			if(Objects.nonNull(matchingHandler)) {
				String mnoId = request.getParameter("providerCode");
				CodeMsg<?> initTnxId = matchingHandler.initTnxId(payOrder, mnoId);
				if(initTnxId.getCode() != CodeMsgType.SUCCESS.getCode()) {
					logger.error(PayUtil.formatErrorMsg(initTnxId, payOrder.getPayType()));
					return initTnxId;
				}
				thirdPayChannel(payOrder,request);
				return initTnxId;
			}else {
				logger.error("当前订单类型不支持该支付接口，"+payOrder.getPayType());
				return CodeMsg.failure(CodeMsgType.ERR_PAYTYPE);
			}
		} catch (Exception e) {
			logger.error("支付失败，错误原因："+e.getMessage());
			return InternationalUtil.handleException(e,CurrencyConstants.Myanmar,translaionService);
		}
		
	}
	

	@Override
	public boolean isLegalRequest(PayOrder payOrder) {
		PayRequestHandler handler = requestHandlerAdapter.matchingHandler(String.valueOf(payOrder.getPayType()));
		return handler == null ? Boolean.FALSE : handler.isBackEnd();
	}

	private boolean isNeedCallBack(PayOrder payOrder) {
		PayRequestHandler handler = requestHandlerAdapter.matchingHandler(String.valueOf(payOrder.getPayType()));
		return handler == null ? Boolean.FALSE : handler.isNeedCallBack();
	}
	/**
	 * 	处理第三方请求订单
	 * @param payOrder
	 * @param type
	 * @param request
	 * @return
	 */
	public CodeMsg<?> processReqeust(PayOrder payOrder,int type, HttpServletRequest request) {
		GeneralPayRequestHandler handler = requestHandlerAdapter.matchingHandler(type+"");
		if(Objects.isNull(handler)) {
			return CodeMsg.failure(CodeMsgType.ERR_PAYTYPE);
		}
		logger.info("######## 开始处理第三方的订单请求 ########");
		CodeMsg<?> retCodeMsg = handler.processRequest(payOrder,request);
		logger.info("######## 停止处理第三方的订单请求 ########");
		return retCodeMsg;
	}

	
	/**
	 * 更新玩家道具信息
	 * @param payOrder
	 * @param paySuccessCommand
	 */
	public void updateUserPorps(PayOrder payOrder,List<UpdatePropsProcedure> updatePropsProcedures) {
		try {
			for(UpdatePropsProcedure updatePropsProcedure:updatePropsProcedures){
				userDBProcedureMapper.updateUserProps(updatePropsProcedure);
			}
		} catch (Exception e) {
			logger.error("調用存儲過程更新用戶道具信息失敗，"+e.getMessage());
			payOrder.setStatus(2);
			throw new SqlQueryException(e.getMessage());
		}
	}

	/**
	 * 	訂單入庫
	 * @param payOrder
	 */
	@Override
	public void addOrderSuccess(PayOrder payOrder) {
		try {
			payOrder.setOrder_date(new Date());
			payOrder.setStatus(PayStatusConstants.NEW_ORDER);
			payOrderMapper.insertPayOrder(payOrder);
		} catch (Exception e) {
			logger.error("订单入庫失败，订单信息："+SerializableUtil.objectToJsonStr(payOrder)+"，失败原因："+e.getMessage());
			throw new SqlQueryException(e.getMessage());
		}
		
	}
	
	public void addOrderFailure(PayOrder payOrder) {
		try {
			payOrder.setOrder_date(new Date());
			payOrder.setStatus(PayStatusConstants.FAILURE_ORDER);
			payOrderMapper.insertPayOrder(payOrder);
		} catch (Exception e) {
			logger.error("订单入庫失败，订单信息："+SerializableUtil.objectToJsonStr(payOrder)+"，失败原因："+e.getMessage());
			throw new SqlQueryException(e.getMessage());
		}
	}
	/**
	 * 数据库是否存在订单号
	 * @param payOrder
	 * @return
	 */
	@Override
	public boolean isHavePayOrder(PayOrder payOrder) {
		PayOrder payOrder2 = null;
		try {
			payOrder2 = payOrderMapper.getPayOrder(payOrder.getOrderNum());
		} catch (Exception e) {
			logger.error("查询订单信息失败，"+SerializableUtil.objectToJsonStr(payOrder)+"，失败原因："+e.getMessage());
		}
		return payOrder2 != null;
	}
	
	@Override
	public PayOrder getPayOrder(String orderNum) {
		try {
			return payOrderMapper.getPayOrder(orderNum);
		} catch (Exception e) {
			logger.error("查询订单信息失败，失败原因："+e.getMessage());
			return null;
		}
	}
	/**
	 *	 更改訂單狀態
	 * @param payOrder
	 * @param status
	 */
	public void updateOrderStatus(PayOrder payOrder,Integer status) {
		try {
			if(status == 1) {
				payOrder.setSuccess_date(new Date());
			}
			payOrder.setStatus(status);
			payOrderMapper.updatePayOrder(payOrder);
		} catch (Exception e) {
			logger.error("更新訂單 "+payOrder.getOrderNum()+" 狀態失敗，"+e.getMessage());
			payOrder.setStatus(PayStatusConstants.FAILURE_ORDER);
			throw new SqlQueryException(e.getMessage());
		}
		
	}
	/**
	 * redis 推送消息
	 * @param payOrder
	 * @param paySuccessCommand
	 */
	public void pushSuccessOrder(PayOrder payOrder,PaySuccessCommand paySuccessCommand) {
		/**推送成功信息**/
		try {
			if(Objects.nonNull(paySuccessCommand)) {
				gmQueneManager.pushPaySuccess(payOrder.getUserId(), paySuccessCommand);
				payOrder.setStatus(1);// 新订单
			}
		} catch (Exception e) {
			logger.error("推送Redis充值成功消息失败，"+e.getMessage());
			payOrder.setStatus(2);// 失敗訂單
			throw new RedisOptException(e.getMessage());
		}
	}
	
	public UserIfm bindUserNickName(PayOrder payOrder) {
		/****获取用户信息 （vip,vipIn）****/
		UserIfm user = null;
		if(Objects.isNull(payOrder.getUser())) {
			try {
				user = userMapper.getUserByUserId(payOrder.getUserId());
			} catch (Exception e) {
				logger.error("查找用戶失敗，訪問數據庫失敗，"+e.getMessage());
				throw new SqlQueryException(e.getMessage());
			}
			if(Objects.isNull(user)) {
				logger.error("玩家 "+payOrder.getUserId()+" 不存在");
				throw new ParamsException(CodeMsgType.ERR_USERID,"用戶不存在");
			}
			payOrder.setNickName(user.getNickName());
			payOrder.setUser(user);
		} else {
			user = payOrder.getUser();
			payOrder.setNickName(user.getNickName());
		}
		return user;
	}
	/**
	 * 	創建指令
	 * @param payOrder
	 * @return
	 */
	public PaySuccessCommand buildCommand(List<UpdatePropsProcedure> updatePropsProcedures,PayOrder payOrder,PayPoint payPoint,UserIfm user) {
		
		PaySuccessCommand paySuccessCommand = new PaySuccessCommand();
		/****记录玩家购买前的道具，VIP积分信息，不计失败**/
		logService.addUserPayLog(payOrder, user);
		
		try {
			Map<Integer, Integer> propMap = new HashMap<Integer, Integer>();
			verifyProps(propMap , user , payOrder, payPoint, paySuccessCommand);
			int obIntegral = verifyVip(propMap, user,payOrder,paySuccessCommand);
			// 积分入库 更新玩家积分，并记录道具日志
			addPropsProcedure(updatePropsProcedures, payOrder.getOrderNum(), PropConstants.VIP_INTEGRAL, obIntegral, user.getUserId()); 

			for (Integer id : propMap.keySet()) {
				addPropsProcedure(updatePropsProcedures, payOrder.getOrderNum(), id, propMap.get(id), payOrder.getUserId());
			}
			for (Integer id : paySuccessCommand.getUpgradeProps().keySet()) {
				addPropsProcedure(updatePropsProcedures, payOrder.getOrderNum(), id, paySuccessCommand.getUpgradeProps().get(id), payOrder.getUserId());
			}
		} catch (Exception e) {
			logger.error("构建redis指令失败失败，"+e.getMessage());
			throw e;
		}
		paySuccessCommand.setUserId(payOrder.getUserId());
		paySuccessCommand.setPointName(payOrder.getPointName());
		return paySuccessCommand;
	}

	/**
	 * 校验付费点的道具信息
	 * @param user
	 * @param payOrder
	 * @param paySuccessCommand
	 */
	public void verifyProps(Map<Integer, Integer> propMap,UserIfm user,PayOrder payOrder,PayPoint payPoint,
								PaySuccessCommand paySuccessCommand) {
		/****道具数量计算*****/
		computeProp(propMap, payPoint, payOrder);
		
		paySuccessCommand.setProps(propMap);
		/***不同玩家类型奖励道具***/
		payConfigManager.checkAwardCondition(payOrder.getPointName(), user.getUserType(), propMap);
		/***检查关卡计费点**/
		checkDynamicPoint(payOrder, paySuccessCommand);
		
	}
	/**
	 * 检查动态计费点
	 * @param payOrder
	 * @param paySuccessCommand
	 */
	private void checkDynamicPoint(PayOrder payOrder, PaySuccessCommand paySuccessCommand) {
		DynamicPayPoint dynamicPayPoint = payOrder.getDynamicPayPoint();
		if(dynamicPayPoint != null) {
			
			DynamicPointLog dynamicPointLog = new DynamicPointLog();
			// 基础信息
			dynamicPointLog.setUserId(payOrder.getUserId());
			dynamicPointLog.setOrderNum(payOrder.getOrderNum());
			dynamicPointLog.setDynamicId(dynamicPayPoint.getId());
			dynamicPointLog.setDynamicType(dynamicPayPoint.getType());
			dynamicPointLog.setUserLevel(payConfigManager.getLevel(payOrder.getUser().getExperience()));
			dynamicPointLog.setUserVip(payOrder.getUser().getVip());
			
			
			int dynamicPointGold = 0;
			if(dynamicPayPoint.getType() == PayMapConstants.PIG_WALLET) {
				UserStatus userStatus = userStatusMapper.getUserStatus(payOrder.getUserId());
				dynamicPointGold = payConfigManager.checkPigWallet(payOrder, paySuccessCommand, dynamicPayPoint, userStatus.getDepositGold(),dynamicPointLog);
			}
			if(dynamicPayPoint.getType() == PayMapConstants.LOGIN_SCROLL) {
				dynamicPointGold = payConfigManager.checkIfLoginScroll(payOrder, paySuccessCommand, dynamicPayPoint,dynamicPointLog);
			}
			if(dynamicPayPoint.getType() == PayMapConstants.MAP_POINT_CELL || dynamicPayPoint.getType() == PayMapConstants.MAP_POINT_TURN) {
				TempPayInfo currentMapPosition = payConfigManager.getCurrentMapPosition(payOrder.getUserId());
				if(currentMapPosition == null) {
					try {
						userStatusMapper.updateUseMapPoint();
					} catch (Exception e) {
						logger.info("订单号【"+payOrder.getOrderNum()+"】更新玩家地图计费点状态失败", e);
					}
				}
				dynamicPointGold =  payConfigManager.checkIfMapPoint(payOrder, paySuccessCommand, dynamicPayPoint,currentMapPosition, dynamicPointLog);
			}
			if(dynamicPayPoint.getType() == PayMapConstants.RANDON_TURNABLE_POSITION_11
					|| dynamicPayPoint.getType() == PayMapConstants.RANDON_TURNABLE_POSITION_12
					|| dynamicPayPoint.getType() == PayMapConstants.RANDON_TURNABLE_POSITION_13
					|| dynamicPayPoint.getType() == PayMapConstants.RANDON_TURNABLE_POSITION_14) {
				dynamicPointGold = payConfigManager.checkIfTurntables(payOrder, dynamicPayPoint, paySuccessCommand,dynamicPointLog);
			}
			if(dynamicPayPoint.getType() == PayMapConstants.THREE_IN_ONE_1 || dynamicPayPoint.getType() == PayMapConstants.THREE_IN_ONE_2) {
				dynamicPointGold = payConfigManager.checkThreeInOneGift(payOrder, paySuccessCommand, dynamicPayPoint,dynamicPointLog);
			}
			Integer goldHave = paySuccessCommand.getProps().get(PropConstants.GOLD);
			if(goldHave != null) {
				goldHave += dynamicPointGold;
			}else {
				goldHave = dynamicPointGold;
			}
			paySuccessCommand.setDynamicType(dynamicPayPoint.getType());
			paySuccessCommand.getProps().put(PropConstants.GOLD, goldHave);
			
			logService.addDynamicPointLog(dynamicPointLog);
		}
	}

	/**
	 *  验证vip 等级
	 * @param user
	 * @param payOrder
	 * @param paySuccessCommand
	 */
	public int verifyVip(Map<Integer, Integer> propMap , UserIfm user,PayOrder payOrder, PaySuccessCommand paySuccessCommand) {
		payOrder.setBeforeInfullAmount(user.getInfullAmount());
		payOrder.setBeforeVipIntegral(user.getVipIntegral());
		/****计算积分*****/
		int obIntegral = payConfigManager.getCurrencyVipIntegral(payOrder.getRealCurrency(), payOrder.getRealMoney());
		Integer propIntegral = propMap.get(PropConstants.VIP_INTEGRAL);
		int totalIntegral = user.getVipIntegral() + obIntegral;
		if(propIntegral != null) {
			totalIntegral += propIntegral.intValue();
		}
		user.setVipIntegral(totalIntegral);

		Map<Integer, Integer> vipPropsMap = new HashMap<Integer, Integer>();
		int vipLevel = payConfigManager.getVipLevel(user.getVipIntegral());
		int vip = user.getVip();
		if(vip < vipLevel) {
			user.setVip(vipLevel);
			paySuccessCommand.setVip(vipLevel);
			for (int i = vip+1; i <= vipLevel; i++) {
				Integer cannonPropId = PropVipConstants.propsCannonMap.get(i);
				if(cannonPropId != null) {
					vipPropsMap.put(cannonPropId, 1);
				}
				Integer iconPropId = PropVipConstants.propsIconMap.get(i);
				if(iconPropId != null) {
					vipPropsMap.put(iconPropId, 1);
				}
			}
		}
		paySuccessCommand.setUpgradeVip(vipPropsMap.isEmpty()?0:1);// 不升VIP
		paySuccessCommand.setVip(user.getVip());
		paySuccessCommand.setVipIntegral(user.getVipIntegral());
		paySuccessCommand.setUpgradeProps(vipPropsMap);
		
		payOrder.setVipIntegral(obIntegral);
		logger.info("玩家："+user.getUserId()+"，付费点："+payOrder.getPointName() + "，获得道具："+propMap);
		try {
			userMapper.updateUserInfullAmount(user.getUserId() , payConfigManager.exchangeCurrencyToUSD(payOrder.getRealCurrency(), payOrder.getRealMoney()));
		} catch (Exception e) {
			logger.error("更新用戶VIP等級，充值金額失敗("+user.getInfullAmount()+")", e);
			throw new SqlQueryException(e.getMessage());
		}
		return obIntegral;
	}
	/**
	 *	 创建道具更新的存储过程参数
	 * @param propId
	 * @param amount
	 * @param userId
	 * @return
	 */
	public void addPropsProcedure(List<UpdatePropsProcedure> updatePropsProcedures, String orderNum,int propId,int amount,int userId) {
		UpdatePropsProcedure updatePropsProcedure = new UpdatePropsProcedure();
		updatePropsProcedure.setiRelatedInfo(orderNum);
		updatePropsProcedure.setiAmount(amount);
		updatePropsProcedure.setiUserID(userId);
		updatePropsProcedure.setiPropID(propId);
		updatePropsProcedure.setiType(301);
		updatePropsProcedures.add(updatePropsProcedure);
	}
	/**
	 * 查询使用户是否购买过
	 * @param payOrder
	 * @return
	 */
	public Boolean isFirstPay(PayOrder payOrder) {
		UserPayPoint userPayPoint = null;
		try {
			userPayPoint = userPayPointMapper.getUserPayPointsByUserIdAndPoint(payOrder.getUserId(), payOrder.getPointName());
			if(userPayPoint == null || userPayPoint.getBuyCount() == 0) {
				return Boolean.TRUE;
			} else {
				return Boolean.FALSE;
			}
		} catch (Exception e) {
			logger.error("查找用户订单信息失败，访问数据库失败，"+e.getMessage());
			throw new SqlQueryException(e.getMessage());
		}
		
	}
	/**
	 * 验证购买
	 * @param payOrder
	 * @param payPoint
	 */
	@Override
	public PayPoint checkAndBindPayPointStatus(PayOrder payOrder) {
		String pointName = payOrder.getPointName();
		if(StringUtils.isBlank(pointName)) {
			logger.warn("付費點 为空无法验证");
			return null;
		}
		PayPoint payPoint = payConfigManager.getPayPoint(pointName);
		if(Objects.isNull(payPoint)){
			logger.error("付費點 " + payOrder.getPointName() + " 不存在");
			throw new ParamsException(CodeMsgType.ERR_ORDER_POINTNAME,"付费点不存在");
		}
		
		if(payPoint.getLimitDay() == 0) {
			checkDynamicPayPoint(payOrder);
			return payPoint;
		}
		PayOrder payOrderHave = payOrderMapper.getPayOrdersByPointName(payOrder.getUserId(),payOrder.getPointName());
		logger.info("历史订单信息：" + payOrderHave);
		if(Objects.isNull(payOrderHave)) {
			return payPoint;
		} else {
			if(PayUtil.checkOrderDate(payOrderHave.getOrder_date(),payPoint.getLimitDay())) {
				return payPoint;
			}else {
				logger.error("付费点【"+pointName+"】购买次数限制为"+payPoint.getLimitDay()+"天，无法完成购买");
				throw new ParamsException(CodeMsgType.ERR_SYS_PARAMS_POINTNAME_HAVE_PURCHASE);
			}
		}
	}
	/**
	 * 检查动态计费点的时间限制
	 * @param payOrder
	 * @param historyPayOrder
	 */
	private void checkDynamicPayPoint(PayOrder payOrder) {
		if(payOrder.getDynamicId() > 0) {
			DynamicPayPoint dynamicPayPoint = payConfigManager.getDynamicPayPointById(payOrder.getDynamicId());
			if(dynamicPayPoint == null) {
				logger.error("动态付費點 " + payOrder.getPointName() + " 不存在");
				throw new ParamsException(CodeMsgType.ERR_ORDER_POINTNAME,"付费点不存在");
			}
			int limitDay = dynamicPayPoint.getLimitDay();
			PayOrder payOrderHave = payOrderMapper.getPayOrdersByDynamicId(payOrder.getUserId(), payOrder.getDynamicId());
			if(payOrderHave != null) {
				if(!PayUtil.checkOrderDate(payOrderHave.getOrder_date(), limitDay)) {
					logger.error("动态付费点【"+payOrder.getPointName()+"】购买次数限制为"+limitDay+"天，无法完成购买");
					throw new ParamsException(CodeMsgType.ERR_SYS_PARAMS_POINTNAME_HAVE_PURCHASE);
				}
			}
			payOrder.setDynamicPayPoint(dynamicPayPoint);
		}
	}
	@Override
	public PayOrder getPayOrderByThirdOrderNum(String thirdOrderNum) {
		List<PayOrder> payOrderByThirdNum = payOrderQueryService.getPayOrderByThirdNum(thirdOrderNum);
		if(!payOrderByThirdNum.isEmpty()) {
			for(int index = 0;index < payOrderByThirdNum.size();index++) {
				PayOrder payOrder = payOrderByThirdNum.get(index);
				if(payOrder.getStatus() == PayStatusConstants.SUCCESS_ORDER) {
					return payOrder;
				}
			}
		}
		return null;
	}
	
	private Map<Integer, Integer> computeProp(Map<Integer, Integer> propMap , PayPoint payPoint , PayOrder payOrder) {
		/****道具数量计算*****/
		Integer gold = payPoint.getGold(); // 金币
		Integer diamond = payPoint.getDiamond(); // 钻石
		Boolean firstDouble = payPoint.getFirstDouble(); // 是否首充翻倍
		Integer extraSend = payPoint.getExtraSend(); // 额外赠送
		Integer propBagId = payPoint.getPropBag(); // 道具包 ID
		Boolean firstPay = isFirstPay(payOrder);
		// 双倍充值活动
		boolean hasActivity = activityTimeComponent.checkPayDoubleActivity(payOrder.getPayType());
		Object[] currencyMoney = getRealMoneyDiff(payPoint, payOrder.getRealMoney() , payOrder.getRealCurrency());
		if((float)currencyMoney[1] >= 0) {
			putScoreAndDiamond(propMap, gold , diamond , extraSend , firstPay , firstDouble  , hasActivity);
			if(propBagId != 0) {
				List<PropBag> propBags = getPropBag(propBagId , payOrder.getOrderNum() , payOrder.getPointName());
				putBag(propMap , propBags);
			}
			if((float)currencyMoney[1] > 0) {
				
				int[] intGold = null;
				if(payConfigManager.checkGoldPayPoint(payOrder.getPointName())) {
					intGold = payConfigManager.computSurplusGold(payOrder, payPoint, Boolean.FALSE); // 这里不需要转换为美元就可以计算比例
				}else {
					intGold = payConfigManager.computSurplusGold(payOrder,(float)currencyMoney[1], (String)currencyMoney[0], null, Boolean.FALSE);
				}
				putScoreAndDiamond(propMap, intGold[0] , 0 , intGold[1] , firstPay , firstDouble  , hasActivity);
			}
		}else{
			// 新手礼包
			Map<Integer, Integer> newGiftProps = payConfigManager.checkAndGetNewGiftProps(payOrder.getRealMoney(), payOrder.getRealCurrency(), payPoint);
			if(Objects.nonNull(newGiftProps)) {
				propMap.putAll(newGiftProps);
				return newGiftProps;
			}else {
				int[] intGold = payConfigManager.computSurplusGold(payOrder, null, Boolean.TRUE);
				if(payOrder.getDynamicPayPoint() != null || payOrder.getDynamicId() > 0) {
					payOrder.setDynamicPayPoint(null);
					payOrder.setDynamicId(0);
				}
				logService.addPointNameChangedPayErrorLog(payOrder, payPoint);
				putScoreAndDiamond(propMap, intGold[0] , 0 , intGold[1] , firstPay , firstDouble  , hasActivity);
			}
		}
		return propMap;
	}
	
	public void putBag(Map<Integer, Integer> propMap, List<PropBag> propBags) {
		for(PropBag propBag:propBags) {
			Integer propID = propBag.getPropID();
			if(propBag.getBagID() == Constants.MOTH_POINT && 
					propID == Constants.MOTH_DAY_POINT) {
				List<PropBag> propBag_Days = payConfigManager.getPropBag(propID);
				for(PropBag propBagMonth : propBag_Days) {
					putProp(propMap,propBagMonth.getPropID(),propBagMonth.getAmount());
				}
			}else {
				putProp(propMap,propBag.getPropID(),propBag.getAmount());
			}
		}
	}
	
	public void putScoreAndDiamond(Map<Integer, Integer> propMap, int gold , int diamond , int extraSend , boolean firstPay , boolean firstDouble , boolean hasActivity) {
		if(firstPay && firstDouble || hasActivity) { // 判断用户是否是第一次钩买该付费点
			if(gold != 0) {
				gold *= 2;
			}
			if(diamond != 0) {
				diamond *= 2;
			}
		}else {
			if(gold != 0) {
				gold += extraSend;
			}
			if(diamond != 0) {
				diamond += extraSend;
			}
		}
		if(gold > 0) {
			putProp(propMap,PropConstants.GOLD,gold);
		}
		if(diamond > 0) {
			putProp(propMap,PropConstants.DIAMOND,diamond);
		}
	}
	
	public void putProp(Map<Integer, Integer> propMap, Integer propId , Integer amount) {
		Integer value = propMap.get(propId);
		if(value == null) {
			propMap.put(propId, amount);
		}else {
			propMap.put(propId, value+amount);
		}
	}
	
	private List<PropBag> getPropBag(int propBagId , String orderNum , String pointName){
		List<PropBag> propBags = payConfigManager.getPropBag(propBagId);
		if(propBags == null || propBags.isEmpty()) {
			logger.error("订单号："+orderNum+",付费点："+pointName+",礼包ID："+propBagId+" 未查找到礼包信息*****");
		}
		return propBags;
	}
	
	private Object[] getRealMoneyDiff(PayPoint point , Float realMoney, String realCurrency) {
		float money = payConfigManager.getPayPointMoney(point, realCurrency);
		if(money < 0.001) { 
			Float usd = payConfigManager.getPointExistMoney(point);
			Float realUsd = payConfigManager.exchangeCurrencyToUSD(realCurrency, realMoney);
			checkMoneyDiff(usd, realUsd , CurrencyConstants.USA);
			return new Object[] {CurrencyConstants.USA, realUsd-usd};
		} else {
			checkMoneyDiff(money, realMoney , realCurrency);
		}
		return new Object[] {realCurrency, realMoney-money};
	}
	
	private void checkMoneyDiff(Float money, Float realMoney, String currency) {
		if(realMoney < money) {
			logger.warn("输入充值金额大于实际金额 货币："+currency+"，实际金额："+realMoney+"，付费点金额："+money);
			if(realMoney == 0) {  // 属于测试过程遇到的错误，正式环境不会出现
				logger.error("实际输入金额为0，无法完成支付");
				throw new ThirdPayException(CodeMsgType.ERR_ORDER_MONEY.getCode(),CodeMsgType.ERR_ORDER_MONEY.getMsg());
			}
		}
	}
	
	@Autowired
	public IncrCacheManager incrCacheManager;
	@Autowired
	public GMQueneManager gmQueneManager;
	@Autowired
	public PayOrderMapper payOrderMapper;
	@Autowired
	public UserMapper userMapper;
	@Autowired
	public UserControlMapper userControlMapper;
	@Autowired
	public WarheadPoolLogMapper warHeadPoolLogMapper;
	@Autowired
	public UserPayPointMapper userPayPointMapper;
	@Autowired
	public UserDBProcedureMapper userDBProcedureMapper;
	@Autowired
	public RequestHandlerAdapter requestHandlerAdapter;
	@Autowired
	public CodaPayMyanmarRequestPageHandler codaPayMyanmarRequestPageHandler;
	@Autowired
	public CurrencyExchangeMapper currencyExchangeMapper;
	@Autowired
	public PayUserLogMapper payUserLogMapper;
	@Autowired
	public PayErrorLogMapper payErrorLogMapper;
	@Autowired
	private ITranslationService translaionService;
	@Autowired
	private ActivityTimeComponent activityTimeComponent;
	@Autowired
	private IPayOrderQueryService payOrderQueryService;
	@Resource
	private ILogService logService;
	@Autowired
	private PayConfigManager payConfigManager;
	@Autowired
	private UserStatusMapper userStatusMapper;
}
