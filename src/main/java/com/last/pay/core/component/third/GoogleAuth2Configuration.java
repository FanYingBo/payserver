package com.last.pay.core.component.third;

import java.util.List;

public class GoogleAuth2Configuration {
	
	private String jsonFile;
	
	private List<String> scopes;
	
	private String verifyUrl;
	
	private String appName;
	
	public String getJsonFile() {
		return jsonFile;
	}

	public GoogleAuth2Configuration setJsonFile(String jsonFile) {
		this.jsonFile = jsonFile;
		return this;
	}

	public List<String> getScopes() {
		return scopes;
	}

	public GoogleAuth2Configuration setScopes(List<String> scopes) {
		this.scopes = scopes;
		return this;
	}

	public String getVerifyUrl() {
		return verifyUrl;
	}

	public GoogleAuth2Configuration setVerifyUrl(String verifyUrl) {
		this.verifyUrl = verifyUrl;
		return this;
	}

	public String getAppName() {
		return appName;
	}

	public GoogleAuth2Configuration setAppName(String appName) {
		this.appName = appName;
		return this;
	}
	
	
}
