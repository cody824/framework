package com.noknown.framework.common.base;

import com.noknown.framework.common.dao.impl.AbstractObjectStoreJsonFileDaoImpl;
import com.noknown.framework.common.exception.DaoException;
import com.noknown.framework.common.util.BaseUtil;
import org.springframework.beans.factory.annotation.Value;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @param <T> 对象类型
 * @author guodong
 */
public class BaseObjJsonRepoImpl<T extends BaseObj> extends AbstractObjectStoreJsonFileDaoImpl implements BaseObjRepo<T> {

	private static final String CLASSPATH = "classpath";


    protected Class<T> clazz;

    @Value("${framework.objectStore.basePath}")
    private String defaultBasePath;

    public BaseObjJsonRepoImpl() {
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

	protected String configName() {
		return clazz.getSimpleName();
	}


    @Override
    public T get(String key) throws DaoException {
	    return (T) getObjectByKey(configName(), key + ".json", clazz);
    }

    @Override
    public void save(T t) throws DaoException {
	    String key = t.getKey();
	    this.saveObject(configName(), key + ".json", t);
    }

    @Override
    public void delete(String key) {
	    this.removeObject(configName(), key + ".json");
    }

    @Override
    public List<T> findAll() {
	    List<Object> objects = this.getObjectList(configName(), clazz);
        List<T> rets = new ArrayList<>();
        for (Object object : objects){
            if (clazz.isInstance(object)) {
                rets.add((T)object);
            }
        }
        return rets;
    }

    @Override
    public String getDefaultBasePath() {
	    if (defaultBasePath.startsWith(CLASSPATH)) {
		    defaultBasePath = BaseUtil.getClassPath()
                    + defaultBasePath.substring(defaultBasePath.indexOf(":") + 1);
        }
        return defaultBasePath;
    }
}
