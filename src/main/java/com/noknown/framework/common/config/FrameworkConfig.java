package com.noknown.framework.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "${conf.protocol:classpath}:conf/${spring.profiles.active}/base.properties", ignoreResourceNotFound = true)
@PropertySource(value = "classpath:app.properties", ignoreResourceNotFound = true)
public class FrameworkConfig {
}
