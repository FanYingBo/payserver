package com.last.pay.core.component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coda.airtime.api.util.CodaSoapHandler;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.last.pay.base.common.constants.Constants;
import com.last.pay.base.common.constants.PayParamConstants;
import com.last.pay.base.common.constants.Constants.CurrencyConstants;
import com.last.pay.cache.CacheManager;
import com.last.pay.cache.JedisClusterFactory;
import com.last.pay.cache.RedisCacheManager;
import com.last.pay.core.component.load.PayParamConfiguration;
import com.last.pay.core.component.sys.RedisConfiguration;
import com.last.pay.core.component.sys.SystemConfiguration;
import com.last.pay.core.component.third.CambodiaCodaPayConfiguration;
import com.last.pay.core.component.third.CodaPayConfiguration;
import com.last.pay.core.component.third.GoogleAuth2Configuration;
import com.last.pay.core.component.third.HuaWeiPayConfiguration;
import com.last.pay.core.component.third.IOSPayConfiguration;
import com.last.pay.core.component.third.MyanmarCodaPayConfiguration;
import com.last.pay.core.component.third.PaycentConfiguraion;
import com.last.pay.core.component.third.ThailandCodaPayConfiguration;
import com.last.pay.core.component.third.VietnamPayConfiguration;
import com.last.pay.util.EncryptUtil;

import redis.clients.jedis.Jedis;

@Configuration
public class PayServerBeanComponent {
	
	private static final Log logger = LogFactory.getLog(PayServerBeanComponent.class);
	
	@Value("${db.encrypt}")
	private Boolean encrypt;
	@Value("${publicKey}")
	private String publicKey;
	@Autowired
	private PayParamConfiguration payParamConfiguration;
	@Autowired
	private RedisConfiguration redisConfiguration;
	
