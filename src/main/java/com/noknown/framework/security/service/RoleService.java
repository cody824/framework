package com.noknown.framework.security.service;

import java.io.Serializable;
import java.util.List;

import com.noknown.framework.common.exception.DAOException;
import com.noknown.framework.common.exception.ServiceException;
import com.noknown.framework.security.model.Role;

public interface RoleService {


	/**
	 * 增加角色
	 * @param roleename 角色英文名
	 * @param rolecname 角色中文名
	 * @param rolecomment 角色的说明
	 * @param type 角色类型，如: system/userdefine
	 * @throws ServiceException
	 */
	public void createRole(String roleename,String rolecname,
			String rolecomment,String type,int status) throws DAOException, ServiceException;
	
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
	public void modifyRole(Serializable id,Role role) throws DAOException, ServiceException;
	
	/**
	 *删除角色 (单个)
	 * @param id 要删除角色的id
	 * @throws ServiceException
 	 */
	public void destroyRole(Serializable id) throws DAOException, ServiceException;

	/**
	 *删除角色 (多个)
	 * @param id 要删除角色的id
	 * @throws ServiceException
	 */
	public void destroyRole(List<Serializable> ids) throws DAOException, ServiceException;
	
	/**
	 * 根据角色id取得角色
	 * @param id 要查找的角色的id
	 * @return 查找到的角色的实体
	 * @throws ServiceException
	 */
	public Role getRoleById(Serializable id) throws DAOException, ServiceException;
	/**
	 * 根据角色中文名称取得角色
	 * @param name 要查找的角色的中文名称
	 * @return 查找到的角色的实体
	 * @throws ServiceException
	 */
	public Role getRoleByName(String name) throws DAOException, ServiceException;
	
	/**
	 * 根据角色是否有效取得角色
	 * @param valid 要查找角色是否有效的标志位
	 * @return 查找到的角色的集合
	 */
	public List<Role> getRoleByStatus(int status)throws DAOException, ServiceException;
	
	
	/**
	 * 分页查询
	 * @param page 第几页
	 * @param size 几条数据
	 * @return List<Role>
	 * @throws DAOException 
	 */
	public List<Role> getRoleByPage(int page, int size )  throws ServiceException, DAOException ;
	
     /**
      * 获取所有角色数量
      * @return int 角色数量
      * @throws ServiceException 
      */
	public int roleCount() throws DAOException, ServiceException;
	
	 /**
     * 获取赋予用户的角色
     * @param uid 用户ID
     * @param type 角色的类型，如果获取全部类型，type为null即可
     * @param containGroup 是否获取用户所在组的角色
     * @return List 用户拥有的角色列表
     * @throws ServiceException 
     */
	public List<Role> getUserRoleList(Serializable uid, String type, String domain, boolean containGroup) throws DAOException, ServiceException;
	
	 /**
     * 判断用户是否拥有角色
     * @param userId 用户ID
     * @param roleId 角色ID
     * @param containGroup 是否获取用户所在组的角色
     * @return true拥有，false不拥有
     * @throws ServiceException 操作失败抛出异常 
     */
	public boolean checkUserRole(Serializable  userId, Serializable roleId, String domain, boolean containGroup) throws DAOException, ServiceException;
	
	 /**
     * 判断用户是否拥有角色
     * @param userId 用户ID
     * @param roleName 角色名称
     * @param domain 所在域
     * @param containGroup 是否获取用户所在组的角色
     * @return true拥有，false不拥有
     * @throws ServiceException 操作失败抛出异常 
     */
	public boolean checkUserRole(Serializable userId, String roleName, String domain, boolean containGroup)throws DAOException, ServiceException;
	
	 /**
     * 判断组是否拥有角色
     * @param groupId 组ID
     * @param roleId 角色ID
     * @return true拥有，false不拥有
     * @throws ServiceException 操作失败抛出异常 
     */
	public boolean checkGroupRole(Serializable  groupId, Serializable roleId) throws DAOException, ServiceException;
	
	/**
	 * 赋予组角色
	 * @param groupId 组ID 字符串 如：【123456,56789】
	 * @param roleId 角色ID
	 * @return 无返回值
	 * @throws ServiceException 操作失败抛出异常
	 */
	public void  attachRoleForGroup(Serializable groupId, Serializable roleId) throws DAOException, ServiceException;
	
	/**
	 * 分离组角色
	 * @param groupId 组ID 字符串 如：【123456,56789】
	 * @param roleId 角色ID
	 * @return 无返回值
	 * @throws ServiceException 操作失败抛出异常
	 */
	public void  detachRoleFromGroup(Serializable groupId, Serializable roleId) throws DAOException, ServiceException;
	
	/**
	 * 赋予用户角色
	 * @param userId 组ID 字符串 如：【123456,56789】
	 * @param roleId 角色ID
	 * @return 无返回值
	 * @throws ServiceException 操作失败抛出异常
	 */
	public void  attachRoleForUser(Serializable userId, Serializable roleId) throws DAOException, ServiceException;
	
	/**
	 * 分离用户角色
	 * @param userId 用户ID 字符串 如：【123456,56789】
	 * @param roleId 角色ID
	 * @return 无返回值
	 * @throws ServiceException 操作失败抛出异常
	 */
	public void  detachRoleFromUser(Serializable userId, Serializable roleId) throws DAOException, ServiceException;
	
	/**
	 * 根据用户id通过关联表得到角色对象集合
	 * @param userId 用户id
	 * @return 角色对象集合
	 * @throws ServiceException
	 */
	public List<Role> getRolesByUserId(Serializable userId) throws DAOException, ServiceException;
	
	/**
	 * 通过小组ID得到拥有的角色集合
	 * @param groupId 小组Id
	 * @return 角色集合
	 */
	public List<Role> getRoleListByGroupId(Serializable groupId) throws DAOException, ServiceException;
	
	/**
	 * 得到当前未选中的角色集合
	 * @param id 小组ID
	 * @return 角色集合
	 * @throws ServiceException
	 */
	public List<Role> getNoUsedRoleList(Serializable id) throws DAOException, ServiceException;
	/**
	 * 根据角色时候激活得到角色集合
	 * @param status
	 * @return
	 * @throws ServiceException
	 */
	public List<Role> getRoleList(int status,int min,int max) throws DAOException, ServiceException;
	/**
	 * 给岗位添加角色
	 * @param groupId 岗位id
	 * @param roleId 前台选定的角色id
	 * @throws ServiceException
	 */
	public void  attachGroupForRole(Serializable groupId, String roleId,String roleIdList) throws DAOException, ServiceException;
	/**
	 * 根据组的Id得到相关角色的Id
	 * @param groupId
	 * @return
	 * @throws ServiceException
	 */
	public List<Serializable> getRoleIdByGroupId(Serializable groupId) throws DAOException, ServiceException;
	/**
	 * 根据用户的账号id和角色的英文名称判断用户是否拥有这个角色
	 * @param userId 用户的账号id
	 * @param name 角色的英文名称
	 * @return true 有权限， false 无权限
	 * @throws ServiceException
	 */
	public boolean checkGetUserIdAndRoleName(Serializable userId,String name) throws DAOException, ServiceException;
}
