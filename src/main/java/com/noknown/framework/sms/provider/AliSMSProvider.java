package com.noknown.framework.sms.provider;

import com.alibaba.media.utils.JSONUtil;
import com.noknown.framework.common.util.JsonUtil;
import com.noknown.framework.sms.pojo.SMS;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

/**
 * @author guodong
 */
public class AliSMSProvider extends BaseSMSProvider implements SMSProvider  {

    private final static String SIGN = "Signature";

    @Value("${sms.ali.apiurl:http://dysmsapi.aliyuncs.com/}")
    private String apiUrl = "http://dysmsapi.aliyuncs.com/";

    @Value("${sms.ali.accesskeyId:}")
    private String accessKeyId;

    @Value("${sms.ali.accessSecret:}")
    private String accessSecret;

    @Value("${sms.ali.signature:}")
    private String signature = "";

    private static String specialUrlEncode(String value) throws Exception {
        return java.net.URLEncoder.encode(value, "UTF-8").replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
    }

    @Override
    public String getSMSUrl(SMS sms) {
        String url = null;
        try {
            url = getUrl(sms);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    private static String sign(String accessSecret, String stringToSign) throws Exception {
        javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA1");
        mac.init(new javax.crypto.spec.SecretKeySpec(accessSecret.getBytes("UTF-8"), "HmacSHA1"));
        byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
        return new sun.misc.BASE64Encoder().encode(signData);
    }

    @Override
    protected void initProvider() {
        this.name = "阿里";
        this.maxNum = 100;
        this.split = ",";
    }

    public String getUrl(SMS sms) throws Exception {
        java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        // 这里一定要设置GMT时区
        df.setTimeZone(new java.util.SimpleTimeZone(0, "GMT"));
        java.util.Map<String, String> paras = new java.util.HashMap<>(20);
        // 1. 系统参数
        paras.put("SignatureMethod", "HMAC-SHA1");
        paras.put("SignatureNonce", java.util.UUID.randomUUID().toString());
        paras.put("AccessKeyId", accessKeyId);
        paras.put("SignatureVersion", "1.0");
        paras.put("Timestamp", df.format(new java.util.Date()));
        paras.put("Format", "JSON");
        // 2. 业务API参数
        paras.put("Action", "SendSms");
        paras.put("Version", "2017-05-25");
        paras.put("RegionId", "cn-hangzhou");
        paras.put("PhoneNumbers", transitionPhones(sms.getPhones()));
        paras.put("SignName", signature);
        paras.put("TemplateParam", JSONUtil.toJSONString(sms.getVars()));
        paras.put("TemplateCode", sms.getTempCode());
        // 3. 去除签名关键字Key
        if (paras.containsKey(SIGN)) {
            paras.remove(SIGN);
        }
        // 4. 参数KEY排序
        java.util.TreeMap<String, String> sortParas = new java.util.TreeMap<>(paras);
        sortParas.putAll(paras);
        // 5. 构造待签名的字符串
        java.util.Iterator<String> it = sortParas.keySet().iterator();
        StringBuilder sortQueryStringTmp = new StringBuilder();
        while (it.hasNext()) {
            String key = it.next();
            sortQueryStringTmp.append("&").append(specialUrlEncode(key)).append("=").append(specialUrlEncode(paras.get(key)));
        }
        // 去除第一个多余的&符号
        String sortedQueryString = sortQueryStringTmp.substring(1);
        String stringToSign = "GET" + "&" +
                specialUrlEncode("/") + "&" +
                specialUrlEncode(sortedQueryString);
        String sign = sign(accessSecret + "&", stringToSign);
        // 6. 签名最后也要做特殊URL编码
        String signature = specialUrlEncode(sign);
        return apiUrl + "?Signature=" + signature + sortQueryStringTmp;
    }

    @Override
    protected void checkResult(String code, String phones) {
        logger.info(String.format("【%s】发送短消息结果【%s】！", name, code));
        Map<String, Object> res = JsonUtil.toMap(code);
        String retCode = (String) res.get("Code");
        String message = (String) res.get("Message");

        switch (retCode) {
            case "OK":
                sendSuccess();
                break;
            default:
                sendError(message,phones);
                break;
        }
    }
}