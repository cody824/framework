package com.noknown.framework.security.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;

/**
 * 短信验证码登录
 * @author cody
 *
 */
public class SurePhoteAuthToken extends AbstractAuthenticationToken {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6996206916077256578L;

	private String photo;
	
	private String photoAuthcode;
	
	private String authcode;
	
	private String clientId;
	
	/**
	 * 
	 * @param photo
	 * @param photoAuthcode
	 * @param authcode
	 * @param clientId
	 */
	public SurePhoteAuthToken(String photo, String photoAuthcode, String authcode, String  clientId) {
		super(null);	
		this.photo = photo;
		this.photoAuthcode = photoAuthcode;
		this.authcode = authcode;
		this.clientId = clientId;
	}


	/**
	 * @return the photo
	 */
	public String getPhoto() {
		return photo;
	}


	/**
	 * @param photo the photo to set
	 */
	public void setPhoto(String photo) {
		this.photo = photo;
	}


	/**
	 * @return the photoAuthcode
	 */
	public String getPhotoAuthcode() {
		return photoAuthcode;
	}


	/**
	 * @param photoAuthcode the photoAuthcode to set
	 */
	public void setPhotoAuthcode(String photoAuthcode) {
		this.photoAuthcode = photoAuthcode;
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


	@Override
	public Object getCredentials() {
		return photoAuthcode;
	}


	@Override
	public Object getPrincipal() {
		return photo;
	}

}
