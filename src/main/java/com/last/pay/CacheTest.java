package com.last.pay;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;


import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

public class CacheTest {
	
	private static Object lock1 = new Object();
	private static Object lock2 = new Object();

	public static void main(String[] args) {
//		batchSql();
//		deadLock();
//		randDeleteTwo();
//		jdbcTest();
//		System.out.println(2&3);
//		System.out.println(Integer.bitCount(2^3));
		String[] ss = new String[] {"3","1"};
		Object[] soj = ss;
		soj[1] = 1;
	}
	
	
	public static void jdbcTest() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Connection connection = DriverManager.getConnection("jdbc:mysql://192.168.1.251:3306/UserDB?useSSL=true","test","123456");
			System.out.println("连接成功");
			PreparedStatement prepareStatement = connection.prepareStatement("update user_status set use_map_point=? where userid=? and use_map_point <> 1;");
			prepareStatement.setInt(1, 0);
			prepareStatement.setInt(2, 30084335);
			int executeUpdate = prepareStatement.executeUpdate();
			System.out.println(executeUpdate);
			prepareStatement.close();
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void randDeleteTwo() {
		String prefix = "hhdsada-";
		List<String> dss = new ArrayList<>();
		for(int i = 1;i <= 1000000;i++) {
			dss.add(prefix+i);
		}
		List<String> removed = new ArrayList<>();
		Random random = new Random();
		int first = random.nextInt(dss.size() - 1);
		int second = random.nextInt(dss.size() - 1);
		String remove = dss.remove(first);
		String remove2 = dss.remove(second);
		removed.add(remove);
		removed.add(remove2);
		System.out.println(removed);
		long start = System.currentTimeMillis();
		List<String> result = new ArrayList<>();
		boolean[] ins = new boolean[1000000];
		for(int i = 0;i <dss.size();i++) {
			ins[Integer.parseInt(dss.get(i).substring(prefix.length())) - 1] = true;
		}
		for(int i = 0;i <dss.size();i++) {
			if(!ins[i]) {
				result.add(prefix+(i+1));
			}
		}
		System.out.println("耗时："+(System.currentTimeMillis() - start)+" "+result);
	}
	public static void deadLock() {
		CountDownLatch countDownLatch = new CountDownLatch(2);
		Thread thread1 = new Thread(()-> {
			synchronized (lock1) {
				countDownLatch.countDown();
				synchronized (lock2) {					
				}
			}
		});
		Thread thread2 = new Thread(()-> {
			synchronized (lock2) {
				countDownLatch.countDown();
				synchronized (lock1) {			
				}
			}
		});
		thread1.start();
		thread2.start();
		try {
			countDownLatch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void batchSql() {
		String[] area = {"daweizhen","chaohuazhen","wangcun","kuangwuju","quliang","baizhai"};
		Random random = new Random();
		File file = new File("F:\\dbdata\\testdata.sql");
		int count = 1000000;
		try {
			file.createNewFile();
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file));
			StringBuilder sbuBuilder = new StringBuilder();
			for(int index = 510000; index < count;index++) {
				
				sbuBuilder.append("insert into `user` (`userId`,`name`,`age`,`job`,`address`) values (");
				sbuBuilder.append(10000+index).append(",");
				if(index % 4 == 1) {
					sbuBuilder.append("'张三'");
				}
				if(index % 4 == 2) {
					sbuBuilder.append("'李四'");					
				}
				if(index % 4 == 3) {
					sbuBuilder.append("'王麻子'");	
				}
				if(index % 4 == 0) {
					sbuBuilder.append("'赵二'");	
				}
				sbuBuilder.append(",");
				int nextInt = random.nextInt(area.length);
				sbuBuilder.append(index % 80 + 20).append(",'程序员','henan sheng zhengzhou xinmishi "+area[nextInt]+index%10000+"num');\r\n");
			}
			outputStreamWriter.write(sbuBuilder.toString());
			outputStreamWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static List<String> runRedis() throws IOException {
		Set<HostAndPort> hostAndPorts = new HashSet<HostAndPort>();
		hostAndPorts.add(new HostAndPort("192.168.1.252", 7001));
		hostAndPorts.add(new HostAndPort("192.168.1.252", 7002));
		hostAndPorts.add(new HostAndPort("192.168.1.252", 7003));
		hostAndPorts.add(new HostAndPort("192.168.1.252", 7004));
		hostAndPorts.add(new HostAndPort("192.168.1.252", 7005));
		hostAndPorts.add(new HostAndPort("192.168.1.252", 7006));
		JedisCluster js = new JedisCluster(hostAndPorts);
		try {
			Map<String, JedisPool> clusterNodes = js.getClusterNodes();
			Set<Entry<String, JedisPool>> entrySet = clusterNodes.entrySet();
			List<String> list = new ArrayList<String>();
			for(Entry<String, JedisPool> entry : entrySet){
				JedisPool jedisPool = entry.getValue();
				Jedis jedis = jedisPool.getResource();
				ScanParams scanParams = new ScanParams();
				scanParams.match("wx:POOL*").count(2000);
				ScanResult<String> scanResult = jedis.scan("", scanParams);
//				Set<String> keys = jedis.keys("wx:POOL*");
				list.addAll(scanResult.getResult());
				jedisPool.close();
			}
			return list;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}finally {
			js.close();
		}
	}
//	public static int tesRet() {
//		try {
//			throw new NullPointerException();
//		}catch (Exception e) {
//			return 3;
//		}finally {
//			return 1;
//		}
//	}
	public static class Payparams{
		Integer userId;     // 玩家ID
		
		Float money; //金额
			
		String currency;//货币符号（非必填）
			
		String pointName; //付费点
			
		Integer payType; //支付类型 越南点卡 3
			
		Integer platform;//平台
			
		Integer channel;//渠道

		String ip; //IP

		String cardNum; //卡号
		String cardPin;//卡PIN
		String providerCode;//提供商代码
		
		
		public Payparams(Integer userId, Float money, String currency, String pointName, Integer payType,
				Integer platform, Integer channel, String ip, String cardNum, String cardPin, String providerCode) {
			super();
			this.userId = userId;
			this.money = money;
			this.currency = currency;
			this.pointName = pointName;
			this.payType = payType;
			this.platform = platform;
			this.channel = channel;
			this.ip = ip;
			this.cardNum = cardNum;
			this.cardPin = cardPin;
			this.providerCode = providerCode;
		}
		public Integer getUserId() {
			return userId;
		}
		public void setUserId(Integer userId) {
			this.userId = userId;
		}
		public Float getMoney() {
			return money;
		}
		public void setMoney(Float money) {
			this.money = money;
		}
		public String getCurrency() {
			return currency;
		}
		public void setCurrency(String currency) {
			this.currency = currency;
		}
		public String getPointName() {
			return pointName;
		}
		public void setPointName(String pointName) {
			this.pointName = pointName;
		}
		public Integer getPayType() {
			return payType;
		}
		public void setPayType(Integer payType) {
			this.payType = payType;
		}
		public Integer getPlatform() {
			return platform;
		}
		public void setPlatform(Integer platform) {
			this.platform = platform;
		}
		public Integer getChannel() {
			return channel;
		}
		public void setChannel(Integer channel) {
			this.channel = channel;
		}
		public String getIp() {
			return ip;
		}
		public void setIp(String ip) {
			this.ip = ip;
		}
		public String getCardNum() {
			return cardNum;
		}
		public void setCardNum(String cardNum) {
			this.cardNum = cardNum;
		}
		public String getCardPin() {
			return cardPin;
		}
		public void setCardPin(String cardPin) {
			this.cardPin = cardPin;
		}
		public String getProviderCode() {
			return providerCode;
		}
		public void setProviderCode(String providerCode) {
			this.providerCode = providerCode;
		}
		
	}
	
	public static class CodeMsg{
		int code;
		String msg;
		public int getCode() {
			return code;
		}
		public void setCode(int code) {
			this.code = code;
		}
		public String getMsg() {
			return msg;
		}
		public void setMsg(String msg) {
			this.msg = msg;
		}
		@Override
		public String toString() {
			return "CodeMsg [code=" + code + ", msg=" + msg + "]";
		}
		
	}
}

