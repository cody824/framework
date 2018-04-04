package com.noknown.framework.security.service;

import com.noknown.framework.common.base.BaseService;
import com.noknown.framework.common.exception.ServiceException;
import com.noknown.framework.security.authentication.SureApiAuthToken;
import com.noknown.framework.security.model.ApiKey;
import com.noknown.framework.security.model.User;

/**
 * @author guodong
 */
public interface ApiKeyService extends BaseService<ApiKey, String> {

	/**
	 * 创建apiKey
	 *
	 * @param userId 用户ID
	 * @return apiKey
	 * @throws ServiceException 异常信息
	 */
	ApiKey create(Integer userId) throws ServiceException;

	/**
	 * 启用apiKey
	 *
	 * @param accessKey 访问key
	 * @throws ServiceException 异常信息
	 */
	void enable(String accessKey) throws ServiceException;

	/**
	 * 停用apiKey
	 *
	 * @param accessKey 访问key
	 * @throws ServiceException 异常信息
	 */
	void disable(String accessKey) throws ServiceException;

	/**
	 * /**
	 * 验证是否是合法请求
	 *
	 * @param token 访问凭证
	 * @return 请求用户
	 * @throws ServiceException 异常信息
	 */
	User check(SureApiAuthToken token) throws ServiceException;

	/**
	 * 获取用户的apiKey
	 *
	 * @param userId 用户ID
	 * @return apiKey
	 */
	ApiKey findByUserId(Integer userId);
}
