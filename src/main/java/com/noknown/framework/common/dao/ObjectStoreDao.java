package com.noknown.framework.common.dao;

import com.noknown.framework.common.exception.DaoException;

import java.util.List;

/**
 * @author guodong
 */
public interface ObjectStoreDao {

	/**
	 * 获取对象
	 * @param path
	 * @param key
	 * @return
	 * @throws DaoException
	 */
	Object getObjectByKey(String path, String key) throws DaoException;

	/**
	 * 获取对象
	 * @param path
	 * @param key
	 * @param clazz
	 * @return
	 * @throws DaoException
	 */
	Object getObjectByKey(String path, String key, Class<?> clazz) throws DaoException;


	/**
	 * 获取对象列表
	 * @param path
	 * @param c
	 * @return
	 * @throws DaoException
	 */
	List<Object> getObjectList(String path, Class<?> c) throws DaoException;

	/**
	 * 保存对象
	 * @param path
	 * @param key
	 * @param obj
	 * @return
	 * @throws DaoException
	 */
	String saveObject(String path, String key, Object obj) throws DaoException;

	/**
	 * 删除对象
	 * @param path
	 * @param key
	 * @return
	 * @throws DaoException
	 */
	boolean removeObject(String path, String key) throws DaoException;

}
