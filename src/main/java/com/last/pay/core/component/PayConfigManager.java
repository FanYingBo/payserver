package com.last.pay.core.component;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.last.pay.base.common.constants.PayMapConstants;
import com.last.pay.base.common.constants.Constants.CurrencyConstants;
import com.last.pay.base.common.constants.Constants.PayPointTypeConstants;
import com.last.pay.base.common.constants.Constants.PropConstants;
import com.last.pay.cache.UserPayCacheManager;
import com.last.pay.cache.command.PaySuccessCommand;
import com.last.pay.cache.mode.TempPayInfo;
import com.last.pay.core.db.pojo.PropAmount;
import com.last.pay.core.db.pojo.game.International;
import com.last.pay.core.db.pojo.game.MapPoint;
import com.last.pay.core.db.pojo.game.PropBag;
import com.last.pay.core.db.pojo.game.Turntable;
import com.last.pay.core.db.pojo.game.VipConfig;
import com.last.pay.core.db.pojo.log.DynamicPointLog;
import com.last.pay.core.db.pojo.web.DynamicPayPoint;
import com.last.pay.core.db.pojo.web.PayOrder;
import com.last.pay.core.db.pojo.web.PayPoint;
import com.last.pay.core.component.load.DynamicMayTurnPayPointConfiguration;
import com.last.pay.core.component.load.InternationalConfiguration;
import com.last.pay.core.component.load.LevelConfiguration;
import com.last.pay.core.component.load.ParamConfiguration;
import com.last.pay.core.component.load.PayAwardConfiguration;
import com.last.pay.core.component.load.PayParamConfiguration;
import com.last.pay.core.component.load.PayPointConfiguration;
import com.last.pay.core.component.load.PropBagConfiguration;
import com.last.pay.core.component.load.VipConfiguration;
import com.last.pay.core.component.sys.SystemConfiguration;
import com.last.pay.core.component.third.CambodiaCodaPayConfiguration;
import com.last.pay.core.component.third.MyanmarCodaPayConfiguration;
import com.last.pay.core.component.third.VietnamPayConfiguration;
import com.last.pay.util.InternationalUtil;
import com.last.pay.util.PayUtil;
import com.last.pay.util.SerializableUtil;
/**
 * 	所有参数的管理
 * @author Administrator
 *
 */
@Component
public class PayConfigManager {
	
	private static final Log logger = LogFactory.getLog(PayConfigManager.class);
	@Autowired
	private VipConfiguration vipConfiguration;
	@Autowired
	private ParamConfiguration paramConfiguration;
	@Autowired
	private PayPointConfiguration payPointConfiguration;
	@Autowired
	private PayParamConfiguration payParamConfiguration;
	@Autowired
	private InternationalConfiguration internationalConfiguration;
	@Autowired
	private PropBagConfiguration propBagConfiguration;
	@Autowired
	private VietnamPayConfiguration vietnamPayConfiguration;
	@Autowired
	private MyanmarCodaPayConfiguration myanmarCodaPayConfiguration;
	@Autowired
	private CambodiaCodaPayConfiguration cambodiaCodaPayConfiguration;
	@Autowired
	private PayAwardConfiguration apyAwardConfiguration;
	@Autowired
	private SystemConfiguration systemConfiguration;
	@Autowired
	private DynamicMayTurnPayPointConfiguration dynamicMayTurnPayPointConfiguration;
	@Autowired
	private UserPayCacheManager userPayCacheManager;
	@Autowired
	private LevelConfiguration levelConfiguration;
	
	/**
	 *	获取VIP积分配置
	 * @return
	 */
	public TreeMap<Integer,VipConfig> getVipLevelMap(){
		return vipConfiguration.getVipLevelMap();
	}
	
	public VipConfig getVipConfig(Integer vipIntegral) {
		Entry<Integer, VipConfig> floorEntry = getVipLevelMap().floorEntry(vipIntegral);
		return floorEntry != null ? floorEntry.getValue() : null;
	}
	/**
	 *  根据VIP 积分获取VIP等级
	 * @param vipIntegral
	 * @return
	 */
	public int getVipLevel(Integer vipIntegral) {
		VipConfig vipConfig = getVipConfig(vipIntegral);
		if (vipConfig != null) {
			return vipConfig.getLevel();
		}
		return 0;
	}
	/**
	 * 	充值获得强发分数
	 * @param beforeVip 充值前vip积分
	 * @return
	 */
	public int computeSendScore(int beforeVipIntegral,int integral) {
		VipConfig vCipConfig = getVipConfig(beforeVipIntegral);
		if(vCipConfig != null) {
			return Math.round(integral * vCipConfig.getForceSendScoreRatio() * getSendScore());
		}
		return 0;
	}
	
