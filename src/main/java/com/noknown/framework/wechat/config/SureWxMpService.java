package com.noknown.framework.wechat.config;

import com.google.gson.JsonElement;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.common.util.http.SimplePostRequestExecutor;
import me.chanjar.weixin.mp.api.WxMpServiceImpl;

import java.io.StringReader;

public class SureWxMpService extends WxMpServiceImpl {

    CacheStorage cacheStorage;

    public SureWxMpService(CacheStorage cacheStorage) {
        super();
        this.setWxMpConfigStorage(cacheStorage);
    }

    public String templateSend(String templateMessage) throws WxErrorException {
        String url = "https://api.weixin.qq.com/cgi-bin/message/template/send";
        String responseContent = execute(new SimplePostRequestExecutor(), url, templateMessage);
        JsonElement tmpJsonElement = Streams.parse(new JsonReader(new StringReader(responseContent)));
        return tmpJsonElement.getAsJsonObject().get("msgid").getAsString();
    }

}
