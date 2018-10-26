package com.noknown.framework.security.service.impl;

import com.noknown.framework.common.base.BaseServiceImpl;
import com.noknown.framework.common.exception.DaoException;
import com.noknown.framework.common.exception.ServiceException;
import com.noknown.framework.security.dao.GroupDao;
import com.noknown.framework.security.dao.UserDao;
import com.noknown.framework.security.model.Group;
import com.noknown.framework.security.model.User;
import com.noknown.framework.security.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * @author guodong
 */
@Service
@Transactional(rollbackOn = Exception.class)
public class GroupServiceImpl extends BaseServiceImpl<Group, Integer> implements GroupService {

	private final UserDao userDao;

	private final GroupDao groupDao;

	@Autowired
	public GroupServiceImpl(UserDao userDao, GroupDao groupDao) {
		this.userDao = userDao;
		this.groupDao = groupDao;
	}

	@Override
	public JpaRepository<Group, Integer> getRepository() {
		return groupDao;
	}

	@Override
	public JpaSpecificationExecutor<Group> getSpecificationExecutor() {
		return groupDao;
	}

	@Override
	public Group create(String name, String comment, Integer parentGroupId) throws ServiceException {
		Group group = getGroupByName(name);
		if (group != null) {
			throw new ServiceException(name + "已经存在");
		}
		group = new Group().setName(name).setComment(comment).setParentGroupId(parentGroupId);
		groupDao.save(group);
		return group;
	}

	@Override
	public Group getGroupByName(String name) {
		return groupDao.findByName(name);
	}

	@Override
	public void addUser(Integer userId, Integer groupId) throws DaoException, ServiceException {
		User user = userDao.findById(userId).orElse(null);
		if (user == null) {
			throw new ServiceException("用户不存在");
		}

		Group group = groupDao.findById(groupId).orElse(null);
		if (group == null) {
			throw new ServiceException("组不存在");
		}

		user.addGroup(group);
		userDao.save(user);
	}

	@Override
	public void removeUser(Integer userId, Integer groupId) throws DaoException, ServiceException {
		User user = userDao.findById(userId).orElse(null);
		if (user == null) {
			throw new ServiceException("用户不存在");
		}

		Group group = groupDao.findById(groupId).orElse(null);
		if (group == null) {
			throw new ServiceException("组不存在");
		}
		user.removeGroup(group);
		userDao.save(user);
	}
}
