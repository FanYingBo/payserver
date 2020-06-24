package com.last.pay.core.db.pojo.game;

import java.util.List;

import com.last.pay.core.db.pojo.PropAmount;

public class MapPoint {
	
	private int position;
	
	private int toll;
	
	private int type;
	
	private String content;
	
	private int dynamicId;
	
	private List<PropAmount> props;

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public int getToll() {
		return toll;
	}

	public void setToll(int toll) {
		this.toll = toll;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getDynamicId() {
		return dynamicId;
	}

	public void setDynamicId(int dynamicId) {
		this.dynamicId = dynamicId;
	}

	public List<PropAmount> getProps() {
		return props;
	}

	public void setProps(List<PropAmount> props) {
		this.props = props;
	}

	

}
