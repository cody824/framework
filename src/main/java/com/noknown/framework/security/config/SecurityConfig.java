package com.noknown.framework.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration("frameworkSecurityConfig")
@PropertySource(value = "${spring.config.custom-path:classpath:}conf/${spring.profiles.active}/security.properties", ignoreResourceNotFound = true)
public class SecurityConfig {
}