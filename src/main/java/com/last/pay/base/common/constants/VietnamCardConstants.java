package com.last.pay.base.common.constants;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.last.pay.base.CodeMsgType;

public class VietnamCardConstants {
	/**
	 * 越南点卡提供商代码
	 * @author Administrator
	 *
	 */
	public static final class VietnamCardProvider{
		public static final String mobifone = "VMS";
		public static final String vinaphone = "VNP";
		public static final String viettel = "VTT";
		public static final String FPTGATE = "FPT";
		public static final String Anpay = "ANPAY";
		
		public static final String KingCard_VTT = "VIETTEL";
		public static final String KingCard_VMS = "MOBI";
		public static final String KingCard_VNP = "VINA";
		
		public static final String BangLang_VTT = "VT";
		public static final String BangLang_VNP = "Vina";
		public static final String BangLang_VMS = "Mobi";
		
		public static final String GATE = "GATE";
	}
	
	public static final class VietnamCardPrintAmount{
		public static final Map<String,List<Integer>> cardPrintAmountMap = new HashMap<>();
		static {
			cardPrintAmountMap.put(VietnamCardProvider.mobifone, Arrays.asList(50000,100000,200000,300000,500000));
			cardPrintAmountMap.put(VietnamCardProvider.vinaphone, Arrays.asList(10000,20000,30000,50000,100000,200000,300000,500000));
			cardPrintAmountMap.put(VietnamCardProvider.viettel, Arrays.asList(10000,20000,30000,50000,100000,200000,300000,500000,1000000));
			cardPrintAmountMap.put(VietnamCardProvider.FPTGATE, Arrays.asList(10000,20000,50000,100000,200000,500000,1000000,2000000,5000000,10000000));
			cardPrintAmountMap.put(VietnamCardProvider.Anpay, Arrays.asList(10000,20000,30000,50000,100000,200000,300000,500000,1000000,2000000,3000000,5000000));
		}
	}
	
	public static final class VietnamKingCardErrorConstans{
		public static final Map<Integer,CodeMsgType> errorCodeMap = new HashMap<>();
		static {
			errorCodeMap.put(0, CodeMsgType.SUCCESS);
			errorCodeMap.put(1, CodeMsgType.ERR_SYS);
			errorCodeMap.put(2, CodeMsgType.ERR_AUTH);
			errorCodeMap.put(3, CodeMsgType.ERR_OBJECT_NOT_FOUND);
			errorCodeMap.put(36, CodeMsgType.ERR_CARD);
			errorCodeMap.put(37, CodeMsgType.ERR_CARD_NUM);
			errorCodeMap.put(38, CodeMsgType.ERR_REQUEST_DENIED);
			errorCodeMap.put(39, CodeMsgType.ERR_REQUEST_PARAMS);
		}
	}
	
}
