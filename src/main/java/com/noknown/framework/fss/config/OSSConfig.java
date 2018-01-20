package com.noknown.framework.fss.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
@ConfigurationProperties(prefix="ali.oss")
public class OSSConfig {
	
	public String accessKeyId = "";

	public String secretAccessKey = "";

	public String endpoint = "";

	public String bucketName = "";
	
	public String domain = "";
	
	public void loadFromProperties(Properties properties){
		accessKeyId = properties.getProperty("accessKeyId", "");
		secretAccessKey = properties.getProperty("secretAccessKey", "");
		endpoint = properties.getProperty("endpoint", "");
		bucketName = properties.getProperty("bucketName", "");
		domain = properties.getProperty("domain", "");
	}

	/**
	 * @return the accessKeyId
	 */
	public String getAccessKeyId() {
		return accessKeyId;
	}

	/**
	 * @param accessKeyId the accessKeyId to set
	 */
	public void setAccessKeyId(String accessKeyId) {
		this.accessKeyId = accessKeyId;
	}

	/**
	 * @return the secretAccessKey
	 */
	public String getSecretAccessKey() {
		return secretAccessKey;
	}

	/**
	 * @param secretAccessKey the secretAccessKey to set
	 */
	public void setSecretAccessKey(String secretAccessKey) {
		this.secretAccessKey = secretAccessKey;
	}

	/**
	 * @return the endpoint
	 */
	public String getEndpoint() {
		return endpoint;
	}

	/**
	 * @param endpoint the endpoint to set
	 */
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	/**
	 * @return the bucketName
	 */
	public String getBucketName() {
		return bucketName;
	}

	/**
	 * @param bucketName the bucketName to set
	 */
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	/**
	 * @return the domain
	 */
	public String getDomain() {
		return domain;
	}

	/**
	 * @param domain the domain to set
	 */
	public void setDomain(String domain) {
		this.domain = domain;
	}
	
}
