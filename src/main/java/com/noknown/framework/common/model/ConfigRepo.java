package com.noknown.framework.common.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author guodong
 */
public class ConfigRepo implements Cloneable{

	/**配置库的名称*/
	private String configName;

	/**配置的库描述信息*/
	private String configDesc;

	/**配置的库ID*/
	private String configId;

	/**配置的库配置信息
	 * Map 的 key为配置的与domain信息
	 * Map 的value为该domain对应的配置
	 * */
	private Map<String, Properties> configs;

	/**顺序存放配置库的key信息
	 * Map 的 key为配置的与domain信息
	 * Map 的value为该domain对应的配置库的key信息
	 * */
	private Map<String, Set<String>> keySets;

	public String getConfigName() {
		return configName;
	}

	public void setConfigName(String configName) {
		this.configName = configName;
	}

	public String getConfigDesc() {
		return configDesc;
	}

	public void setConfigDesc(String configDesc) {
		this.configDesc = configDesc;
	}

	public String getConfigId() {
		return configId;
	}

	public void setConfigId(String configId) {
		this.configId = configId;
	}

	public Map<String, Properties> getConfigs() {
		if (configs == null) {
			configs = new HashMap<>(10);
		}
		return configs;
	}

	public void setConfigs(Map<String, Properties> configs) {
		this.configs = configs;
	}

	public Map<String, Set<String>> getKeySets() {
		if (keySets == null) {
			keySets = new HashMap<>(10);
		}
		return keySets;
	}

	public void setKeySets(Map<String, Set<String>> keySets) {
		this.keySets = keySets;
	}

	@Override
	public ConfigRepo clone(){
		ConfigRepo o = null;
		try{
			o = (ConfigRepo)super.clone();
		}catch(CloneNotSupportedException e){
			e.printStackTrace();
		}
		return o;
	}

	@Override
	public String toString() {
		return "ConfigRepo [configName=" + configName + ", configDesc="
				+ configDesc + ", configId=" + configId + ", configs="
				+ configs + "]";
	}


}
