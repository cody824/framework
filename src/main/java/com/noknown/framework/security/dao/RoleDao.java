package com.noknown.framework.security.dao;

import com.noknown.framework.security.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RoleDao extends JpaRepository<Role, Integer>,JpaSpecificationExecutor<Role> {

	Role findByName(String name);

}
