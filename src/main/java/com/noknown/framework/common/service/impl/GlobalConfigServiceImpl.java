package com.noknown.framework.common.service.impl;

import com.noknown.framework.common.dao.GlobalConfigDao;
import com.noknown.framework.common.model.ConfigRepo;
import com.noknown.framework.common.model.GlobalConfig;
import com.noknown.framework.common.service.GlobalConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

@Service
public class GlobalConfigServiceImpl implements GlobalConfigService {

	@Autowired
	private GlobalConfigDao gcDao;

	private static GlobalConfig globalConfig;
	
	@Override
	public void fetchConfig(String configType, String domain, String key, boolean isDelete) {
		Map<String, ConfigRepo> crs = globalConfig.getConfigRepos();
		ConfigRepo cr = crs.get(configType);
		Map<String, Properties> configs = cr.getConfigs();
		Map<String, Set<String>> keySets = cr.getKeySets();
		String val = gcDao.getConfig(configType, domain, key);
		Properties p = configs.get(domain);
		p.remove(key);
		if (!isDelete) {
			p.put(key, val);
		}
		configs.remove(domain);
		configs.put(domain, p);
		keySets.remove(domain);
		keySets.put(domain, p.stringPropertyNames());
		cr.setConfigs(configs);
		cr.setKeySets(keySets);
		crs.remove(configType);
		crs.put(configType, cr);
		globalConfig.setConfigRepos(crs);
	}

	@Override
	public String getConfig(String configType, String domain, String key, boolean isFetch) {
		String value = null;
		if (globalConfig == null || isFetch) {
			getGlobalConfig(true);
		}
		value = globalConfig.getConfig(configType, domain, key);
		if (value != null) {
			value = new String(value);
		}
		return value;
	}

	@Override
	public Properties getProperties(String configType, String domain, boolean isFetch) {
		Properties p = null;
		if (globalConfig == null || isFetch) {
			getGlobalConfig(true);
		}
		p = globalConfig.getProperties(configType, domain);
		if (p != null) {
			p = (Properties) p.clone();
		}
		return p;
	}

	@Override
	public ConfigRepo getConfigRepo(String configType, boolean isFetch) {
		ConfigRepo cr = null;
		if (globalConfig == null || isFetch) {
			getGlobalConfig(true);
		}
		cr = globalConfig.getConfigRepo(configType);
		if (cr != null) {
			cr = cr.clone();
		}
		return cr;
	}

	@Override
	public GlobalConfig getGlobalConfig(boolean fetch) {
		if (globalConfig == null || fetch) {
			globalConfig = gcDao.getGlobalConfig();
		}
		return globalConfig;
	}

	@Override
	public void updateGlobalConfig(GlobalConfig gc) {
		gcDao.updateGlobalConfig(gc);
		getGlobalConfig(true);
	}

	@Override
	public void updateConfigRepo(String configType, ConfigRepo cr) {
		gcDao.updateConfigRepo(configType, cr);
		getGlobalConfig(true);
	}

	@Override
	public void updateProperties(String cofnigType, String domain,
			Properties configs) {
		gcDao.updateProperties(cofnigType, domain, configs);
		getGlobalConfig(true);

	}
	

	@Override
	public void updateValue(String cofnigType, String domain, String key,
			String value) {
		gcDao.updateValue(cofnigType, domain, key, value);
		fetchConfig(cofnigType, domain, key, false);
	}
	
	@Override
	public void deleteValue(String cofnigType, String domain, String key) {
		gcDao.deleteValue(cofnigType, domain, key);
		fetchConfig(cofnigType, domain, key, true);
	}

	@Override
	public void deleteConfigRepo(String configType) {
		gcDao.removeConfigRepo(configType);
		getGlobalConfig(true);

	}

	@Override
	public void deleteProperties(String configType, String domain) {
		gcDao.removeProperties(configType, domain);
		getGlobalConfig(true);
	}
	
	@Override
	public Properties getNameConfig() {
		Properties ncp;
		
		ncp = gcDao.getNameConfig();
		
		return ncp;
	}
	
	@Override
	public void updateNameConfig(String key, String value) {
		gcDao.updateNameConfig(key, value);
	}
	
	@Override
	public void deleteNameConfig(String key) {
		gcDao.deleteNameConfig(key);
	}

	public GlobalConfigDao getGcDao() {
		return gcDao;
	}

	public void setGcDao(GlobalConfigDao gcDao) {
		this.gcDao = gcDao;
	}
}
