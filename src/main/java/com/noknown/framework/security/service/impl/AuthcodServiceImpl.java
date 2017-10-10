package com.noknown.framework.security.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.noknown.framework.common.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.noknown.framework.cache.service.CacheService;
import com.noknown.framework.common.exception.ServiceException;
import com.noknown.framework.common.util.algo.RandomString;
import com.noknown.framework.email.processor.MailProcessor;
import com.noknown.framework.security.service.AuthcodeService;
import com.noknown.framework.sms.provider.SMSProvider;

@Service
public class AuthcodServiceImpl  implements AuthcodeService {
	
	public final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private CacheService cacheService;
	
	@Autowired
	private SMSProvider smsProvider;
	
	@Autowired
	private MailProcessor MailProcessor;
	
	@Value("${security.authcode.email.tpl:}")
	private String tpl;
	
	@Value("${security.authcode.email.subject:验证码}")
	private String mailSubject;

	@Override
	public String generateAuthCode(String to, int len, int timeout) throws ServiceException {
		//生成验证码 : 全部为数字
		String authcode = RandomString.RandomNumber(len);
		
		//存到cache中
		Date now = new Date();
		Date expireTime = new Date(now.getTime() + timeout * 60 * 1000);
		
		String key = "authcode:" + to;
		cacheService.set(key, authcode, expireTime);
		
		return authcode;
	}

	@Override
	public boolean checkAuthCode(String to, String authcode)
			throws ServiceException {
		
		String key = "authcode:" + to;
		String code = (String) cacheService.get(key);

		return (code != null && code.equals(authcode));
	}

	@Override
	public void sendAuthCode(String type, String to, int len, int timeout) throws ServiceException {
		
		String authcode = generateAuthCode(to, len, timeout);
		if("phone".equals(type)){
			String sms = "您本次操作需要的验证码是:"+authcode+"，" + timeout + "分钟内有效，请尽快操作。";
			sendAuthCodeSms(to, sms);
		}else if("email".equals(type)){
			sendAuthCodeMail(to, authcode, timeout);
		}else{
			throw new ServiceException("参数错误");
		}
	}

	@Override
	public void expireAuthCode(String type, String to) throws ServiceException {

		String key = "authcode:" + to;
		cacheService.delete(key);
	}
	
	private boolean sendAuthCodeMail(String to, String authcode, int timeout) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH点mm分ss秒");
		if (StringUtil.isBlank(tpl)) {
			String msg = "您本次操作需要的验证码是:"+authcode+"，" + timeout + "分钟内有效，请尽快操作。";
			MailProcessor.sendMail(null, to, mailSubject, msg, true);
		} else {
			Map<String, String> tplData = new HashMap<>();
			tplData.put("authcode", authcode);
			tplData.put("timeout", Integer.toString(timeout));
			tplData.put("time", sdf.format(new Date()));
			MailProcessor.sendMail(null, to, mailSubject, tpl, tplData, true);
		}
		return true;
	}
	
	private boolean sendAuthCodeSms(String to, String msg) {
		try {
			return smsProvider.send(to, msg);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
