package com.noknown.framework.common.base;

import com.noknown.framework.common.dao.impl.AbstractObjectStoreJSONFileDaoImpl;
import com.noknown.framework.common.exception.DAOException;
import com.noknown.framework.common.util.BaseUtil;
import org.springframework.beans.factory.annotation.Value;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BaseObjJsonRepoImpl<T extends BaseObj> extends AbstractObjectStoreJSONFileDaoImpl implements BaseObjRepo<T> {

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


    @Override
    public T get(String key) throws DAOException {
        return (T) getObjectByKey(clazz.getSimpleName(), key + ".json", clazz);
    }

    @Override
    public void save(T t) throws DAOException {
        String key = t.getKey();
        this.saveObject(clazz.getSimpleName(), key + ".json", t);
    }

    @Override
    public void delete(String key) throws DAOException {
        this.removeObject(clazz.getSimpleName(), key);
    }

    @Override
    public List<T> findAll() throws DAOException {
        List<Object> objects = this.getObjectList(clazz.getSimpleName(), clazz);
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
        if (defaultBasePath.startsWith("classpath")) {
            defaultBasePath = BaseUtil.getClassPath()
                    + defaultBasePath.substring(defaultBasePath.indexOf(":") + 1);
        }
        return defaultBasePath;
    }
}
