package com.noknown.framework.common.dao;

import java.util.List;

import com.noknown.framework.common.exception.DAOException;

public interface ObjectStoreDao {
	
	/**
	 * 获取对象
	 * @param path
	 * @param key
	 * @return
	 * @throws DAOException
	 */
	Object getObjectByKey(String path, String key) throws DAOException;
	
	/**
	 * 获取对象
	 * @param path
	 * @param key
	 * @param clazz
	 * @return
	 * @throws DAOException
	 */
	Object getObjectByKey(String path, String key, Class<?> clazz) throws DAOException;

	
	/**
	 * 获取对象列表
	 * @param path
	 * @param c
	 * @return
	 * @throws DAOException
	 */
	List<Object> getObjectList(String path, Class<?> c) throws DAOException;
	
	/**
	 * 保存对象
	 * @param path
	 * @param key
	 * @param obj
	 * @throws DAOException
	 */
	String saveObject (String path, String key, Object obj) throws DAOException;
	
	/**
	 * 删除对象
	 * @param path
	 * @param key
	 * @return
	 * @throws DAOException
	 */
	boolean removeObject(String path, String key) throws DAOException;

}
