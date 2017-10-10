package com.noknown.framework.security.authentication.oauth2;

import com.noknown.framework.security.model.ThirdPartyAccount;

public interface Oauth2Handler {

	ThirdPartyAccount doAuth(String code, String state);
	
	
}
