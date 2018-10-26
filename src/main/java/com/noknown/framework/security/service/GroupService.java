package com.noknown.framework.security.service;

import com.noknown.framework.common.base.BaseService;
import com.noknown.framework.common.exception.DaoException;
import com.noknown.framework.common.exception.ServiceException;
import com.noknown.framework.security.model.Group;

/**
 * @author guodong
 */
public interface GroupService extends BaseService<Group, Integer> {

	/**
	 * 增加组
	 *
	 * @param name          组名
	 * @param comment       组说明
	 * @param parentGroupId 父组
	 * @return 组
	 * @throws ServiceException 异常信息
	 * @throws DaoException     异常信息
	 */
	Group create(String name, String comment, Integer parentGroupId) throws ServiceException;

	/**
	 * 根据组名取得组
	 *
	 * @param name 要查找的组的名称
	 * @return 查找到的组的实体
	 * @throws ServiceException 异常信息
	 * @throws DaoException     异常信息
	 */
	Group getGroupByName(String name) throws DaoException, ServiceException;

	/**
	 * 添加用户
	 *
	 * @param userId  用户ID
	 * @param groupId 组ID
	 * @throws ServiceException 异常信息
	 * @throws DaoException     异常信息
	 */
	void addUser(Integer userId, Integer groupId) throws DaoException, ServiceException;

	/**
	 * 从组中移除用户
	 *
	 * @param userId  用户ID
	 * @param groupId 角色ID
	 * @throws ServiceException 异常信息
	 * @throws DaoException     异常信息
	 */
	void removeUser(Integer userId, Integer groupId) throws DaoException, ServiceException;

}
