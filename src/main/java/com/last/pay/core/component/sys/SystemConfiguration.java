package com.last.pay.core.component.sys;

public class SystemConfiguration {
	
	private int systemEnv;
	
	private int warheadPerson;
	
	private int warheadPublic;
	
	private int reTry;
	
	private int forceSendScore;

	public int getSystemEnv() {
		return systemEnv;
	}

	public SystemConfiguration setSystemEnv(int systemEnv) {
		this.systemEnv = systemEnv;
		return this;
	}

	public int getWarheadPerson() {
		return warheadPerson;
	}

	public SystemConfiguration setWarheadPerson(int warheadPerson) {
		this.warheadPerson = warheadPerson;
		return this;
	}

	public int getWarheadPublic() {
		return warheadPublic;
	}

	public SystemConfiguration setWarheadPublic(int warheadPublic) {
		this.warheadPublic = warheadPublic;
		return this;
	}

	public int getReTry() {
		return reTry;
	}

	public SystemConfiguration setReTry(int reTry) {
		this.reTry = reTry;
		return this;
	}

	public int getForceSendScore() {
		return forceSendScore;
	}

	public SystemConfiguration setForceSendScore(int forceSendScore) {
		this.forceSendScore = forceSendScore;
		return this;
	}
	
	
}
