package com.noknown.framework.common.base;

import com.noknown.framework.common.exception.DaoException;

import java.util.List;

/**
 * @param <T> 配置对象类型
 * @author guodong
 */
public interface BaseConfigRepo<T> {

	/**
	 * 获取对象
	 *
	 * @return 对象
	 * @throws DaoException 异常
	 */
	T get() throws DaoException;

	/**
	 * 保存对象
	 *
	 * @param t 对象
	 * @throws DaoException 异常
	 */
	void save(T t) throws DaoException;

	/**
	 * 删除对象
	 *
	 * @throws DaoException 异常
	 */
	void delete() throws DaoException;
}
