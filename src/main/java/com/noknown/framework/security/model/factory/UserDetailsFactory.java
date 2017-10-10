package com.noknown.framework.security.model.factory;

import com.noknown.framework.security.model.UserDetails;

public interface UserDetailsFactory {

	UserDetails createUD(Integer id);
	
	UserDetails parseUD(String  text);
}
