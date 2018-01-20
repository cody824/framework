package com.noknown.framework.common.base;

import com.noknown.framework.common.exception.DAOException;
import com.noknown.framework.common.exception.ServiceException;
import com.noknown.framework.common.web.model.PageData;
import com.noknown.framework.common.web.model.SQLFilter;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public interface BaseObjRepo<T extends BaseObj> {

	T get(String key) throws DAOException;

	void save(T t) throws DAOException;

	void delete(String key) throws DAOException;

	List<T> findAll() throws DAOException;
}
