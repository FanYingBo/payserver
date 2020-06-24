package com.last.pay.core.dto.ios.response;

import java.util.List;

public class IOSReceiptParams {
	
	private String application_version;
	
	private String bundle_id;
	
	private List<IOSInAppParams> in_app;
	
	public List<IOSInAppParams> getIn_app() {
		return in_app;
	}

	public void setIn_app(List<IOSInAppParams> in_app) {
		this.in_app = in_app;
	}
	public String getApplication_version() {
		return application_version;
	}

	public void setApplication_version(String application_version) {
		this.application_version = application_version;
	}

	public String getBundle_id() {
		return bundle_id;
	}

	public void setBundle_id(String bundle_id) {
		this.bundle_id = bundle_id;
	}
	
	
}
