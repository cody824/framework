package com.noknown.framework.security.authentication;

import com.noknown.framework.common.exception.DAOException;
import com.noknown.framework.common.exception.ServiceException;
import com.noknown.framework.common.util.StringUtil;
import com.noknown.framework.security.authentication.oauth2.Oauth2Handler;
import com.noknown.framework.security.model.BaseUserDetails;
import com.noknown.framework.security.model.Role;
import com.noknown.framework.security.model.ThirdPartyAccount;
import com.noknown.framework.security.model.User;
import com.noknown.framework.security.service.UserDetailsService;
import com.noknown.framework.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 第三方登录（oauth2）登录处理器
 * @author cody
 *
 */
@Component
public class TpaAuthenticationProvider implements AuthenticationProvider{

	/**
	 * 使用时注入
	 */
	private Map<String, Oauth2Handler> supportOauth2Handler = new HashMap<>();
	
	/**
	 * 使用时注入
	 */
	private final UserDetailsService udService;

	private final UserService userService;

	@Autowired
	public TpaAuthenticationProvider(UserDetailsService udService, UserService userService) {
		this.udService = udService;
		this.userService = userService;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		SureOauthToken token = (SureOauthToken)authentication;
		List<GrantedAuthority> gaList = new ArrayList<>();
		SureAuthenticationInfo saInfo;
		String code;
		String authType;
		String[] stateParam;
		
		ThirdPartyAccount tpa;
		User user;
		BaseUserDetails ud = null;
		Integer userId;
		
		if (StringUtil.isBlank(token.getCode())){
			 throw new BadCredentialsException("无效的验证code");
		} else {
			code = token.getCode();
		}
		if (StringUtil.isBlank(token.getState())){
			 throw new BadCredentialsException("state参数错误");
		} else {
			stateParam = token.getState().split(",");
			authType = stateParam[0];
		}
		
		Oauth2Handler handler = supportOauth2Handler.get(authType);
		if (handler == null) {
			throw new org.springframework.security.authentication.ProviderNotFoundException("不支持该类型的oauth登录：" + authType );
		}
		try {
			tpa = handler.doAuth(code, token.getState());
			if (tpa.getUserId() != null) {
				userId = tpa.getUserId();
				userService.updateTpa(tpa);
			} else {
				ud = userService.addUserFromTpa(tpa);
				userId = ud.getId();
			}
			user = userService.get(userId);
			if (ud == null) {
				ud = udService.getUserDetail(user.getId());
			}
			List<Role> roleList = user.getRoles();
			if (roleList != null && roleList.size() > 0) {
				for (Role role : roleList) {
					GrantedAuthority ga = new SimpleGrantedAuthority(role.getName());
    				gaList.add(ga);
				}
			}
			saInfo = new SureAuthenticationInfo(tpa.getAccountType(), user, ud, roleList, gaList);
			saInfo.setPrincipal(user.getId());
			saInfo.setCredentials(tpa.getAccessToken());
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
		return authentication.equals(SureOauthToken.class);  
	}

	public void addHandler(String key, Oauth2Handler handler) {
		this.supportOauth2Handler.put(key, handler);
	}

}
