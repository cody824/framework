package com.noknown.framework.security.authentication;

import com.noknown.framework.common.util.StringUtil;
import com.noknown.framework.security.model.Role;
import com.noknown.framework.security.model.ThirdPartyAccount;
import com.noknown.framework.security.model.User;
import com.noknown.framework.security.model.UserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * 登录信息
 * 登录后会保存到session中
 * @author cody
 *
 */
public class SureAuthenticationInfo  implements Serializable, Authentication {

	/**
	 * 
	 */
	private static final long serialVersionUID = 823067859386824904L;
	
	public static final String AUTH_TYPE_PI = "photeAuthcode";
	
	public static final String AUTH_TYPE_UP = "userNameAndPassoword";
	
	public static final String AUTH_TYPE_QQ = "qq";
	
	public static final String AUTH_TYPE_WEIBO = "weibo";
	
	public static final String AUTH_TYPE_WECHAT = "wechat";
	
	public SureAuthenticationInfo() {
	}

	public SureAuthenticationInfo(String authType, User user, UserDetails ud, List<Role> roleList, List<GrantedAuthority> gaList) {
		this.loginAuthType = authType;
		this.user = user;
		this.ud = ud;
		this.roles = roleList;
		this.accesses = gaList;
		this.authenticated = true;
		this.principal = user.getId();
		this.credentials = user.getPassword();
	}

	private boolean authenticated;
	
	private Serializable principal;
	
	private String credentials;
	
	private UserDetails ud;
	
	private User user;
	
	private List<ThirdPartyAccount> tpaList = new ArrayList<>();;
    
	private String loginAuthType;
	
	private List<Role> roles;
	
	private Collection<? extends GrantedAuthority> accesses;

	@Override
	public String getName() {
		if (this.getUd() != null && StringUtil.isNotBlank(this.getUd().getFullName()))
			return this.getUd().getFullName();
		if (this.getUser() != null && StringUtil.isNotBlank(this.getUser().getName()))
			return this.getUser().getName();
		return "";
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return accesses;
	}

	@Override
	public Object getCredentials() {
		return credentials;
	}

	@Override
	public Object getDetails() {
		return ud;
	}

	@Override
	public Object getPrincipal() {
		return principal;
	}

	@Override
	public boolean isAuthenticated() {
		return authenticated;
	}

	@Override
	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
		this.authenticated = isAuthenticated;
		
	}

	/**
	 * @return the ud
	 */
	public UserDetails getUd() {
		return ud;
	}

	/**
	 * @param ud the ud to set
	 */
	public void setUd(UserDetails ud) {
		this.ud = ud;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * @return the accesses
	 */
	public Collection<? extends GrantedAuthority> getAccesses() {
		return accesses;
	}

	/**
	 * @param accesses the accesses to set
	 */
	public void setAccesses(Collection<? extends GrantedAuthority> accesses) {
		this.accesses = accesses;
	}

	/**
	 * @param principal the principal to set
	 */
	public void setPrincipal(Serializable principal) {
		this.principal = principal;
	}

	/**
	 * @param credentials the credentials to set
	 */
	public void setCredentials(String credentials) {
		this.credentials = credentials;
	}

	/**
	 * @return the roles
	 */
	public List<Role> getRoles() {
		return roles;
	}

	/**
	 * @param roles the roles to set
	 */
	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}


	/**
	 * @return the loginAuthType
	 */
	public String getLoginAuthType() {
		return loginAuthType;
	}

	/**
	 * @param loginAuthType the loginAuthType to set
	 */
	public void setLoginAuthType(String loginAuthType) {
		this.loginAuthType = loginAuthType;
	}

	/**
	 * @return the tpaList
	 */
	public List<ThirdPartyAccount> getTpaList() {
		return tpaList;
	}

	/**
	 * @param tpaList the tpaList to set
	 */
	public void setTpaList(List<ThirdPartyAccount> tpaList) {
		this.tpaList = tpaList;
	}

}
