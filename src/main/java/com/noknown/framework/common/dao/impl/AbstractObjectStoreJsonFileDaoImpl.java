package com.noknown.framework.common.dao.impl;

import com.noknown.framework.common.dao.ObjectStoreDao;
import com.noknown.framework.common.exception.DaoException;
import com.noknown.framework.common.util.JsonFileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author guodong
 */
public abstract class AbstractObjectStoreJsonFileDaoImpl implements ObjectStoreDao {

	private  final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public Object getObjectByKey(String path, String key) throws DaoException {
		return getObjectByKey(path, key, Object.class);
	}

	@Override
	public Object getObjectByKey(String path, String key, Class<?> clazz) throws DaoException {
		Object object;
		String filePath = getDefaultBasePath() + File.separator + path
				+ File.separator + key;
		try {
			object = JsonFileUtil.readObjectFromJsonFile(filePath, clazz);
		} catch (Exception e) {
			throw new DaoException(e);
		}
		return object;
	}

	@Override
	public List<Object> getObjectList(String path, Class<?> c) {
		List<Object> list = new ArrayList<>();
		Object object;
		String filePath = getDefaultBasePath() + File.separator + path
				+ File.separator;
		File dir = new File(filePath);
		if (dir.exists() && dir.isDirectory()) {
			File[] files = dir.listFiles();
			if (files != null){
				for (File file : files) {
					try {
						object = JsonFileUtil.readObjectFromJsonFile(file.getPath(), c);
						list.add(object);
					} catch (Exception e) {
						logger.warn(e.getLocalizedMessage());
					}
				}
			}
		}
		return list;
	}

	@Override
	public String saveObject(String path, String key, Object obj) throws DaoException {
		String filePath = getDefaultBasePath() + File.separator + path
				+ File.separator + key;
		try {
			JsonFileUtil.writeToJsonFile(filePath, obj);
			return filePath;
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

	@Override
	public boolean removeObject(String path, String key) {
		String filePath = getDefaultBasePath() + File.separator + path
				+ File.separator + key;
		boolean ret = false;
		File file = new File(filePath);
		if (file.exists() && file.isFile()) {
			ret = file.delete();
		}
		return ret;
	}

	/**
	 * 获取基础路径
	 *
	 * @return 默认路径
	 */
	public abstract String getDefaultBasePath();

}
