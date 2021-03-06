package com.noknown.framework.security.service;

import com.noknown.framework.common.base.BaseService;
import com.noknown.framework.common.exception.DaoException;
import com.noknown.framework.common.exception.ServiceException;
import com.noknown.framework.security.model.Role;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author guodong
 */
public interface RoleService extends BaseService<Role, Integer> {


	/**
	 * 增加角色
	 * @param roleName 角色英文名
	 * @param comment 角色的说明
	 * @return 角色
	 * @throws ServiceException 异常信息
	 * @throws DaoException     异常信息
	 */
	Role createRole(String roleName, String comment) throws DaoException, ServiceException;

	/**
	 * 增加角色
	 * @param role 角色对象
	 * @return 角色
	 * @throws ServiceException 异常信息
	 * @throws DaoException     异常信息
	 */
	Role createRole(Role role) throws DaoException, ServiceException;

	/**
	 *修改角色 
	 * @param role 角色对象
	 * @throws ServiceException 异常信息
	 * @throws DaoException     异常信息
	 */
	void modifyRole(Role role) throws DaoException, ServiceException;

	/**
	 *删除角色 (单个)
	 * @param id 要删除角色的id
	 * @throws ServiceException 异常信息
	 * @throws DaoException     异常信息
	 */
	void destroyRole(Integer id) throws DaoException, ServiceException;

	/**
	 *删除角色 (多个)
	 * @param ids 要删除角色的id
	 * @throws ServiceException 异常信息
	 * @throws DaoException     异常信息
	 */
	void destroyRole(List<Integer> ids) throws DaoException, ServiceException;

	/**
	 * 根据角色id取得角色
	 * @param id 要查找的角色的id
	 * @return 查找到的角色的实体
	 * @throws ServiceException 异常信息
	 * @throws DaoException     异常信息
	 */
	Role getRoleById(Integer id) throws DaoException, ServiceException;

	/**
	 * 根据角色中文名称取得角色
	 * @param name 要查找的角色的中文名称
	 * @return 查找到的角色的实体
	 * @throws ServiceException 异常信息
	 * @throws DaoException     异常信息
	 */
	Role getRoleByName(String name) throws DaoException, ServiceException;


	/**
	 * 分页查询
	 * @param page 第几页
	 * @param size 几条数据
	 * @return Page<Role>
	 * @throws ServiceException 异常信息
	 * @throws DaoException     异常信息
	 */
	Page<Role> getRoleByPage(int page, int size) throws ServiceException, DaoException;


	/**
	 * 赋予用户角色
	 * @param userId 用户ID
	 * @param roleId 角色ID
	 * @throws ServiceException 异常信息
	 * @throws DaoException     异常信息
	 */
	void attachRoleForUser(Integer userId, Integer roleId) throws DaoException, ServiceException;

	/**
	 * 赋予用户角色
	 * @param userId    用户ID
	 * @param roleName 角色
	 * @throws ServiceException 异常信息
	 * @throws DaoException     异常信息
	 */
	void attachRoleForUser(Integer userId, String roleName) throws DaoException, ServiceException;

	/**
	 * 分离用户角色
	 * @param userId 用户ID 字符串 如：【123456,56789】
	 * @param roleId 角色ID
	 * @throws ServiceException 异常信息
	 * @throws DaoException     异常信息
	 */
	void detachRoleFromUser(Integer userId, Integer roleId) throws DaoException, ServiceException;

	/**
	 * 分离用户角色
	 * @param userId 用户ID
	 * @param roleName 角色
	 * @throws ServiceException 异常信息
	 * @throws DaoException     异常信息
	 */
	void detachRoleFromUser(Integer userId, String roleName) throws DaoException, ServiceException;
}
