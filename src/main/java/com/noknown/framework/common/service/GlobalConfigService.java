package com.noknown.framework.common.service;

import com.noknown.framework.common.model.ConfigRepo;
import com.noknown.framework.common.model.GlobalConfig;

import java.util.Properties;


public interface GlobalConfigService {
	
	/**
	 * 刷新key对应配置
	 * @param configType 配置类型
	 * @param domain domain名
	 * @param key 配置名
	 * @param isDelete 是否删除配置
	 * @return 
	 */
	void fetchConfig(String configType, String domain, String key, boolean isDelete);
	
	/**
	 * 获取key对应配置
	 * @param configType 配置类型
	 * @param domain domain名
	 * @param key 配置名
	 * @return 配置值
	 */
	String getConfig(String configType, String domain, String key, boolean isFetch);
	
	/**
	 * 获取domain对应配置表
	 * @param configType 配置类型
	 * @param domain domain名
	 * @return 配置表
	 */
	Properties getProperties(String configType, String domain, boolean isFetch);
	
	/**
	 * 获取configType对应配置库
	 * @param configType 配置类型
	 * @return 配置库
	 */
	ConfigRepo getConfigRepo(String configType, boolean isFetch);
	
	/**
	 * 获取全局配置对象
	 * @return 全局配置对象ClobalConfig
	 */
	GlobalConfig getGlobalConfig(boolean fetch);
	
	/**
	 * 更新全局配置对象
	 * @param gc GlobalConfig对象
	 * @return
	 */
	void updateGlobalConfig(GlobalConfig gc);
	
	/**
	 * 更新配置类型对应的配置库
	 * @param configType 配置类型
	 * @param cr configType对应的配置库
	 * @return
	 */
	void updateConfigRepo(String configType, ConfigRepo cr);
	
	/**
	 * 更新配置库下的domain对应的配置
	 * @param configType 配置类型
	 * @param domain domain名
	 * @param configs domain对应的配置
	 * @return
	 */
	void updateProperties(String configType, String domain, Properties configs);
	
	/**
	 * 更新配置库下的domain对应的配置
	 * @param configType 配置类型
	 * @param domain domain名
	 * @param key 配置名称
	 * @param value 配置值
	 * @return
	 */
	void updateValue(String configType, String domain, String key, String value);
	
	/**
	 * 删除配置库下的domain对应的配置
	 * @param configType 配置类型
	 * @param domain domain名
	 * @param key 配置名称
	 * @return
	 */
	void deleteValue(String configType, String domain, String key);
	
	/**
	 * 删除configType类型的配置库
	 * @param configType 配置类型
	 * @return
	 */
	void deleteConfigRepo(String configType);
	
	/**
	 * 删除configType类型的配置库下domain对应的配置信息
	 * @param configType 配置类型
	 * @param domain domain名
	 * @return
	 */
	void deleteProperties(String configType, String domain);
	
	/**
	 * 获取全局配置别名
	 * @return 全局配置别名
	 */
	Properties getNameConfig();
	
	/**
	 * 更新配置库下的配置名称对应的别名
	 * @param key 配置名称
	 * @param value 配置别名
	 * @return
	 */
	void updateNameConfig(String key, String value);
	
	/**
	 * 删除配置库下的配置名称对应的别名
	 * @param key 配置名称
	 * @return
	 */
	void deleteNameConfig(String key);

}
