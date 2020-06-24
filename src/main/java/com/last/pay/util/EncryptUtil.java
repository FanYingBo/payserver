package com.last.pay.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;

import javax.crypto.Cipher;

public class EncryptUtil {
	
	private static final int keySize = 1024;
	
	private static final String ALGORITHM = "RSA";
	
	private static final String ENCRYPE = "encrypt";
	
	private static final String DECRYPT = "decrypt";
	
	private static final String PRIVATE = "private";
	private static final String PUBLIC = "public";
	
	private static final String PUBLICKEY = "publicKey";
	
	private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";
	
	public static String[] genKeyPair() throws Exception {
		KeyPairGenerator kIKeyPairGenerator;
		kIKeyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
		kIKeyPairGenerator.initialize(keySize);
		KeyPair genKeyPair = kIKeyPairGenerator.genKeyPair();
		RSAPrivateKey private1 = (RSAPrivateKey)genKeyPair.getPrivate();
		RSAPublicKey public1 = (RSAPublicKey)genKeyPair.getPublic();
		return new String[]{base64Encode(private1.getEncoded()),base64Encode(public1.getEncoded())};
	}
	
	
	public static RSAPrivateKey getRSAPrivateKey(String base64Str) throws Exception {
		byte[] decode = base64Decode(base64Str);
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decode);
		KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
		return (RSAPrivateKey)keyFactory.generatePrivate(keySpec);
	}
	
	
	public static RSAPublicKey getRSAPublicKey(String base64Str) throws Exception {
		byte[] decode = base64Decode(base64Str);
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decode);
		KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
		return (RSAPublicKey)keyFactory.generatePublic(keySpec);
	}
	
	public static byte[] encryptByRSAPrivateKey(RSAPrivateKey encodedKey, byte[] byts) throws Exception {
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, encodedKey);
		return cipher.doFinal(byts);
	}
	
	public static byte[] encryptByRSAPublicKey(RSAPublicKey encodedKey, byte[] byts) throws Exception {
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, encodedKey);
		return cipher.doFinal(byts);
	} 
	
	public static byte[] decryptByRSAPrivateKey(RSAPrivateKey decodedKey, String base64Str) throws Exception {
		byte[] decode = base64Decode(base64Str);
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE,decodedKey);
		return cipher.doFinal(decode);
	}
	
	public static byte[] decryptByRSAPublicKey(RSAPublicKey decodedKey, String base64Str) throws Exception {
		byte[] decode = base64Decode(base64Str);
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, decodedKey);
		return cipher.doFinal(decode);
	}
	
	/**
	 *
	 * @param data
	 * @param base64SignData
	 * @param base64PublicKey
	 * @return
	 * @throws Exception
	 */
	public static boolean verifyBase64SignData(String data, String base64SignData,String base64PublicKey) throws Exception {
		if(data == null || base64SignData == null){
			throw new IllegalArgumentException("data or sign data must not be null or empty");
		}
		if(base64PublicKey == null){
			throw new IllegalArgumentException("pubic key must not be null or empty");
		}
		RSAPublicKey rsaPublicKey = getRSAPublicKey(base64PublicKey);
		return verifyBase64SignData(data, base64SignData, rsaPublicKey);
	}
	/**
	 *
	 * @param data
	 * @param base64SignData
	 * @param rsaPublicKey
	 * @return
	 * @throws Exception
	 */
	public static boolean verifyBase64SignData(String data, String base64SignData,RSAPublicKey rsaPublicKey) throws Exception {
		if(data == null || base64SignData == null){
			throw new IllegalArgumentException("data or sign data must not be null or empty");
		}
		if(rsaPublicKey == null){
			throw new IllegalArgumentException("pubic key must not be null or empty");
		}
		byte[] bytes = base64Decode(base64SignData);
		return verifySignData(data, bytes,rsaPublicKey);
	}

	/**
	 *
	 * @param data
	 * @param signData
	 * @param rsaPublicKey
	 * @return
	 * @throws Exception
	 */
	public static boolean verifySignData(String data , byte[] signData,RSAPublicKey rsaPublicKey) throws Exception {
		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		signature.initVerify(rsaPublicKey);
		signature.update(data.getBytes(StandardCharsets.UTF_8));
		return signature.verify(signData);
	}
	
	public static String bytesToString(byte[] bytes) {
		return new String(bytes,StandardCharsets.UTF_8);
	}
	
	public static void main(String[] args) throws Exception {
		dealWithOption(args);
//		String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDJNkfwgA69KaUFlK5PthKD+o/fxjIGSob6kB8Ldm28XtbUYqhwriXo3kz7IV4Q7PzQ35Ac1bAIPA/cnjMGLR7NHaHaixw4RbrBOtEZBExdL6SA+CcOlIy2KgTg/EoG/Lod2ks//FoefU7YGsnLZ/yrl3nIFqNCYO4yY8GGh7oPYQIDAQAB";
//		RSAPublicKey rsaPublicKey = getRSAPublicKey(publicKey);
//		System.out.println(decodeToString(decryptByRSAPublicKey(rsaPublicKey, "fFuUWjwlKNnoyHPnO0YgnBol+8PZeDktMxbUmfKdQlmbDwaUJ3YqtrybT23UWh0nF07IZ+gEDC3q/CCu4oG2IBJGL5ZdBERvaexOFBl3OSuM1PDk8Ni89y1wuUpyatntALgr64f2/r4yUyk3ZxlKUD4KodQJ3JpyYSL9oJMRSvQ=")));
	}
	
	private static void dealWithOption(String[] args) {
		if(args.length < 0) {
			return;
		}
		if(ALGORITHM.equalsIgnoreCase(args[0])) {
			try {
				String[] genKeyPair = genKeyPair();
				System.out.println("privateKey="+ genKeyPair[0]);
				System.out.println("publicKey="+ genKeyPair[1]);
				writePublicKey(genKeyPair[1]);
				writeEncryptFile(genKeyPair[0]);
				return;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(args.length > 4) {
			if(ENCRYPE.equalsIgnoreCase(args[0])) {
				String dataStr = args[1];
				String keyType = args[2];
				String privateKeyStr = args[3];
				if(PRIVATE.equalsIgnoreCase(keyType)) {
					try {
						RSAPrivateKey rsaPrivateKey = getRSAPrivateKey(privateKeyStr);
						String encodeStr = new String(encryptByRSAPrivateKey(rsaPrivateKey, dataStr.getBytes("UTF-8")));
						System.out.println("privatekey-encrypt="+encodeStr);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else if(PUBLIC.equalsIgnoreCase(keyType)) {
					try {
						RSAPublicKey rsaPublicKey = getRSAPublicKey(privateKeyStr);
						String encodeStr = new String(encryptByRSAPublicKey(rsaPublicKey, dataStr.getBytes("UTF-8")));
						System.out.println("publickey-encrypt=" + encodeStr);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}else if(DECRYPT.equalsIgnoreCase(args[0])){
				String dataStr = args[1];
				String keyType = args[2];
				String privateKeyStr = args[3];
				if(PRIVATE.equalsIgnoreCase(keyType)) {
					try {
						RSAPrivateKey rsaPrivateKey = getRSAPrivateKey(privateKeyStr);
						String encodeStr = new String(decryptByRSAPrivateKey(rsaPrivateKey, dataStr));
						System.out.println("privatekey-decrypt="+encodeStr);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else if(PUBLIC.equalsIgnoreCase(keyType)) {
					try {
						RSAPublicKey rsaPublicKey = getRSAPublicKey(privateKeyStr);
						String encodeStr = new String(decryptByRSAPublicKey(rsaPublicKey, dataStr));
						System.out.println("publickey-decrypt=" + encodeStr);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		
	}
	
	private static void writePublicKey(String publicKey) {
		Properties preProperties = new Properties();
		URL url = EncryptUtil.class.getClassLoader().getResource("application.properties");
		String file = url.getFile();
		try {
			FileInputStream fis = new FileInputStream(file);
			preProperties.load(fis);
			preProperties.setProperty(PUBLICKEY, publicKey);
			FileOutputStream fos = new FileOutputStream(file);
			Iterator<Entry<Object, Object>> iterator = preProperties.entrySet().iterator();
			while(iterator.hasNext()) {
				Entry<Object, Object> next = iterator.next();
				fos.write((next.getKey()+"="+next.getValue()).getBytes());
				fos.write(("\r\n").getBytes());
			}
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void writeEncryptFile(String privateKey) {
		try {
			RSAPrivateKey rsaPrivateKey = getRSAPrivateKey(privateKey);
			writeJdbcFile(rsaPrivateKey);
			writeRedisFile(rsaPrivateKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private static void writeRedisFile(RSAPrivateKey rsaPrivateKey) {
		Properties preProperties = new Properties();
		URL url = EncryptUtil.class.getClassLoader().getResource("config/jdbc.properties");
		String file = url.getFile();
		try {
			FileInputStream fis = new FileInputStream(file);
			preProperties.load(fis);
			FileOutputStream fos = new FileOutputStream(file);
			Iterator<Entry<Object, Object>> iterator = preProperties.entrySet().iterator();
			while(iterator.hasNext()) {
				Entry<Object, Object> next = iterator.next();
				fos.write((next.getKey()+"="+base64Encode(encryptByRSAPrivateKey(rsaPrivateKey, next.getValue().toString().getBytes()))).getBytes());
				fos.write(("\r\n").getBytes());
			}
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private static void writeJdbcFile(RSAPrivateKey rsaPrivateKey) {
		Properties preProperties = new Properties();
		URL url = EncryptUtil.class.getClassLoader().getResource("config/redis.properties");
		String file = url.getFile();
		try {
			FileInputStream fis = new FileInputStream(file);
			preProperties.load(fis);
			FileOutputStream fos = new FileOutputStream(file);
			Iterator<Entry<Object, Object>> iterator = preProperties.entrySet().iterator();
			while(iterator.hasNext()) {
				Entry<Object, Object> next = iterator.next();
				if("address".equals(next.getKey())) {
					fos.write((next.getKey()+"="+base64Encode(encryptByRSAPrivateKey(rsaPrivateKey, next.getValue().toString().getBytes()))).getBytes());
				}else{
					fos.write((next.getKey()+"="+next.getValue()).getBytes());
				}
				fos.write(("\r\n").getBytes());
			}
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static String base64Encode(byte[] bys) {
		return new String(Base64.getEncoder().encode(bys),StandardCharsets.UTF_8);
	}
	
	public static byte[] base64Decode(String bustr) {
		return Base64.getDecoder().decode(bustr.getBytes(StandardCharsets.UTF_8));
	}
	
	public static String base64DecodeToString(String bustr) {
		return new String(base64Decode(bustr),StandardCharsets.UTF_8);
	}
	
	public static byte[] base64Decode(byte[] bys) {
		return Base64.getDecoder().decode(bys);
	}
	
	public static String base64DecodeToString(byte[] bys) {
		return new String(base64Decode(bys),StandardCharsets.UTF_8);
	}
}
