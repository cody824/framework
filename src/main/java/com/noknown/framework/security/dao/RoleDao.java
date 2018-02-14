package com.noknown.framework.security.dao;

import com.noknown.framework.security.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author guodong
 */
public interface RoleDao extends JpaRepository<Role, Integer>,JpaSpecificationExecutor<Role> {

	/**
	 * 通过名称获取角色
	 *
	 * @param name 名称
	 * @return 角色
	 */
	Role findByName(String name);

}
