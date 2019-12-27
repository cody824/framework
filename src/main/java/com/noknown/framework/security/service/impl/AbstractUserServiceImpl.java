package com.noknown.framework.security.service.impl;

import com.noknown.framework.common.base.BaseServiceImpl;
import com.noknown.framework.common.exception.DaoException;
import com.noknown.framework.common.exception.ServiceException;
import com.noknown.framework.common.util.BaseUtil;
import com.noknown.framework.common.util.ObjectUtil;
import com.noknown.framework.common.util.RegexValidateUtil;
import com.noknown.framework.common.util.StringUtil;
import com.noknown.framework.security.dao.TpaDao;
import com.noknown.framework.security.dao.UserDao;
import com.noknown.framework.security.model.BaseUserDetails;
import com.noknown.framework.security.model.ThirdPartyAccount;
import com.noknown.framework.security.model.User;
import com.noknown.framework.security.pojo.UserWarpForReg;
import com.noknown.framework.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.List;

/**
 * @author guodong
 */
public abstract class AbstractUserServiceImpl extends BaseServiceImpl<User, Integer> implements UserService {

	@Autowired
	private UserDao userDao;

	@Autowired
	private TpaDao tpaDao;
	
	@Autowired
	private PasswordEncoder pswdEncoder;
	
	@Value("${security.auth.email:true}")
	private boolean emailAuth;
	
	@Value("${security.auth.mobile:true}")
	private boolean mobileAuth;

	@Override
	public JpaRepository<User, Integer> getRepository() {
		return userDao;
	}

	@Override
	public JpaSpecificationExecutor<User> getSpecificationExecutor() {
		return userDao;
	}

	@Override
	public User findByNick(String nick) {
		return userDao.findByNick(nick);
	}

	@Override
	public User findByEmail(String email) {
		return userDao.findByEmail(email);
	}


	@Override
	public User loginAuth(String userName, String password) throws ServiceException {
		User user = (User) loadUserByUsername(userName);
		if (user != null){
			if (!user.isEnabled()) {
				throw new ServiceException("用户被禁用，无法登陆");
			}
			if (!pswdEncoder.matches(password, user.getPassword())){
				throw new ServiceException("密码错误");
			}
		}
		return user;
	}


