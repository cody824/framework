package com.noknown.framework.security.model.factory;

import com.noknown.framework.security.model.BaseUserDetails;

/**
 * @author guodong
 */
public interface UserDetailsFactory {

	/**
	 * 创建用户详情对象
	 *
	 * @param id 用户ID
	 * @return 用户详情对象
	 */
	BaseUserDetails createUD(Integer id);

	/**
	 * 解析Json字符串返回用户详情对象
	 * @param text  Json字符串
	 * @return 用户详情对象
	 */
	BaseUserDetails parseUD(String text);
}
