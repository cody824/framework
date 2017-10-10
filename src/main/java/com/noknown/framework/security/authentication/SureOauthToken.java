package com.noknown.framework.security.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;

/**
 * oauth2登录token
 * @author cody
 *
 */
public class SureOauthToken extends AbstractAuthenticationToken {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6996206916077256578L;

	private String code;
	
	private String state;
	

	public SureOauthToken(String code, String state) {
		super(null);	
		this.code = code;
		this.state = state;
	}

	@Override
	public Object getCredentials() {
		return code;
	}

	@Override
	public Object getPrincipal() {
		return state;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}

}
