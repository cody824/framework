package com.noknown.framework.email.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "${conf.protocol:classpath}:conf/${spring.profiles.active}/mail.properties", ignoreResourceNotFound = true)
public class MailConfig {

	public final Logger logger = LoggerFactory.getLogger(getClass());

}