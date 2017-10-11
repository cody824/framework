package com.noknown.framework.security.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.noknown.framework.common.exception.DAOException;
import com.noknown.framework.common.exception.ServiceException;
import com.noknown.framework.common.web.model.PageData;
import com.noknown.framework.common.web.model.SQLFilter;
import com.noknown.framework.security.model.ThirdPartyAccount;
import com.noknown.framework.security.model.User;
import com.noknown.framework.security.model.UserDetails;

public interface UserService extends UserDetailsService {


	User loginAuth(String userName, String password) throws ServiceException;
	
	/**
	 * 通过昵称查找用户
	 * 
	 * @param nick
	 * @return
	 */
	User findByNick(String nick);
	

	User findByMobile(String photo);

	UserDetails addUser(User user) throws ServiceException;
	
	User findById(Integer id);

	PageData<User> findBySQLFilter(SQLFilter sqlFilter, int start, int limit)throws ServiceException, DAOException;
	
	
	UserDetails addUserFromTpa(String tpaType, String tpaId, String avatar, String avatar_hd,
			String nickname) throws ServiceException, DAOException;
	
	UserDetails addUserFromWx(String wxId, String unionId, String openId, String avatar,
			String avatar_hd, String nickname) throws ServiceException, DAOException;
	
	UserDetails addUserFromTpa(ThirdPartyAccount tpa) throws ServiceException, DAOException;

	/**
	 * 绑定邮箱
	 * 
	 * @param userId
	 *            用户ID
	 * @param email
	 *            用户邮箱
	 * @return
	 * @since 2.2
	 * @throws ServiceException
	 */
	void bindEmail(Integer userId, String email) throws DAOException, ServiceException;

	/**
	 * 解绑邮箱
	 * 
	 * @param userId
	 *            用户ID
	 * @return
	 * @since 2.2
	 * @throws ServiceException
	 */
	void unbindEmail(Integer userId) throws DAOException, ServiceException;

	/**
	 * 绑定手机
	 * 
	 * @param userId
	 *            用户ID
	 * @param mobile
	 *            用户手机
	 * @return
	 * @since 2.2
	 * @throws ServiceException
	 */
	void bindMobile(Integer userId, String mobile) throws DAOException, ServiceException;

	/**
	 * 解绑手机
	 * 
	 * @param userId
	 *            用户ID
	 * @return
	 * @since 2.2
	 * @throws ServiceException
	 */
	void unbindMobile(Integer userId) throws DAOException, ServiceException;

	/**
	 * 绑定第三方账号
	 * 
	 * @param userId
	 *            用户ID
	 * @param tpaType
	 *            第三方账号类型 qq renren weibo
	 * @param tpaId
	 *            第三方账号ID
	 * @param avatar
	 *            头像
	 * @param avatar_hd
	 *            高清头像
	 * @param nickname
	 *            昵称
	 * @return
	 * @throws ServiceException
	 * @throws DAOException
	 */
	ThirdPartyAccount bindTpaAccout(Integer userId, String tpaType, String tpaId, String avatar, String avatar_hd,
			String nickname) throws ServiceException, DAOException;

	/**
	 * 绑定微信账号
	 * 
	 * @param userId
	 * @param wxId
	 * @param unionId
	 * @param openId
	 * @param avatar
	 * @param avatar_hd
	 * @param nickname
	 * @return
	 * @throws ServiceException
	 * @throws DAOException
	 */
	ThirdPartyAccount bindWxAccout(Integer userId, String wxId, String unionId, String openId, String avatar,
			String avatar_hd, String nickname) throws ServiceException, DAOException;



	void updateTpa(ThirdPartyAccount tpa)throws ServiceException, DAOException;
	
	/**
	 * 根据openId获取微信第三方绑定账号
	 * 
	 * @param wxId
	 * @param openId
	 * @return
	 * @throws ServiceException
	 * @throws DAOException
	 */
	ThirdPartyAccount getWxAccoutByOpenId(String openId) throws ServiceException, DAOException;

	/**
	 * 通过微信的unionId获取绑定账号
	 *
	 * @param unionId
	 *            唯一标示
	 * @return
	 * @throws ServiceException
	 * @throws DAOException
	 */
	List<ThirdPartyAccount> getWxAcountByUnionId(String unionId) throws ServiceException, DAOException;

	/**
	 * 根据openId获取微信第三方绑定账号
	 * 
	 * @param wxId
	 * @param unionId
	 * @return
	 * @throws ServiceException
	 * @throws DAOException
	 */
	ThirdPartyAccount getWxAccoutByUnionId(String wxId, String unionId) throws ServiceException, DAOException;

	/**
	 * 根据userId获取微信第三方绑定账号
	 * 
	 * @param wxId
	 * @param userId
	 * @return
	 * @throws ServiceException
	 * @throws DAOException
	 */
	ThirdPartyAccount getWxAccoutByUserId(String wxId, Integer userId) throws ServiceException, DAOException;

	/**
	 * 根据userId获取微信第三方绑定账号
	 * 
	 * @param wxId
	 * @param userId
	 * @return
	 * @throws ServiceException
	 * @throws DAOException
	 */
	List<ThirdPartyAccount> getWxAccoutByUserId(Integer userId) throws ServiceException, DAOException;

	/**
	 * 解绑第三方账号
	 * 
	 * @param userId
	 * @param tpaType
	 * @return
	 */
	void unbindTpaAccout(Integer userId, String tpaType) throws DAOException, ServiceException;

	/**
	 * 解绑第三方账号
	 * 
	 * @param openId
	 * @param tpaType
	 * @return
	 */
	void unbindTpaAccoutByOpenId(String openId, String tpaType) throws DAOException, ServiceException;

	/**
	 * 根据openId获取第三方绑定账号
	 * 
	 * @param openId
	 * @param tpaType
	 * @return
	 * @throws ServiceException
	 * @throws DAOException
	 */
	ThirdPartyAccount getThirdPartyAccountByOpenId(String openId, String tpaType) throws ServiceException, DAOException;

	/**
	 * 获取用户第三方登陆的绑定状态
	 * 
	 * @param userId
	 *            用户ID
	 * @param tpaType
	 *            用户ID
	 * @return 是否绑定
	 * @since 2.2
	 * @throws ServiceException
	 */
	ThirdPartyAccount getThirdPartyAccount(Integer userId, String tpaType) throws DAOException, ServiceException;

	/**
	 * 获取用户第三方账号绑定列表
	 * 
	 * @param userId
	 * @return
	 * @throws DAOException
	 * @throws ServiceException
	 */
	List<ThirdPartyAccount> getThirdPartyList(Integer userId) throws DAOException, ServiceException;

	void resetUsersPasswd(List<Integer> userIds) throws DAOException, ServiceException;

	void deleteUsersByName(List<String> userNames)throws DAOException, ServiceException;

	void unlockUsersByName(List<String> userNames)throws DAOException, ServiceException;

	void lockUsersByName(List<String> userNames)throws DAOException, ServiceException;

	void lockUsersById(List<Integer> userIds)throws DAOException, ServiceException;

	void unlockUsersById(List<Integer> userIds)throws DAOException, ServiceException;

	void updateUserPasswd(Integer userId, String oldPassword, String newPassword)throws DAOException, ServiceException;

	void updateNick(Integer userId, String name)throws DAOException, ServiceException;



}
