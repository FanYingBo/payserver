package com.last.pay.base.common.constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.last.pay.base.CodeMsgType;
import com.last.pay.base.vo.CountryCurrency;
import com.last.pay.core.db.pojo.game.PropBag;

public class Constants {
	/***运行环境**/
	public static final int ENV_TEST = 1;
	public static final int ENV_SANDBOX = 2;
	public static final int ENV_PRODUCT = 3;
	
	public static final int MOTH_POINT = 151;  // 贵族礼包付费点ID
	public static final int MOTH_DAY_POINT = 153; // 贵族礼包对应的礼包ID
	
	public static final int YEAR_GIFT = 197; // 礼包ID
	
	public static final int CALLBACKCHECK_FALSE = 0;  // CODAPAY 支付后端回调不验证
	public static final int CALLBACKCHECK_TRUE = 1; // CODAPAY 支付后端回调验证MD5
	
	public static final String FORMAT_ERRORCODE = "pay_error_";
	
	public static final class PayStatusConstants{
		public static final int NEW_ORDER = 0;
		public static final int SUCCESS_ORDER = 1;
		public static final int FAILURE_ORDER = 2;
		public static final int DEALING_ORDER = 3;
	}
	/**
	 * 支付类型（本地或后端）
	 * @author Administrator
	 *
	 */
	public static final class PayTypeConstants{
		public static final int IOS = 1;
		public static final int GOOGLE = 2;
		public static final int Vietnam_CARD = 3;
		public static final int Myanmar_CODAPAY_Vouchers = 4;
		public static final int Myanmar_CODAPAY_SMS = 5;
		public static final int Myanmar_CODAPAY_WAVE = 6;
		public static final int HUAWEI_INTERNATIONAL = 7;
		public static final int PAYCENT = 8;
		public static final int HUAWEI_INLAND = 9;
		
		public static final int VIETNAM_UPAY_EBANK = 10;
		
		public static final int Cambodia_CODAPAY_SMS = 11;
		public static final int Cambodia_CODAPAY_Wing = 12;
		
		public static final int Thailand_CODAPAY_SMS = 13;
		public static final int Thailand_CODAPAY_TrueMoneyCashCard = 14;
		public static final int Thailand_CODAPAY_TrueMoneyWallet = 15;
		public static final int Thailand_CODAPAY_7_ElevenTH = 16;
		public static final int Thailand_Bank_Transfers_and_cash = 17;
		public static final int Thailand_Rabbit_LINE_Pay = 18;
		
		public static final int Vietnam_Test = 999;
	}
	
	/**
	 * 不同的国家货币相互映射
	 * VND 越南盾
	 * BUK 缅甸元
	 * IDR 印尼币
	 * @author Administrator
	 *
	 */
	public static final class CurrencyConstants{
		public static final String USA = "USD";
		public static final String Vietnam = "VND";
		public static final String Myanmar = "BUK";
		public static final String Indonesia = "IDR";
		public static final String China = "CNY";
		public static final String Cambodia = "KHR";
		public static final String Thailand = "THB";
		
		public static final String MyanmarCountry_PaycentCode = "MM";
		
		public static final Map<String,CountryCurrency> country = new HashMap<String, CountryCurrency>();
		
		static {
			country.put(USA,new CountryCurrency("USA","USA",(short)840,(short)840));
			country.put(Myanmar,new CountryCurrency("Myanmar","Myanmar kyat",(short)104,(short)104));
			country.put(Vietnam,new CountryCurrency("Vietnam","Vietnamese dong",(short)704,(short)704));
			country.put(Indonesia, new CountryCurrency("Indonesia", "IDR", (short)0, (short)0));
			country.put(Cambodia, new CountryCurrency("Cambodia", "United States dollar", (short)116, (short)840));
			country.put(Thailand, new CountryCurrency("Thailand", "Thai baht", (short)764, (short)764));
		}
	}


	/**查找错误码翻译的key***/
	public static String formatInternationalKey(Integer code) {
		return FORMAT_ERRORCODE + code;
	}
	
	public static final class PayChannelConstants{
		public static final String pay_channel_param = "pay_channel";
		public static final String WEB_CHANNEL = "web";
		public static final String INAPP_CHANNEL = "inapp";
		public static final int OLD_CHANNEL = 1; // 越南点卡旧渠道
		public static final int UPAY_CHANNEL = 2;// uPay
		public static final int KING_CARD_CHANNEL = 3; // KingCard
		public static final int BANG_LANG_CHANNEL = 4; // BangLang
	}
	
	public static final class PayPlatformConstants{
		public static final int IN_GAME = 1;
		public static final int WEB_OLD_CHANNEL = 4;
		public static final int IN_GAEM_UPAY = 5;
		public static final int WEB_UPAY = 6;
		public static final int IN_GAME_KING_CARD = 7;
		public static final int WEB_KING_CARD = 8;
		public static final int IN_GAME_BANG_LANG = 9;
		public static final int WEB_BANG_LANG = 10;
	}
	
	public static final class DefaultPropBagConstants{
		public static final Map<Integer,List<PropBag>> propBagMap = new HashMap<>();
		static {
			List<PropBag> propBags = new ArrayList<PropBag>();
			propBags.add(creatPropBag(YEAR_GIFT, 325, 1));
			propBags.add(creatPropBag(YEAR_GIFT, 345, 1));
			propBagMap.put(YEAR_GIFT, propBags);
		}
		private static PropBag creatPropBag(int bagID,int propID,int amount) {
			PropBag propBag = new PropBag();
			propBag.setBagID(bagID);
			propBag.setPropID(propID);
			propBag.setAmount(amount);
			return propBag;
		}
	}
	
	public static final class PropConstants{
		public static final int GOLD = 1;		//金币
		public static final int DIAMOND = 2;	//钻石
		public static final int VIP_INTEGRAL = 5;	//VIP积分
	}
	
	/**
	 * 	类型 1金币 2钻石 3 金幣禮包 4促銷禮包  10礼包 11成長基金 13喇叭 14 新手禮包 15 新年禮包 16 富豪礼包
	 * @author Administrator
	 *
	 */
	public static final class PayPointTypeConstants{
		public static final int GOLD = 1;
		public static final int DIAMOND = 2;
		public static final int GOLD_GIFT = 3;
		public static final int PROMOTION = 4;
		public static final int FUND = 11;
		public static final int MONTH_GIFT = 10;
		public static final int NEW_GIFT = 14;
		public static final int HORN_GIFT = 13;
		public static final int YEAR_GIFT = 15;
		public static final int RICH_GIFT = 16;
	}
	
	public static final class PaycentErrorCode{
		public static Map<Integer,CodeMsgType> callBackMap = new HashMap<Integer,CodeMsgType>();
		
		static {
			callBackMap.put(100, CodeMsgType.SUCCESS);
			callBackMap.put(101, CodeMsgType.ERR_PAYCENT_FAIL);
			callBackMap.put(102, CodeMsgType.ERR_PAYCENT_WAIT_CONFIRM);
			callBackMap.put(103, CodeMsgType.ERR_PAYCENT_CANCLE);
			callBackMap.put(104, CodeMsgType.ERR_PAYCENT_EXPIRE_CODE);
		}
	}
	
	public static final class LanguageConstants{
		/**英语**/
		public static final String EN = "en";
		/**缅甸语**/
		public static final String MY = "my";
	}
	
	public static final class ReplacementOrderConstants{
		public static final int NOT_SUCCESS = 0;
		public static final int SUCCESS_ORDER = 1;
		public static final int TRANS_ERROR = 2;
	}
}
