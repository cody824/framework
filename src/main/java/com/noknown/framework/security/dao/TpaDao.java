package com.noknown.framework.security.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.noknown.framework.security.model.ThirdPartyAccount;


public interface TpaDao extends JpaRepository<ThirdPartyAccount, Integer>{

	List<ThirdPartyAccount> findByUserId(Integer userId);
	
	List<ThirdPartyAccount> findByUserIdAndAccountType(Integer userId, String accountType);
	
	ThirdPartyAccount findByOpenIdAndAccountType(String openId, String accountType);
	
	ThirdPartyAccount findByAppIdAndUnionId(String appId, String unionId);
	
	ThirdPartyAccount findByAppIdAndUserId(String appId, Integer userId);
	
	List<ThirdPartyAccount> findByUnionId(String unionId);
}
