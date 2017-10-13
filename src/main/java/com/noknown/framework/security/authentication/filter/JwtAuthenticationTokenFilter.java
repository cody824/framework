package com.noknown.framework.security.authentication.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.noknown.framework.security.authentication.util.JwtTokenUtil;
import com.noknown.framework.security.model.User;
import com.noknown.framework.security.service.UserService;

public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

	@Autowired
	UserService userService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Value("${security.jwt.header}")
	private String tokenHeader;

	@Value("${security.jwt.tokenHead}")
	private String tokenHead;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		String authHeader = request.getHeader(this.tokenHeader);
		if (authHeader != null && authHeader.startsWith(tokenHead)) {
			final String authToken = authHeader.substring(tokenHead.length()); // The part after "Bearer"
			User user = jwtTokenUtil.getUserFromToken(authToken);
			if (user != null) {
				if (jwtTokenUtil.validateToken(authToken, user)) {
					user.setAuthenticated(true);
					SecurityContextHolder.getContext().setAuthentication(user);
				} else {
					SecurityContextHolder.getContext().setAuthentication(null);
				}
			} 
//			String username = jwtTokenUtil.getUsernameFromToken(authToken);
//
//			logger.info("checking authentication " + username);
//
//			if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//
//				// 如果我们足够相信token中的数据，也就是我们足够相信签名token的secret的机制足够好
//				// 这种情况下，我们可以不用再查询数据库，而直接采用token中的数据
//				// 本例中，我们还是通过Spring Security的 @UserDetailsService 进行了数据查询
//				// 但简单验证的话，你可以采用直接验证token是否合法来避免昂贵的数据查询
//				UserDetails userDetails = this.userService.loadUserByUsername(username);
//				if (jwtTokenUtil.validateToken(authToken, userDetails)) {
//					UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
//							userDetails, null, userDetails.getAuthorities());
//					authentication.setDetails(userDetails);
//					logger.info("authenticated user " + username + ", setting security context");
//					request.setAttribute("auth", authentication);
//					SecurityContextHolder.getContext().setAuthentication(authentication);
//				}
//			}
		}

		chain.doFilter(request, response);
	}

}
