package com.noknown.framework.security.authentication.service;

import com.noknown.framework.security.model.User;

/**
 * @author guodong
 */
public interface TokenAuthService {

	/**
	 * 获取token
	 *
	 * @param user 用户
	 * @return token
	 */
	String login(User user);

	/**
	 * 获得token
	 * @param username  用户名
	 * @param password  密码
	 * @return  token
	 */
	String login(String username, String password);

	/**
	 * 刷新token
	 *
	 * @param oldToken 旧的token
	 * @return token
	 */
	String refresh(String oldToken);

}
