package com.noknown.framework.security.pojo;

import com.noknown.framework.security.model.User;

import java.util.Map;

/**
 * @author guodong
 * @date 2018/2/5
 */
public class UserWarpForReg extends User {

	private Map<String, String> params;

	public Map<String, String> getParams() {
		return params;
	}

	public UserWarpForReg setParams(Map<String, String> params) {
		this.params = params;
		return this;
	}
}
