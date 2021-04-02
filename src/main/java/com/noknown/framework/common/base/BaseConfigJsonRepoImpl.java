package com.noknown.framework.common.base;

import com.noknown.framework.common.dao.impl.AbstractObjectConfigJsonFileDaoImpl;
import com.noknown.framework.common.exception.DaoException;
import org.springframework.beans.factory.annotation.Value;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @param <T> 对象类型
 * @author guodong
 */
public abstract class BaseConfigJsonRepoImpl<T> extends AbstractObjectConfigJsonFileDaoImpl implements BaseConfigRepo<T> {

	private static final String CLASSPATH = "classpath";


	protected Class<T> clazz;

	@Value("${framework.objectStore.basePath}")
	private String defaultBasePath;

	public BaseConfigJsonRepoImpl() {
		@SuppressWarnings("rawtypes")
		Class clazz = getClass();

		while (clazz != Object.class) {
			Type t = clazz.getGenericSuperclass();
			if (t instanceof ParameterizedType) {
				Type[] args = ((ParameterizedType) t).getActualTypeArguments();
				if (args[0] instanceof Class) {
					this.clazz = (Class<T>) args[0];
					break;
				}
			}
			clazz = clazz.getSuperclass();
		}
	}

	@Override
	public T get() throws DaoException {
		return (T) getObjectByKey(getKey() + ".json", clazz);
	}

	@Override
	public void save(T t) throws DaoException {
		this.saveObject(getKey() + ".json", t);
	}

	@Override
	public void delete() {
		this.removeObject(getKey() + ".json");
	}

	public abstract String getKey();

}
