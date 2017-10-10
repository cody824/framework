package com.noknown.framework.sms.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.noknown.framework.sms.provider.LanChuangSMSProvider;
import com.noknown.framework.sms.provider.SMSProvider;
import com.noknown.framework.sms.provider.TestSMSProvider;
import com.noknown.framework.sms.provider.XuanWuSMSProvider;

@Configuration
@PropertySource(value = "classpath:conf/${spring.profiles.active}/sms.properties", ignoreResourceNotFound = true)
public class SMSConfig {

	public final Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${sms.global.provder:noprovder}")
	private String provder;

	
	@Bean
	public SMSProvider createSMSProvider() {
		SMSProvider smsProvider = null;
		switch (provder) {
		case "xuanwu":
			smsProvider = new XuanWuSMSProvider();
			break;
		case "lanchuang":
			smsProvider = new LanChuangSMSProvider();
		case "test":
			smsProvider = new TestSMSProvider();
			break;
		default:
			break;
		}
		return smsProvider;
	}
}