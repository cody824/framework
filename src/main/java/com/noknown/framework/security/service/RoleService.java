package com.noknown.framework.security.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.noknown.framework.common.exception.DAOException;
import com.noknown.framework.common.exception.ServiceException;
import com.noknown.framework.security.model.Role;

public interface RoleService {


	/**
	 * 增加角色
	 * @param roleName 角色英文名
	 * @param rolecname 角色中文名
	 * @param rolecomment 角色的说明
	 * @throws ServiceException
	 */
	public void createRole(String roleName, String comment) throws DAOException, ServiceException;
	
	/**
	 * 增加角色
	 * @param role 角色对象
	 * @throws ServiceException
	 */
	public void createRole(Role role) throws DAOException, ServiceException;
	
	/**
	 *修改角色 
	 * @param id   id字符串 如：【123456,789578】 
	 * @param role 角色对象
	 * @throws ServiceException
	 */
	public void modifyRole(Role role) throws DAOException, ServiceException;
	
	/**
	 *删除角色 (单个)
	 * @param id 要删除角色的id
	 * @throws ServiceException
 	 */
	public void destroyRole(Integer id) throws DAOException, ServiceException;

	/**
	 *删除角色 (多个)
	 * @param id 要删除角色的id
	 * @throws ServiceException
	 */
	public void destroyRole(List<Integer> ids) throws DAOException, ServiceException;
	
	/**
	 * 根据角色id取得角色
	 * @param id 要查找的角色的id
	 * @return 查找到的角色的实体
	 * @throws ServiceException
	 */
	public Role getRoleById(Integer id) throws DAOException, ServiceException;
	/**
	 * 根据角色中文名称取得角色
	 * @param name 要查找的角色的中文名称
	 * @return 查找到的角色的实体
	 * @throws ServiceException
	 */
	public Role getRoleByName(String name) throws DAOException, ServiceException;
	
	
	/**
	 * 分页查询
	 * @param page 第几页
	 * @param size 几条数据
	 * @return Page<Role>
	 * @throws DAOException 
	 */
	public Page<Role> getRoleByPage(int page, int size )  throws ServiceException, DAOException ;
	
	
	/**
	 * 赋予用户角色
	 * @param userId 组ID 字符串 如：【123456,56789】
	 * @param roleId 角色ID
	 * @return 无返回值
	 * @throws ServiceException 操作失败抛出异常
	 */
	public void  attachRoleForUser(Integer userId, Integer roleId) throws DAOException, ServiceException;
	
	/**
	 * 分离用户角色
	 * @param userId 用户ID 字符串 如：【123456,56789】
	 * @param roleId 角色ID
	 * @return 无返回值
	 * @throws ServiceException 操作失败抛出异常
	 */
	public void  detachRoleFromUser(Integer userId, Integer roleId) throws DAOException, ServiceException;
}
