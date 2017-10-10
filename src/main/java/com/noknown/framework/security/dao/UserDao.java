package com.noknown.framework.security.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.noknown.framework.security.model.User;

public interface UserDao extends JpaRepository<User, Integer>{

	User findByNick(String nick);
	
	User findByMobile(String mobile);
	
	User findByEmail(String email);
}
