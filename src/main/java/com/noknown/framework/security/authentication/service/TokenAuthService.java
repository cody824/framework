package com.noknown.framework.security.authentication.service;

import com.noknown.framework.security.model.User;

public interface TokenAuthService {
	
	String login(User user);
	
	String login(String username, String password);
    
    String refresh(String oldToken);

}
