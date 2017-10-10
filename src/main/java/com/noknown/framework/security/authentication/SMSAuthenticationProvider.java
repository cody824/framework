package com.noknown.framework.security.authentication;

import java.util.ArrayList;
import java.util.List;

import com.noknown.framework.common.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.noknown.framework.common.exception.DAOException;
import com.noknown.framework.common.exception.ServiceException;
import com.noknown.framework.security.exception.AuthodeErrorException;
import com.noknown.framework.security.model.Role;
import com.noknown.framework.security.model.ThirdPartyAccount;
import com.noknown.framework.security.model.User;
import com.noknown.framework.security.model.UserDetails;
import com.noknown.framework.security.service.AuthcodeService;
import com.noknown.framework.security.service.UserDetailsService;
import com.noknown.framework.security.service.UserService;

/**
 * 手机验证码
 * PA photo authcode
 * @author cody
 *
 */
@Component
public class SMSAuthenticationProvider implements AuthenticationProvider{


	/**
	 * 使用时注入
	 */
	@Autowired
	private UserDetailsService udService;
	

	@Autowired
	private AuthcodeService authcodeService;
	
	@Autowired
	private UserService userService;
	
	/**
	 * 是否开启验证码
	 * 	关闭
	 */
	@Value("${security.login.needAuthcode:false}")
	private boolean authcodeValid;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		SurePhoteAuthToken upToken = (SurePhoteAuthToken)authentication;
		List<GrantedAuthority> gaList = new ArrayList<>();
		SureAuthenticationInfo saInfo = null;
		String photo = null;
		String photoAuthcode = null;
		String authcode = null;
		String clientId = null;
		User user;
		UserDetails ud;
		
		if (upToken.getPrincipal() == null || StringUtil.isBlank(upToken.getPrincipal().toString())){
			 throw new BadCredentialsException("请输入用户名/手机号/邮箱");
		} else {
			photo = upToken.getPrincipal().toString().trim();
		}
		if (upToken.getCredentials() == null || StringUtil.isBlank(upToken.getCredentials().toString())){
			 throw new BadCredentialsException("请输入密码");
		} else {
			photoAuthcode = upToken.getCredentials().toString().trim();
		}
		
		if (authcodeValid){
			if (StringUtil.isBlank(upToken.getAuthcode())){
				 throw new AuthodeErrorException("请输入验证码");
			} else {
				authcode = upToken.getAuthcode().trim();
			}
		}
		clientId = upToken.getClientId();
		
		if (authcodeValid) {
			boolean ret = false;
			try {
				ret = authcodeService.checkAuthCode(clientId, authcode);
			} catch (DAOException | ServiceException e) {
				e.printStackTrace();
			}
			if (!ret)
				throw new AuthodeErrorException("验证码错误!");
		}
		
		try {
			user = userService.findByMobile(photo);
			if (user == null)
				throw new BadCredentialsException("手机号【" + photo + "】没有注册");
			boolean isOk = authcodeService.checkAuthCode(photo, photoAuthcode);
			if (!isOk)
				throw new BadCredentialsException("验证码错误");
			ud = udService.get(user.getId());
			List<Role> roleList = user.getRoles();
			if (roleList != null && roleList.size() > 0) {
				for (Role role : roleList) {
					GrantedAuthority ga = new SimpleGrantedAuthority(role.getName());
    				gaList.add(ga);
				}
			}
			saInfo = new SureAuthenticationInfo(SureAuthenticationInfo.AUTH_TYPE_PI, user, ud, roleList, gaList);
			saInfo.setPrincipal(user.getId());
			saInfo.setCredentials(user.getPassword());
			List<ThirdPartyAccount> tpaList = userService.getThirdPartyList(user.getId());
			saInfo.setTpaList(tpaList);
		} catch (ServiceException e) {
			throw new BadCredentialsException(e.getLocalizedMessage());
		} catch (DAOException e) {
			throw new org.springframework.security.authentication.AuthenticationServiceException(e.getLocalizedMessage());
		}
		return saInfo;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(SurePhoteAuthToken.class);  
	}

	/**
	 * @return the udService
	 */
	public UserDetailsService getUdService() {
		return udService;
	}

	/**
	 * @param udService the udService to set
	 */
	public void setUdService(UserDetailsService udService) {
		this.udService = udService;
	}

}
