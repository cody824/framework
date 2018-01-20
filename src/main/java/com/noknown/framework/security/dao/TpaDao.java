package com.noknown.framework.security.dao;

import com.noknown.framework.security.model.ThirdPartyAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface TpaDao extends JpaRepository<ThirdPartyAccount, Integer>{

	List<ThirdPartyAccount> findByUserId(Integer userId);
	
	List<ThirdPartyAccount> findByUserIdAndAccountType(Integer userId, String accountType);
	
	ThirdPartyAccount findByOpenIdAndAccountType(String openId, String accountType);
	
	ThirdPartyAccount findByAppIdAndUnionId(String appId, String unionId);
	
	ThirdPartyAccount findByAppIdAndUserId(String appId, Integer userId);
	
	List<ThirdPartyAccount> findByUnionId(String unionId);
}
