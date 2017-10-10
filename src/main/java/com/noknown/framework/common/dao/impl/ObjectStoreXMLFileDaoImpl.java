package com.noknown.framework.common.dao.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.noknown.framework.common.dao.ObjectStoreDao;
import com.noknown.framework.common.exception.DAOException;
import com.noknown.framework.common.util.XmlUtil;

public abstract class ObjectStoreXMLFileDaoImpl implements ObjectStoreDao{
	

	private  final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public Object getObjectByKey(String path, String key) throws DAOException {
		return getObjectByKey(path, key, Object.class);
	}

	@Override
	public Object getObjectByKey(String path, String key, Class<?> clazz) throws DAOException {
		Object object = null;
		String filePath = getBasePath() + File.separator + path
				+ File.separator + key;
		try {
			object = XmlUtil.readObjectFromXmlFile(filePath, clazz);
		} catch (Exception e) {
			throw new DAOException(e);
		}
		return object;
	}

	@Override
	public List<Object> getObjectList(String path, Class<?> c) throws DAOException {
		List<Object> list = new ArrayList<Object>();
		Object object = null;
		String filePath = getBasePath() + File.separator + path
				+ File.separator;
		File dir = new File(filePath);
		if (dir.exists() && dir.isDirectory()) {
			File[] files = dir.listFiles();
			for (File file : files) {
				try {
					object = XmlUtil.readObjectFromXmlFile(file.getPath(), c);
					list.add(object);
				} catch (Exception e) {
					logger.warn(e.getLocalizedMessage());
				}
			}
		}
		return list;
	}

	@Override
	public String saveObject(String path, String key, Object obj) throws DAOException {
		String filePath = getBasePath() + File.separator + path
				+ File.separator + key;
		
		try {
			XmlUtil.writeToXmlFile(filePath, obj);
			return filePath;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	@Override
	public boolean removeObject(String path, String key) throws DAOException {
		String filePath = getBasePath() + File.separator + path
				+ File.separator + key;
		boolean ret = false;
		File file = new File(filePath);
		if (file.isFile()) {
			ret = file.delete();
		}
		return ret;
	}
	
	public abstract String getBasePath();

}
