package com.last.pay.core.service.impl;

import java.util.Date;
import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.last.pay.base.common.constants.Constants.CurrencyConstants;
import com.last.pay.core.db.mapper.logdb.DynamicPointLogMapper;
import com.last.pay.core.db.mapper.logdb.PayUserLogMapper;
import com.last.pay.core.db.mapper.logdb.TicketIntegralLogMapper;
import com.last.pay.core.db.mapper.logdb.WarheadPoolLogMapper;
import com.last.pay.core.db.pojo.log.DynamicPointLog;
import com.last.pay.core.db.pojo.log.PayErrorLog;
import com.last.pay.core.db.pojo.log.PayUserLog;
import com.last.pay.core.db.pojo.log.TicketIntegralLog;
import com.last.pay.core.db.pojo.log.WarheadPoolLog;
import com.last.pay.core.db.pojo.user.UserIfm;
import com.last.pay.core.db.pojo.user.UserPayPoint;
import com.last.pay.core.db.pojo.web.PayOrder;
import com.last.pay.core.db.pojo.web.PayPoint;
import com.last.pay.core.component.PayConfigManager;
import com.last.pay.core.db.mapper.userdb.UserPayPointMapper;
import com.last.pay.core.service.ILogService;
import com.last.pay.core.service.IPayErrorLogService;

@Service
public class LogServiceImpl implements ILogService{
	
	private static final Log logger = LogFactory.getLog(LogServiceImpl.class);
	
	@Autowired
	private TicketIntegralLogMapper ticketIntegralLogMapper;
	@Autowired
	private IPayErrorLogService payErrorLogService;
	@Resource
	private UserPayPointMapper userPayPointMapper;
	@Resource
	private WarheadPoolLogMapper warHeadPoolLogMapper;
	@Autowired
	public PayUserLogMapper payUserLogMapper;
	@Autowired
	public DynamicPointLogMapper dynamicPointLogMapper;
	
	@Resource
	private PayConfigManager payConfigManager;

	protected LinkedList<Object> list = new LinkedList<Object>();
	
	private	ExecutorService newCachedThreadPool = Executors.newCachedThreadPool();

	
	@PostConstruct
    public void taskStart(){
		newCachedThreadPool.execute(new Runnable() {
			
			@Override
			public void run() {
				while (true) {
					try {
						popEvent();
					} catch (Exception e) {
						e.printStackTrace();
						logger.error("pop event error ", e);
					}
				}
			}
		});
    }


