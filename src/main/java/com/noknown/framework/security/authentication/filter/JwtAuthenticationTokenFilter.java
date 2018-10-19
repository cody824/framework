package com.noknown.framework.security.authentication.filter;

import com.noknown.framework.security.authentication.util.JwtTokenUtil;
import com.noknown.framework.security.model.User;
import com.noknown.framework.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author guodong
 */
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

	@Autowired
	UserService userService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Value("${security.jwt.header:jwtheader}")
	private String tokenHeader;

	@Value("${security.jwt.tokenHead:common}")
	private String tokenHead;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		String authHeader = request.getHeader(this.tokenHeader);
		if (authHeader != null && authHeader.startsWith(tokenHead)) {
			final String authToken = authHeader.substring(tokenHead.length());
			User user = jwtTokenUtil.getUserFromToken(authToken);
			if (user != null) {
				if (jwtTokenUtil.validateToken(authToken, user)) {
					user.setAuthenticated(true);
					SecurityContextHolder.getContext().setAuthentication(user);
				} else {
					SecurityContextHolder.getContext().setAuthentication(null);
				}
			}
		}

		chain.doFilter(request, response);
	}

}
