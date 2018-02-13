package com.noknown.framework.security.service;

import com.noknown.framework.common.base.BaseService;
import com.noknown.framework.common.exception.ServiceException;
import com.noknown.framework.security.model.BaseUserDetails;

/**
 * @author guodong
 */
public interface UserDetailsService<T extends BaseUserDetails> extends BaseService<T, Integer> {
	
	/**
	 * 更新userDetail
	 * @param ud    用户详情
	 * @return 更新后的用户详情
	 * @throws ServiceException 异常信息
	 */
	BaseUserDetails updateUserDetails(BaseUserDetails ud) throws ServiceException;

	/**
	 * 获取userDetail
	 * @param id    用户ID
	 * @return 用户详情
	 * @throws ServiceException 异常信息
	 */
	BaseUserDetails getUserDetail(Integer id) throws ServiceException;

}
