package com.noknown.framework.common.dao.impl;

import com.noknown.framework.common.dao.GlobalConfigDao;
import com.noknown.framework.common.exception.UtilException;
import com.noknown.framework.common.model.ConfigRepo;
import com.noknown.framework.common.model.GlobalConfig;
import com.noknown.framework.common.util.BaseUtil;
import com.noknown.framework.common.util.OrderProperties;
import com.noknown.framework.common.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Map;
import java.util.Properties;

/**
 * @author guodong
 */
@Component
public class GlobalConfigImplDao implements GlobalConfigDao {
	
	private static final Logger logger = LoggerFactory.getLogger(GlobalConfigImplDao.class);

	private static final String DESC_SUFFIX = ".desc";
	private static final String NAME_CONFIG_FILE = "nameConfig";

	@Value("${framework.globalConfig.basePath:/var/noknown/properties/}")
	private String basePath;

	@Override
	public GlobalConfig getGlobalConfig() {
		GlobalConfig gc = new GlobalConfig();
		String path = getBasePath();
		File baseDir = new File(path);
		boolean baseOk;

		baseOk = baseDir.exists() || baseDir.mkdir();

		baseOk = baseOk && baseDir.isDirectory();

		if (baseOk) {
			File[] files = baseDir.listFiles();
			if (files != null) {
				for (File dir : files) {
					ConfigRepo cr = getConfigRepoFromDir(dir);
					if (cr != null) {
						gc.getSupportConfigTypes().add(cr.getConfigId());
						gc.getConfigRepos().put(cr.getConfigId(), cr);
					}
				}
			}
		}
		return gc;
	}

	@Override
	public Properties getProperties(String configType, String domain) {
		String path = getBasePath() + File.separator + configType
				+ File.separator + domain;
		Properties p = null;
		try {
			p = BaseUtil.loadPropertiesFromRealPath(path, false);
		} catch (UtilException e) {
			logger.warn(e.getLocalizedMessage());
		}
		return p;
	}

	@Override
	public String getConfig(String configType, String domain, String key) {
		String value = null;
		Properties p = getProperties(configType, domain);
		if (p != null && key != null) {
			value = p.getProperty(key);
		}
		return value;
	}

	private ConfigRepo getConfigRepoFromDir(File dir) {
		boolean dirOk;
		File descFile;
		File[] configFiles;
		Properties crp = null;
		ConfigRepo cr;

		dirOk = dir.exists() && dir.isDirectory();

		if (!dirOk) {
			return null;
		}
		descFile = new File(dir.getPath() + File.separator + dir.getName()
				+ DESC_SUFFIX);

		dirOk = descFile.exists() && descFile.isFile();

		if (dirOk) {
			try {
				crp = BaseUtil.loadPropertiesFromRealPath(descFile.getPath());
			} catch (UtilException e) {
				return null;
			}
		}

		cr = new ConfigRepo();
		cr.setConfigId(dir.getName());
		if (crp != null) {
			cr.setConfigName(crp.getProperty("configName", dir.getName()));
			cr.setConfigDesc(crp.getProperty("configDesc", ""));
		}
		configFiles = dir.listFiles();
		if (configFiles != null) {
			for (File file : configFiles) {
				if (file.getName().endsWith(DESC_SUFFIX)) {
					continue;
				}
				try {
					Properties p = BaseUtil.loadPropertiesFromFile(file);
					cr.getConfigs().put(file.getName(), p);
					cr.getKeySets().put(file.getName(), p.stringPropertyNames());
				} catch (UtilException ignored) {
				}
			}
		}
		return cr;
	}

	@Override
	public void updateGlobalConfig(GlobalConfig gc) {
		Map<String, ConfigRepo> repoMap = gc.getConfigRepos();
		for (String key : repoMap.keySet()) {
			updateConfigRepo(key, repoMap.get(key));
		}
	}

