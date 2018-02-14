package com.noknown.framework.common.base;

import com.noknown.framework.common.exception.DaoException;

import java.util.List;

/**
 * @param <T> 配置对象类型
 * @author guodong
 */
public interface BaseObjRepo<T extends BaseObj> {

	/**
	 * 获取对象
	 *
	 * @param key key
	 * @return 对象
	 * @throws DaoException 异常
	 */
	T get(String key) throws DaoException;

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
	 * @param key key
	 * @throws DaoException 异常
	 */
	void delete(String key) throws DaoException;

	/**
	 * 获取全部对象
	 *
	 * @return 全部对象
	 * @throws DaoException 异常
	 */
	List<T> findAll() throws DaoException;
}