	/**
	 *  获取不通货币对应的积分
	 * @return
	 */
	public Map<String,Integer[]> getCurrencyMap(){
		return vipConfiguration.getCurrencyVipIntegralMap();
	}
	/**
	 * 	根据货币和金额计算积分
	 * @param currency 货币
	 * @param money 金额
	 * @return
	 */
	public int getCurrencyVipIntegral(String currency , float money) {
		Integer[] currencyIntegral = getCurrencyMap().get(currency);
		if(currencyIntegral == null) {
			return 0;
		}
		float integral = money*currencyIntegral[1]/currencyIntegral[0];
		return new BigDecimal(integral).setScale(0, RoundingMode.HALF_UP).intValue();
	}
	/**
	 * 	获取弹头的比例
	 * @return
	 */
	public String getWarHeadIntegralRadioValue() {
		return paramConfiguration.getWarHeadIntegralRadio();
	}
	
	/**
	 * 	获取弹头的比例
	 * @return 0:person 1:public 
	 */
	public double[] getWarHeadIntegralRadio() {
		Map<String, Object> jsonStrToMap = SerializableUtil.jsonStrToMap(getWarHeadIntegralRadioValue());
		Map<String,Double> objectMap = (Map<String,Double>)jsonStrToMap.get("pay");
		Double radioPerson = objectMap.get("person");
		Double radioPublic = objectMap.get("public");
		return new double[] {radioPerson, radioPublic};
	}
	/**
	 * 获取奖券积分的比例参数
	 * @return
	 */
	public String getTicketIntegralRadioValue() {
		return paramConfiguration.getTicketIntegralRadio();
	}
	
	/**
	 * 	获取付费点
	 * @param pointName
	 * @return
	 */
	public PayPoint getPayPoint(String pointName) {
		return payPointConfiguration.getPayPointMap().get(pointName);
	}
	/**
	 * 
	 * @return
	 */
	public Collection<PayPoint> getAllPayPoint(){
		return payPointConfiguration.getPayPointMap().values();
	}
	/**
	 * 
	 * @param realCurrency
	 * @return
	 */
	public float getUSDRate(String realCurrency) {
		return payParamConfiguration.getUSDRate(realCurrency);
	}
	/**
	 * 	获取国际化配置
	 * @param key
	 * @return
	 */
	public International getInternationalByKey(String key) {
		return internationalConfiguration.getInternationalByKey(key);
	}
	/**
	 * 
	 * @param itInternational
	 */
	public void addInternational(International itInternational) {
		internationalConfiguration.addInternational(itInternational.getKey(), itInternational);
	}
	
	public List<PropBag> getPropBag(Integer propBagId){
		return propBagConfiguration.getPropBagMap().get(propBagId);
	}
	
	/**
	 * 	判断是否为金币
	 * @param pointName
	 * @return
	 */
	public boolean checkGoldPayPoint(String pointName) {
		PayPoint pp = getPayPoint(pointName);
		return pp != null && pp.getType() == PayPointTypeConstants.GOLD;
	}
	/**
	 *	 获取付费点金额
	 * @param pointName
	 * @param currency
	 * @return
	 */
	public float getPayPointMoney(PayPoint payPoint,String currency) {
		float money = 0;
		if(currency.equalsIgnoreCase(CurrencyConstants.USA)) {
			money = payPoint.getUsd();
		}else if(currency.equalsIgnoreCase(CurrencyConstants.Vietnam)) {
			money = payPoint.getVnd();
		}else if(currency.equalsIgnoreCase(CurrencyConstants.Myanmar)) {
			money = payPoint.getMmk();
		}else if(currency.equalsIgnoreCase(CurrencyConstants.China)) {
			money = payPoint.getCny();
		}else if(currency.equalsIgnoreCase(CurrencyConstants.Thailand)) {
			money = payPoint.getThb();
		}else {
			logger.error("未知的货币金额：" +currency);
		}
		if(money < 0.00001) {
			logger.error("未查找【"+payPoint.getName()+"】货币【" +currency + "】的金额。");
		}
		return money;
	}
	
