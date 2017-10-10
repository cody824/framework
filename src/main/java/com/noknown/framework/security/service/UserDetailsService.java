package com.noknown.framework.security.service;

import com.noknown.framework.common.exception.ServiceException;
import com.noknown.framework.security.model.UserDetails;

public interface UserDetailsService {
	
	/**
	 * 更新userDetail
	 * @param ud
	 * @return
	 * @throws ServiceException
	 */
	UserDetails updateUserDetails(UserDetails ud) throws ServiceException;
	
	/**
	 * 获取userDetail
	 * @param id
	 * @return
	 * @throws ServiceException
	 */
	UserDetails get(Integer id)throws ServiceException;

}
