package com.noknown.framework.security.service.impl;

import com.noknown.framework.cache.service.CacheService;
import com.noknown.framework.common.exception.ServiceException;
import com.noknown.framework.common.util.StringUtil;
import com.noknown.framework.common.util.algo.RandomString;
import com.noknown.framework.email.processor.MailProcessor;
import com.noknown.framework.security.service.VerificationCodeService;
import com.noknown.framework.sms.provider.SMSProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author guodong
 */
@Service
public class VerificationCodeServiceImpl implements VerificationCodeService {

	private final static String PHONE = "phone";

	private static final String EMAIL = "email";

	private final CacheService cacheService;

	private final SMSProvider smsProvider;

	private final MailProcessor mailProcessor;

	@Value("${security.authcode.email.tpl:}")
	private String tpl;

	@Value("${security.authcode.email.subject:验证码}")
	private String mailSubject;

	@Value("${security.authcode.sms.tpl:}")
	private String smsTpl;

	@Autowired
	public VerificationCodeServiceImpl(CacheService cacheService, SMSProvider smsProvider, MailProcessor mailProcessor) {
		this.cacheService = cacheService;
		this.smsProvider = smsProvider;
		this.mailProcessor = mailProcessor;
	}


	@Override
	public String generate(String to, int len, int timeout) {
		//生成验证码 : 全部为数字
		String authcode = RandomString.randomNumber(len);

		//存到cache中
		Date now = new Date();
		Date expireTime = new Date(now.getTime() + timeout * 60 * 1000);

		String key = "authcode:" + to;
		cacheService.set(key, authcode, expireTime);

		return authcode;
	}

	@Override
	public boolean check(String to, String authcode) {

		String key = "authcode:" + to;
		String code = (String) cacheService.get(key);

		return (code != null && code.equals(authcode));
	}

	@Override
	public void send(String type, String to, int len, int timeout) throws ServiceException {

		String authcode = generate(to, len, timeout);
		if (PHONE.equals(type)) {
			if (StringUtil.isNotBlank(smsTpl)) {
				Map<String, String> params = new HashMap<>(2);
				params.put("code", authcode);
				params.put("timeout", timeout + "");
				sendAuthCodeSms(to, smsTpl, params);
			} else {
				String sms = "您本次操作需要的验证码是:"+authcode+"，" + timeout + "分钟内有效，请尽快操作。";
				sendAuthCodeSms(to, sms);
			}
		} else if (EMAIL.equals(type)) {
			sendAuthCodeMail(to, authcode, timeout);
		}else{
			throw new ServiceException("参数错误");
		}
	}

	@Override
	public void expire(String type, String to) {

		String key = "authcode:" + to;
		cacheService.delete(key);
	}

	private void sendAuthCodeMail(String to, String authcode, int timeout) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH点mm分ss秒");
		if (StringUtil.isBlank(tpl)) {
			String msg = "您本次操作需要的验证码是:"+authcode+"，" + timeout + "分钟内有效，请尽快操作。";
			mailProcessor.sendMail(null, to, mailSubject, msg, true);
		} else {
			Map<String, String> tplData = new HashMap<>(3);
			tplData.put("authcode", authcode);
			tplData.put("timeout", Integer.toString(timeout));
			tplData.put("time", sdf.format(new Date()));
			mailProcessor.sendMail(null, to, mailSubject, tpl, tplData, true);
		}
	}

	private void sendAuthCodeSms(String to, String msg) {
		try {
			smsProvider.send(to, msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void sendAuthCodeSms(String to, String tpl, Map<String, String> tplParams) {
		try {
			smsProvider.send(to, tpl, tplParams);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
