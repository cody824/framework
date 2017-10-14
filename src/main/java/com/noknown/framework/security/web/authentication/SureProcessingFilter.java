package com.noknown.framework.security.web.authentication;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ProviderNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.noknown.framework.common.util.StringUtil;
import com.noknown.framework.security.Constants;
import com.noknown.framework.security.authentication.SureAuthenticationInfo;
import com.noknown.framework.security.authentication.SureOauthToken;
import com.noknown.framework.security.authentication.SurePhoteAuthToken;
import com.noknown.framework.security.authentication.SureUsernamePasswordAuthenticationToken;

@Component
public class SureProcessingFilter  extends AbstractAuthenticationProcessingFilter {
	
	private static String defautlLoginAction = "/base/auth";

	private static String userNameParam = "username";
	private static String passwordParam = "password";
	private static String phoneParam = "phone";
	private static String phoneAuthcodeParam = "phoneAuthcode";
	private static String clientIdParam = "clientId";
	private static String authcodeParam = "authcode";
	private static String codeParam = "code";
	private static String stateParam = "state";
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	public SureProcessingFilter() {
		super(defautlLoginAction);
		super.setAuthenticationManager(authenticationManager);
	}
	
	public SureProcessingFilter(String defaultFilterProcessesUrl) {
		super(defaultFilterProcessesUrl);
		super.setAuthenticationManager(authenticationManager);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {
		HttpSession session = request.getSession();
		String userName = request.getParameter(userNameParam);
        String password = request.getParameter(passwordParam);
        String phone = request.getParameter(phoneParam);
        String phoneAuthcode = request.getParameter(phoneAuthcodeParam);
        String authcode = request.getParameter(authcodeParam);
        String clientId = request.getParameter(clientIdParam);
        String code = request.getParameter(codeParam);
        String state = request.getParameter(stateParam);
        Authentication token = null;
        if (clientId == null) clientId = session.getId();
        if (userName != null && password != null){
        	token = new SureUsernamePasswordAuthenticationToken(userName, password, authcode, clientId);
        } else if (code != null && state != null){
	   		String[] stateParam = state.split(",");
	   		String authSessionId = stateParam[1];
	   		if (StringUtil.isBlank(authSessionId) || !authSessionId.equals(session.getId())) {
	   			throw new ServletException("非法请求");
	   		}
        	token = new SureOauthToken(code, state);

        } else if (phone != null && phoneAuthcode != null) {
        	token = new SurePhoteAuthToken(phone, phoneAuthcode, authcode, clientId);
        } 
        if (token == null) {
			//微博取消之后 返回下面的地址
			// http://192.168.2.180:8080/browse/ZYY-894
			//http://www.smartprt.com.cn/base/auth?state=weibo,za6v4z09jncp1kgsmy5ifeton
			// &error_uri=%2Foauth2%2Fauthorize
			// &error=access_denied
			// &error_description=user%20denied%20your%20request.
			// &error_code=21330
			String errorCode = request.getParameter("error_code");
			if (errorCode != null) {
				String error = request.getParameter("error");
				String errorDescription = request.getParameter("error_description");
				throw new org.springframework.security.authentication.BadCredentialsException("登录失败，错误编码:" + errorCode + ",错误类型：" + error + ",错误描述：" + errorDescription);
			} else {
				throw new ProviderNotFoundException("验证参数错误,没有对应的处理器");
			}
		}

        Authentication auth = this.getAuthenticationManager().authenticate(token);  
        //此处为登录成功后，相应的处理逻辑  
        if (auth == null || !auth.isAuthenticated()) { 
       		throw new BadCredentialsException("登录失败");
        }
        session.setAttribute(Constants.SURE_LOGIN_INFO, auth);
        if (auth instanceof SureAuthenticationInfo) {
        	SureAuthenticationInfo saInfo = (SureAuthenticationInfo) auth;
        	session.setAttribute(Constants.SURE_LOGIN_USER_NAME, saInfo.getUd().getFullName());
        	session.setAttribute(Constants.SURE_LOGIN_USER_ID, saInfo.getUser().getId());
        	session.setAttribute(Constants.SURE_LOGIN_USER, saInfo.getUser());
        	session.setAttribute(Constants.SURE_LOGIN_USER_DETAIL, saInfo.getUd());
        	session.setAttribute(Constants.SURE_LOGIN_USER_ROLES, saInfo.getRoles());
		}
        return auth;  
	}
	
	@Autowired
	public void setAuthenticationSuccessHandler(
			AuthenticationSuccessHandler successHandler) {
		super.setAuthenticationSuccessHandler(successHandler);
	}

	@Autowired
	public void setAuthenticationFailureHandler(
			AuthenticationFailureHandler failureHandler) {
		super.setAuthenticationFailureHandler(failureHandler);
	}
	
	
	@Override  
    public void afterPropertiesSet() {  
		super.setAuthenticationManager(this.authenticationManager);
        super.afterPropertiesSet();  
        /* 
        *该处理器实现了AuthenticationSuccessHandler, AuthenticationFailureHandler 
        *用于处理登录成功或者失败后，跳转的界面 
        */  
  
    }  
	
}
