package com.noknown.framework.common.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class GlobalConfig {
	
	/**支持的配置类型*/
	private Set<String> supportConfigTypes;
	
	/**配置库*/
	private Map<String, ConfigRepo> configRepos;

	/**
	 * 根据配置类型获取配置库
	 * @param configType     配置类型
	 * @return               配置库
	 */
	public ConfigRepo getConfigRepo(String configType) {
		ConfigRepo cr = this.getConfigRepos().get(configType);
		return cr;
	}
	
	/**
	 * 根据domain获取配置表
	 * @param configType     配置类型
	 * @param domain         domain名
	 * @return               配置表
	 */
	public Properties getProperties(String configType, String domain) {
		ConfigRepo cr = this.getConfigRepo(configType);
		if (cr == null)
			return null;
		Properties p = cr.getConfigs().get(domain);
		return p;
	}
	
	/**
	 * 根据key获取配置值
	 * @param configType     配置类型
	 * @param domain         domain名
	 * @param key            配置名
	 * @return               配置值
	 */
	public String getConfig(String configType, String domain, String key) {
		Properties p = this.getProperties(configType, domain);
		if (p == null)
			return null;
		return p.getProperty(key);
	}
	
	
	public Set<String> getSupportConfigTypes() {
		if (supportConfigTypes == null)
			supportConfigTypes = new HashSet<String>();
		return supportConfigTypes;
	}
	
	public void setSupportConfigTypes(Set<String> supportConfigTypes) {
		this.supportConfigTypes = supportConfigTypes;
	}

	public Map<String, ConfigRepo> getConfigRepos() {
		if (configRepos == null)
			configRepos = new HashMap<String, ConfigRepo>();
		return configRepos;
	}

	public void setConfigRepos(Map<String, ConfigRepo> configRepos) {
		this.configRepos = configRepos;
	}

	@Override
	public String toString() {
		return "GlobalConfig [supportConfigTypes=" + supportConfigTypes
				+ ", configRepos=" + configRepos + "]";
	}
	
}
