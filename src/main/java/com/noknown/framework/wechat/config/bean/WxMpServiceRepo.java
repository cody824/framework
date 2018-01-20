package com.noknown.framework.wechat.config.bean;

import com.noknown.framework.cache.service.CacheService;
import com.noknown.framework.common.config.AppInfo;
import com.noknown.framework.common.model.ConfigRepo;
import com.noknown.framework.common.service.GlobalConfigService;
import com.noknown.framework.wechat.config.CacheStorage;
import com.noknown.framework.wechat.config.SureWxMpService;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Component
public class WxMpServiceRepo {

    @Autowired
    private GlobalConfigService gcService;

    @Autowired
    private CacheService cacheService;
    
    @Autowired WxMpService defaultWxMpImplServie;

    @Autowired
    AppInfo baseAppIdUtil;
    
    Map<String, WxMpService> wmMap = new HashMap<>();
	
    @PostConstruct
    public void initConfig(){
    	ConfigRepo cr = gcService.getConfigRepo("wxMpService", true);
    	if (cr != null && cr.getConfigs() != null) {
    		Map<String, Properties> configs = cr.getConfigs();
    		
    		Properties wechatConfig = gcService.getProperties("wechat", baseAppIdUtil.getAppId(), false);
    		String defaultAppId = wechatConfig.getProperty("wechat_appid");
    		
    		for (String appId : configs.keySet()) {
    			CacheStorage cs = new CacheStorage(cacheService);
    			
    			if (defaultAppId != null && defaultAppId.trim().equals(appId.trim())) {
    				wmMap.put(appId, defaultWxMpImplServie);
    			} else {
    				cs.initConfig(configs.get(appId));
        			SureWxMpService swms = new SureWxMpService(cs);
        			wmMap.put(appId, swms);
    			}
    		}
    	}
    }
    
    public WxMpService getService(String appId) {
    	return wmMap.get(appId);
    }
    
}
