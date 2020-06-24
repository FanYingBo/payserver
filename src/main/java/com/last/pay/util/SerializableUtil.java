package com.last.pay.util;

import java.util.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 序列化util
 * @author Justin Hu
 *
 */
public class SerializableUtil {
	/**
	 * object to json
	 * @param object
	 * @return
	 */
	public static String objectToJsonStr(Object object) {
		Gson gson = new GsonBuilder() 
				.setDateFormat("yyyyMMddHHmmss").create();
		return gson.toJson(object);
	}
	
	/**
	 * json to object
	 * @param json
	 * @param clazz
	 * @return
	 */
	public static <T> T jsonStrToObject(String json, Class<T> clazz) {
		Gson gson = new GsonBuilder()
				.setDateFormat("yyyyMMddHHmmss").serializeNulls().create();
		return  gson.fromJson(json, clazz);
	}
	
	/**
	 * json 字符串转化为Map
	 * @param json
	 * @return
	 */
	public static Map<String,Object> jsonStrToMap(String json){
		Map<String,Object> dataMap = jsonStrToObject(json, Map.class);
		return dataMap;
	}

	/**
	 * json 转化为list map 集合
	 * @param json
	 * @return
	 */
	public static List<Map<String,Object>> jsonStrToList(String json){
		List<Map<String,Object>> list = jsonStrToObject(json, List.class);
		return list;
	}

}
