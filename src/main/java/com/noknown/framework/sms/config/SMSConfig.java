package com.noknown.framework.sms.config;

import com.noknown.framework.sms.provider.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author guodong
 */
@Configuration
@PropertySource(value = "${spring.config.custom-path:classpath:}conf/${spring.profiles.active}/sms.properties", ignoreResourceNotFound = true)
public class SMSConfig {

	public final Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${sms.global.provder:test}")
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
//			case "test":
//				smsProvider = new TestSMSProvider();
//				break;
			case "ali":
				smsProvider = new AliSMSProvider();
				break;
			default:
				smsProvider = new TestSMSProvider();
		}
		return smsProvider;
	}
}