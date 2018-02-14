package com.noknown.framework.security.dao;

import com.noknown.framework.security.model.ThirdPartyAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author guodong
 */
public interface TpaDao extends JpaRepository<ThirdPartyAccount, Integer>{

	/**
	 * 获取用户所有第三方登录账号
	 *
	 * @param userId 用户ID
	 * @return 第三方登录账号列表
	 */
	List<ThirdPartyAccount> findByUserId(Integer userId);

	/**
	 * 获取用户某个类型的三方登录账号
	 * @param userId    用户ID
	 * @param accountType   第三方账户类型
	 * @return 第三方登录账号列表
	 */
	List<ThirdPartyAccount> findByUserIdAndAccountType(Integer userId, String accountType);

	/**
	 * 获取OpenId对应的第三方账户
	 * @param openId    OpenId
	 * @param accountType 第三方账户类型
	 * @return 第三方登录账号
	 */
	ThirdPartyAccount findByOpenIdAndAccountType(String openId, String accountType);

	/**
	 * 获取appId和unionId对应的微信账户
	 * @param appId 微信appId
	 * @param unionId   微信unionId
	 * @return  微信账户
	 */
	ThirdPartyAccount findByAppIdAndUnionId(String appId, String unionId);

	/**
	 * 获取appId和userId对应的微信账户
	 * @param appId 微信appId
	 * @param userId    用户ID
	 * @return  微信账户
	 */
	ThirdPartyAccount findByAppIdAndUserId(String appId, Integer userId);

	/**
	 * 获取用户所有的微信账户
	 * @param unionId   微信unionId
	 * @return  微信账户列表
	 */
	List<ThirdPartyAccount> findByUnionId(String unionId);
}
