package com.noknown.framework.security.service.impl;

import com.noknown.framework.common.base.BaseServiceImpl;
import com.noknown.framework.common.exception.ServiceException;
import com.noknown.framework.security.dao.RoleDao;
import com.noknown.framework.security.dao.UserDao;
import com.noknown.framework.security.model.Role;
import com.noknown.framework.security.model.User;
import com.noknown.framework.security.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author guodong
 */
@Service
@Transactional(rollbackOn = Exception.class)
public  class RoleServiceImpl extends BaseServiceImpl<Role, Integer> implements RoleService {

	private final UserDao userDao;

	private final RoleDao roleDao;

	@Autowired
	public RoleServiceImpl(UserDao userDao, RoleDao roleDao) {
		this.userDao = userDao;
		this.roleDao = roleDao;
	}

	@Override
	public void createRole(String roleName, String comment) {
		Role role = new Role();
		role.setName(roleName);
		role.setComment(comment);
		createRole(role);
	}

	@Override
	public void createRole(Role role) {
		roleDao.save(role);
	}

	@Override
	public void modifyRole(Role role) {
		roleDao.save(role);
	}

	@Override
	public void destroyRole(Integer id) {
		roleDao.delete(id);
	}

	@Override
	public void destroyRole(List<Integer> ids) {
		for (Integer idInteger : ids) {
			roleDao.delete(idInteger);
		}

	}

	@Override
	public Role getRoleById(Integer id) {
		return roleDao.findOne(id);
	}

	@Override
	public Role getRoleByName(String name) {
		return roleDao.findByName(name);
	}

	@Override
	public Page<Role> getRoleByPage(int page, int size) {
		Pageable pageable = new PageRequest(page, size);
		return roleDao.findAll(pageable);
	}

	@Override
	public void attachRoleForUser(Integer userId, Integer roleId) throws ServiceException {
		User user = userDao.findOne(userId);
		if (user == null) {
			throw new ServiceException("用户不存在");
		}

		Role role = roleDao.findOne(roleId);
		if (role == null) {
			throw new ServiceException("角色不存在");
		}

		user.addRole(role);
		userDao.save(user);

	}

	@Override
	public void attachRoleForUser(Integer userId, String roleName) throws ServiceException {
		User user = userDao.findOne(userId);
		if (user == null) {
			throw new ServiceException("用户不存在");
		}

		Role role = roleDao.findByName(roleName);
		if (role == null) {
			throw new ServiceException("角色不存在");
		}

		user.addRole(role);
		userDao.save(user);
	}

	@Override
	public void detachRoleFromUser(Integer userId, Integer roleId) throws ServiceException {
		User user = userDao.findOne(userId);
		if (user == null) {
			throw new ServiceException("用户不存在");
		}

		Role role = roleDao.findOne(roleId);
		if (role == null) {
			throw new ServiceException("角色不存在");
		}

		user.removeRole(role);
		userDao.save(user);

	}

	@Override
	public void detachRoleFromUser(Integer userId, String roleName) throws ServiceException {
		User user = userDao.findOne(userId);
		if (user == null) {
			throw new ServiceException("用户不存在");
		}

		Role role = roleDao.findByName(roleName);
		if (role == null) {
			throw new ServiceException("角色不存在");
		}

		user.removeRole(role);
		userDao.save(user);
	}


	@Override
	public JpaRepository<Role, Integer> getRepository() {
		return roleDao;
	}

	@Override
	public JpaSpecificationExecutor<Role> getSpecificationExecutor() {
		return roleDao;
	}

}
