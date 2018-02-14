package com.noknown.framework.email.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author guodong
 */
@Configuration
@PropertySource(value = "${spring.config.custom-path:classpath:}conf/${spring.profiles.active}/mail.properties", ignoreResourceNotFound = true)
public class MailConfig {

}