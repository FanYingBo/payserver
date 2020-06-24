package com.last.pay.core.handler.common;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.text.MessageFormat;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Base64.Decoder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;
import com.last.pay.base.CodeMsg;
import com.last.pay.base.CodeMsgType;
import com.last.pay.core.dto.huawei.HuaWeiAccessToken;
import com.last.pay.util.RetryUtil;

@Component
public class HuaWeiRequestTool {
	
	private static final Log logger = LogFactory.getLog(HuaWeiRequestTool.class);
	@Autowired
	private RestTemplate restTemplate;
	@Value("${pay.interface.retry.count}")
	private Integer reTry;
	
	/**
	 * 获取授权的头信息
	 * @param authUrl 授权连接
	 * @param clientSecretKey 客户端密钥
	 * @param clientId 客户端ID
	 * @return
	 */
	public CodeMsg<String> getAuthHead(String authUrl,String clientSecretKey,String clientId){
		CodeMsg<String> accessTokenRet = getAccessToken(authUrl, clientSecretKey, clientId);
		if(accessTokenRet.getCode() == CodeMsgType.SUCCESS.getCode()) {
			String accessToken = accessTokenRet.getData();
			String oriString = MessageFormat.format("APPAT:{0}", accessToken);
		    try {
				String authHead = MessageFormat.format("Basic {0}", Base64.getEncoder().encodeToString(oriString.getBytes("UTF-8")));
				accessTokenRet.setData(authHead);
				return accessTokenRet;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return accessTokenRet;
	}
	
	/**
	 * 获取授权AccessToken
	 * @param authUrl
	 * @param clientSecretKey
	 * @param clientId
	 * @return
	 */
	public CodeMsg<String> getAccessToken(String authUrl,String clientSecretKey,String clientId) {
		MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
		map.add("grant_type", "client_credentials");
		try {
			map.add("client_secret", URLEncoder.encode(clientSecretKey, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		map.add("client_id", clientId);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(map,headers);
		int retryCount = reTry.intValue();
		String responseBody = RetryUtil.retryPayOrder(null, retryCount, ()->restTemplate.postForObject(authUrl,httpEntity, String.class), "调用HuaWei授权接口失败");
		if(Objects.nonNull(responseBody)) {
			HuaWeiAccessToken accessTokey = JSONObject.parseObject(responseBody, HuaWeiAccessToken.class);
			String access_token = accessTokey.getAccess_token();
			logger.info("调用HuaWei授权接口成功，access_token："+access_token);
			return CodeMsg.common(CodeMsgType.SUCCESS,access_token);
		}else {
			logger.error("调用HuaWei授权接口失败，获取access_token失败");
			return CodeMsg.common(CodeMsgType.ERR_HUAWEI_GETAUTH);
		}
	}

	private static Map<Integer, CodeMsgType> responseMap = new HashMap<Integer, CodeMsgType>(); 
	
	public CodeMsgType getCodeMsgType(int status) {
		return responseMap.get(status);
	}
	
	static {
		responseMap.put(0,CodeMsgType.SUCCESS);
		responseMap.put(1,CodeMsgType.ERR_HUAWEI_WORD_CANCEL);
		responseMap.put(2,CodeMsgType.ERR_HUAWEI_NETWORK);
		responseMap.put(3,CodeMsgType.ERR_HUAWEI_REQUEST_NOT_SUPPORT);
		responseMap.put(4,CodeMsgType.ERR_HUAWEI_PRODUCT_NOT_SUPPORT);
		responseMap.put(5,CodeMsgType.ERR_HUAWEI_PARAM);
		responseMap.put(6,CodeMsgType.ERR_HUAWEI_API_ERROR);
		responseMap.put(7,CodeMsgType.ERR_HUAWEI_PURCHASE_HAS_PAY);
		responseMap.put(8,CodeMsgType.ERR_HUAWEI_PURCHASE_NOT_HAS);
		responseMap.put(9,CodeMsgType.ERR_HUAWEI_PRODUCT_HAS_SPEND);
	}
	
	/** 
	 * 	校验签名信息 
	 * @param content 结果字符串 
	 * @param sign 签名字符串 
	 * @param publicKey 支付公钥 
	 * @return 是否校验通过 
	 */ 
	public boolean  verifySignature(String content, String sign, String publicKeyStr) {
	    if (sign == null) {
	        return false;
	    }
	    if (publicKeyStr == null) {
	        return false;
	    }
	    Decoder decoder = Base64.getDecoder();
	    byte[] encodedKey = null;
	    RSAPublicKey publicKey = null;
	    try {
	        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
	        encodedKey = decoder.decode(publicKeyStr);
	        publicKey = (RSAPublicKey)keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
	        java.security.Signature signature = null;
	        signature = java.security.Signature.getInstance("SHA256WithRSA");
	  
	        signature.initVerify(publicKey);
	        signature.update(content.getBytes(StandardCharsets.UTF_8));
	 
	        byte[] bsign = decoder.decode(sign);
	        return signature.verify(bsign);
	    } catch (RuntimeException e) {
	    	e.printStackTrace();
	    	return false;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}

}
