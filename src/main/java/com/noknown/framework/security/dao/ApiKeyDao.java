package com.noknown.framework.security.dao;

import com.noknown.framework.security.model.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author guodong
 */
public interface ApiKeyDao extends JpaRepository<ApiKey, String>, JpaSpecificationExecutor<ApiKey> {

	/**
	 * 获取用户的apiKey
	 *
	 * @param userId 用户ID
	 * @return apiKey
	 */
	ApiKey findByUserId(Integer userId);

}
