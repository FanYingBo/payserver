package com.last.pay.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.last.pay.base.CodeMsg;
import com.last.pay.base.common.constants.Constants.PayTypeConstants;
import com.last.pay.cache.command.ForceCommand;
import com.last.pay.core.db.pojo.user.UserControl;
import com.last.pay.core.service.impl.PayOrderServiceImpl;

public class PayUtil {
	
	public static final Log logger = LogFactory.getLog(PayOrderServiceImpl.class);
	
	/**
	 * 判断是否可购买
	 */
	public static boolean checkOrderDate(Date date,int days) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DAY_OF_MONTH, days);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND,0);
		c.set(Calendar.MILLISECOND,0);
		Date now = new Date();
		return now.after(c.getTime());
	}
	
	/**
	 * 创建订单号
	 * @param type
	 * @return
	 */
	public static String createOrderNum(int type, long incr) {
	    int _t = type % 26;
	    char c = (char)('A'+_t);
	    StringBuffer o = new StringBuffer();
	    o.append(String.valueOf(c));
	    o.append(System.currentTimeMillis()/10);
	    o.append(String.format("%03d", incr%1000));
	    return o.toString();
	  }

	/**
	 * 计算金币的方式
	 * @param gold
	 * @return
	 */
	public static int getIntGoldNum(double gold) {
		int unit = (gold < 1000000)?1000:10000;
		BigDecimal decimal = new BigDecimal(gold/unit);
		return decimal.setScale(0, RoundingMode.CEILING).intValue()*unit;
	}
	
	/**
	 * 计算话费积分
	 * @param realMoney
	 * @param ticketIntegralRadio
	 * @return
	 */
	public static int computeTickets(float realMoneyUSD , float beforeInfullAmount,String ticketParam) {
		try {
			Map<String, Object> jsonStrToMap = SerializableUtil.jsonStrToMap(ticketParam);
			Map<String,Double> dataMap = (Map<String,Double>)jsonStrToMap.get("pay");
			int le6 = dataMap.get("le6").intValue();
			int le40 = dataMap.get("le40").intValue();
			int gt40 = dataMap.get("gt40").intValue();
			float money = realMoneyUSD;
			// 之前充值的金额
			float amountMoney = beforeInfullAmount;
			int tickets = 0;
			if(amountMoney > 0) {
				if (amountMoney <= 6) {
					if(amountMoney + money <= 6) {
						tickets += money * le6;
					}else if (amountMoney + money > 6 && amountMoney + money <= 40) {
						tickets += (6 - amountMoney) * le6 + (money - (6 - amountMoney)) * le40;
					} else{
						tickets += (6 - amountMoney) * le6 + 34  * le40 + (amountMoney + money -40) * gt40;
					}
				} else if(amountMoney > 6 && amountMoney <= 40){
					if(amountMoney + money <= 40) {
						tickets += money * le40;
					} else {
						tickets += ((34 - (amountMoney-6)) * le40 + (amountMoney + money - 40) * gt40);
					}
				} else if(amountMoney > 40) {
					tickets += money * gt40;
				}
			} else { // 金额为0
				if(money <= 6){ // 充值金额小于6
					tickets += (money * le6);
				}else if(money > 6 && money <= 40){// 充值金额小于等于6
					tickets += (6 * le6 + (money - 6) * le40);
				}else {
					tickets += (6 * le6 + 34 * le40 + (money - 40) * gt40);
				}
			}
			return tickets;
		} catch (Exception e) {
			return 0;
		}
	}	

	/**
	 *  检查设置请发
	 * @param userControl
	 * @param sendScore
	 */
	public static final ForceCommand checkUserControl(UserControl userControl,int sendScore) {
		ForceCommand command = new ForceCommand();
		// 强发状态
		if(userControl.getWinScore() <= 0 && userControl.getLoseScore() <= 0 && userControl.getEatScore() <= 0 && userControl.getSendScore() > 0) {
			userControl.setSendScore(userControl.getSendScore()+ sendScore);
		}else {
			if(sendScore > 0) {
				userControl.setSendScore(sendScore);
				userControl.setLoseScore(0);
				userControl.setWinScore(0);
				userControl.setEatScore(0);
				userControl.setShotCount(0);
				userControl.setNetScore(0);
			}
		}
		command.setUserId(userControl.getUserId());
		command.setSendScore(userControl.getSendScore());
		command.setEatScore(userControl.getEatScore());
		return command;
	}
	/**
	 * 检查是否可以补单或者redis
	 * @param payType
	 * @return
	 */
	public static boolean redisCachePay(int payType) {
		return payType == PayTypeConstants.IOS
				|| payType == PayTypeConstants.GOOGLE 
				|| payType == PayTypeConstants.Myanmar_CODAPAY_SMS 
				|| payType == PayTypeConstants.Myanmar_CODAPAY_WAVE
				|| payType == PayTypeConstants.Myanmar_CODAPAY_Vouchers
				|| payType == PayTypeConstants.HUAWEI_INTERNATIONAL
				|| payType == PayTypeConstants.HUAWEI_INLAND
				|| payType == PayTypeConstants.Vietnam_CARD
				|| payType == PayTypeConstants.VIETNAM_UPAY_EBANK
				|| payType == PayTypeConstants.Cambodia_CODAPAY_SMS
				|| payType == PayTypeConstants.Cambodia_CODAPAY_Wing
				|| payType == PayTypeConstants.Thailand_CODAPAY_SMS
				|| payType == PayTypeConstants.Thailand_CODAPAY_TrueMoneyCashCard
				|| payType == PayTypeConstants.Thailand_CODAPAY_TrueMoneyWallet
				|| payType == PayTypeConstants.Thailand_CODAPAY_7_ElevenTH
				|| payType == PayTypeConstants.Thailand_Bank_Transfers_and_cash
				|| payType == PayTypeConstants.Thailand_Rabbit_LINE_Pay
				;
	}
	/**
	 * 检查是否是CodaPay
	 * @param payType
	 * @return
	 */
	public static boolean checkIfCodaPay(int payType) {
		return payType == PayTypeConstants.Myanmar_CODAPAY_Vouchers
				|| payType == PayTypeConstants.Myanmar_CODAPAY_SMS 
				|| payType == PayTypeConstants.Myanmar_CODAPAY_WAVE 
				|| payType == PayTypeConstants.Cambodia_CODAPAY_SMS
				|| payType == PayTypeConstants.Cambodia_CODAPAY_Wing;
	}
	/**
	 * 检查是否是PayCent
	 * @param payType
	 * @return
	 */
	public static boolean checkIfPayCent(int payType) {
		return payType == PayTypeConstants.PAYCENT;
	}
	
	public static String formatErrorMsg(CodeMsg<?> codeMsg, int payType) {
		String errorMsg = "";
		if(checkIfCodaPay(payType)) {
			errorMsg = "CodaPay初始化事务失败";
		}
		if(checkIfPayCent(payType)) {
			errorMsg = "PayCent初始化事务失败";
		}
		return formatErrorMsg(codeMsg,errorMsg);
	}
	
	public static String formatErrorMsg(CodeMsg<?> codeMsg,String errorMsg) {
		return errorMsg+"，"+codeMsg.getCode()+"，错误信息："+codeMsg.getMsg();
	}
}
