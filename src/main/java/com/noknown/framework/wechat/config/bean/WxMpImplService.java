/**
 * 文件名:
 * CopyRright (c) 2014-2015 SOUL
 * <p>
 * 创建人:邢伟伟 (weiwei.xing@soulinfo.com)
 * 日期:15/12/15
 * 修改人:邢伟伟 (weiwei.xing@soulinfo.com)
 * <p>
 * 描述:
 */
package com.noknown.framework.wechat.config.bean;

import com.google.gson.JsonElement;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.common.util.http.SimplePostRequestExecutor;
import me.chanjar.weixin.mp.api.WxMpConfigStorage;
import me.chanjar.weixin.mp.api.WxMpServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.StringReader;

@Service
public class WxMpImplService extends WxMpServiceImpl {

    @Autowired
    WxMpConfigStorage cacheStorage;

    public WxMpImplService() {
        super();
    }

    @PostConstruct
    public void initConfig() {
        this.setWxMpConfigStorage(cacheStorage);
    }

    public String templateSend(String templateMessage) throws WxErrorException {
        String url = "https://api.weixin.qq.com/cgi-bin/message/template/send";
        String responseContent = execute(new SimplePostRequestExecutor(), url, templateMessage);
        JsonElement tmpJsonElement = Streams.parse(new JsonReader(new StringReader(responseContent)));
        return tmpJsonElement.getAsJsonObject().get("msgid").getAsString();
    }

}
