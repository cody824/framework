package com.noknown.framework.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author guodong
 */
@Component
@ConfigurationProperties(prefix="app")
public class AppInfo {

	private String appId;

	private String warProject;

	private String version;

	public String getAppId() {
		return appId;
	}

	public AppInfo setAppId(String appId) {
		this.appId = appId;
		return this;
	}

	public String getWarProject() {
		return warProject;
	}

	public AppInfo setWarProject(String warProject) {
		this.warProject = warProject;
		return this;
	}

	public String getVersion() {
		return version;
	}

	public AppInfo setVersion(String version) {
		this.version = version;
		return this;
	}
}
