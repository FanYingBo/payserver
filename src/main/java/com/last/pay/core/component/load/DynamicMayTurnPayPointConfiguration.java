package com.last.pay.core.component.load;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.alibaba.fastjson.JSONObject;
import com.last.pay.base.common.constants.PayMapConstants;
import com.last.pay.core.db.pojo.PropAmount;
import com.last.pay.core.db.pojo.game.MapPoint;
import com.last.pay.core.db.pojo.game.Turntable;
import com.last.pay.core.db.pojo.web.DynamicPayPoint;
import com.last.pay.core.db.mapper.gamedb.MapPointMapper;
import com.last.pay.core.db.mapper.gamedb.TurntableMapper;
import com.last.pay.core.db.mapper.webdb.DynamicPayPointMapper;
/**
 * 
 * @author Administrator
 * @date 2020年5月25日
 *
 */
@Configuration
public class DynamicMayTurnPayPointConfiguration {
	
	@Autowired
	private DynamicPayPointMapper dynamicPayPointMapper;
	@Autowired
	private TurntableMapper turntableMapper;
	@Autowired
	private MapPointMapper mapPointMapper;
	 
	private Map<Integer,DynamicPayPoint> dynamicPayPoints = new HashMap<>();
	
	private Map<Integer,TreeMap<Double,Turntable>> turntablesRatio = new HashMap<>();
	
	private TreeMap<Integer,TreeMap<Integer,MapPoint>> mapPoints = new TreeMap<>();
	
	private MapPoint finalMapPoint;
	
	@PostConstruct
	public void loadConfig() {
		
		
		List<DynamicPayPoint> allDynamicPayPoints = dynamicPayPointMapper.getAllDynamicPayPoint();
		allDynamicPayPoints.stream().forEach(allDynamicPayPoint -> dynamicPayPoints.put(allDynamicPayPoint.getId(), allDynamicPayPoint));
		
		
		List<Turntable> allTurnTables = turntableMapper.getAllTurnTables();
		Map<Integer,List<Turntable>> tmpRatio = new HashMap<>();
		for(int i = 0;i < allTurnTables.size(); i ++) {
			Turntable turntable = allTurnTables.get(i);
			int type = turntable.getType();
			List<Turntable> turntableList = tmpRatio.get(type);
			if(turntableList == null) {
				turntableList = new ArrayList<Turntable>();
				tmpRatio.put(type,turntableList);
			} 
			turntableList.add(turntable);
		}
		
		for(Entry<Integer, List<Turntable>> ratioEntry : tmpRatio.entrySet()) {
			Integer type = ratioEntry.getKey();
			List<Turntable> turntables = ratioEntry.getValue();
			TreeMap<Double, Turntable> turntableMapCache = turntablesRatio.get(type);
			double ratio = 0;
			for(Turntable  turntable: turntables) {
				Float eachRatio = turntable.getRadio();
				ratio = new BigDecimal(ratio).add(new BigDecimal(eachRatio)).setScale(4, RoundingMode.HALF_UP).doubleValue();
				if(turntableMapCache != null) {
					turntableMapCache.put(ratio, turntable);
				}else {
					turntableMapCache = new TreeMap<>();
					turntableMapCache.put(ratio, turntable);
					turntablesRatio.put(type, turntableMapCache);
				}
			}
		}
		
		List<MapPoint> allMapPoints = mapPointMapper.getAllMapPoints();
		allMapPoints.stream().forEach(mapPoint ->{
			int toll = mapPoint.getToll();
			if(toll == PayMapConstants.FINAL_MAP_TOLL) {
				finalMapPoint = mapPoint;
			}else {
				TreeMap<Integer, MapPoint> positionMap = mapPoints.get(toll);
				if(positionMap == null) {
					positionMap = new TreeMap<>();
					mapPoints.put(toll, positionMap);
				}
				positionMap.put(mapPoint.getPosition(), mapPoint);
			}
		});
		
		afterInitConfig();
		
	}
	
	private void afterInitConfig() {
		if(mapPoints.isEmpty()) {
			throw new NullPointerException("地图位置信息未配置");
		}
		if(dynamicPayPoints.isEmpty()) {
			throw new NullPointerException("动态计费点信息未配置");
		}
		if(finalMapPoint == null) {
			throw new NullPointerException("地图计费点的最终关卡奖励未配置");
		}
		String content = finalMapPoint.getContent();
		List<PropAmount> props = JSONObject.parseArray(content, PropAmount.class);
		finalMapPoint.setProps(props);
	}

	/**
	 * 获取动态的计费点
	 * @param dynamicId
	 * @return
	 */
	public DynamicPayPoint getDynamicPayPointById(int dynamicId) {
		return dynamicPayPoints.get(dynamicId);
	}
	/**
	 * 获取转盘
	 * @param type
	 * @return
	 */
	public TreeMap<Double,Turntable> getTurntableByType(int type){
		return turntablesRatio.get(type);
	}
	
	/**
	 * 获取下个地图位置
	 * @param toll 关卡
	 * @param position 位置
	 * @return
	 */
	public MapPoint getNextMapPoint(int toll,int position) {
		TreeMap<Integer,MapPoint> treeMap = mapPoints.get(toll);
		if(treeMap != null) {
			Entry<Integer, MapPoint> higherEntry = treeMap.higherEntry(position);
			if(higherEntry != null) {
				return higherEntry.getValue();
			} 
		}
		return null;
	}
	
	/**
	 * 获取下个地图位置
	 * @param toll 关卡
	 * @param position 位置
	 * @return
	 */
	public MapPoint getNextTollMapPoint(int toll,int position) {
		MapPoint nextMapPoint = getNextMapPoint(toll, position);
		if(nextMapPoint == null) {
			Entry<Integer, TreeMap<Integer, MapPoint>> tollEntry = mapPoints.higherEntry(toll);
			if(tollEntry != null) {
				TreeMap<Integer, MapPoint> treeMap = tollEntry.getValue();
				Entry<Integer, MapPoint> firstEntry = treeMap.firstEntry();
				return firstEntry.getValue();
			}
		}
		return null;
	}
	/**
	 *  地图起始位置
	 * @return
	 */
	public MapPoint getFirstMapPoint() {
		return mapPoints.firstEntry().getValue().firstEntry().getValue();
	}
	/**
	 * 奖励关卡
	 * @return
	 */
	public MapPoint getFinalRewardMapPoint() {
		return finalMapPoint;
	}
}
