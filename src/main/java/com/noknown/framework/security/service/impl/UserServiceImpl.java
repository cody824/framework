package com.noknown.framework.security.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.noknown.framework.common.exception.DAOException;
import com.noknown.framework.common.exception.ServiceException;
import com.noknown.framework.common.util.BaseUtil;
import com.noknown.framework.common.util.RegexValidateUtil;
import com.noknown.framework.security.dao.TpaDao;
import com.noknown.framework.security.dao.UserDao;
import com.noknown.framework.security.model.ThirdPartyAccount;
import com.noknown.framework.security.model.User;
import com.noknown.framework.security.service.UserService;


public abstract class UserServiceImpl implements UserService {

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private TpaDao tpaDao;
	
	@Autowired
	private PasswordEncoder pswdEncoder;

	@Override
	public User findByNick(String nick) {
		return userDao.findByNick(nick);
	}
	
	@Override
	public User findById(Integer id) {
		return userDao.findOne(id);
	}
	

	@Override
	public User loginAuth(String userName, String password) throws ServiceException {
		User user = (User) loadUserByUsername(userName);
		if (user != null){
			if (!pswdEncoder.matches(password, user.getPassword())){
				throw new ServiceException("密码错误");
			}
		}
		return user;
	}


	@Override
	public com.noknown.framework.security.model.UserDetails addUser(User userToAdd) throws ServiceException{
		final String username = userToAdd.getUsername();
        if(userDao.findByNick(username)!=null) {
            throw new ServiceException("用户名已经存在，请更换用户名");
        }
        final String mobile = userToAdd.getMobile();
        if (mobile != null && userDao.findByMobile(mobile) != null){
        	throw new ServiceException("手机号码已经被绑定，请更换手机号");
        }
        final String email = userToAdd.getEmail();
        if (email != null && userDao.findByEmail(email) != null){
        	throw new ServiceException("邮箱已经被绑定，请更换邮箱");
        }
        final String rawPassword = userToAdd.getPassword();
        userToAdd.setPassword(pswdEncoder.encode(rawPassword));
        userToAdd.setLastPasswordResetDate(new Date());
        userToAdd.setCreateDate(new Date());
        userToAdd = userDao.save(userToAdd);
		return null;
	}
	
	protected User addUserFromTpaBase(String tpaType, String tpaId, String avatar, String avatar_hd,
			String nickname) throws ServiceException, DAOException {
		User user = new User();
		user.setNick(tpaType + BaseUtil.getUUID());
		user.setCreateDate(new Date());
		user.setPassword(pswdEncoder.encode(tpaId));
		user.setLastPasswordResetDate(new Date());
		user = userDao.save(user);
		this.bindTpaAccout(user.getId(), tpaType, tpaId, avatar, avatar_hd, nickname);
		return user;
	}
	
	protected User addUserFromWxBase(String wxId, String unionId, String openId, String avatar,
			String avatar_hd, String nickname) throws ServiceException, DAOException {
		User user = new User();
		user.setNick("wechat" + BaseUtil.getUUID());
		user.setCreateDate(new Date());
		user.setPassword(pswdEncoder.encode(openId));
		user.setLastPasswordResetDate(new Date());
		user = userDao.save(user);
		this.bindWxAccout(user.getId(), wxId, unionId, openId, avatar, avatar_hd, nickname);
		return user;
	}
	

	protected User addUserFromTpaBase(ThirdPartyAccount tpa) {
		User user = new User();
		user.setNick(tpa.getAccountType() + BaseUtil.getUUID());
		user.setCreateDate(new Date());
		user.setPassword(pswdEncoder.encode(tpa.getOpenId()));
		user.setLastPasswordResetDate(new Date());
		user = userDao.save(user);
		tpa.setUserId(user.getId());
		tpaDao.save(tpa);
		return user;
	}

