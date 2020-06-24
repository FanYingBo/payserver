package com.last.pay.base.common.constants;

import java.util.HashMap;
import java.util.Map;
/**
 *  VIP等级道具
 * @author Administrator
 *
 */
public class PropVipConstants {
	/***VIP1炮**/
	public static final int VIP_CANNON_1 = 310;
	public static final int VIP_CANNON_2 = 311;
	public static final int VIP_CANNON_3 = 312;
	public static final int VIP_CANNON_4 = 313;
	public static final int VIP_CANNON_5 = 314;
	public static final int VIP_CANNON_6 = 315;
	public static final int VIP_CANNON_7 = 316;
	public static final int VIP_CANNON_8 = 317;
	/**VIP头像框****/
	public static final int VIP_ICON_1 = 330;
	public static final int VIP_ICON_2 = 331;
	public static final int VIP_ICON_3 = 332;
	public static final int VIP_ICON_4 = 333;
	public static final int VIP_ICON_5 = 334;
	public static final int VIP_ICON_6 = 335;
	public static final int VIP_ICON_7 = 336;
	public static final int VIP_ICON_8 = 337;
	public static Map<Integer, Integer> propsCannonMap = new HashMap<Integer, Integer>();
	public static Map<Integer, Integer> propsIconMap = new HashMap<Integer, Integer>();
	static {
		propsCannonMap.put(1, VIP_CANNON_1);
		propsCannonMap.put(2, VIP_CANNON_2);
		propsCannonMap.put(3, VIP_CANNON_3);
		propsCannonMap.put(4, VIP_CANNON_4);
		propsCannonMap.put(5, VIP_CANNON_5);
		propsCannonMap.put(6, VIP_CANNON_6);
		propsCannonMap.put(7, VIP_CANNON_7);
		propsCannonMap.put(8, VIP_CANNON_8);
		
		propsIconMap.put(1, VIP_ICON_1);
		propsIconMap.put(2, VIP_ICON_2);
		propsIconMap.put(3, VIP_ICON_3);
		propsIconMap.put(4, VIP_ICON_4);
		propsIconMap.put(5, VIP_ICON_5);
		propsIconMap.put(6, VIP_ICON_6);
		propsIconMap.put(7, VIP_ICON_7);
		propsIconMap.put(8, VIP_ICON_8);
	}
}