	@Bean
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate;
	}
	@Bean
	public SystemConfiguration systemConfiguration(@Value("${pay.systemEnv}") Integer systemEnv , @Value("${pay.interface.retry.count}") Integer retryCount) {
		String warheadedPerson = payParamConfiguration.getPayParam(PayParamConstants.warheadPerson);
		String warheadPublic = payParamConfiguration.getPayParam(PayParamConstants.warheadPublic);
		String forceSendScore = payParamConfiguration.getPayParam(PayParamConstants.sendScore);
		return new SystemConfiguration()
				.setSystemEnv(systemEnv)
				.setReTry(retryCount)
				.setWarheadPerson(Integer.parseInt(warheadedPerson))
				.setWarheadPublic(Integer.parseInt(warheadPublic))
				.setForceSendScore(Integer.parseInt(forceSendScore));
	}
	@Bean
	public CodaPayConfiguration codaPayConfiguration(@Value("${pay.coda.timeout}") Integer timeout,
						@Value("${pay.coda.retryInterval}") Integer retryInterval, @Value("${pay.coda.retryDelay}") Integer retryDelay,@Value("${pay.coda.callBackCheckMD5}") Integer callBackCheckMD5) {
		String payParam = payParamConfiguration.getPayParam(PayParamConstants.coda_pay_soap);
		JSONObject parseObject = JSONObject.parseObject(payParam);
		String soapSandboxUrl = parseObject.getString(PayParamConstants.coda_pay_soap_sandbox_url);
		String soapSroductUrl = parseObject.getString(PayParamConstants.coda_pay_soap_product_url);
		String codaPayWsdl = payParamConfiguration.getPayParam(PayParamConstants.coda_pay_soap_wsdl);
		JSONObject codaPayWsdlObj = JSONObject.parseObject(codaPayWsdl);
		String sanboxWsdl = codaPayWsdlObj.getString(PayParamConstants.coda_pay_soap_sandbox_wsdl);
		String productWsdl = codaPayWsdlObj.getString(PayParamConstants.coda_pay_soap_prouct_wsdl);
		
		String codaPayIframeUrl = payParamConfiguration.getPayParam(PayParamConstants.coda_pay_iframe_url);
		JSONObject iframeUrl = JSONObject.parseObject(codaPayIframeUrl);
		String iframeSandboxUrl = iframeUrl.getString(PayParamConstants.coda_pay_iframe_sandbox_url);
		String iframeProductUrl = iframeUrl.getString(PayParamConstants.coda_pay_iframe_product_url);
		String codaPayBackendUrl = payParamConfiguration.getPayParam(PayParamConstants.coda_pay_backend_url);
		JSONObject backendUrlObj = JSONObject.parseObject(codaPayBackendUrl);
		String sandboxUrl = backendUrlObj.getString(PayParamConstants.coda_pay_soap_sandbox_url);
		String productUrl = backendUrlObj.getString(PayParamConstants.coda_pay_soap_product_url);
		return new CodaPayConfiguration()
				.setSandboxUrl(sandboxUrl)
				.setProductionUrl(productUrl)
				.setSoapSandboxUrl(soapSandboxUrl)
				.setSoapProductUrl(soapSroductUrl)
				.setSoapSandboxWsdlUrl(sanboxWsdl)
				.setSoapProductWsdlUrl(productWsdl)
				.setTimeout(timeout)
				.setRetryInterval(retryInterval)
				.setRetryDelay(retryDelay)
				.setCallBackCheckMD5(callBackCheckMD5)
				.setIframeSandboxUrl(iframeSandboxUrl)
				.setIframeProductUrl(iframeProductUrl);
	}
	
	@Bean
	public MyanmarCodaPayConfiguration myanmarCodaPayConfiguration() {
		String mynamarLang = payParamConfiguration.getPayParam(PayParamConstants.coda_pay_myanmar_lang);
		String mmApiKey = payParamConfiguration.getPayParam(PayParamConstants.coda_pay_apikey);
		JSONArray parseArray = JSONObject.parseArray(mynamarLang);
		List<String> langLists = new ArrayList<String>(parseArray.size());
		for(int i = 0;i < parseArray.size(); i++) {
			langLists.add(parseArray.getString(i));
		}
		String pay_channel = payParamConfiguration.getPayParam(PayParamConstants.coda_pay_channel);
		float usaRate = payParamConfiguration.getUSDRate(CurrencyConstants.Myanmar);
		return new MyanmarCodaPayConfiguration()
				.setCurrency(CurrencyConstants.Myanmar)
				.setLang(langLists)
				.setPay_channel(Integer.parseInt(pay_channel))
				.setRate(usaRate)
				.setApi_key(mmApiKey);
	}
	@Bean
	public CambodiaCodaPayConfiguration cambodiaCodaPayConfiguration() {
		String mmApiKey = payParamConfiguration.getPayParam(PayParamConstants.coda_pay_apikey_cambodia);
		return new CambodiaCodaPayConfiguration().setCurrency(CurrencyConstants.USA).setApi_key(mmApiKey);
	}
	
	@Bean
	public ThailandCodaPayConfiguration thailandCodaPayConfiguration() {
		String thApiKey = payParamConfiguration.getPayParam(PayParamConstants.coda_pay_apikey_thailand);
		return new ThailandCodaPayConfiguration().setThailandApikey(thApiKey).setCurrency(CurrencyConstants.Thailand);
	}
	
	@Bean
	public CodaSoapHandler codaSoapHandler(SystemConfiguration systemConfiguration, CodaPayConfiguration codaPayConfiguration) {
		CodaSoapHandler handler = new CodaSoapHandler();
		try {
			if(systemConfiguration.getSystemEnv() == Constants.ENV_TEST || systemConfiguration.getSystemEnv() == Constants.ENV_SANDBOX) {
				logger.info("CodaPay 沙箱环境.... url: "+codaPayConfiguration.getSoapSandboxUrl());
				handler.initCodaSoap(codaPayConfiguration.getSoapSandboxUrl(), codaPayConfiguration.getSoapSandboxWsdlUrl());
			}else {
				logger.info("CodaPay 生产环境.... url: "+codaPayConfiguration.getSoapProductUrl());
				handler.initCodaSoap(codaPayConfiguration.getSoapProductUrl(), codaPayConfiguration.getSoapProductWsdlUrl());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return handler;
	}

	@Bean
	public GoogleAuth2Configuration googleAuth2Configuration() {
		String jsonFile = payParamConfiguration.getPayParam(PayParamConstants.google_api_json_file);
		String appName = payParamConfiguration.getPayParam(PayParamConstants.google_api_appname);
		String authUrl = payParamConfiguration.getPayParam(PayParamConstants.google_auth_url);
		String verifyUrl = payParamConfiguration.getPayParam(PayParamConstants.google_pay_verify_url);
		ArrayList<String> arrayList = new ArrayList<>();
		arrayList.add(authUrl);
		return new GoogleAuth2Configuration().setAppName(appName).setVerifyUrl(verifyUrl).setJsonFile(jsonFile).setScopes(arrayList);
	}
	
	@Bean
	public GoogleCredential googleCredential(GoogleAuth2Configuration googleAuth2Configuration) {
		URL resource = Thread.currentThread().getContextClassLoader().getResource(googleAuth2Configuration.getJsonFile());
		File file = new File(resource.getPath());
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(file);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		GoogleCredential credential = null;
		try {
			credential = GoogleCredential
					.fromStream(fileInputStream)
				    .createScoped(googleAuth2Configuration.getScopes());
			logger.info("PrivateKeyId: "+credential.getServiceAccountPrivateKeyId());
			logger.info("ServiceAccountUser: "+credential.getServiceAccountUser());
			logger.info("ServiceAccountId: "+credential.getServiceAccountId());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return credential;
	}

	
	@Bean
	public HuaWeiPayConfiguration huaWeiPayConfiguration() {
		String url_suffix = payParamConfiguration.getPayParam(PayParamConstants.huawei_api_client_url_suffix);
		String clientUrl = payParamConfiguration.getPayParam(PayParamConstants.huawei_api_client_url);
		
		JSONObject parseObject = JSONObject.parseObject(clientUrl);
		String chinaUrl = parseObject.getString(PayParamConstants.huawei_api_client_china_url);
		String germanyUrl = parseObject.getString(PayParamConstants.huawei_api_client_germany_url);
		String singaporeUrl = parseObject.getString(PayParamConstants.huawei_api_client_singapore_url);
		String russiaUrl = parseObject.getString(PayParamConstants.huawei_api_client_russia_url);
		
		String publicKeyStr = payParamConfiguration.getPayParam(PayParamConstants.huawei_api_publickey);
		JSONObject publicKeyObj = JSONObject.parseObject(publicKeyStr);
		String inlandPublicKey = publicKeyObj.getString(PayParamConstants.huawei_api_inland);
		String internationalPublicKey = publicKeyObj.getString(PayParamConstants.huawei_api_international);
		
		String clientIdStr = payParamConfiguration.getPayParam(PayParamConstants.huawei_client_id);
		JSONObject clientIdObj = JSONObject.parseObject(clientIdStr);
		String inlandClientId = clientIdObj.getString(PayParamConstants.huawei_api_inland);
		String internationalClientId = clientIdObj.getString(PayParamConstants.huawei_api_international);
		
		String secretKeyStr = payParamConfiguration.getPayParam(PayParamConstants.huawei_client_secretkey);
		JSONObject secretKeyObj = JSONObject.parseObject(secretKeyStr);
		String inlandSecretKey = secretKeyObj.getString(PayParamConstants.huawei_api_inland);
		String internationalSecretKey = secretKeyObj.getString(PayParamConstants.huawei_api_international);
		
		String authUrlStr = payParamConfiguration.getPayParam(PayParamConstants.huawei_auth_url);
		JSONObject authUrlObj = JSONObject.parseObject(authUrlStr);
		String inlandAuthUrl = authUrlObj.getString(PayParamConstants.huawei_api_inland);
		String internationalAuthUrl = authUrlObj.getString(PayParamConstants.huawei_api_international);
		
		return new HuaWeiPayConfiguration()
				.setChinaUrl(chinaUrl + url_suffix)
				.setSingaporeUrl(singaporeUrl + url_suffix)
				.setGermanyUrl(germanyUrl + url_suffix)
				.setRussiaUrl(russiaUrl + url_suffix)
				.setIlandAuthUrl(inlandAuthUrl)
				.setInternationalAuthUrl(internationalAuthUrl)
				.setInlandClientId(inlandClientId).setInlandPublicKey(inlandPublicKey).setInlandSecretKey(inlandSecretKey)
				.setInternationalClientId(internationalClientId).setInternationalPublicKey(internationalPublicKey).setInternationalSecretKey(internationalSecretKey);
	}
	
	@Bean
	public IOSPayConfiguration iosPayConfiguration() {
		String iosUrl = payParamConfiguration.getPayParam(PayParamConstants.ios_api_url);
		JSONObject parseObject = JSONObject.parseObject(iosUrl);
		return new IOSPayConfiguration().setProductUrl(parseObject.getString(PayParamConstants.ios_product)).setSandboxUrl(parseObject.getString(PayParamConstants.ios_sandbox));
	}
	
	@Bean
	public PaycentConfiguraion paycentConfiguraion() {
		String merchantParam = payParamConfiguration.getPayParam(PayParamConstants.paycent_merchant);
		JSONObject parseObject = JSONObject.parseObject(merchantParam);
		String merchantno = parseObject.getString(PayParamConstants.paycent_merchantno);
		String secret = parseObject.getString(PayParamConstants.paycent_secret);
		String paycentUrl = payParamConfiguration.getPayParam(PayParamConstants.paycent_url);
		JSONObject urlObject = JSONObject.parseObject(paycentUrl);
		String sandboxUrl = urlObject.getString(PayParamConstants.paycent_sandbox_url);
		String productUrl = urlObject.getString(PayParamConstants.paycent_product_url);
		String returnUrl = urlObject.getString(PayParamConstants.paycent_return_url);
		String notifyUrl = urlObject.getString(PayParamConstants.paycent_notify_url);
		return new PaycentConfiguraion()
				.setMerchant(merchantno)
				.setSecret(secret)
				.setSandboxUrl(sandboxUrl)
				.setProductUrl(productUrl)
				.setReturnUrl(returnUrl)
				.setNotifyUrl(notifyUrl);
	}
	
	@Bean
	public VietnamPayConfiguration vietnamPayConfiguration() {
		float vietnam = payParamConfiguration.getUSDRate(CurrencyConstants.Vietnam);
		String payVietnam = payParamConfiguration.getPayParam(PayParamConstants.pay_vietnam);
		
		JSONObject payVietnamObj = JSONObject.parseObject(payVietnam);
		String partnerId = payVietnamObj.getString(PayParamConstants.pay_vietnam_partner_id);
		String partnerUsername = payVietnamObj.getString(PayParamConstants.pay_vietnam_partner_username);
		String payVietnamUrl = payParamConfiguration.getPayParam(PayParamConstants.pay_vietnam_url);
		
		JSONObject payUrlObj = JSONObject.parseObject(payVietnamUrl);
		String queryUrl = payUrlObj.getString(PayParamConstants.pay_vietnam_query_url);
		String transUrl = payUrlObj.getString(PayParamConstants.pay_vietnam_trans_url);
		
		String vietanmUpay = payParamConfiguration.getPayParam(PayParamConstants.pay_vietnam_upay);
		JSONObject upayParamObj = JSONObject.parseObject(vietanmUpay);
		String upayAppKey = upayParamObj.getString(PayParamConstants.pay_vietnam_upay_appkey);
		String upayUrl = upayParamObj.getString(PayParamConstants.pay_vietnam_upay_url);
		String upayReceiveKey = upayParamObj.getString(PayParamConstants.pay_vietnam_upay_receivekey);
		
		String kingCardClientStr = payParamConfiguration.getPayParam(PayParamConstants.pay_vietnam_king_card_client);
		JSONObject kingCardClientObj = JSONObject.parseObject(kingCardClientStr);
		String kingCardClientId = kingCardClientObj.getString(PayParamConstants.pay_vietnam_king_card_client_id);
		String kingCardSecretKey = kingCardClientObj.getString(PayParamConstants.pay_vietnam_king_card_secret_key);
		String kingCardApiKey = kingCardClientObj.getString(PayParamConstants.pay_vietnam_king_card_api_key);
		
		String kingCardUrlStr = payParamConfiguration.getPayParam(PayParamConstants.pay_vietnam_kingcard_url);
		JSONObject kingCardUrlObj = JSONObject.parseObject(kingCardUrlStr);
		String kingCardSandboxUrl = kingCardUrlObj.getString(PayParamConstants.pay_vietnam_king_card_sandbox_url);
		String kingCardProductUrl = kingCardUrlObj.getString(PayParamConstants.pay_vietnam_king_card_product_url);
		
		String kingCardUriStr = payParamConfiguration.getPayParam(PayParamConstants.pay_vietnam_kingcard_uri);
		JSONObject kingCardUriObj = JSONObject.parseObject(kingCardUriStr);
		String kingCardTransUri = kingCardUriObj.getString(PayParamConstants.pay_vietnam_king_card_trans_uri);
		
		String kingCardNotifyUrlStr = payParamConfiguration.getPayParam(PayParamConstants.pay_vietnam_kingcard_notify_url);
		JSONObject kingCardNotifyUrlObj = JSONObject.parseObject(kingCardNotifyUrlStr);
		String kingCardNotifySandboxUrl = kingCardNotifyUrlObj.getString(PayParamConstants.pay_vietnam_kingcard_notify_sandbox_url);
		String kingCardNotifyProductUrl = kingCardNotifyUrlObj.getString(PayParamConstants.pay_vietnam_kingcard_notify_product_url);
		
		String ipAddress = payParamConfiguration.getPayParam(PayParamConstants.pay_vietnam_king_card_ip_address);
		JSONArray jsonArray = JSONObject.parseArray(ipAddress);
		List<String> ipAddressList = jsonArray.toJavaList(String.class);
		String bangLangAppKey = payParamConfiguration.getPayParam(PayParamConstants.pay_vietnam_banglang_apikey);
		String bangLangUrl = payParamConfiguration.getPayParam(PayParamConstants.pay_vietnam_banglang_url);
		
		String upayCbank = payParamConfiguration.getPayParam(PayParamConstants.pay_vietnam_upay_ebank);
		JSONObject upayCbankObj = JSONObject.parseObject(upayCbank);
		String upayEbankUrl = upayCbankObj.getString("url");
		
		String secretyKey = payParamConfiguration.getPayParam(PayParamConstants.vietnam_momo_secrety_key);
		
		return new VietnamPayConfiguration()
				.setPartner_username(partnerUsername).setPartnerId(partnerId)
				.setQueryUrl(queryUrl).setTransUrl(transUrl)
				.setUPayAppKey(upayAppKey).setUPayUrl(upayUrl).setReceiveKey(upayReceiveKey)
				.setRate(vietnam)
				.setKingCardClientId(kingCardClientId)
				.setKingCardSecretKey(kingCardSecretKey)
				.setKingCardSandboxUrl(kingCardSandboxUrl)
				.setKingCardProductUrl(kingCardProductUrl)
				.setKingCardSandboxNotifyUrl(kingCardNotifySandboxUrl)
				.setKingCardProductNotifyUrl(kingCardNotifyProductUrl)
				.setKingCardTransUri(kingCardTransUri)
				.setKingCardApiKey(kingCardApiKey)
				.setIpAddressesOfKingCard(ipAddressList)
				.setBangLangApiKey(bangLangAppKey)
				.setBangLangUrl(bangLangUrl)
				.setUpayEbankUrl(upayEbankUrl)
				.setVietnamMomoSecretyKey(secretyKey);
	}
	@Bean
	public GenericObjectPoolConfig<Jedis> genericObjectPoolConfig() {
		GenericObjectPoolConfig<Jedis> genericObjectPoolConfig = new GenericObjectPoolConfig<Jedis>();
		genericObjectPoolConfig.setMaxIdle(redisConfiguration.getMaxIdle());
		genericObjectPoolConfig.setMaxTotal(redisConfiguration.getMaxTotal());
		genericObjectPoolConfig.setMinIdle(redisConfiguration.getMaxIdle());
		genericObjectPoolConfig.setTestOnBorrow(Boolean.TRUE);
		genericObjectPoolConfig.setTestOnReturn(Boolean.TRUE);
		return genericObjectPoolConfig;
	}
	
	
	@Bean
	public JedisClusterFactory jedisCluster(@Qualifier("genericObjectPoolConfig") GenericObjectPoolConfig<Jedis> genericObjectPoolConfig) {
		String address = redisConfiguration.getAddress();
		if(encrypt) {
			try {
				RSAPublicKey rsaPublicKey = EncryptUtil.getRSAPublicKey(publicKey);
				address = EncryptUtil.bytesToString(EncryptUtil.decryptByRSAPublicKey(rsaPublicKey, address));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		JedisClusterFactory jedisClusterFactory = new JedisClusterFactory(address); 
		jedisClusterFactory.setGenericObjectPoolConfig(genericObjectPoolConfig);
		jedisClusterFactory.setMaxRedirections(redisConfiguration.getMaxRedirections());
		jedisClusterFactory.setTimeOut(redisConfiguration.getTimeout());
		return jedisClusterFactory;
	}
	@Bean
	public CacheManager cacheManager() {
		RedisCacheManager cacheManagerImpl = new RedisCacheManager();
		cacheManagerImpl.setKeystart(redisConfiguration.getKey());
		cacheManagerImpl.setUseCache(redisConfiguration.getUseCache());
		return cacheManagerImpl;
	}
	
	public ThreadPoolTaskExecutor getAsyncThreadPoolTaskExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setCorePoolSize(20);
		taskExecutor.setMaxPoolSize(50);
		taskExecutor.setQueueCapacity(25);
		taskExecutor.setKeepAliveSeconds(10);
		taskExecutor.setThreadNamePrefix("pay-async-");
		// 线程池对拒绝任务（无线程可用）的处理策略，目前只支持AbortPolicy、CallerRunsPolicy；默认为后者
		taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		//调度器shutdown被调用时等待当前被调度的任务完成
		taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
		//等待时长
		taskExecutor.setAwaitTerminationSeconds(60);
		taskExecutor.initialize();
		return taskExecutor;
	}
}
