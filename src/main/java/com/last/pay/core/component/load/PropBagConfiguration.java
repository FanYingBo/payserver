package com.last.pay.core.component.load;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.last.pay.core.db.pojo.game.PropBag;
import com.last.pay.core.db.mapper.gamedb.PropBagMapper;
/**
 *  GameDB.PropBag
 * @author Administrator
 *
 */
@Component
public class PropBagConfiguration {

	@Autowired
	public PropBagMapper propBagMapper;
	
	private Map<Integer, List<PropBag>> propBagMap = new HashMap<Integer, List<PropBag>>();
	
	@PostConstruct
	public void loadPropBags() {
		List<PropBag> propBagList = propBagMapper.getAllPropBag();
		
		for (PropBag propBag : propBagList) {
			List<PropBag> propBagProps = propBagMap.get(propBag.getBagID());
			if(propBagProps == null) {
				propBagProps = new ArrayList<>();
				propBagMap.put(propBag.getBagID(), propBagProps);
			}
			propBagProps.add(propBag);
		}
	}

	public Map<Integer, List<PropBag>> getPropBagMap() {
		return propBagMap;
	}
	
}
