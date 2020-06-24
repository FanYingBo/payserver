package com.last.pay.base.common.constants;

import java.util.HashMap;
import java.util.Map;

import com.last.pay.base.CodeMsgType;
import com.last.pay.base.vo.CodaPayMno;
/**
 * 
 * coda pay 电信运营商支付限额 
 * @author Administrator
 *
 */
public class CodaPayConstants {
	public static final String MPT = "103";
	public static final String Telenor_Myanmar = "101";
	public static final String Ooredoo = "102";
	public static final String Mytel = "104";
	
	public static final String PayCentTelenor = "MM-WEB-TELENOR";
	public static final String PayCentMytel = "MM-WEB-MYTEL";
	public static final String PayCentOoredoo = "MM-WEB-OOREDOO";
	public static final String PayCentMpt = "MM-WEB-MPT";
	
	public static Map<String, CodaPayMno> codaPayMnos = new HashMap<String, CodaPayMno>();
	static {
		codaPayMnos.put(Telenor_Myanmar, new CodaPayMno("Telenor_Myanmar", "101", 0.03f, 31.25f, PayCentTelenor));
		codaPayMnos.put(Ooredoo, new CodaPayMno("Ooredoo", "102", 0.01f, 125f, PayCentOoredoo));
		codaPayMnos.put(MPT, new CodaPayMno("MPT", "103", 0.19f, 25f, PayCentMpt));
		codaPayMnos.put(Mytel, new CodaPayMno("Mytel", "104", 0.03f, 31.25f, PayCentMytel));
	}
	
	/**
	 * coda pay 异常错误码对应
	 * @author Administrator
	 *
	 */
	public static final class CodaErrorCodeConstants {
		public static Map<Integer,CodeMsgType> responseMap = new HashMap<Integer,CodeMsgType>();
		static {
			responseMap.put(0,CodeMsgType.SUCCESS);
			responseMap.put(102,CodeMsgType.ERR_CODA_PAY_CANCLE);
			responseMap.put(103,CodeMsgType.ERR_CODA_PAY_PARAM);
			responseMap.put(202,CodeMsgType.ERR_CODA_PAY_SYS);
			responseMap.put(204,CodeMsgType.ERR_CODA_PAY_API_KEY);
			responseMap.put(205,CodeMsgType.ERR_CODA_PAY_BIZ);
			responseMap.put(214,CodeMsgType.ERR_CODA_PAY_REQUEST_INVALID);
			responseMap.put(216,CodeMsgType.ERR_CODA_PAY_TRANS_STATUS);
			responseMap.put(225,CodeMsgType.ERR_CODA_PAY_CHANNEL_ACTIVITY);
			responseMap.put(309,CodeMsgType.ERR_CODA_PAY_VOUCHER_PARAMS_BLACKLIST);
			responseMap.put(310,CodeMsgType.ERR_CODA_PAY_VOUCHER_PARAMS_BLACKLIST);
			responseMap.put(311,CodeMsgType.ERR_CODA_PAY_VOUCHER_PARAMS_BLACKLIST);
			responseMap.put(361,CodeMsgType.ERR_CODA_PAY_CHANNEL_INVALID);
			responseMap.put(403,CodeMsgType.ERR_CODA_PAY_VOUCHER_CARD_INVALID);
			responseMap.put(407,CodeMsgType.ERR_CODA_PAY_BIZ_TRANS);
			responseMap.put(408,CodeMsgType.ERR_CODA_PAY_OPTS);
			responseMap.put(411,CodeMsgType.ERR_CODA_PAY_VOUCHER_CODE);
			responseMap.put(412,CodeMsgType.ERR_CODA_PAY_VOUCHER_PIN);
			responseMap.put(413,CodeMsgType.ERR_CODA_PAY_VOUCHER_CARD_TYPE);
			responseMap.put(431,CodeMsgType.ERR_CODA_PAY_TRANS_STATUS);
		}
	}
	
	
	public static final class CodaPayRequestParam{
		/***Secret key assigned by Coda (unique to each country).**/
		public static final String API_KEY = "api_key";
		/***Merchant-assigned unique transaction identifier.****/
		public static final  String ORDER_ID = "order_id";
		/***https://online.codapayments.com/merchant/developer/backend_api#language***/
		public static final  String LANG = "lang";
		/***https://online.codapayments.com/merchant/developer/backend_api#pay_type***/
		public static final  String PAY_TYPE = "pay_type";
		/***pay_type=2 时需要****/
		public static final String PAY_CHANNEL = "pay_channel";
		/**1 Bank Counter  2 Internet Banking  3  Online Bill Payment   4 E-wallet  5 ATM  14 Convenience Store	***/
		public static final String PAY_AT = "pay_at";
		/*** 缅甸：Ooredoo	MT-MO	41405****/
		public static final String MNO_CODE = "mno_code";
		/***MSISDN (non-formatted; without country code) 不帶国家编号的手机号码***/
		public static final String MSISDN = "msisdn";
		