	/**
	 * 	获取不同的货币对应的付费点
	 * @return
	 */
	public Map<String, TreeMap<Float, PayPoint>> getCurrencyPayPoint(){
		return payPointConfiguration.getCurrencyPayPoint();
	}
	
	/**
	 *   计算结余的金币
	 * @param payOrder
	 * @param oldPayPoint 已经支付过的计费点
	 * @param resetPayPoint 金額高於計費點金額不需要重置計費點 {@link Boolean.FALSE}
	 * @return 0本身部分 1赠送部分
	 */
	public int[] computSurplusGold(PayOrder payOrder , PayPoint oldPayPoint, Boolean resetPayPoint) {
		float money = payOrder.getRealMoney();
		String currency = payOrder.getRealCurrency();
		return computSurplusGold(payOrder, money, currency, oldPayPoint, resetPayPoint);
	}
	/**
	 * 计算结余的金币
	 * @param payOrder
	 * @param money
	 * @param currency
	 * @param oldPayPoint
	 * @param resetPayPoint 金額高於計費點金額不需要重置計費點 {@link Boolean.FALSE}
	 * @return
	 */
	public int[] computSurplusGold(PayOrder payOrder,float money, String currency,PayPoint oldPayPoint, Boolean resetPayPoint) {
		TreeMap<Float, PayPoint> currentTreeMap = getCurrencyPayPoint().get(currency.toUpperCase());
		if(currentTreeMap == null) {
			//未找到货币按照0.99美元计算
			currency = CurrencyConstants.USA;
			money = 0.99f;
			currentTreeMap =  getCurrencyPayPoint().get(currency);
		}
		PayPoint payPoint = null;
		Entry<Float, PayPoint> higherPoint = currentTreeMap.higherEntry(money);
		if(higherPoint == null) {
			payPoint = currentTreeMap.lastEntry().getValue();
		}else {
			payPoint = higherPoint.getValue();
		}
		float newMoney = getPointExistMoney(payPoint);
		float rate = 0;
		if(oldPayPoint != null) {
			float oldMoney = getPointExistMoney(oldPayPoint);
			rate = (newMoney-oldMoney)/newMoney;
		}else {
			money = exchangeCurrencyToUSD(currency, money);
			rate = money/newMoney;
		}
		int intGoldNum = PayUtil.getIntGoldNum(rate * payPoint.getGold());
		int intExtraGoldNum = PayUtil.getIntGoldNum(rate * payPoint.getExtraSend());
		if(resetPayPoint) {
			payOrder.setPointName(payPoint.getName());
			payOrder.setRealCurrency(currency);
		}
		return new int[] {intGoldNum , intExtraGoldNum};
	}
	/**
	 * 新手礼包获取金币和钻石数量
	 * @param realMoney
	 * @param currency
	 * @param oldPayPoint
	 * @return
	 */
	public Map<Integer,Integer> checkAndGetNewGiftProps(float realMoney,String currency, PayPoint oldPayPoint) {
		if(!isNewGift(oldPayPoint)) {
			return null;
		}
		float notEnoughRate = computeNotEnoughRate(realMoney,currency,oldPayPoint);
		int intGoldNum = PayUtil.getIntGoldNum(notEnoughRate * oldPayPoint.getGold());
		int diamondNum = new BigDecimal(notEnoughRate * oldPayPoint.getDiamond()).setScale(0, RoundingMode.CEILING).intValue();
		Map<Integer,Integer> propMap = new HashMap<>();
		propMap.put(PropConstants.GOLD, intGoldNum);
		propMap.put(PropConstants.DIAMOND, diamondNum);
		List<PropBag> propBags = getPropBag(oldPayPoint.getPropBag());
		for(int i = 0;i < propBags.size();i ++) {
			PropBag propBag = propBags.get(i);
			propMap.put(propBag.getPropID(), propBag.getAmount());
		}
		return propMap;
	}
	
