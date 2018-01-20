package com.noknown.framework.security.web.authentication;

import com.noknown.framework.common.util.JsonUtil;
import com.noknown.framework.common.web.model.ErrorMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class AjaxLoginUrlAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint{
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private static String defaultView = "/gotoLoginView";
	

	private boolean response401 = true;
	
	private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	public AjaxLoginUrlAuthenticationEntryPoint(){
		super(defaultView);
	}
	
	  /**
     * Performs the redirect (or forward) to the login form URL.
     */
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {

        String redirectUrl = null;
        
        boolean isAjax = isAjaxRequest(request);
        if (isAjax) {
        	if (response401)
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			ErrorMsg msg = new ErrorMsg();
			msg.setErrorNum(401);
			msg.setErrorMsg("对不起，您需要登录");
			responseOutWithJson(response, msg);
			return;
        } 

        if (isUseForward()) {

            if (isForceHttps() && "http".equals(request.getScheme())) {
                // First redirect the current request to HTTPS.
                // When that request is received, the forward to the login page will be used.
                redirectUrl = buildHttpsRedirectUrlForRequest(request);
            }

            if (redirectUrl == null) {
                String loginForm = determineUrlToUseForThisRequest(request, response, authException);

                if (logger.isDebugEnabled()) {
                    logger.debug("Server side forward to: " + loginForm);
                }

                RequestDispatcher dispatcher = request.getRequestDispatcher(loginForm);

                dispatcher.forward(request, response);

                return;
            }
        } else {
            // redirect to login page. Use https if forceHttps true

            redirectUrl = buildRedirectUrlToLoginPage(request, response, authException);

        }

        redirectStrategy.sendRedirect(request, response, redirectUrl);
    }
	
	
	private boolean isAjaxRequest(HttpServletRequest request) {
		boolean check = false;
		if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
			check = true;
		} else if ("application/json".equalsIgnoreCase(request.getHeader("Accept"))) {
			check = true;
		}
		return check;
	}
	
	/**
	 * 以JSON格式输出
	 * 
	 * @param response
	 */
	protected void responseOutWithJson(HttpServletResponse response, Object responseObject) {
		// 将实体对象转换为JSON Object转换
		String json = JsonUtil.toJson(responseObject);
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json; charset=utf-8");
		PrintWriter out = null;
		try {
			out = response.getWriter();
			out.append(json);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return the response401
	 */
	public boolean isResponse401() {
		return response401;
	}

	/**
	 * @param response401 the response401 to set
	 */
	public void setResponse401(boolean response401) {
		this.response401 = response401;
	}
	
	public void setUseForward(@Value("${security.login.userForward:true}") boolean useForward) {
		super.setUseForward(useForward);
	}
}