		public static final String EMAIL = "email";
		/***将显示在SMS消息中的商家名称，如果商家想指定商家名称，请在此字段中给出名称。默认值为空***/
		public static final String MERCHANT_NAME = "merchant_name";
		/**1: digital content, 2: non-digital content****/
		public static final String TYPE = "type";
		/***{@link CodaPayItems} 集合类型****/
		public static final String ITEMS= "items";
		/***{@link CodaPayProfileVouchersDetails} map类型****/
		public static final String PROFILE = "profile";
	}
	public static final class CodaPayInitParam{
		/***Secret key assigned by Coda (unique to each country).**/
		public static final String API_KEY = "apiKey";
		/***Merchant-assigned unique transaction identifier.****/
		public static final  String ORDER_ID = "orderId";
		/***https://online.codapayments.com/merchant/developer/backend_api#pay_type***/
		public static final  String PAY_TYPE = "payType";
		/**国家ISO3166代码***/
		public static final String COUNTRY = "country";
		/**货币代码***/
		public static final String CURRENCY = "currency";
		/***{@link CodaPayItems} 集合类型****/
		public static final String ITEMS= "items";
		/***{@link CodaPayProfileVouchersDetails} map类型****/
		public static final String PROFILE = "profile";
	}
	/**
	 * coda pay iteminfo参数
	 * @author Administrator
	 *
	 */
	public static final class CodaPayItems{
		/***商品代码***/
		public static final String Code = "Code";
		/***商品名称***/
		public static final String Name = "Name";
		/***商品价格***/
		public static final String Price = "Price";
		/***商品类型 默认为1***/
		public static final String Type = "Type";
	}
	/**
	 * 	coda pay 点卡支付的参数
	 * @author Administrator
	 *
	 */
	public static final class CodaPayProfileVouchersDetails{
		/***卡片卡号***/
		public static final String VOUCHER_CODE = "voucher_code";
		/***卡片密码***/
		public static final String VOUCHER_PIN = "voucher_pin";
		/***运营商，点卡可以为空***/
		public static final String NEED_MNO_ID = "need_mno_id";
		/***卡片类型***/
		public static final String CARD_TYPE = "card_type";
		/***自定义***/
		public static final String USER_ID = "user_id";
		/***自定义***/
		public static final String CLIENT_IP = "client_ip";
	}
	
	/**
	 * coda pay profile参数(訂閲的商品)
	 * @author Administrator
	 *
	 */
	public static final class CodaPayProfileSubscriptionDetails{
		public static final String SUBSCRIPTIONTYPE = "subscriptionType";
		public static final String SUBSCRIPTIONID = "subscriptionId";
		public static final String SUBSCRIPTIONPLANID = "subscriptionPlanID";
	}
	/**
	 * coda pay profile参数
	 * @author Administrator
	 *
	 */
	public static final class CodaPayProfileOtherDetails{
		public static final String USER_ID = "user_id";
		public static final String CLIENT_IP = "client_ip";
		public static final String USER_AGENT = "user_agent";
		public static final String ACCOUNT_NUMBER = "account_number";
		public static final String SECURITY_CODE = "security_code";
		public static final String NAME = "name";
		public static final String ID_NO = "id_no";
	}
	
	/**
	 * coda pay 支付类型（页面）
	 * @author Administrator
	 *
	 */
	public static final class CodaPayTypeConstants{
		public static final String Myanmar_CODAPAY_SMS = "1";
		public static final String Myanmar_CODAPAY_WAVE = "380";
		
		public static final String Cambodia_CODAPAY_Wing = "450";
		public static final String Cambodia_CODAPAY_SMS = "1";
		
		public static final String Thailand_CODAPAY_SMS = "1";
		public static final String Thailand_Bank_Transfers_and_cash  = "331";
		public static final String Thailand_Rabbit_LINE_Pay = "332";
		public static final String Thailand_TrueMoneyCashCard = "340";
		public static final String Thailand_CODAPAY_TrueMoneyWallet = "342";
		public static final String Thailand_7_ElevenTH = "344";
	}
}
