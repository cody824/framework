package com.noknown.framework.security.dao;

import com.noknown.framework.security.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserDao extends JpaRepository<User, Integer> ,JpaSpecificationExecutor<User>{

	User findByNick(String nick);
	
	User findByMobile(String mobile);
	
	User findByEmail(String email);
}
