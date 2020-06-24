package com.last.pay.util;

import java.util.UUID;

public class CodaPayUtil {
	
	public static String generateOrderId() {
		UUID randomUUID = UUID.randomUUID();
		String uuid = randomUUID.toString();
		String randomStr = uuid.replaceAll("-", "");
		return "CODATOPFISH" + randomStr.toUpperCase();
	}
}
