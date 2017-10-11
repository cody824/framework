package com.noknown.framework.security.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.noknown.framework.security.model.User;

public interface UserDao extends JpaRepository<User, Integer> ,JpaSpecificationExecutor<User>{

	User findByNick(String nick);
	
	User findByMobile(String mobile);
	
	User findByEmail(String email);
}