	/***
	 * 如果实际金额小于计费点金额（礼包）
	 * @param realMoney
	 * @param currency
	 * @param oldPayPoint
	 * @return
	 */
	private float computeNotEnoughRate(float realMoney,String currency, PayPoint oldPayPoint) {
		float pointExistMoney = getPointExistMoney(oldPayPoint);
		float currencyRealMoney = exchangeCurrencyToUSD(currency, realMoney);
		return currencyRealMoney / pointExistMoney;
	}
	/**
	 *  检查是否是新手礼包
	 * @param payPoint
	 * @return
	 */
	private boolean isNewGift(PayPoint payPoint) {
		return payPoint != null && payPoint.getPropBag() != 0 && payPoint.getType() == PayPointTypeConstants.NEW_GIFT ;
	}
	/**
	 * 	换算为美元金额
	 * @param currency
	 * @param money
	 * @return
	 */
	public float exchangeCurrencyToUSD(String currency,float money) {
		return payParamConfiguration.exchangeCurrencyToUSD(currency, money);
	}
	
	/**
	 * 	若找不到计费点，就取有金额的计费点信息
	 * @param point
	 * @return
	 */
	public float getPointExistMoney(PayPoint point) {
		if(point.getUsd() > 0) {
			return point.getUsd();
		}
		if(point.getVnd() > 0) {
			return exchangeCurrencyToUSD(CurrencyConstants.Vietnam, point.getVnd());
		}
		if(point.getCny() > 0) {
			return exchangeCurrencyToUSD(CurrencyConstants.China, point.getCny());
		}
		if(point.getMmk() > 0) {
			return exchangeCurrencyToUSD(CurrencyConstants.Myanmar, point.getMmk());
		}
		if(point.getThb() > 0) {
			return exchangeCurrencyToUSD(CurrencyConstants.Thailand, point.getThb());
		}
		return 0;
	}
	
	/**
	 * 获取付费点的货币，与价格
	 * @param pointName
	 * @param currency
	 * @return
	 */
	public Object[] getPayPointExistMoney(String pointName) {
		PayPoint payPoint = getPayPoint(pointName);
		if(payPoint == null) {
			return new Object[] {CurrencyConstants.USA, 0f};
		}
		if(payPoint.getUsd() > 0) {
			return new Object[]{CurrencyConstants.USA, payPoint.getUsd()};
		}
		if(payPoint.getVnd() > 0) {
			return new Object[]{CurrencyConstants.Vietnam, payPoint.getVnd()};
		}
		if(payPoint.getMmk() > 0) {
			return new Object[]{CurrencyConstants.Myanmar,payPoint.getMmk()};
		}
		if(payPoint.getCny() > 0) {
			return new Object[]{CurrencyConstants.China, payPoint.getCny()};
		}
		if(payPoint.getThb() > 0) {
			return new Object[]{CurrencyConstants.Thailand, payPoint.getThb()};
		}
		return new Object[] {CurrencyConstants.USA, 0f};
	}
	/**
	 * 	换算为美元金额
	 * @param pointName
	 * @return
	 */
	public float getPointMoneyExchangeUSD(String pointName) {
		PayPoint point = getPayPoint(pointName);
		if(point == null) {
			return 0;
		}
		return getPointExistMoney(point);
	}
	/**
	 *  	获得计费点金额
	 * @param pointName
	 * @param currency
	 * @return
	 */
	public float getPayPointMoney(String pointName , String currency) {
		PayPoint point = getPayPoint(pointName);
		if(point == null) {
			return 0;
		}
		return getPayPointMoney(point , currency);
	}

	/**
	 * 	计费点国际化
	 * @param pointName
	 * @param currency
	 * @param isEn
	 * @return
	 */
	public String getPayPointTitle(String pointName, String currency, Boolean isEn){
		PayPoint payPoint = getPayPoint(pointName);
		if(Objects.isNull(payPoint)) {
			return null;
		}
		String key = String.format("pay_point_%d", payPoint.getRelateInternational());
		International international = getInternationalByKey(key);
		if(isEn && Objects.nonNull(international)) {
			return international.getEnValue() != null ? international.getEnValue() : payPoint.getTitle();
		}
		String internationalTitle = InternationalUtil.getInternationalMsg(currency, international);
		return internationalTitle != null ? internationalTitle : payPoint.getTitle();
	}
	
	
	public String getVietnamKingCardSecretKey() {
		return vietnamPayConfiguration.getKingCardSecretKey();
	}
	