	@Override
	public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
		User user = null;
		boolean isMobile = RegexValidateUtil.checkMobile(name);
		boolean isEmail = RegexValidateUtil.checkEmail(name);
		if (isMobile) {
			user = userDao.findByMobile(name);
		} else if(isEmail) {
			user = userDao.findByEmail(name);
		} else {
			user = userDao.findByNick(name);
		}
		if (user == null) {
			throw new UsernameNotFoundException(String.format("User with username=%s was not found", name));
		}
		return user;
	}

	@Override
	public void bindEmail(Integer userId, String email) throws DAOException, ServiceException {
		User user = userDao.getOne(userId);
		if (user == null)
			throw new ServiceException("用户[" + userId + "]不存在");
		User user2 = userDao.findByEmail(email);
		if (user2 != null && !user2.getId().equals(user.getId()))
			throw new ServiceException("电子邮箱[" + email + "]已经被其他账户绑定，请尝试其他邮箱或者通过找回密码功能找回绑定账户密码!");
		user.setEmail(email);
		userDao.save(user);
	}

	@Override
	public void unbindEmail(Integer userId) throws DAOException, ServiceException {
		User user = userDao.getOne(userId);
		if (user == null)
			throw new ServiceException("用户[" + userId + "]不存在");
		user.setEmail(null);
		userDao.save(user);
		
	}

	@Override
	public void bindMobile(Integer userId, String mobile) throws DAOException, ServiceException {
		User user = userDao.getOne(userId);
		if (user == null)
			throw new ServiceException("用户[" + userId + "]不存在");
		User user2 = userDao.findByMobile(mobile);
		if (user2 != null && !user2.getId().equals(user.getId()))
			throw new ServiceException("手机号[" + mobile + "]已经被其他账户绑定，请尝试其他手机号或者通过找回密码功能找回绑定账户密码!");
		user.setMobile(mobile);
		userDao.save(user);
	}

	@Override
	public void unbindMobile(Integer userId) throws DAOException, ServiceException {
		User user = userDao.getOne(userId);
		if (user == null)
			throw new ServiceException("用户[" + userId + "]不存在");
		user.setMobile(null);
		userDao.save(user);
	}

	@Override
	public ThirdPartyAccount bindTpaAccout(Integer userId, String tpaType, String tpaId, String avatar,
			String avatar_hd, String nickname) throws ServiceException, DAOException {
		ThirdPartyAccount tpa = null;
		tpa = getThirdPartyAccount(userId, tpaType);
		if (tpa == null) {
			tpa = new ThirdPartyAccount();
		}
		tpa.setOpenId(tpaId);
		tpa.setAccountType(tpaType);
		tpa.setUserId(userId);
		tpa.setAvatar(avatar);
		tpa.setAvatar_hd(avatar_hd);
		tpa.setNickname(nickname);
		tpaDao.save(tpa);
		return tpa;
	}

	@Override
	public ThirdPartyAccount bindWxAccout(Integer userId, String wxId, String unionId, String openId, String avatar,
			String avatar_hd, String nickname) throws ServiceException, DAOException {
		ThirdPartyAccount tpa = null;
		tpa = this.getWxAccoutByOpenId(openId);
		if (tpa == null) {
			tpa = new ThirdPartyAccount();
		}
		tpa.setOpenId(openId);
		tpa.setAccountType("wechat");
		tpa.setUnionId(unionId);
		tpa.setAppId(wxId);
		tpa.setUserId(userId);
		tpa.setAvatar(avatar);
		tpa.setAvatar_hd(avatar_hd);
		tpa.setNickname(nickname);
		tpaDao.save(tpa);
		return tpa;
	}

	@Override
	public ThirdPartyAccount getWxAccoutByOpenId(String openId) throws ServiceException, DAOException {
		ThirdPartyAccount tpa = tpaDao.findByOpenIdAndAccountType(openId, "wechat");
		return tpa;
	}

	@Override
	public ThirdPartyAccount getWxAccoutByUnionId(String wxId, String unionId) throws ServiceException, DAOException {
		return tpaDao.findByAppIdAndUnionId(wxId, unionId);
	}

	@Override
	public ThirdPartyAccount getWxAccoutByUserId(String wxId, Integer userId) throws ServiceException, DAOException {
		return tpaDao.findByAppIdAndUserId(wxId, userId);
	}
	

	@Override
	public List<ThirdPartyAccount> getWxAccoutByUserId(Integer userId) throws ServiceException, DAOException {
		return tpaDao.findByUserIdAndAccountType(userId, "wechat");
	}

	@Override
	public List<ThirdPartyAccount> getWxAcountByUnionId(String unionId) throws ServiceException, DAOException {
		return tpaDao.findByUnionId(unionId);
	}

	@Override
	public void unbindTpaAccout(Integer userId, String tpaType) throws DAOException, ServiceException {
		ThirdPartyAccount tpa = null;

		tpa = getThirdPartyAccount(userId, tpaType);
		if (tpa != null) {
			tpaDao.delete(tpa);
		}
	}

	@Override
	public ThirdPartyAccount getThirdPartyAccount(Integer userId, String type) throws DAOException, ServiceException {
		List<ThirdPartyAccount> list = tpaDao.findByUserIdAndAccountType(userId, type);
		if (list != null && list.size() > 0){
			return list.get(0);
		}
		return null;
	}
	
	@Override
	public ThirdPartyAccount getThirdPartyAccountByOpenId(String openId, String tpaType) throws ServiceException, DAOException {
		return tpaDao.findByOpenIdAndAccountType(openId, tpaType);
	}

	@Override
	public List<ThirdPartyAccount> getThirdPartyList(Integer userId) throws DAOException, ServiceException {
		return tpaDao.findByUserId(userId);
	}


	@Override
	public void unbindTpaAccoutByOpenId(String openId, String tpaType) throws DAOException, ServiceException {
		ThirdPartyAccount tpa = null;
		tpa = tpaDao.findByOpenIdAndAccountType(openId, tpaType);
		if (tpa != null) {
			tpaDao.delete(tpa);
		}
	}
	

	@Override
	public User findByMobile(String photo) {
		return userDao.findByMobile(photo);
	}

	@Override
	public void updateTpa(ThirdPartyAccount tpa) throws ServiceException, DAOException {
		tpaDao.save(tpa);
	}


}
