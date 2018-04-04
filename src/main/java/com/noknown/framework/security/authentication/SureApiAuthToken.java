package com.noknown.framework.security.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Map;

/**
 * api验证token
 *
 * @author cody
 */
public class SureApiAuthToken extends AbstractAuthenticationToken {


	private static final long serialVersionUID = 6372653900747384294L;

	private String accessKey;

	private String sign;

	private Map<String, String> params;

	private Long timestamp;

	private String signMethod;

	public SureApiAuthToken(Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
	}

	@Override
	public Object getCredentials() {
		return accessKey;
	}

	@Override
	public Object getPrincipal() {
		return sign;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public SureApiAuthToken setAccessKey(String accessKey) {
		this.accessKey = accessKey;
		return this;
	}

	public String getSign() {
		return sign;
	}

	public SureApiAuthToken setSign(String sign) {
		this.sign = sign;
		return this;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public SureApiAuthToken setParams(Map<String, String> params) {
		this.params = params;
		return this;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public SureApiAuthToken setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
		return this;
	}

	public String getSignMethod() {
		return signMethod;
	}

	public SureApiAuthToken setSignMethod(String signMethod) {
		this.signMethod = signMethod;
		return this;
	}
}