	public String getMMCodaPayApiKey() {
		return myanmarCodaPayConfiguration.getApi_key();
	}
	
	public String getCambodiaCodaPayApiKey() {
		return cambodiaCodaPayConfiguration.getApi_key();
	}
	
	public boolean checkVietnamKingCardIpAddress(String ip) {
		List<String> addressesOfKingCard = vietnamPayConfiguration.getIpAddressesOfKingCard();
		if(addressesOfKingCard != null) {
			int binarySearch = Collections.binarySearch(addressesOfKingCard, ip);
			return binarySearch >= 0;
		}
		return Boolean.FALSE;
	}
	/**
	 * 
	 * @param pointName
	 * @param userType
	 * @param propMap  获得道具总信息
	 * @return
	 */
	public Map<Integer,Integer> checkAwardCondition(String pointName,int userType,Map<Integer,Integer> propMap){
		if(Objects.nonNull(propMap)) {
			return apyAwardConfiguration.loadPayAwardPropAmount(pointName, userType, propMap);
		}
		return null;
	}
	/**
	 * 	获取动态计费点
	 * @param dynamicId
	 * @return
	 */
	public DynamicPayPoint getDynamicPayPointById(int dynamicId) {
		if(dynamicId > 0) {
			return dynamicMayTurnPayPointConfiguration.getDynamicPayPointById(dynamicId);
		}
		return null;
	}
	/**
	 * 	检查是否是转盘
	 * @param pointName
	 * @param dynamicPayPoint
	 * @param paySuccessCommand
	 * @return 获得金币
	 */
	public int checkIfTurntables(PayOrder payOrder,DynamicPayPoint dynamicPayPoint, PaySuccessCommand paySuccessCommand,DynamicPointLog dynamicPointLog) {
		// 金币转盘
		TreeMap<Double, Turntable> turntables = dynamicMayTurnPayPointConfiguration.getTurntableByType(dynamicPayPoint.getType());
		int dynamicGold = getDynamicGoldByPointName(payOrder.getPointName(), dynamicPayPoint);
		if(turntables != null) {
			double random = Math.random();
			Entry<Double, Turntable> higherTurnTable = turntables.higherEntry(random);
			Turntable turntable = higherTurnTable.getValue();
			logger.info("订单号【"+payOrder.getOrderNum()+"】翻倍转盘随机概率:"+random+",翻倍转盘："+turntable);
			paySuccessCommand.setMultipleTurntablePosition(turntable.getPosition());
			Float amount = turntable.getAmount();
			
			dynamicPointLog.setPointGold(dynamicGold);
			dynamicPointLog.setMultipler(amount);
			
			return (int)(dynamicGold * amount);
		}
		return 0;
	}
	/**
	 * 获取当前地图位置
	 * @param userId
	 * @return
	 */
	public TempPayInfo getCurrentMapPosition(int userId) {
		TempPayInfo userMapPointInfo = userPayCacheManager.getUserMapPointInfo(userId);
		return userMapPointInfo;
	}
	/**
	 * 	海盗冒险
	 * @param pointName 计费点名称
	 * @param paySuccessCommand
	 * @param dynamicPayPoint 动态计费点信息
	 * @return 获得金币
	 */
	public int checkIfMapPoint(PayOrder payOrder, PaySuccessCommand paySuccessCommand, DynamicPayPoint dynamicPayPoint,TempPayInfo currentPassPayInfo, DynamicPointLog dynamicPointLog) {
		int dynamicGold = getDynamicGoldByPointName(payOrder.getPointName(), dynamicPayPoint);
		int finalGold = 0;
		// 当前关卡
		if(currentPassPayInfo == null) {
			MapPoint firstMapPoint = dynamicMayTurnPayPointConfiguration.getFirstMapPoint();
			currentPassPayInfo = userPayCacheManager.setUserMapPointInfo(payOrder.getUserId(), firstMapPoint.getToll(), firstMapPoint.getPosition());
		}else {
			// 检查是否为最后的关卡，并给与关卡奖励
			finalGold = checkIfFinalMapPointAndResetToll(paySuccessCommand, payOrder, currentPassPayInfo);
		}
		dynamicPointLog.setPosition(currentPassPayInfo.getPosition());
		dynamicPointLog.setToll(currentPassPayInfo.getToll());
		
		if(dynamicPayPoint.getType() == PayMapConstants.MAP_POINT_CELL) {
			dynamicGold += finalGold;
			dynamicPointLog.setPointGold(dynamicGold);
			return dynamicGold;
		}else {
			TreeMap<Double, Turntable> freeTurntables = dynamicMayTurnPayPointConfiguration.getTurntableByType(PayMapConstants.FREE_MAP_TURNTABLE);
			TreeMap<Double, Turntable> payTurntables = dynamicMayTurnPayPointConfiguration.getTurntableByType(PayMapConstants.PAY_MAP_TURNTABLE);
			if(freeTurntables != null && payTurntables !=  null) {
				double freeRandom = Math.random();
				Entry<Double, Turntable> freeTurntableEntry = freeTurntables.higherEntry(freeRandom);
				
				double payRandom = Math.random();
				Turntable freeTurntable = freeTurntableEntry.getValue();
				Entry<Double, Turntable> payTurnTableEntry = payTurntables.higherEntry(payRandom);
				Turntable payTurntable = payTurnTableEntry.getValue();
				logger.info("订单号【"+payOrder.getOrderNum()+"】地图金币转盘随机概率:"+freeRandom+",金币转盘："+freeTurntable);
				logger.info("订单号【"+payOrder.getOrderNum()+"】地图翻倍转盘随机概率:"+payRandom+",翻倍转盘："+payTurntable);
				paySuccessCommand.setGoldTurntablePosition(freeTurntable.getPosition());
				paySuccessCommand.setMultipleTurntablePosition(payTurntable.getPosition());

				dynamicPointLog.setMultipler(payTurntable.getAmount());
				dynamicPointLog.setPointGold(freeTurntable.getAmount().intValue());
				int extraGold = (int)(freeTurntable.getAmount() * payTurntable.getAmount());
				return  dynamicGold + finalGold + extraGold;
			}
		}
		return 0;
	}
	/**
	 * 	登录卷轴
	 * @param pointName 計費點
	 * @param paySuccessCommand
	 * @param dynamicPayPoint
	 * @return 获得金币
	 */
	public int checkIfLoginScroll(PayOrder payOrder, PaySuccessCommand paySuccessCommand, DynamicPayPoint dynamicPayPoint,DynamicPointLog dynamicPointLog) {
		int dynamicGold = getDynamicGoldByPointName(payOrder.getPointName(), dynamicPayPoint);
		TempPayInfo userMapPointInfo = userPayCacheManager.getUserSlotsPointInfo(payOrder.getUserId());
		if(userMapPointInfo != null) {
			float slotMultipler = userMapPointInfo.getSlotMultipler();
			dynamicPointLog.setMultipler(slotMultipler);
			dynamicPointLog.setPointGold(dynamicGold);
			return userMapPointInfo.getGold();
		}
		return 0;
	}
	/**
	 *  小猪钱包
	 * @param payOrder
	 * @param paySuccessCommand
	 * @param dynamicPayPoint
	 * @param originalGold
	 * @return
	 */
	public int checkPigWallet(PayOrder payOrder, PaySuccessCommand paySuccessCommand, DynamicPayPoint dynamicPayPoint, int originalGold,DynamicPointLog dynamicPointLog) {
		int dynamicGold = getDynamicGoldByPointName(payOrder.getPointName(), dynamicPayPoint);
		int totalGold = originalGold + dynamicGold;
		dynamicPointLog.setPointGold(totalGold);
		return totalGold;
	}

