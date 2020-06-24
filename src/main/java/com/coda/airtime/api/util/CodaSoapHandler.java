package com.coda.airtime.api.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.ws.BindingProvider;

import com.coda.airtime.ws.airtime.api1.InitRequest;
import com.coda.airtime.ws.airtime.api1.InitResult;
import com.coda.airtime.ws.airtime.api1.InquiryPaymentRequest;
import com.coda.airtime.ws.airtime.api1.ItemInfo;
import com.coda.airtime.ws.airtime.api1.PaymentResult;
import com.coda.airtime.ws.airtime.api1.InitRequest.Profile;
import com.coda.airtime.ws.airtime.api1_0.CodaAPI;
import com.coda.airtime.ws.airtime.api1_0.Payment;
import com.last.pay.base.vo.CountryCurrency;

public class CodaSoapHandler {
	private String airtimeURL = null;
	private Payment airtime = null;

	public String getAirtimeURL () {
		return airtimeURL;
	}
	
	public void initCodaSoap(String airtimeURL,String airtimeWSDLURL){
		this.airtimeURL = airtimeURL;
		URL WSDL_LOCATION = null;
		try {
			WSDL_LOCATION = new URL(airtimeWSDLURL);
			Payment codaPortPayment = (Payment) (new CodaAPI(WSDL_LOCATION)).getApi();
			BindingProvider bp = (BindingProvider)codaPortPayment;
			bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, WSDL_LOCATION.toExternalForm());
			airtime = codaPortPayment;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	public InitRequest builderRequest(String apiKey,String payType,String orderId,CountryCurrency cunCountryCurrency, ArrayList<ItemInfo> items, Profile profile) {
		InitRequest initReq = new InitRequest();
		initReq.setApiKey(apiKey);
		initReq.setOrderId(orderId);
		initReq.setCountry(cunCountryCurrency.getCountryCode());
		initReq.setCurrency(cunCountryCurrency.getCurrencyCode());
		initReq.setPayType(Short.parseShort(payType));
		initReq.getItems().addAll(items);
		initReq.setProfile(profile);
		return initReq;
	}
	public InitResult initTxn(InitRequest initReq) {
		return airtime.init(initReq);
	}

	public PaymentResult inquiryTxn(String apiKey, long txnId) {
		InquiryPaymentRequest inqReq = new InquiryPaymentRequest();
		inqReq.setApiKey(apiKey);
		inqReq.setTxnId(txnId);
		PaymentResult result = airtime.inquiryPaymentResult(inqReq);
		return result;
	}
	
	public boolean validateChecksum (String txnId,String apiKey,String orderId,String resultCode,String checksum) {
		try {
			String values = txnId + apiKey + orderId + resultCode;
			byte[] b = HashUtils.MD5(values);
			String sum = HashUtils.convertToHex(b);
			return sum.equals(checksum);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
}
