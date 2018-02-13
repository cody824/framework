package com.noknown.framework.security.model.factory;

import com.noknown.framework.security.model.BaseUserDetails;

public interface UserDetailsFactory {

	BaseUserDetails createUD(Integer id);

	BaseUserDetails parseUD(String text);
}
