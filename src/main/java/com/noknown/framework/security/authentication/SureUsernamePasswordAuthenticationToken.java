package com.noknown.framework.security.authentication;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

/**
 * 用户密码登录处理器
 * @author cody
 *
 */
public class SureUsernamePasswordAuthenticationToken extends UsernamePasswordAuthenticationToken{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4904913986159086030L;
	
	
	private String authcode;
	
	private String clientId;

	public SureUsernamePasswordAuthenticationToken(Object principal, Object credentials, String authcode, String clientId) {
		super(principal, credentials);
		this.authcode = authcode;
		this.clientId = clientId;
	}


	/**
	 * @return the authcode
	 */
	public String getAuthcode() {
		return authcode;
	}


	/**
	 * @param authcode the authcode to set
	 */
	public void setAuthcode(String authcode) {
		this.authcode = authcode;
	}


	/**
	 * @return the clientId
	 */
	public String getClientId() {
		return clientId;
	}


	/**
	 * @param clientId the clientId to set
	 */
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}


}
