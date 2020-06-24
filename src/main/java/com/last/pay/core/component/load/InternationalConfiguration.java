package com.last.pay.core.component.load;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.last.pay.core.db.pojo.game.International;
import com.last.pay.core.db.mapper.gamedb.InternationalMapper;

@Component
public class InternationalConfiguration {

	@Autowired
	private InternationalMapper itInternationalMapper;
	
	private Map<String,International> interMap = new HashMap<String, International>();
	
	@PostConstruct
	public void initCache(){
		List<International> internationalConfig = itInternationalMapper.getAllInternational();
		if(Objects.nonNull(internationalConfig)) {
			for(International international : internationalConfig) {
				interMap.put(international.getKey(), international);
			}
		}			
		
	} 
	
	public International getInternationalByKey(String key) {
		return interMap.get(key);
	}
	
	public synchronized void addInternational(String key,International integrnational) {
		interMap.put(key, integrnational);
	}
}