	/**
	 * 三合一礼包
	 * @param payOrder
	 * @param paySuccessCommand
	 * @param dynamicPayPoint
	 * @return
	 */
	public int checkThreeInOneGift(PayOrder payOrder, PaySuccessCommand paySuccessCommand, DynamicPayPoint dynamicPayPoint,DynamicPointLog dynamicPointLog) {
		int dynamicGold = getDynamicGoldByPointName(payOrder.getPointName(), dynamicPayPoint);
		dynamicPointLog.setPointGold(dynamicGold);
		return dynamicGold;
	}
	/**
	 * 
	 * @param pointName 计费点
	 * @param dynamicPayPoint 地图或者转盘计费点信息
	 * @return
	 */
	private int getDynamicGoldByPointName(String pointName, DynamicPayPoint dynamicPayPoint) {
		if(pointName == null) {
			return 0;
		}
		if(pointName.equals(dynamicPayPoint.getPointNameUsd())) {
			return dynamicPayPoint.getUsdGold();
		}
		if(pointName.equals(dynamicPayPoint.getPointNameCny())) {
			return dynamicPayPoint.getCnyGold();
		}
		if(pointName.equals(dynamicPayPoint.getPointNameVnd())) {
			return dynamicPayPoint.getVndGold();
		}
		if(pointName.equals(dynamicPayPoint.getPointNameMmk())) {
			return dynamicPayPoint.getMmkGold();
		}
		if(pointName.equals(dynamicPayPoint.getPointNameThb())) {
			return dynamicPayPoint.getThbGold();
		}
		return 0;
	}
	/**
	 * 检查是否为关卡最后的位置，如果是开始下一个关卡，并且给与最后的奖励
	 * @param paySuccessCommand
	 * @param payOrder
	 * @param userMapPointInfo
	 * @return
	 */
	private int checkIfFinalMapPointAndResetToll(PaySuccessCommand paySuccessCommand,PayOrder payOrder,TempPayInfo userMapPointInfo) {
		MapPoint nextMapPoint = dynamicMayTurnPayPointConfiguration.getNextMapPoint(userMapPointInfo.getToll(), userMapPointInfo.getPosition());
		int goldAmount = 0;
		if(nextMapPoint != null) {
			MapPoint nextTollMapPoint = dynamicMayTurnPayPointConfiguration.getNextMapPoint(nextMapPoint.getToll(), nextMapPoint.getPosition());
			if(nextTollMapPoint == null) {
				List<PropAmount> props = dynamicMayTurnPayPointConfiguration.getFinalRewardMapPoint().getProps();
				PropAmount prop = props.get(userMapPointInfo.getToll() - 1);
				int propId = prop.getPropId();
				int amount = prop.getAmount();
				if(propId == PropConstants.GOLD) {
					goldAmount = amount;
				}else {
					Integer otherAmount = paySuccessCommand.getProps().get(propId);
					if(otherAmount != null) {
						otherAmount += amount;
					}else {
						otherAmount = amount;
					}
					paySuccessCommand.getProps().put(propId, otherAmount);
				}
			}
		}else {
			nextMapPoint = dynamicMayTurnPayPointConfiguration.getNextTollMapPoint(userMapPointInfo.getToll(), userMapPointInfo.getPosition());
		}
		if(nextMapPoint != null) {
			paySuccessCommand.setPosition(nextMapPoint.getPosition());
			paySuccessCommand.setToll(nextMapPoint.getToll());
			userMapPointInfo.setToll(nextMapPoint.getToll());
			userMapPointInfo.setPosition(nextMapPoint.getPosition());
			userPayCacheManager.setUserMapPointInfo(payOrder.getUserId(), nextMapPoint.getToll(), nextMapPoint.getPosition());	
		}
		return goldAmount;
	}
	/**
	 * 获取环境
	 * @return
	 */
	public int getSystemEnv() {
		return systemConfiguration.getSystemEnv();
	}
	/***
	 * 获取重试次数
	 * @return
	 */
	public int getRetryCount() {
		return systemConfiguration.getReTry();
	}
	/**
	 * 强发分数
	 * @return
	 */
	public int getForceSendScore() {
		return systemConfiguration.getForceSendScore();
	}
	/**
	 * 弹头奖励（公共）
	 * @return
	 */
	public int getWarheadPublic() {
		return systemConfiguration.getWarheadPublic();
	}
	/**
	 * 弹头奖励（个人）
	 * @return
	 */
	public int getWarheadPerson() {
		return systemConfiguration.getWarheadPerson();
	}
	/**
	 * 充值强发基数
	 * @return
	 */
	public int getSendScore() {
		return systemConfiguration.getForceSendScore();
	}
	/**
	 * momo 验证密钥
	 * @return
	 */
	public String getMomoSecretKey() {
		return vietnamPayConfiguration.getVietnamMomoSecretyKey();
	}
	/**
	 * 获取等级
	 * @param experience
	 * @return
	 */
	public Integer getLevel(long experience) {
		return levelConfiguration.getLevel(experience);
	}
	
}
