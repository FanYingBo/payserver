package com.last.pay.core.component.load;

import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.last.pay.core.db.pojo.game.LevelConfig;
import com.last.pay.core.db.mapper.gamedb.LevelConfigMapper;

@Configuration
public class LevelConfiguration {

	private TreeMap<Long,Integer> levelMap = new TreeMap<>();
	
	@Autowired
	private LevelConfigMapper levelConfigMapper;
	
	@PostConstruct
	public void initLevelConfig() {
		List<LevelConfig> levelConfigs = levelConfigMapper.getLevelConfigs();
		levelConfigs.stream().forEach(levelConfig->{levelMap.put(levelConfig.getExperience(), levelConfig.getLevel());});
	}
	
	public Integer getLevel(long experience) {
		Entry<Long, Integer> floorEntry = levelMap.floorEntry(experience);
		if(floorEntry == null) {
			floorEntry = levelMap.firstEntry();
		}
		return floorEntry.getValue();
	}
}