	protected BaseUserDetails addUser(UserWarpForReg userToAdd, Class<? extends BaseUserDetails> clazz) throws ServiceException {
		User user = new User();
		final String username = userToAdd.getUsername();
		BaseUserDetails udDetails = null;
        if(userDao.findByNick(username)!=null) {
            throw new ServiceException("用户名已经存在，请更换用户名");
        }
		user.setNick(username);
        final String mobile = userToAdd.getMobile();
        if (mobile != null && userDao.findByMobile(mobile) != null){
        	throw new ServiceException("手机号码已经被绑定，请更换手机号");
        }
		user.setMobile(mobile);
        final String email = userToAdd.getEmail();
        if (email != null && userDao.findByEmail(email) != null){
        	throw new ServiceException("邮箱已经被绑定，请更换邮箱");
        }
		user.setEmail(email);
        final String rawPassword = userToAdd.getPassword();
		user.setPassword(pswdEncoder.encode(rawPassword));
		user.setLastPasswordResetDate(new Date());
		user.setCreateDate(new Date());
		userDao.save(user);

		try {
			udDetails = clazz.newInstance();
			udDetails.setId(user.getId());
			udDetails.setNick(user.getNick());
			if (StringUtil.isNotBlank(user.getMobile())) {
				udDetails.setMobile(user.getMobile());
			}
			if (StringUtil.isNotBlank(user.getEmail())) {
				udDetails.setEmail(user.getEmail());
			}
			if (userToAdd.getParams() != null) {
				ObjectUtil.assignByMap(udDetails, userToAdd.getParams());
			}
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return udDetails;
	}

	protected BaseUserDetails addUserFromTpaBase(String tpaType, String tpaId, String avatar, String avatarHd,
	                                             String nickname, Class<? extends BaseUserDetails> clazz) {
		User user = new User();
		user.setNick(tpaType + BaseUtil.getUUID());
		user.setCreateDate(new Date());
		user.setPassword(pswdEncoder.encode(tpaId));
		user.setLastPasswordResetDate(new Date());
		user = userDao.save(user);
		ThirdPartyAccount tpa = this.bindTpaAccout(user.getId(), tpaType, tpaId, avatar, avatarHd, nickname);
		return buildUD(user, tpa, clazz);
	}

	protected BaseUserDetails addUserFromWxBase(String wxId, String unionId, String openId, String avatar,
	                                            String avatarHd, String nickname, Class<? extends BaseUserDetails> clazz) {
		User user = new User();
		user.setNick("wechat" + BaseUtil.getUUID());
		user.setCreateDate(new Date());
		user.setPassword(pswdEncoder.encode(nickname));
		user.setLastPasswordResetDate(new Date());
		user = userDao.save(user);
		ThirdPartyAccount tpa = this.bindWxAccout(user.getId(), wxId, unionId, openId, avatar, avatarHd, nickname);
		return buildUD(user, tpa, clazz);
	}


	protected BaseUserDetails addUserFromTpaBase(ThirdPartyAccount tpa, Class<? extends BaseUserDetails> clazz) {
		User user = new User();
		user.setNick(tpa.getAccountType() + BaseUtil.getUUID());
		user.setCreateDate(new Date());
		user.setPassword(pswdEncoder.encode(tpa.getOpenId()));
		user.setLastPasswordResetDate(new Date());
		user = userDao.save(user);
		tpa.setUserId(user.getId());
		tpaDao.save(tpa);
		return buildUD(user, tpa, clazz);
	}

	private BaseUserDetails buildUD(User user, ThirdPartyAccount tpa, Class<? extends BaseUserDetails> clazz) {
		BaseUserDetails udDetails = null;
		try {
			udDetails = clazz.newInstance();
			udDetails.setId(user.getId());
			udDetails.setNick(user.getNick());
			if (StringUtil.isNotBlank(user.getMobile())) {
				udDetails.setMobile(user.getMobile());
			}
			if (StringUtil.isNotBlank(user.getEmail())) {
				udDetails.setEmail(user.getEmail());
			}
			udDetails.setAvatar(tpa.getAvatar());
			udDetails.setAvatarHd(tpa.getAvatarHd());
			udDetails.setFullName(tpa.getNickname());
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return udDetails;
	}

	@Override
	public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
		User user;
		boolean isMobile = RegexValidateUtil.checkMobile(name);
		boolean isEmail = RegexValidateUtil.checkEmail(name);
		if (isMobile && mobileAuth) {
			user = userDao.findByMobile(name);
		} else if(isEmail && emailAuth) {
			user = userDao.findByEmail(name);
		} else {
			user = userDao.findByNick(name);
		}
		if (user == null) {
			throw new UsernameNotFoundException(String.format("用户%s不存在", name));
		}
		return user;
	}

	@Override
	public void bindEmail(Integer userId, String email) throws DaoException, ServiceException {
		User user = userDao.getOne(userId);
		if (user == null) {
			throw new ServiceException("用户[" + userId + "]不存在");
		}
		User user2 = userDao.findByEmail(email);
		if (user2 != null && !user2.getId().equals(user.getId())) {
			throw new ServiceException("电子邮箱[" + email + "]已经被其他账户绑定，请尝试其他邮箱或者通过找回密码功能找回绑定账户密码!");
		}
		user.setEmail(email);
		userDao.save(user);
	}

	@Override
	public void unbindEmail(Integer userId) throws DaoException, ServiceException {
		User user = userDao.getOne(userId);
		if (user == null) {
			throw new ServiceException("用户[" + userId + "]不存在");
		}
		user.setEmail(null);
		userDao.save(user);
		
	}

	@Override
	public void bindMobile(Integer userId, String mobile) throws DaoException, ServiceException {
		User user = userDao.getOne(userId);
		if (user == null) {
			throw new ServiceException("用户[" + userId + "]不存在");
		}
		User user2 = userDao.findByMobile(mobile);
		if (user2 != null && !user2.getId().equals(user.getId())) {
			throw new ServiceException("手机号[" + mobile + "]已经被其他账户绑定，请尝试其他手机号或者通过找回密码功能找回绑定账户密码!");
		}
		user.setMobile(mobile);
		userDao.save(user);
	}

	@Override
	public void unbindMobile(Integer userId) throws DaoException, ServiceException {
		User user = userDao.getOne(userId);
		if (user == null) {
			throw new ServiceException("用户[" + userId + "]不存在");
		}
		user.setMobile(null);
		userDao.save(user);
	}

	@Override
	public ThirdPartyAccount bindTpaAccout(Integer userId, String tpaType, String tpaId, String avatar,
	                                       String avatarHd, String nickname) {
		ThirdPartyAccount tpa;
		tpa = getThirdPartyAccount(userId, tpaType);
		if (tpa == null) {
			tpa = new ThirdPartyAccount();
		}
		tpa.setOpenId(tpaId);
		tpa.setAccountType(tpaType);
		tpa.setUserId(userId);
		tpa.setAvatar(avatar);
		tpa.setAvatarHd(avatarHd);
		tpa.setNickname(nickname);
		tpaDao.save(tpa);
		return tpa;
	}

	@Override
	public ThirdPartyAccount bindWxAccout(Integer userId, String wxId, String unionId, String openId, String avatar,
	                                      String avatarHd, String nickname) {
		ThirdPartyAccount tpa;
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
		tpa.setAvatarHd(avatarHd);
		tpa.setNickname(nickname);
		tpaDao.save(tpa);
		return tpa;
	}

	@Override
	public ThirdPartyAccount getWxAccoutByOpenId(String openId) {
		return tpaDao.findByOpenIdAndAccountType(openId, "wechat");
	}

	@Override
	public ThirdPartyAccount getWxAccoutByUnionId(String wxId, String unionId) {
		return tpaDao.findByAppIdAndUnionId(wxId, unionId);
	}

	@Override
	public ThirdPartyAccount getWxAccoutByUserId(String wxId, Integer userId) {
		return tpaDao.findByAppIdAndUserId(wxId, userId);
	}
	

	@Override
	public List<ThirdPartyAccount> getWxAccoutByUserId(Integer userId) {
		return tpaDao.findByUserIdAndAccountType(userId, "wechat");
	}

	@Override
	public List<ThirdPartyAccount> getWxAcountByUnionId(String unionId) {
		return tpaDao.findByUnionId(unionId);
	}

	@Override
	public void unbindTpaAccout(Integer userId, String tpaType) {
		ThirdPartyAccount tpa = getThirdPartyAccount(userId, tpaType);
		if (tpa != null) {
			tpaDao.delete(tpa);
		}
	}

	@Override
	public ThirdPartyAccount getThirdPartyAccount(Integer userId, String type) {
		List<ThirdPartyAccount> list = tpaDao.findByUserIdAndAccountType(userId, type);
		if (list != null && list.size() > 0){
			return list.get(0);
		}
		return null;
	}
	
	@Override
	public ThirdPartyAccount getThirdPartyAccountByOpenId(String openId, String tpaType) {
		return tpaDao.findByOpenIdAndAccountType(openId, tpaType);
	}

	@Override
	public List<ThirdPartyAccount> getThirdPartyList(Integer userId) {
		return tpaDao.findByUserId(userId);
	}


	@Override
	public void unbindTpaAccoutByOpenId(String openId, String tpaType) {
		ThirdPartyAccount tpa = tpaDao.findByOpenIdAndAccountType(openId, tpaType);
		if (tpa != null) {
			tpaDao.delete(tpa);
		}
	}
	

	@Override
	public User findByMobile(String photo) {
		return userDao.findByMobile(photo);
	}

	@Override
	public void updateTpa(ThirdPartyAccount tpa) {
		tpaDao.save(tpa);
	}

	@Override
	public void resetUserPasswd(String identity, String password) {
		User user;
		boolean isMobile = RegexValidateUtil.checkMobile(identity);
		boolean isEmail = RegexValidateUtil.checkEmail(identity);
		if (isMobile) {
			user = userDao.findByMobile(identity);
		} else if(isEmail) {
			user = userDao.findByEmail(identity);
		} else {
			user = userDao.findByNick(identity);
		}
		if (user == null) {
			throw new UsernameNotFoundException(String.format("用户%s找不到", identity));
		}
		user.setPassword(pswdEncoder.encode(password));
		userDao.save(user);
	}

	@Override
	public void resetUsersPasswd(List<Integer> userIds) throws ServiceException {
		for (Integer userId : userIds) {
			User user = userDao.findById(userId).orElse(null);
			if (user == null) {
				throw new ServiceException("用户ID:" + userId + " 对应的用户不存在");
			}

			String password = pswdEncoder.encode("123456");
			user.setPassword(password);
			userDao.save(user);
		}
		
	}

	@Override
	public void deleteUsersByName(List<String> userNames) throws ServiceException {
		User user;
		for (String name : userNames) {
			user = userDao.findByNick(name);
			if (user == null) {
				throw new ServiceException("用户:" + name + " 不存在");
			}
			userDao.delete(user);
		}
		
	}

	@Override
	public void unlockUsersByName(List<String> userNames) {
		for (String name : userNames) {
			User user = userDao.findByNick(name);
			if (user != null) {
				user.setEnable(true);
				userDao.save(user);
			}
		}
		
	}

	@Override
	public void lockUsersByName(List<String> userNames) {
		for (String name : userNames) {
			User user = userDao.findByNick(name);
			if (user != null) {
				user.setEnable(false);
				userDao.save(user);
			}
		}
		
	}

	@Override
	public void lockUsersById(List<Integer> userSIds) {
		for (Integer userId : userSIds) {
			User user = userDao.findById(userId).orElse(null);
			if (user != null) {
				user.setEnable(false);
				userDao.save(user);
			}
		}
		
	}

	@Override
	public void unlockUsersById(List<Integer> userSIds) {
		for (Integer userId : userSIds) {
			User user = userDao.findById(userId).orElse(null);
			if (user != null) {
				user.setEnable(true);
				userDao.save(user);
			}
		}
		
	}

	@Override
	public void updateUserPasswd(Integer userId, String oldPassword, String newPassword) throws ServiceException {
		User user = userDao.findById(userId).orElse(null);
		if (user != null){
			if (!pswdEncoder.matches(oldPassword, user.getPassword())){
				throw new ServiceException("密码错误");
			}
			user.setLastPasswordResetDate(new Date());
			user.setPassword(pswdEncoder.encode(newPassword));
			userDao.save(user);
		}
	}

	@Override
	public void updateNick(Integer userId, String name) throws ServiceException {
		User user = userDao.findById(userId).orElse(null);
		if (user == null){
			throw new ServiceException("用户不存在");
		}
		if (user.getNickOk()) {
			throw new ServiceException("昵称只能修改一次，您不可以再修改昵称");
		}
		User existUser = userDao.findByNick(name);
		if (existUser != null) {
			throw new ServiceException(name + "已经被使用，请换一个昵称");
		}
		
		user.setNick(name);
		userDao.save(user);
	}

	@Override
	public List<User> findUserByRoleName(String roleName) {
		return userDao.findAllByRoles_Name(roleName);
	}


	@Override
	public List<User> findUserByGroupName(String groupName) {
		return userDao.findAllByGroups_Name(groupName);
	}

	@Override
	public List<User> findUserByGroupId(Integer groupId) {
		return userDao.findAllByGroups_Id(groupId);
	}

}
