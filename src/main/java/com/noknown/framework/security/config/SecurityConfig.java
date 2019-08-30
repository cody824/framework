package com.noknown.framework.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

/**
 * @author guodong
 */
@Configuration("frameworkSecurityConfig")
@PropertySource(value = "${spring.config.custom-path:classpath:}conf/${spring.profiles.active}/security.properties", ignoreResourceNotFound = true)
public class SecurityConfig {

	@Bean
	public SessionRegistry getSessionRegistry() {
		return new SessionRegistryImpl();
	}

	@Bean
	public SessionAuthenticationStrategy getSessionAuthenticationStrategy(SessionRegistry sessionRegistry) {
		return new ConcurrentSessionControlAuthenticationStrategy(sessionRegistry);
	}


}