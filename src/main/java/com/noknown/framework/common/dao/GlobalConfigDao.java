package com.noknown.framework.common.dao;

import com.noknown.framework.common.model.ConfigRepo;
import com.noknown.framework.common.model.GlobalConfig;

import java.util.Properties;


public interface GlobalConfigDao {
	
	/**
	 * 获取全局配置对象
	 * @return 全局配置对象ClobalConfig
	 */
	GlobalConfig getGlobalConfig();
	
	/**
	 * 获取domain对应配置表
	 * @param configType 配置类型
	 * @param domain domain名
	 * @return 配置表
	 */
	Properties getProperties(String configType, String domain);
	
	/**
	 * 获取key对应配置
	 * @param configType 配置类型
	 * @param domain domain名
	 * @param key 配置名
	 * @return 配置值
	 */
	String getConfig(String configType, String domain, String key);
	
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
	void removeConfigRepo(String configType);
	
	/**
	 * 删除configType类型的配置库下domain对应的配置信息
	 * @param configType 配置类型
	 * @param domain domain名
	 * @return
	 */
	void removeProperties(String configType, String domain);
	
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
