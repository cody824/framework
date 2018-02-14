package com.noknown.framework.security.dao;

import com.noknown.framework.security.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * @author guodong
 */
public interface UserDao extends JpaRepository<User, Integer> ,JpaSpecificationExecutor<User>{

	/**
	 * 通过昵称找用户
	 *
	 * @param nick 昵称
	 * @return 用户
	 */
	User findByNick(String nick);

	/**
	 * 通过手机号查找用户
	 * @param mobile    手机号
	 * @return  用户
	 */
	User findByMobile(String mobile);

	/**
	 * 通过Email查找用户
	 * @param email email
	 * @return  用户
	 */
	User findByEmail(String email);

	/**
	 * 通过角色名查找用户
	 * @param name 角色名
	 * @return  用户列表
	 */
	@EntityGraph(attributePaths = {"roles"})
	List<User> findAllByRoles_Name(String name);
}
