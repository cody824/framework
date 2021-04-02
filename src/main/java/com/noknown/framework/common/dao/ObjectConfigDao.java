package com.noknown.framework.common.dao;

import com.noknown.framework.common.exception.DaoException;

import java.util.List;

/**
 * @author guodong
 */
public interface ObjectConfigDao {

	/**
	 * 获取对象
	 * @param key
	 * @return
	 * @throws DaoException
	 */
	Object getObjectByKey(String key) throws DaoException;

	/**
	 * 获取对象
	 * @param key
	 * @param clazz
	 * @return
	 * @throws DaoException
	 */
	Object getObjectByKey(String key, Class<?> clazz) throws DaoException;


	/**
	 * 保存对象
	 * @param key
	 * @param obj
	 * @return
	 * @throws DaoException
	 */
	String saveObject(String key, Object obj) throws DaoException;

	/**
	 * 删除对象
	 * @param key
	 * @return
	 * @throws DaoException
	 */
	boolean removeObject(String key) throws DaoException;

}
