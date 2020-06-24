package com.last.pay.core.compute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.coda.airtime.api.util.CodaSoapHandler;
import com.coda.airtime.ws.airtime.api1.InitRequest;
import com.coda.airtime.ws.airtime.api1.InitResult;
import com.coda.airtime.ws.airtime.api1.ItemInfo;
import com.coda.airtime.ws.airtime.api1.InitRequest.Profile;
import com.coda.airtime.ws.airtime.api1.InitRequest.Profile.Entry;
import com.last.pay.base.CodeMsg;
import com.last.pay.base.CodeMsgType;
import com.last.pay.base.common.constants.CodaPayConstants.CodaPayProfileVouchersDetails;
import com.last.pay.base.common.constants.Constants.CurrencyConstants;
import com.last.pay.base.vo.CountryCurrency;
import com.last.pay.core.db.pojo.web.PayOrder;
import com.last.pay.core.dto.coda.response.CodaPayTransParam;
import com.last.pay.core.component.PayConfigManager;
import com.last.pay.core.component.third.ThailandCodaPayConfiguration;

@Component
public class ThailandPayBuilder implements ExchangePayParamBuilder{

	private static final Log logger = LogFactory.getLog(ThailandPayBuilder.class);
	@Autowired
	private CodaSoapHandler codaSoapHandler;
	@Autowired
	private PayConfigManager payConfigManager;
	@Autowired
	private ThailandCodaPayConfiguration thailandCodaPayConfiguration;
	@Override
	public String getCurrency() {
		return CurrencyConstants.Thailand;
	}

	@Override
	public CountryCurrency getCountryCurrency() {
		return CurrencyConstants.country.get(getCurrency());
	}

	@Override
	public Map<String, Object> buildCodaParamMap(String orderId, HttpServletRequest request) {
		return new HashMap<String, Object>();
	}

	@Override
	public CodeMsg<?> getTranscationParam(PayOrder payOrder, String orderId, String subPayType, String url,
			String mnoId) {
		ArrayList<ItemInfo> items = new ArrayList<ItemInfo>();
		ItemInfo itemInfo = new ItemInfo();
		itemInfo.setCode(payOrder.getPointName());

		float realMoney = payConfigManager.getPayPointMoney(payOrder.getPointName(), payOrder.getRealCurrency());
		if(realMoney > 0) {
			payOrder.setRealMoney(realMoney);
		} else {
			logger.error("泰国CodaPay支付付费点【"+ payOrder.getPointName() +"】【THB】金额未配置，请检查数据库付费点配置");
			return CodeMsg.failure(CodeMsgType.ERR_ORDER_MONEY);
		}
		itemInfo.setPrice(realMoney);
		itemInfo.setName(payConfigManager.getPayPointTitle(payOrder.getPointName(),payOrder.getRealCurrency(), Boolean.FALSE));
		itemInfo.setType((short)1);
		items.add(itemInfo);
		Profile roProfile = new Profile();
		Entry entry = new Entry();
		entry.setKey(CodaPayProfileVouchersDetails.USER_ID);
		entry.setValue(String.valueOf(payOrder.getUserId()));
		roProfile.getEntry().add(entry);
		Entry entry_2 = new Entry();
		entry_2.setKey(CodaPayProfileVouchersDetails.CLIENT_IP);
		entry_2.setValue(payOrder.getIp());
		roProfile.getEntry().add(entry_2);
		Entry entry_3 = new Entry();
		entry_3.setKey(CodaPayProfileVouchersDetails.NEED_MNO_ID);
		entry_3.setValue(mnoId);
		roProfile.getEntry().add(entry_3);
		InitResult initTxn = null;
		try {
			InitRequest request = codaSoapHandler.builderRequest(thailandCodaPayConfiguration.getThailandApikey(),subPayType,orderId, getCountryCurrency(),items, roProfile);
			logger.info("泰国支付：调用CodaPay初始化事务请求，请求信息："+request);
		    initTxn = codaSoapHandler.initTxn(request);
		} catch (Exception e) {
			logger.error("泰国支付：调用CodaPay初始化事务接口失败，"+e.getMessage());
			return CodeMsg.failure(CodeMsgType.ERR_CALL_CODA_INIT);
		}
		if(Objects.nonNull(initTxn)) {
			logger.info("泰国支付：调用CodaPay初始化事务接口成功，响应信息："+initTxn);
			return CodeMsg.common((int)initTxn.getResultCode(),initTxn.getResultDesc(),buildParam(String.valueOf(initTxn.getTxnId()),url));
		} else {
			logger.error("泰国支付：获取CodaPay事务ID失败，响应信息为空");
			return CodeMsg.common(CodeMsgType.ERR_CALL_CODA_PAY_ERROR);
		}
	}

	@Override
	public CodeMsg<?> notifyTransStatus(long txnId) {
		return CodeMsg.success(CodeMsgType.SUCCESS);
	}
	
	private CodaPayTransParam buildParam(String txnId,String url) {
		CodaPayTransParam codaPayTransParam = new CodaPayTransParam();
		codaPayTransParam.setUrl(url);
		codaPayTransParam.setTxnId(txnId);
		return codaPayTransParam;
	}
}
