package com.noknown.framework.common.util;

import com.noknown.framework.common.service.GlobalConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;


public class ConfigUtil {

	private static final Logger logger = LoggerFactory.getLogger(ConfigUtil.class);

	public static String loadAppVersion(ServletContext sc, String warProject){
		String version = "unknown";
		String basePath = sc.getRealPath("/");
		String path = basePath + File.separator + "META-INF" + File.separator + 
				"maven" + File.separator + "com.noknown" + File.separator + 
				warProject + File.separator + "pom.properties";
		Properties p = null;
		try {
			p = BaseUtil.loadPropertiesFromRealPath(path);
			version = p.getProperty("version", "unknown");
		} catch (Exception e) {
			logger.warn("读取版本文件错误：" + e.getLocalizedMessage());
		}
		return version;
	}

	public static boolean loadConfigToContext(String config, Properties properties, ServletContext sc, Map<Object, Object> allConfigs) {
		logger.info("加载配置文件{}到运行环境！", config);
		if (properties != null) {
			Iterator<Entry<Object, Object>> keyIt = properties.entrySet().iterator();
			while (keyIt.hasNext()) {
				Entry<Object, Object> entry = keyIt.next();
				Object key = entry.getKey();
				Object value = entry.getValue();
				sc.setAttribute((String) key, value);
				if (allConfigs != null)
					allConfigs.put(key, value);
				logger.debug("Key --> {} : value --> {}", key, value);
			}
			if (allConfigs != null)
				allConfigs.put(config, properties);
		} else {
			logger.error("配置[" + config + "]不存在！");
			return false;
		}
		logger.info("加载成功！");
		
		return true;
	}
	
	public static void serverInitialized(String appid, String warProject, GlobalConfigService gcs, ServletContext sc) {
		gcs.getGlobalConfig(true);
		Properties baseConfig = gcs.getProperties("baseConfig", appid, true);
		Map<Object, Object> allConfigs = new HashMap<>();
		if (baseConfig != null) {
			loadConfigToContext("baseConfig", baseConfig, sc, allConfigs);
			String loadConfigs = baseConfig.getProperty("load_configs");
			String[] loadConfigList = null;
			if (StringUtil.isNotBlank(loadConfigs)) {
				loadConfigList = loadConfigs.split(",");
			}
			
			if (loadConfigList != null) {
				for (String config : loadConfigList) {
					Properties properties = gcs.getProperties(config, appid, false);
					loadConfigToContext(config, properties, sc, allConfigs);
				}
			}
		}
		String version = ConfigUtil.loadAppVersion(sc, warProject);
		sc.setAttribute("app_version", version);
		sc.setAttribute("allAppConfig", allConfigs);
	}
}
