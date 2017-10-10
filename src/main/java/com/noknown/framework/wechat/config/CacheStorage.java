package com.noknown.framework.wechat.config;

import java.io.File;
import java.util.Date;
import java.util.Properties;

import com.noknown.framework.common.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.noknown.framework.cache.service.CacheService;
import com.noknown.framework.common.util.FileUtil;

import me.chanjar.weixin.common.bean.WxAccessToken;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;

@Component
public class CacheStorage  extends WxMpInMemoryConfigStorage {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private String WX_ACCESS_TOKEN = "WX_ACCESS_TOKEN";
    private String WX_JSAPI_TICKET = "WX_JSAPI_TICKET";

    @Autowired
    private CacheService cacheService;

    private String mediaDownloadTempPath = "/var/";

    public CacheStorage(CacheService cacheService) {
    	this.cacheService = cacheService;
    }

    /**
     * 初始化配置
     * @return true 成功 false 失败
     */
    public void initConfig(Properties wechatConfig){
    	if (wechatConfig != null) {
	        this.setAppId((String) wechatConfig.get("wechat_appid"));
	        this.setSecret((String) wechatConfig.get("wechat_secret"));
	        this.setToken((String) wechatConfig.get("wechat_token"));
	        this.setAesKey((String) wechatConfig.get("wechat_aeskey"));
	        WX_ACCESS_TOKEN = this.getAppId() + "_" + WX_ACCESS_TOKEN;
	        WX_JSAPI_TICKET = this.getAppId() + "_" + WX_JSAPI_TICKET;
	
	        this.expireAccessToken();
	        this.expireJsapiTicket();
	
	        logger.debug("微信Config:{}", this);
	
	    } else {
	        logger.error("微信配置文件读取失败!");
	    }
	
	    String tempPath = (String) wechatConfig.getProperty("mediaDownloadTempPath");
	    if (!StringUtil.isBlank(tempPath)) {
	        mediaDownloadTempPath = tempPath;
	    } else {
	        mediaDownloadTempPath += this.getAppId() + "/wechatTemp/";
	    }
	    FileUtil.isExist(mediaDownloadTempPath, true);
	    this.tmpDirFile = new File(mediaDownloadTempPath);
	    logger.debug(this.getAppId() + "微信多媒体下载临时文件夹：" + mediaDownloadTempPath);
    }

    @Override
    public synchronized void updateAccessToken(WxAccessToken accessToken) {
        updateAccessToken(accessToken.getAccessToken(), accessToken.getExpiresIn());
    }

    @Override
    public synchronized void updateAccessToken(String accessToken, int expiresInSeconds) {
        this.accessToken = accessToken;
        this.expiresTime = System.currentTimeMillis() + (expiresInSeconds - 200) * 1000l;

        cacheService.set(WX_ACCESS_TOKEN, accessToken, new Date(expiresTime));
    }

    @Override
    public void expireAccessToken() {
        this.expiresTime = 0;

        cacheService.delete(WX_ACCESS_TOKEN);
    }

    @Override
    public void setJsapiTicketExpiresTime(long jsapiTicketExpiresTime) {
        this.jsapiTicketExpiresTime = jsapiTicketExpiresTime;

        cacheService.set(WX_JSAPI_TICKET, jsapiTicket, new Date(expiresTime));
    }

    @Override
    public synchronized void updateJsapiTicket(String jsapiTicket, int expiresInSeconds) {
        this.jsapiTicket = jsapiTicket;
        // 预留200秒的时间
        this.jsapiTicketExpiresTime = System.currentTimeMillis() + (expiresInSeconds - 200) * 1000l;

        cacheService.set(WX_JSAPI_TICKET, jsapiTicket, new Date(jsapiTicketExpiresTime));
    }
    @Override
    public void expireJsapiTicket() {
        this.jsapiTicketExpiresTime = 0;

        cacheService.delete(WX_JSAPI_TICKET);
    }

    @Override
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;

        cacheService.set(WX_ACCESS_TOKEN, accessToken, new Date(expiresTime));
    }
    @Override
    public void setExpiresTime(long expiresTime) {
        this.expiresTime = expiresTime;

        cacheService.set(WX_ACCESS_TOKEN, accessToken, new Date(expiresTime));
    }

    @Override
    public File getTmpDirFile() {
        return tmpDirFile;
    }

    @Override
    public String toString() {
        return "WxConfigCacheStorage {" +
                "appId='" + appId + '\'' +
                ", secret='" + secret + '\'' +
                ", token='" + token + '\'' +
                ", partnerId='" + partnerId + '\'' +
                ", partnerKey='" + partnerKey + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", aesKey='" + aesKey + '\'' +
                ", expiresTime=" + expiresTime +
                ", http_proxy_host='" + http_proxy_host + '\'' +
                ", http_proxy_port=" + http_proxy_port +
                ", http_proxy_username='" + http_proxy_username + '\'' +
                ", http_proxy_password='" + http_proxy_password + '\'' +
                ", jsapiTicket='" + jsapiTicket + '\'' +
                ", jsapiTicketExpiresTime='" + jsapiTicketExpiresTime + '\'' +
                ", tmpDirFile='" + tmpDirFile + '\'' +
                '}';
    }
}
