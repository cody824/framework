package com.noknown.framework.others.wantu.config;

import com.alibaba.media.MediaConfiguration;
import com.alibaba.media.client.impl.DefaultMediaClient;
import com.alibaba.media.upload.UploadTokenClient;
import com.alibaba.media.upload.impl.DefaultUploadTokenClient;
import com.noknown.framework.common.util.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

/*@Configuration
@PropertySource(value = "classpath:conf/${spring.profiles.active}/wantu.properties", ignoreResourceNotFound = true)*/
public abstract class AbstractWantuConfig {
	
	private @Value("${wantu.config.ak:}")  String ak;
	
	private @Value("${wantu.config.sk:}")String sk;
	
	private @Value("${wantu.config.namespace:}")String namespace;
	
	@Bean
	public MediaConfiguration getMediaConfiguration() {
		MediaConfiguration mediaConfiguration = null;
		if (StringUtil.isNotBlank(ak)) {
			mediaConfiguration = new MediaConfiguration();
			mediaConfiguration.setAk(ak);
			mediaConfiguration.setSk(sk);
			mediaConfiguration.setNamespace(namespace);
		}
		return mediaConfiguration;
	}
	
	@Bean("publicUploadTokenClient")
	public UploadTokenClient getDefaultUploadTokenClient() {
		DefaultUploadTokenClient client = null;
		MediaConfiguration mediaConfiguration = getMediaConfiguration();
		if (mediaConfiguration != null) {
			client = new DefaultUploadTokenClient(mediaConfiguration);
		}
		return client;
	}

	@Bean
	public DefaultMediaClient getDefaultMediaClient() {
		DefaultMediaClient client = null;
		MediaConfiguration mediaConfiguration = getMediaConfiguration();
		if (mediaConfiguration != null) {
			client = new DefaultMediaClient(mediaConfiguration);
		}
		return client;
	}
	
}