	@Override
	public void updateConfigRepo(String configType, ConfigRepo cr) {
		String dirPath = getBasePath() + File.separator + configType;
		File file = new File(dirPath);
		if (!file.exists()) {
			boolean ret = file.mkdirs();
			if (!ret) {
				logger.warn("配置库目录生成失败");
			}
		}

		Properties repoProps = new OrderProperties();
		if (StringUtil.isNotBlank(cr.getConfigDesc())) {
			repoProps.put("configDesc", cr.getConfigDesc());
		}
		if (StringUtil.isNotBlank(cr.getConfigDesc())) {
			repoProps.put("configName", cr.getConfigName());
		}

		if (!repoProps.isEmpty()) {
			String path = getBasePath() + File.separator + configType
					+ File.separator + configType + DESC_SUFFIX;
			try {
				BaseUtil.savePropertiesToRealPath(repoProps, path);
			} catch (UtilException e) {
				logger.warn(e.getLocalizedMessage());
			}
		}
		Map<String, Properties> configMap = cr.getConfigs();

		for (String key : configMap.keySet()) {
			updateProperties(configType, key, configMap.get(key));
		}
	}

	@Override
	public void updateProperties(String configType, String domain,
			Properties configs) {
		String path = getBasePath() + File.separator + configType
				+ File.separator + domain;
		try {
			BaseUtil.savePropertiesToRealPath(configs, path);
		} catch (UtilException e) {
			logger.warn(e.getLocalizedMessage());
		}
	}

	@Override
	public void updateValue(String cofnigType, String domain, String key,
			String value) {
		String path = getBasePath() + File.separator + cofnigType
				+ File.separator + domain;
		try {
			Properties p = BaseUtil.loadPropertiesFromRealPath(path);
			p.remove(key);
			p.put(key, value);
			BaseUtil.savePropertiesToRealPath(p, path);
		} catch (UtilException e) {
			logger.warn(e.getLocalizedMessage());
		}

	}
	
	@Override
	public void deleteValue(String cofnigType, String domain, String key) {
		String path = getBasePath() + File.separator + cofnigType
				+ File.separator + domain;
		try {
			Properties p = BaseUtil.loadPropertiesFromRealPath(path);
			p.remove(key);
			BaseUtil.savePropertiesToRealPath(p, path);
		} catch (UtilException e) {
			logger.warn(e.getLocalizedMessage());
		}

	}

	@Override
	public void removeConfigRepo(String configType) {
		String path = getBasePath() + File.separator + configType;
		File file = new File(path);
		BaseUtil.deleteDir(file);

	}

	@Override
	public void removeProperties(String configType, String domain) {
		String path = getBasePath() + File.separator + configType
				+ File.separator + domain;
		File file = new File(path);
		BaseUtil.deleteDir(file);
	}
	
	@Override
	public Properties getNameConfig() {
		Properties ncp = null;
		String nameConfigPath = getBasePath() + File.separator + NAME_CONFIG_FILE;
		
		try {
			ncp = BaseUtil.loadPropertiesFromRealPath(nameConfigPath);
		} catch (UtilException e) {
			logger.warn(e.getLocalizedMessage());
		}
		
		return ncp;
	}
	
	@Override
	public void updateNameConfig(String key, String value) {
		Properties ncp;
		String nameConfigPath = getBasePath() + File.separator + NAME_CONFIG_FILE;
		
		try {
			ncp = BaseUtil.loadPropertiesFromRealPath(nameConfigPath);
			ncp.remove(key);
			ncp.put(key, value);
			BaseUtil.savePropertiesToRealPath(ncp, nameConfigPath);
		} catch (UtilException e) {
			logger.warn(e.getLocalizedMessage());
		}
	}
	
	@Override
	public void deleteNameConfig(String key) {
		String nameConfigPath = getBasePath() + File.separator + NAME_CONFIG_FILE;
		
		File file = new File(nameConfigPath);
		if (!file.exists()) {
			return ;
		}
		
		try {
			Properties ncp = BaseUtil.loadPropertiesFromRealPath(nameConfigPath);
			ncp.remove(key);
			BaseUtil.savePropertiesToRealPath(ncp, nameConfigPath);
		} catch (UtilException e) {
			logger.warn(e.getLocalizedMessage());
		}
	}

	public File getBaseDir() {
		String path = getBasePath();
		File baseDir = new File(path);
		boolean baseOk;

		baseOk = baseDir.exists() || baseDir.mkdir();

		baseOk = baseOk && baseDir.isDirectory();
		if (baseOk) {
			return baseDir;
		} else {
			return null;
		}
	}

	public String getBasePath() {
		if (basePath.startsWith("classpath")) {
			basePath = BaseUtil.getClassPath()
					+ basePath.substring(basePath.indexOf(":") + 1);
		}
		return basePath;
	}

}
