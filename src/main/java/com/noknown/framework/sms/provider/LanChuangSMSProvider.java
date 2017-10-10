
package com.noknown.framework.sms.provider;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.noknown.framework.common.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.noknown.framework.sms.pojo.SMS;

public class LanChuangSMSProvider extends BaseSMSProvider implements SMSProvider {

    private final Logger logger = (Logger) LoggerFactory.getLogger(getClass());

    @Value("${sms.lanchuang.apiurl}")
    private String apiUrl = "";
    @Value("${sms.lanchuang.account}")
    private String account = "";
    @Value("${sms.lanchuang.password}")
    private String pswd = "";
    @Value("${sms.lanchuang.signature}")
    private String signature = "";

    @Override
	protected void initProvider() {
        this.name = "蓝创科技";
        this.maxNum = 100;
    }

    @Override
    public String getSMSUrl(SMS sms) {
        String urlFmt = "%s?account=%s&pswd=%s&mobile=%s&msg=%s&needstatus=true";

        if(StringUtil.isNotBlank(signature))
        {
        	urlFmt="%s?un=%s&pw=%s&da=%s&sm=%s&dc=15&rd=1&tf=3&rf=2e";
        }


       
        try {
            return String.format(urlFmt, apiUrl, account, pswd,
                    transitionPhones(sms.getPhones()), URLEncoder.encode(signature+sms.getContent(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 0	提交成功
     * 101	无此用户
     * 102	密码错
     * 103	提交过快（提交速度超过流速限制）
     * 104	系统忙（因平台侧原因，暂时无法处理提交的短信）
     * 105	敏感短信（短信内容包含敏感词）
     * 106	消息长度错（>536或<=0）
     * 107	包含错误的手机号码
     * 108	手机号码个数错（群发>50000或<=0;单发>200或<=0）
     * 109	无发送额度（该用户可用短信数已使用完）
     * 110	不在发送时间内
     * 111	超出该账户当月发送额度限制
     * 112	无此产品，用户没有订购该产品
     * 113	extno格式错（非数字或者长度不对）
     * 115	自动审核驳回
     * 116	签名不合法，未带签名（用户必须带签名的前提下）
     * 117	IP地址认证错,请求调用的IP地址不是系统登记的IP地址
     * 118	用户没有相应的发送权限
     * 119	用户已过期
     * 120	短信内容不在白名单中
     *
     * @param code
     */
    @Override
	protected void checkResult(String code,String phones) {

        logger.info(String.format("【%s】发送短消息结果【%s】！", name, code));

        String state=code.split(",")[1];
        
        if(state.startsWith("0"))
        {
        	 sendSuccess();
        	 return;
        }
        if(code.contains("id"))
        {
            sendSuccess();
            return;
        }

        switch (state) {
            case "113":
                sendError(BaseSMSProvider.ERROR_PARA,phones);
                break;
            case "103":
                sendError(BaseSMSProvider.ERROR_SUBMIT_TOO_FAST,phones);
                break;
            case "102":
                sendError(BaseSMSProvider.ERROR_PASSWARD,phones);
                break;
            case "105":
                sendError(BaseSMSProvider.ERROR_LIMIT_WORDS,phones);
                break;
            case "108":
                sendError(BaseSMSProvider.OUT_OF_MAX_SEND_NUM,phones);
                break;
            case "109":
                sendError(BaseSMSProvider.NOT_ENOUGH_MONEY,phones);
                break;
            case "104":
                sendError(BaseSMSProvider.ERROR_SYSTEM_BUSY,phones);
                break;
            default:
                sendError(BaseSMSProvider.UNKNOWN_ERROR,phones);
                break;
        }
    }

}