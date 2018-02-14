package com.noknown.framework.security.authentication.oauth2;

import com.noknown.framework.security.model.ThirdPartyAccount;

/**
 * @author guodong
 */
public interface Oauth2Handler {

	/**
	 * 验证
	 *
	 * @param code  验证码
	 * @param state 状态码
	 * @return 第三方登录账号
	 */
	ThirdPartyAccount doAuth(String code, String state);


}
