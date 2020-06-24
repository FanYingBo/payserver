package com.last.pay.junit;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.last.pay.PayApplication;
import com.last.pay.core.db.pojo.user.UserIfm;
import com.last.pay.core.db.pojo.web.PayOrder;
import com.last.pay.core.service.ILogService;

/**
 * SpringBoot 测试类
 *
 * @RunWith:启动器 SpringJUnit4ClassRunner.class：让 junit 与 spring 环境进行整合
 * @SpringBootTest(classes={PayApplication.class}) 1, 当前类为 springBoot 的测试类
 * @SpringBootTest(classes={PayApplication.class}) 2, 加载 SpringBoot 启动类。启动springBoot
 * junit 与 spring 整合@Contextconfiguartion("classpath:applicationContext.xml")
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {PayApplication.class})
public class LogServiceTest {

	@Resource
	private ILogService logServiceImpl;
	
	@Test
	public void testLogService() throws InterruptedException {
		logServiceImpl.addTiketInteralLog(88, 88, 88);
		logServiceImpl.addUserWarHeadLog(88, 88, 88L, 88.f, 88, 88);
		PayOrder payOrder = new PayOrder();
		payOrder.setOrderNum("xxx"+System.currentTimeMillis());
		payOrder.setBeforeInfullAmount(1.f);
		payOrder.setChannel(2);
		payOrder.setErrorInfo("4");
		payOrder.setErrorThirdNum("5");
		payOrder.setIosPurchaseInfo("6");
		payOrder.setIp("127.0.0.1");
		payOrder.setNickName("9");
		payOrder.setRealCurrency("10");
		payOrder.setRealMoney(11.f);
		payOrder.setPayType(1);
		payOrder.setPlatform(2);
		payOrder.setPointName("xx.xx.xx");
		payOrder.setUserId(12);
		logServiceImpl.addNormalPayErrorLog(payOrder, new Exception("error"));
		logServiceImpl.addUserPayPointLog(payOrder);
		UserIfm userIfm = new UserIfm();
		userIfm.setUserId(11);
		userIfm.setDiamond(1L);
		userIfm.setExperience(2L);
		userIfm.setInfullAmount(3.f);
		userIfm.setLockCannon(4);
		userIfm.setVip(5);
		userIfm.setScore(6L);
		userIfm.setVipIntegral(7);
		userIfm.setPlayTime(10L);
		logServiceImpl.addUserPayLog(payOrder, userIfm);
		
		synchronized (this) {
			this.wait();
		}
	}


}
