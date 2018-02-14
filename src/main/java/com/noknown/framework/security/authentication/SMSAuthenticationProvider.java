package com.noknown.framework.security.authentication;

import com.noknown.framework.common.exception.DaoException;
import com.noknown.framework.common.exception.ServiceException;
import com.noknown.framework.common.util.StringUtil;
import com.noknown.framework.security.exception.AuthodeErrorException;
import com.noknown.framework.security.model.BaseUserDetails;
import com.noknown.framework.security.model.Role;
import com.noknown.framework.security.model.ThirdPartyAccount;
import com.noknown.framework.security.model.User;
import com.noknown.framework.security.service.UserDetailsService;
import com.noknown.framework.security.service.UserService;
import com.noknown.framework.security.service.VerificationCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 手机验证码
 * PA photo authcode
 * @author cody
 *
 */
@Component
public class SMSAuthenticationProvider implements AuthenticationProvider{

	private final UserDetailsService udService;

	private final VerificationCodeService verificationCodeService;

	private final UserService userService;
	
	/**
	 * 是否开启验证码
	 * 	关闭
	 */
	@Value("${security.login.needAuthcode:false}")
	private boolean authcodeValid;

	@Autowired
	public SMSAuthenticationProvider(UserDetailsService udService, VerificationCodeService verificationCodeService, UserService userService) {
		this.udService = udService;
		this.verificationCodeService = verificationCodeService;
		this.userService = userService;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		SurePhoteAuthToken upToken = (SurePhoteAuthToken)authentication;
		List<GrantedAuthority> gaList = new ArrayList<>();
		SureAuthenticationInfo saInfo;
		String photo;
		String photoAuthcode;
		String clientId;
		String authcode = null;

		User user;
		BaseUserDetails ud;
		
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
				ret = verificationCodeService.check(clientId, authcode);
			} catch (DaoException | ServiceException e) {
				e.printStackTrace();
			}
			if (!ret) {
				throw new AuthodeErrorException("验证码错误!");
			}
		}
		
		try {
			user = userService.findByMobile(photo);
			if (user == null) {
				throw new BadCredentialsException("手机号【" + photo + "】没有注册");
			}
			boolean isOk = verificationCodeService.check(photo, photoAuthcode);
			if (!isOk) {
				throw new BadCredentialsException("验证码错误");
			}
			ud = udService.getUserDetail(user.getId());
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
		} catch (DaoException e) {
			throw new org.springframework.security.authentication.AuthenticationServiceException(e.getLocalizedMessage());
		}
		return saInfo;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(SurePhoteAuthToken.class);  
	}

}