	protected void addLog(Object t) {
		try {
			synchronized (this) {
				list.add(t);
				try {
					this.notify();
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("notify error ", e);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("add event error ", e);
		}
		
	}


	private void popEvent() throws InterruptedException {
		Object t = null;
		if(list.isEmpty()) {
			synchronized (this) {
				try {
					this.wait();
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("wait error ", e);
				}
			}
		}
		synchronized (this) {
			t = list.poll();
		}
		if(t != null) {
			try {
				asynHandlerEvent(t);
			} catch (Exception e2) {
				e2.printStackTrace();
				logger.error("handler log event error ", e2);
			}
		}
	}
	
	public void asynHandlerEvent(final Object o) {
		newCachedThreadPool.execute(new Runnable() {
			
			@Override
			public void run() {
				try {
					handlerEvent(o);
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("pop event error ", e);
				}
			}
		});
	}
	
	public void handlerEvent(final Object o) {
		if (o instanceof TicketIntegralLog) {
			ticketIntegralLogMapper.insertTicketIntegeralLog((TicketIntegralLog)o);
		}else if(o instanceof UserPayPoint) {
			userPayPointMapper.insertUserPayPoint((UserPayPoint)o);
		}else if(o instanceof PayErrorLog) {
			payErrorLogService.addPayErrorLog((PayErrorLog)o);
		}else if(o instanceof PayUserLog) {
			payUserLogMapper.insertPayUserLog((PayUserLog)o);
		}else if(o instanceof WarheadPoolLog) {
			warHeadPoolLogMapper.insertWarheadPoolLog((WarheadPoolLog)o);
		}else if(o instanceof DynamicPointLog) {
			dynamicPointLogMapper.addDynamicPointLog((DynamicPointLog)o);
		}
	}
	
	/**
	 * 	添加话费变更日志
	 * @param ticketIntegralLog
	 */
	public void addTiketInteralLog(Integer userId, Integer beforeIntegral, Integer integral) {
		addLog(new TicketIntegralLog(userId, beforeIntegral, beforeIntegral, 1, new Date()));
	}

	/**
	 * 	玩家购买付费点信息入库
	 * @param payOrder
	 * @param pointCount
	 */
	public void addUserPayPointLog(PayOrder payOrder) {
		try {
			int buyCountNow = 1;
			if(Objects.nonNull(payOrder.getStatus()) && payOrder.getStatus() == 1) {
				UserPayPoint payLog = userPayPointMapper.getUserPayPointsByUserIdAndPoint(payOrder.getUserId(), payOrder.getPointName());
				if(payLog != null) {
					userPayPointMapper.updateUserPayPoint(payOrder.getUserId(), payOrder.getPointName());
				} else {
					UserPayPoint userPayPoint = new UserPayPoint();
					userPayPoint.setPointName(payOrder.getPointName());
					userPayPoint.setBuyCount(buyCountNow);
					userPayPoint.setBuyDate(new Date());
					userPayPoint.setReceiveDate(new Date());
					userPayPoint.setUserid(payOrder.getUserId());
					if(payOrder.getDynamicPayPoint() != null) {
						userPayPoint.setType(payOrder.getDynamicPayPoint().getType());
					}else {
						userPayPoint.setType(0);
					}
					addLog(userPayPoint);
				}
			}
		} catch (Exception e) {
			logger.error("玩家购买付费点记录日志失败 ", e);
		}
	}
	
	/**
	 * 记录弹头变化日志
	 * @param payOrder
	 */
	public void addUserWarHeadLog(Integer userId, Integer personPool, Long publicPool, Float money,Integer add_personPool, Integer add_publicPool) {
		addLog(new WarheadPoolLog(userId, 1, personPool.longValue(), publicPool, money, add_personPool, add_publicPool, new Date()));
	}

	/**
	 * 
	 * @param payOrder
	 * @param oldPayPoint
	 */
	public void addPointNameChangedPayErrorLog(PayOrder payOrder,PayPoint oldPayPoint) {
		payOrder.setErrorInfo("支付金额不满足付费点金额");
		addPayErrorLog(payOrder, oldPayPoint, null);
	}
	/**
	 * @param payOrder
	 * @param e
	 */
	public void addNormalPayErrorLog(PayOrder payOrder,Exception e) {
		addPayErrorLog(payOrder, null, e);
	}
	
	/**
	 * 	记录玩家的错误支付订单
	 * @param payOrder
	 * @param e
	 */
	public void addPayErrorLog(PayOrder payOrder,PayPoint oldPayPoint, Exception e) {
		PayErrorLog payErrorLog = new PayErrorLog();
		if(Objects.isNull(payOrder.getErrorInfo())) {
			if(e != null) {
				payErrorLog.setErrorInfo("订单处理失败，失败原因:"+e.getMessage());
			}else {
				payErrorLog.setErrorInfo("未知错误");
			}
		}else {
			payErrorLog.setErrorInfo(payOrder.getErrorInfo());
		}
		payErrorLog.setChannel(payOrder.getChannel());
		payErrorLog.setCurrency(CurrencyConstants.USA);
		payErrorLog.setIp(payOrder.getIp());
		payErrorLog.setLogDate(new Date());
		payErrorLog.setMoney(payConfigManager.getPointMoneyExchangeUSD(payOrder.getPointName()));
		payErrorLog.setNickName(payOrder.getNickName());
		payErrorLog.setOrderNum(payOrder.getOrderNum());
		payErrorLog.setPointName(Objects.isNull(oldPayPoint) ? payOrder.getPointName() : oldPayPoint.getName());
		payErrorLog.setPlatform(payOrder.getPlatform());
		payErrorLog.setPayType(payOrder.getPayType());
		payErrorLog.setRealCurrency(StringUtils.isBlank(payOrder.getRealCurrency()) ? "":payOrder.getRealCurrency());
		payErrorLog.setRealMoney(Objects.isNull(payOrder.getRealMoney()) ? 0:payOrder.getRealMoney());
		payErrorLog.setThirdOrderNum(Objects.isNull(payOrder.getErrorThirdNum()) ? payOrder.getThird_order_num():payOrder.getErrorThirdNum());
		payErrorLog.setUserId(payOrder.getUserId());
		payErrorLog.setDynamicId(payOrder.getDynamicId() == null ? 0: payOrder.getDynamicId());
		addLog(payErrorLog);
	}
	
	public void addUserPayLog(PayOrder payOrder,UserIfm userIfm) {
		PayUserLog userLog = new PayUserLog();
		userLog.setBeforeGold(userIfm.getScore());
		userLog.setBeforeDiamond(userIfm.getDiamond());
		userLog.setBeforeExperience(userIfm.getExperience());
		userLog.setBeforeCannon(userIfm.getLockCannon());
		userLog.setBeforeVipIntegral(userIfm.getVipIntegral());
		userLog.setBeforePlayTime(userIfm.getPlayTime());
		
		userLog.setUserId(userIfm.getUserId());
		userLog.setOrderNum(payOrder.getOrderNum());
		userLog.setPointName(payOrder.getPointName());
		userLog.setLogDate(new Date());
		addLog(userLog);
	}


	@Override
	public void addDynamicPointLog(DynamicPointLog dynamicPointLog) {
		addLog(dynamicPointLog);
	}

	
}
