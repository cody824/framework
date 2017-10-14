package com.noknown.framework.security.web.authentication;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class SureUrlAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler{
	
	private String targetUrlParameter = null;
	private String defaultFailureUrl = "/gotoLoginView?error=true";
	private boolean alwaysUseDefaultTargetUrl = false;
	private boolean useReferer = false;
	
	
	
	   /**
     * Performs the redirect or forward to the {@code defaultFailureUrl} if set, otherwise returns a 401 error code.
     * <p>
     * If redirecting or forwarding, {@code saveException} will be called to cache the exception for use in
     * the target view.
     */
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {

    	String failureUrl = determineTargetUrl(request, response);
    	
    	
        if (failureUrl == null) {
            logger.debug("No failure URL set, sending 401 Unauthorized error");

            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication Failed: " + exception.getMessage());
        } else {
            saveException(request, exception);

            if (isUseForward()) {
                logger.debug("Forwarding to " + failureUrl);

                request.getRequestDispatcher(failureUrl).forward(request, response);
            } else {
                logger.debug("Redirecting to " + defaultFailureUrl);
                getRedirectStrategy().sendRedirect(request, response, defaultFailureUrl);
            }
        }
    }

	   /**
     * Builds the target URL according to the logic defined in the main class Javadoc.
     */
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response) {
        if (isAlwaysUseDefaultTargetUrl()) {
            return defaultFailureUrl;
        }

        // Check for the parameter and use that if available
        String targetUrl = null;

        if (targetUrlParameter != null  ) {
            targetUrl = request.getParameter(targetUrlParameter);

            if (StringUtils.hasText(targetUrl)) {
                logger.debug("Found targetUrlParameter in request: " + targetUrl);

                return targetUrl;
            }
        }

        if (useReferer && !StringUtils.hasLength(targetUrl)) {
            targetUrl = request.getHeader("Referer");
            logger.debug("Using Referer header: " + targetUrl);
        }

        if (!StringUtils.hasText(targetUrl)) {
            targetUrl = defaultFailureUrl;
            logger.debug("Using default Url: " + targetUrl);
        }

        return targetUrl;
    }
    
    /**
     * If <code>true</code>, will always redirect to the value of {@code defaultTargetUrl}
     * (defaults to <code>false</code>).
     */
    public void setAlwaysUseDefaultTargetUrl(boolean alwaysUseDefaultTargetUrl) {
        this.alwaysUseDefaultTargetUrl = alwaysUseDefaultTargetUrl;
    }

    protected boolean isAlwaysUseDefaultTargetUrl() {
        return alwaysUseDefaultTargetUrl;
    }

    /**
     * If this property is set, the current request will be checked for this a parameter with this name
     * and the value used as the target URL if present.
     *
     * @param targetUrlParameter the name of the parameter containing the encoded target URL. Defaults
     * to null.
     */
    public void setTargetUrlParameter(String targetUrlParameter) {
        if(targetUrlParameter != null) {
            Assert.hasText(targetUrlParameter,"targetUrlParameter cannot be empty");
        }
        this.targetUrlParameter = targetUrlParameter;
    }

    protected String getTargetUrlParameter() {
        return targetUrlParameter;
    }

    /**
     * If set to {@code true} the {@code Referer} header will be used (if available). Defaults to {@code false}.
     */
    public void setUseReferer(boolean useReferer) {
        this.useReferer = useReferer;
    }
    
    /**
     * The URL which will be used as the failure destination.
     *
     * @param defaultFailureUrl the failure URL, for example "/loginFailed.jsp".
     */
    public void setDefaultFailureUrl(String defaultFailureUrl) {
        Assert.isTrue(UrlUtils.isValidRedirectUrl(defaultFailureUrl),
                "'" + defaultFailureUrl + "' is not a valid redirect URL");
        this.defaultFailureUrl = defaultFailureUrl;
    }

}
