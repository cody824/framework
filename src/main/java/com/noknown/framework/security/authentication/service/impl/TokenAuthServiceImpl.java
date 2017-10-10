package com.noknown.framework.security.authentication.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.noknown.framework.security.authentication.SureUsernamePasswordAuthenticationToken;
import com.noknown.framework.security.authentication.service.TokenAuthService;
import com.noknown.framework.security.authentication.util.JwtTokenUtil;
import com.noknown.framework.security.model.User;
import com.noknown.framework.security.service.UserService;

@Service
public class TokenAuthServiceImpl implements TokenAuthService {

	private AuthenticationManager authenticationManager;
	private UserService userDetailsService;
	private JwtTokenUtil jwtTokenUtil;
	

    @Value("${security.jwt.tokenHead}")
    private String tokenHead;

	@Autowired
	public TokenAuthServiceImpl(AuthenticationManager authenticationManager, UserService userDetailsService,
			JwtTokenUtil jwtTokenUtil) {
		this.authenticationManager = authenticationManager;
		this.userDetailsService = userDetailsService;
		this.jwtTokenUtil = jwtTokenUtil;
	}

	@Override
	public String login(String username, String password) {
		SureUsernamePasswordAuthenticationToken upToken = new SureUsernamePasswordAuthenticationToken(username, password, null, null);
		// Perform the security
		final Authentication authentication = authenticationManager.authenticate(upToken);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		// Reload password post-security so we can generate token
		final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
		final String token = jwtTokenUtil.generateToken(userDetails);
		return token;
	}
	

	@Override
	public String login(User user) {
		user.setAuthenticated(true);
		SecurityContextHolder.getContext().setAuthentication(user);
		final String token = jwtTokenUtil.generateToken(user);
		return token;
	}

	
	
	@Override
	public String refresh(String oldToken) {
		final String token = oldToken.substring(tokenHead.length());
		String username = jwtTokenUtil.getUsernameFromToken(token);
		User user = (User) userDetailsService.loadUserByUsername(username);
		if (jwtTokenUtil.canTokenBeRefreshed(token, user.getLastPasswordResetDate())) {
			return jwtTokenUtil.refreshToken(token);
		}
		return null;
	}

}
