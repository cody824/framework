package com.noknown.framework.security.dao;

import com.noknown.framework.security.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author guodong
 */
public interface GroupDao extends JpaRepository<Group, Integer>, JpaSpecificationExecutor<Group> {

	/**
	 * 根据名称获取组
	 *
	 * @param name
	 * @return
	 */
	Group findByName(String name);
}
