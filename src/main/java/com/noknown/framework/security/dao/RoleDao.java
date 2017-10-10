package com.noknown.framework.security.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.noknown.framework.security.model.Role;

public interface RoleDao extends JpaRepository<Role, Integer>{

	Role findByName(String name);

}
