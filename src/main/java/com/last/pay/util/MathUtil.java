package com.last.pay.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathUtil {
	/**
	 * 	保留4位小数
	 * @param divisor 除数
	 * @param dividend 被除数
	 * @return
	 */
	public static double divide(double divisor,double dividend) {
		BigDecimal bigDecimalDiv = new BigDecimal(divisor);
		BigDecimal bigDecimalDived = new BigDecimal(dividend);
		return bigDecimalDived.divide(bigDecimalDiv,4,RoundingMode.HALF_UP).doubleValue();
	}
	
	/**
	 * 	保留4位小数
	 * @param divisor 除数
	 * @param dividend 被除数
	 * @return
	 */
	public static float divide(float divisor,float dividend) {
		BigDecimal bigDecimalDiv = new BigDecimal(divisor);
		BigDecimal bigDecimalDived = new BigDecimal(dividend);
		return bigDecimalDived.divide(bigDecimalDiv,4,RoundingMode.HALF_UP).floatValue();
	}
	
	/**
	 * 	加法 保留4位小数
	 * @param addend1
	 * @param addend2
	 * @return
	 */
	public static float add(float addend1,float addend2) {
		BigDecimal bigDecimalA = new BigDecimal(addend1);
		BigDecimal bigDecimalB = new BigDecimal(addend2);
		return bigDecimalA.add(bigDecimalB).setScale(4, RoundingMode.HALF_UP).floatValue();
	}

	/**
	 * 	加法 保留4位小数
	 * @param addend1
	 * @param addend2
	 * @return
	 */
	public static float add(float addend1,double addend2) {
		BigDecimal bigDecimalA = new BigDecimal(addend1);
		BigDecimal bigDecimalB = new BigDecimal(addend2);
		return bigDecimalA.add(bigDecimalB).setScale(4, RoundingMode.HALF_UP).floatValue();
	}


	/**
	 * 	加法 保留4位小数
	 * @param addend1
	 * @param addend2
	 * @return
	 */
	public static double add(double addend1,double addend2) {
		BigDecimal bigDecimalA = new BigDecimal(addend1);
		BigDecimal bigDecimalB = new BigDecimal(addend2);
		return bigDecimalA.add(bigDecimalB).setScale(4, RoundingMode.HALF_UP).floatValue();
	}
	
	/**
	 * 	减法 保留4位小数
	 * @param subtrahend 减数
	 * @param minuend 被减数
	 * @return
	 */
	public static float subtract(float subtrahend,float minuend) {
		BigDecimal bigDecimalA = new BigDecimal(subtrahend);
		BigDecimal bigDecimalB = new BigDecimal(minuend);
		return bigDecimalB.subtract(bigDecimalA).setScale(4, RoundingMode.HALF_UP).floatValue();
	}
	
	/**
	 * 	乘法
	 * @param mult1
	 * @param mult2
	 * @return
	 */
	public static float multiply(float mult1,float mult2) {
		BigDecimal bigDecimalA = new BigDecimal(mult1);
		BigDecimal bigDecimalB = new BigDecimal(mult2);
		return bigDecimalA.multiply(bigDecimalB).setScale(4, RoundingMode.HALF_UP).floatValue();
	}
	
	/**
	 * 	乘法
	 * @param mult1
	 * @param mult2
	 * @return
	 */
	public static int multiply(float mult1,int mult2) {
		BigDecimal bigDecimalA = new BigDecimal(mult1);
		BigDecimal bigDecimalB = new BigDecimal(mult2);
		return bigDecimalA.multiply(bigDecimalB).setScale(4, RoundingMode.HALF_UP).intValue();
	}
	
	
	/**
	 * 	乘法
	 * @param mult1
	 * @param mult2
	 * @return
	 */
	public static float multiply(double mult1,double mult2) {
		BigDecimal bigDecimalA = new BigDecimal(mult1);
		BigDecimal bigDecimalB = new BigDecimal(mult2);
		return bigDecimalA.multiply(bigDecimalB).setScale(4, RoundingMode.HALF_UP).floatValue();
	}
}
