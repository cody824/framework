package com.noknown.framework.sms.provider;

import com.noknown.framework.sms.pojo.SMS;
import org.springframework.beans.factory.annotation.Value;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author guodong
 */
public class TestSMSProvider  extends BaseSMSProvider implements SMSProvider  {


	@Value("${sms.test.apiurl:/sms/test/send}")
	private String apiUrl = "";

	@Override
	protected void initProvider() {
		this.name = "测试覅我";
		this.maxNum = 100;
		this.split = ",";
	}

	@Override
	public String getSMSUrl(SMS sms) {
		String urlFmt = "http://localhost:9090%s?to=%s&text=%s";
		try {
			return String.format(urlFmt, apiUrl,
					transitionPhones(sms.getPhones()), URLEncoder.encode(sms.getContent(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void checkResult(String code, String phones) {
		logger.info(String.format("【%s】发送短消息结果【%s】！", name, code));
		switch (code) {
			case "0":
				sendSuccess();
				break;
			case "-2":
				sendError(BaseSMSProvider.ERROR_PARA, phones);
				break;
			case "-3":
				sendError(BaseSMSProvider.UNKNOWN_ERROR, phones);
				break;
			case "-6":
				sendError(BaseSMSProvider.ERROR_PASSWARD, phones);
				break;
			case "-7":
				sendError(BaseSMSProvider.USER_NOT_EXIST, phones);
				break;
			case "-11":
				sendError(BaseSMSProvider.OUT_OF_MAX_SEND_NUM, phones);
				break;
			case "-12":
				sendError(BaseSMSProvider.NOT_ENOUGH_MONEY, phones);
				break;
			case "-99":
				sendError(BaseSMSProvider.UNKNOWN_ERROR, phones);
				break;
			default:
				sendError(BaseSMSProvider.UNKNOWN_ERROR, phones);
				break;
		}
	}
}