package com.last.pay.core.component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.last.pay.base.common.constants.Constants.PayTypeConstants;
import com.last.pay.cache.ConfigManager;
/**
 * 活动参数配置
 * @author Administrator
 *
 */
@Component
public class ActivityTimeComponent {

	private static Log logger = LogFactory.getLog(ActivityTimeComponent.class);
	
	public static final int NEW_YEAR_ACTIVITY = 7;
	public static final int DOUBLE_PAY_ACTIVITY = 8;
	
	private Map<Integer,Date[]> activeTimeMap = new HashMap<>();
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	
	@Autowired
	private ConfigManager configManager;
	
	@PostConstruct
	public void initActivityParam() {
		String paramValue = configManager.getParamValue("activity_time");
		if(StringUtils.isNotBlank(paramValue)) {
			try {
				JSONObject jsonObject = JSONObject.parseObject(paramValue);
				Set<Entry<String, Object>> entrySet = jsonObject.entrySet();
				Iterator<Entry<String, Object>> iterator = entrySet.iterator();
				while(iterator.hasNext()) {
					Entry<String, Object> entry = iterator.next();
					String key = entry.getKey();
					if(StringUtils.isNumeric(key)) {
						String dateArrStr = entry.getValue().toString();
						JSONArray parseArray = JSONArray.parseArray(dateArrStr);
						Date[] date = new Date[parseArray.size()];
						for(int i = 0;i < parseArray.size();i++) {
							date[i] = sdf.parse(parseArray.get(i).toString());
						}
						activeTimeMap.put(Integer.parseInt(key), date);
					}
				}
			} catch (Exception e) {
				logger.warn("初始化活动参数activity_time失败,"+e.getMessage());
			}
		}
	}
	
	public Date[] getActivityDates(int type) {
		return activeTimeMap.get(type);
	}
	
	public boolean checkActivityTime(int type) {
		Date[] activityDates = getActivityDates(type);
		if(Objects.isNull(activityDates)) {
			return Boolean.FALSE;
		}
		Date time = Calendar.getInstance().getTime();
		return time.after(activityDates[0]) && time.before(activityDates[1]);
	}
	
	public boolean checkNewYearActivity() {
		return checkActivityTime(NEW_YEAR_ACTIVITY);
	}
	
	public boolean checkPayDoubleActivity(int payType) {
		if(payType != PayTypeConstants.Vietnam_CARD) {
			return Boolean.FALSE;
		}
		return checkActivityTime(DOUBLE_PAY_ACTIVITY);
	}
}
