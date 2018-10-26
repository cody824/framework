package com.noknown.framework.security.service;

import com.noknown.framework.common.base.BaseService;
import com.noknown.framework.common.exception.DaoException;
import com.noknown.framework.common.exception.ServiceException;
import com.noknown.framework.security.model.BaseUserDetails;
import com.noknown.framework.security.model.ThirdPartyAccount;
import com.noknown.framework.security.model.User;
import com.noknown.framework.security.pojo.UserWarpForReg;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

/**
 * @author guodong
 */
public interface UserService extends UserDetailsService, BaseService<User, Integer>{

	/**
	 * 登录验证
	 *
	 * @param userName 用户名
	 * @param password 密码
	 * @return 登录用户
	 * @throws ServiceException 异常信息
	 */
	User loginAuth(String userName, String password) throws ServiceException;

	/**
	 * 通过昵称查找用户
	 *
	 * @param nick  昵称
	 * @return 用户
	 */
	User findByNick(String nick);

	/**
	 * 通过手机号查找用户
	 * @param photo 手机号
	 * @return 用户
	 */
	User findByMobile(String photo);

	/**
	 * 通过email查找用户
	 *
	 * @param email 电子邮件
	 * @return 用户
	 */
	User findByEmail(String email);

	/**
	 * 注册用户
	 *
	 * @param user 用户
	 * @return 用户详情
	 * @throws ServiceException 异常信息
	 */
	BaseUserDetails addUser(UserWarpForReg user) throws ServiceException;

	/**
	 * 第三方登录注册用户
	 *
	 * @param tpa 第三方账户
	 * @return 用户详情
	 * @throws ServiceException 异常信息
	 * @throws DaoException     异常信息
	 */
	BaseUserDetails addUserFromTpa(ThirdPartyAccount tpa) throws ServiceException, DaoException;

	/**
	 * 第三方登录注册用户
	 *
	 * @param tpaType  第三方类型
	 * @param tpaId    openId
	 * @param avatar   头像
	 * @param avatarHd 高清头像
	 * @param nickname 昵称
	 * @return 用户详情
	 * @throws ServiceException 异常信息
	 * @throws DaoException     异常信息
	 */
	BaseUserDetails addUserFromTpa(String tpaType, String tpaId, String avatar, String avatarHd,
	                               String nickname) throws ServiceException, DaoException;

	/**
	 * 微信注册用户给
	 *
	 * @param wxId     微信公众号ID
	 * @param unionId  unionId
	 * @param openId   openId
	 * @param avatar   头像
	 * @param avatarHd 高清头像
	 * @param nickname 昵称
	 * @return 用户详情
	 * @throws ServiceException 异常信息
	 * @throws DaoException     异常信息
	 */
	BaseUserDetails addUserFromWx(String wxId, String unionId, String openId, String avatar,
	                              String avatarHd, String nickname) throws ServiceException, DaoException;

	/**
	 * 绑定邮箱
	 *
	 * @param userId
	 *            用户ID
	 * @param email
	 *            用户邮箱
	 * @throws ServiceException 异常信息
	 * @throws DaoException     异常信息
	 */
	void bindEmail(Integer userId, String email) throws DaoException, ServiceException;

	/**
	 * 解绑邮箱
	 *
	 * @param userId
	 *            用户ID
	 * @throws ServiceException 异常信息
	 * @throws DaoException     异常信息
	 */
	void unbindEmail(Integer userId) throws DaoException, ServiceException;

	/**
	 * 绑定手机
	 *
	 * @param userId
	 *            用户ID
	 * @param mobile
	 *            用户手机
	 * @throws ServiceException 异常信息
	 * @throws DaoException     异常信息
	 */
	void bindMobile(Integer userId, String mobile) throws DaoException, ServiceException;

