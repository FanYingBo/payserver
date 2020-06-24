package com.last.pay.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang.StringEscapeUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public final class HashUtils {
	/**
	 *  SHA256
	 * @param str
	 * @return
	 */
	public static String getSHA256(String str){
		try {
			if(Objects.isNull(str)) {
				return "";
			}
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
			byte[] digest = messageDigest.digest(str.getBytes(StandardCharsets.UTF_8));
			return byte2HexString(digest);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}
	
	/**
	 *  HmacSHA256
	 * @param str
	 * @return
	 */
	public static String getHMAC256(String str, String secretKey){
		try {
			if(Objects.isNull(str)) {
				return "";
			}
			Mac sha256HMAC  = Mac.getInstance("HmacSHA256");
			SecretKeySpec secret_key = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
			sha256HMAC.init(secret_key);
			byte[] doFinal = sha256HMAC.doFinal(str.getBytes(StandardCharsets.UTF_8));
			return byte2HexString(doFinal);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return "";
	}
	/**
	 * bytes 转 16 进制
	 * @param bytes
	 * @return
	 */
	public static String byte2HexString(byte[] bytes) {
		StringBuffer stringBuffer = new StringBuffer();
		String temp = null;
		for (int i=0;i < bytes.length; i++){
			temp = Integer.toHexString(bytes[i] & 0xFF);
			if (temp.length() == 1){
				stringBuffer.append("0");
			}
			stringBuffer.append(temp);
		}
		return stringBuffer.toString();
	}

	/**
	 * 获取base64 32 为向量
	 * @return
	 */
	public static String getBase6432IV() {
		Cipher enCipher = null;
		try {
			enCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			final byte[] ivData = new byte[enCipher.getBlockSize()];
			// create (or retrieve) a cryptographic secure random implementation (auto-seeded)
			final SecureRandom rng = new SecureRandom();
			// fill the IV array with random data
			rng.nextBytes(ivData);
			IvParameterSpec ivParam = new IvParameterSpec(ivData);
			byte[] iv2 = ivParam.getIV();
			return EncryptUtil.base64Encode(iv2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	
	public static void main(String[] args) throws IOException {
		String secretKey = "87aecbec39ff4f169ff0a53594d611c2";
		String sign = "cb6a3c3c73fa24a5a750543d6576c60bd86d129d1a4adc0f84b0c24e4f03fe45";
//		String sign = "cb6a3c3c73fa24a5a750543d6576c60bd86d129d1a4adc0f84b0c24e4f03fe45";
//		String kingCard = "{\"order\":{\"id\":1150084,\"user_id\":5425543,\"mrc_order_id\":\"VGT_CARD_1339837\",\"txn_id\":1101108,\"pin\":\"113284599141455\",\"seri\":\"10002260419709\",\"type\":\"VIETTEL\",\"tmp_amount\":\"50000.00\",\"discount\":\"34.00\",\"stat\":2,\"webhooks\":\"https:\\/\\/c.sanrongvang.vn\\/card\\/bk\",\"logs\":null,\"created_at\":\"2020-03-30 15:32:33\",\"updated_at\":\"2020-03-30 15:33:20\"},\"txn\":{\"user_id\":5425543,\"account_id\":1000000041,\"amount\":33000,\"freeze_amount\":0,\"pin\":\"113284599141455\",\"seri\":\"10002260419709\",\"card_type\":\"VIETTEL\",\"gateway\":\"charging\",\"type\":1,\"stat\":2,\"description\":\"Thanh to\\u00e1n th\\u00e0nh c\\u00f4ng \\u0111\\u01a1n h\\u00e0ng 1150084\",\"fee_amount\":17000,\"fee_display\":0,\"updated_at\":\"2020-03-30 15:33:20\",\"created_at\":\"2020-03-30 15:33:20\",\"id\":1101108},\"sign\":\"cb6a3c3c73fa24a5a750543d6576c60bd86d129d1a4adc0f84b0c24e4f03fe45\"}";
		URL path = HashUtils.class.getClassLoader().getResource("webhooks.json");
		File f = new File(path.getFile());
		BufferedReader br = new BufferedReader(new FileReader(f));
		
		String dataJson = br.readLine();
		JsonObject jsonObject = new JsonParser().parse(dataJson).getAsJsonObject();
		System.out.println(jsonObject.get("sign").getAsString());
		jsonObject.remove("sign");
		String dataToSign = jsonObject.toString();
		String newdataToSign = StringEscapeUtils.escapeJava(dataToSign);
//		String newdataToSign = StringUtils.escape();
		String dataToSign1 = newdataToSign.replaceAll("\\\\\"", "\"");
		String dataToSign2 = dataToSign1.replaceAll("/", "\\\\\\/");
	    System.out.println(dataToSign2);
	    String mySign = getHMAC256(dataToSign2, secretKey);
		System.out.println(mySign);
		br.close();
//		System.out.println(parseObject.toJSONString());
//		Set<String> keySet = parseObject.keySet();
//		ArrayList<String> arrayList = new ArrayList<String>(keySet);
//		StringBuffer sb = new StringBuffer();
//		sb.append("{");
//		for(int index = 0;index < arrayList.size();index ++) {
//			sb.append("\"").append(arrayList.get(index)).append("\":");
//			JSONObject jsonObject = parseObject.getJSONObject(arrayList.get(index));
//			ArrayList<String> arrayList2 = new ArrayList<>(jsonObject.keySet());
//			sb.append("{");
//			for(int io = 0;io < arrayList2.size();io++) {
//				Object object = jsonObject.get(arrayList2.get(io));
//				sb.append("\"").append(arrayList2.get(io)).append("\":");
//				if(object != null) {
//					if(object.getClass().equals(String.class)) {
//						sb.append("\"").append(object).append("\"");
//					}else {
//						sb.append(object);
//					}
//				}else {
//					sb.append(object);
//				}
//				if(io != arrayList2.size() -1) {
//					sb.append(",");
//				}else {
//					sb.append("}");
//				}
//			}
//			if(index != arrayList.size() - 1) {
//				sb.append(",");
//			}else if(index == arrayList.size() - 1) {
//				sb.append("}");
//			}
//		}
//		System.out.println(String.class.getCanonicalName());
//		System.out.println(sb.toString());
//		String hmac256 = getHMAC256(kingCard, "431bf99d73f040d385cea7ad1bb6cd3d");
//		System.out.println(hmac256);
	}
}
