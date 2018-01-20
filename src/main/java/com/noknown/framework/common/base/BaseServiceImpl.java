package com.noknown.framework.common.base;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;


import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import com.noknown.framework.common.util.JsonUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.noknown.framework.common.exception.DAOException;
import com.noknown.framework.common.exception.ServiceException;
import com.noknown.framework.common.util.JpaUtil;
import com.noknown.framework.common.web.model.PageData;
import com.noknown.framework.common.web.model.SQLFilter;


@Transactional
public abstract  class BaseServiceImpl<T, ID extends Serializable> implements BaseService<T, ID> {
	
	protected Class<T> clazz;
	
	@SuppressWarnings("unchecked")
	public BaseServiceImpl() {
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
	
	public abstract JpaRepository<T, ID> getRepository();
	
	public abstract JpaSpecificationExecutor<T> getSpecificationExecutor();

	@Override
	public PageData<T> find(SQLFilter filter, int start, int limit) {
		Pageable pageable = new PageRequest(start / limit, limit);
		Specification<T> spec = (root, query, cb) -> {
			return JpaUtil.sqlFilterToPredicate(clazz, root, query, cb, filter);
        };
		Page<T> pd = getSpecificationExecutor().findAll(spec , pageable);
		
		PageData<T> pageData = new PageData<>();
		pageData.setTotal(pd.getTotalElements());
		pageData.setTotalPage(pd.getTotalPages());
		pageData.setData(pd.getContent());
		pageData.setStart(start);
		pageData.setLimit(limit);
		return pageData;
	}

	@Override
	public T get(ID entityid) throws DAOException, ServiceException {
		return getRepository().findOne(entityid);
	}


	@SuppressWarnings("unchecked")
	@Override
	public T get(String attrName, Object attrValue) {
		T t = null;
		Field f;
		try {
			f = clazz.getField(attrName);
			Method getMethod = getRepository().getClass().getMethod(
					"findBy" + attrName.substring(0, 1).toUpperCase()
							+ attrName.substring(1), f.getType());
			if (getMethod != null) {
				t = (T) getMethod.invoke(getRepository(), attrValue);
			}
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return t;
	}
	@Override
	public void delete(@SuppressWarnings("unchecked") ID ... entityids) {
		for (ID id : entityids) {
			getRepository().delete(id);
		}
	}

	@Override
	public void delete(Collection<T> objs) {
		getRepository().delete(objs);
	}

	@Override
	public void delete(T obj)  {
		getRepository().delete(obj);
	}

	@Override
	public void update(T obj) {
		getRepository().save(obj);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<T> find(String attrName, Object attrValue) {
		Collection<T> col = null;
		Field f;
		try {
			f = clazz.getField(attrName);
			Method getMethod = getRepository().getClass().getMethod(
					"findBy" + attrName.substring(0, 1).toUpperCase()
							+ attrName.substring(1), f.getType());
			if (getMethod != null) {
				col = (Collection<T>) getMethod.invoke(getRepository(), attrValue);
			}
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return col;
	}

	@Override
	public Collection<T> find() {
		return getRepository().findAll();
	}

	@Override
	public Collection<T> find(SQLFilter filter) {
		Specification<T> spec = new Specification<T>(){

			@Override
			public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return JpaUtil.sqlFilterToPredicate(clazz, root, query, cb, filter);
			}} ;
		return getSpecificationExecutor().findAll(spec);
	}

	@Override
	public PageData<T> find(int start, int limit) {
		Pageable pageable = new PageRequest(start / limit, limit);
	
		Page<T> pd = getRepository().findAll(pageable);
		
		PageData<T> pageData = new PageData<>();
		pageData.setTotal(pd.getTotalElements());
		pageData.setTotalPage(pd.getTotalPages());
		pageData.setData(pd.getContent());
		pageData.setStart(start);
		pageData.setLimit(limit);
		return pageData;
	}

	@Override
	public long getCount(SQLFilter filter) {
		Specification<T> spec = new Specification<T>(){

			@Override
			public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return JpaUtil.sqlFilterToPredicate(clazz, root, query, cb, filter);
			}} ;
		return getSpecificationExecutor().count(spec);
	}

}