	/**
	 * 解绑手机
	 *
	 * @param userId
	 *            用户ID
	 * @throws ServiceException 异常信息
	 * @throws DaoException     异常信息
	 */
	void unbindMobile(Integer userId) throws DaoException, ServiceException;

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
	 * @param avatarHd
	 *            高清头像
	 * @param nickname
	 *            昵称
	 * @return 第三方账号
	 * @throws ServiceException 异常信息
	 * @throws DaoException     异常信息
	 */
	ThirdPartyAccount bindTpaAccout(Integer userId, String tpaType, String tpaId, String avatar, String avatarHd,
	                                String nickname) throws ServiceException, DaoException;

	/**
	 * 绑定微信账号
	 *
	 * @param userId    用户ID
	 * @param wxId      微信公众号ID
	 * @param unionId   unionId
	 * @param openId    openId
	 * @param avatar
	 *            头像
	 * @param avatarHd
	 *            高清头像
	 * @param nickname
	 *            昵称
	 * @return      第三方账号
	 * @throws ServiceException 异常信息
	 * @throws DaoException     异常信息
	 */
	ThirdPartyAccount bindWxAccout(Integer userId, String wxId, String unionId, String openId, String avatar,
	                               String avatarHd, String nickname) throws ServiceException, DaoException;


	/**
	 * 更新第三方登录账号
	 * @param tpa   第三方登录账号
	 * @throws ServiceException 异常信息
	 * @throws DaoException     异常信息
	 */
	void updateTpa(ThirdPartyAccount tpa) throws ServiceException, DaoException;

	/**
	 * 根据openId获取微信第三方绑定账号
	 *
	 * @param openId    openId
	 * @return 第三方登录账号
	 * @throws ServiceException 异常信息
	 * @throws DaoException     异常信息
	 */
	ThirdPartyAccount getWxAccoutByOpenId(String openId) throws ServiceException, DaoException;

	/**
	 * 通过微信的unionId获取绑定账号
	 *
	 * @param unionId
	 *            唯一标示
	 * @return  第三方登录账户列表
	 * @throws ServiceException 异常信息
	 * @throws DaoException     异常信息
	 */
	List<ThirdPartyAccount> getWxAcountByUnionId(String unionId) throws ServiceException, DaoException;

	/**
	 * 根据openId获取微信第三方绑定账号
	 *
	 * @param wxId  微信公众号ID
	 * @param unionId   unionId
	 * @return 第三方登录账户
	 * @throws ServiceException 异常信息
	 * @throws DaoException     异常信息
	 */
	ThirdPartyAccount getWxAccoutByUnionId(String wxId, String unionId) throws ServiceException, DaoException;

	/**
	 * 根据userId获取微信第三方绑定账号
	 *
	 * @param wxId  微信公众号ID
	 * @param userId 用户ID
	 * @return 第三方登录账户
	 * @throws ServiceException 异常信息
	 * @throws DaoException     异常信息
	 */
	ThirdPartyAccount getWxAccoutByUserId(String wxId, Integer userId) throws ServiceException, DaoException;

	/**
	 * 根据userId获取微信第三方绑定账号
	 *
	 * @param userId    用户ID
	 * @return  第三方登录账户列表
	 * @throws ServiceException 异常信息
	 * @throws DaoException     异常信息
	 */
	List<ThirdPartyAccount> getWxAccoutByUserId(Integer userId) throws ServiceException, DaoException;

	/**
	 * 解绑第三方账号
	 *
	 * @param userId    用户ID
	 * @param tpaType   第三方账户类型
	 * @throws ServiceException 异常信息
	 * @throws DaoException     异常信息
	 */
	void unbindTpaAccout(Integer userId, String tpaType) throws DaoException, ServiceException;

	/**
	 * 解绑第三方账号
	 *
	 * @param openId    openId
	 * @param tpaType   第三方账户类型
	 * @throws ServiceException 异常信息
	 * @throws DaoException     异常信息
	 */
	void unbindTpaAccoutByOpenId(String openId, String tpaType) throws DaoException, ServiceException;

	/**
	 * 根据openId获取第三方绑定账号
	 *
	 * @param openId    openId
	 * @param tpaType   第三方账户类型
	 * @return 第三方登录账户
	 * @throws ServiceException 异常信息
	 * @throws DaoException     异常信息
	 */
	ThirdPartyAccount getThirdPartyAccountByOpenId(String openId, String tpaType) throws ServiceException, DaoException;

