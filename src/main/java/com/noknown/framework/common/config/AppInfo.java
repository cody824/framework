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

	/**
	 * @return the appId
	 */
	public String getAppId() {
		return appId;
	}

	/**
	 * @param appId the appId to set
	 */
	public void setAppId(String appId) {
		this.appId = appId;
	}

	/**
	 * @return the warProject
	 */
	public String getWarProject() {
		return warProject;
	}

	/**
	 * @param warProject the warProject to set
	 */
	public void setWarProject(String warProject) {
		this.warProject = warProject;
	}

}
