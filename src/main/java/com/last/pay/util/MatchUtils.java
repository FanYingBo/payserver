package com.last.pay.util;

import java.util.Objects;

/**
 * 检查字符串
 * @author Administrator
 *
 */
public final class MatchUtils {
	
	public static final String MATCH_FLOAT_REG = "^\\d+(\\.\\d+)?$";
	/**
	 * 验证数字或浮点数
	 * @param str
	 * @return
	 */
	public static boolean isNumberStr(String str) {
		if(Objects.isNull(str)) {
			return Boolean.FALSE;
		}
		String reg = MATCH_FLOAT_REG;
		return str.matches(reg);
	}
}