	/**
	 * 根据用户ID获取第三方绑定账号
	 *
	 * @param userId
	 *            用户ID
	 * @param tpaType
	 *            用户ID
	 * @return 第三方登录账户
	 * @throws ServiceException 异常信息
	 * @throws DaoException     异常信息
	 */
	ThirdPartyAccount getThirdPartyAccount(Integer userId, String tpaType) throws DaoException, ServiceException;

	/**
	 * 获取用户第三方账号绑定列表
	 *
	 * @param userId    用户ID
	 * @return  第三方登录账户列表
	 * @throws ServiceException 异常信息
	 * @throws DaoException     异常信息
	 */
	List<ThirdPartyAccount> getThirdPartyList(Integer userId) throws DaoException, ServiceException;

	/**
	 * 修改密码
	 * @param identity nick/mobile/email
	 * @param password  新密码
	 * @throws ServiceException 异常信息
	 * @throws DaoException     异常信息
	 */
	void resetUserPasswd(String identity, String password) throws DaoException, ServiceException;

	/**
	 * 批量重置密码
	 * @param userIds   用户ID
	 * @throws ServiceException 异常信息
	 * @throws DaoException     异常信息
	 */
	void resetUsersPasswd(List<Integer> userIds) throws DaoException, ServiceException;

	/**
	 * 批量删除用户
	 * @param userNames 用户昵称
	 * @throws ServiceException 异常信息
	 * @throws DaoException     异常信息
	 */
	void deleteUsersByName(List<String> userNames) throws DaoException, ServiceException;

	/**
	 * 批量解锁用户
	 * @param userNames 用户昵称
	 * @throws ServiceException 异常信息
	 * @throws DaoException     异常信息
	 */
	void unlockUsersByName(List<String> userNames) throws DaoException, ServiceException;

	/**
	 * 批量锁定用户
	 * @param userNames 用户昵称
	 * @throws ServiceException 异常信息
	 * @throws DaoException     异常信息
	 */
	void lockUsersByName(List<String> userNames) throws DaoException, ServiceException;

	/**
	 * 批量锁定用户
	 * @param userIds 用户ID
	 * @throws ServiceException 异常信息
	 * @throws DaoException     异常信息
	 */
	void lockUsersById(List<Integer> userIds) throws DaoException, ServiceException;

	/**
	 * 批量解锁用户
	 * @param userIds 用户ID
	 * @throws ServiceException 异常信息
	 * @throws DaoException     异常信息
	 */
	void unlockUsersById(List<Integer> userIds) throws DaoException, ServiceException;

	/**
	 * 修改密码
	 * @param userId    用户ID
	 * @param oldPassword   旧密码
	 * @param newPassword   新密码
	 * @throws ServiceException 异常信息
	 * @throws DaoException     异常信息
	 */
	void updateUserPasswd(Integer userId, String oldPassword, String newPassword) throws DaoException, ServiceException;

	/**
	 * 更新昵称 只有一次更新机会
	 * @param userId    用户ID
	 * @param name      昵称
	 * @throws ServiceException 异常信息
	 * @throws DaoException     异常信息
	 */
	void updateNick(Integer userId, String name) throws DaoException, ServiceException;

	/**
	 * 获取角色的所有用户
	 * @param roleName  角色名
	 * @return 用户列表
	 * @throws DaoException     异常信息
	 * @throws ServiceException 异常信息
	 */
	List<User> findUserByRoleName(String roleName) throws DaoException, ServiceException;

	/**
	 * 获取组中的所有用户
	 *
	 * @param groupName 组名
	 * @return 用户列表
	 * @throws DaoException     异常信息
	 * @throws ServiceException 异常信息
	 */
	List<User> findUserByGroupName(String groupName) throws DaoException, ServiceException;

	/**
	 * 获取组中的所有用户
	 *
	 * @param groupId 组ID
	 * @return 用户列表
	 * @throws DaoException     异常信息
	 * @throws ServiceException 异常信息
	 */
	List<User> findUserByGroupId(Integer groupId) throws DaoException, ServiceException;
}
