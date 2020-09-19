package com.noknown.framework.common.base;

import com.noknown.framework.common.exception.DaoException;
import com.noknown.framework.common.exception.ServiceException;
import com.noknown.framework.common.util.JpaUtil;
import com.noknown.framework.common.util.ObjectUtil;
import com.noknown.framework.common.web.model.PageData;
import com.noknown.framework.common.web.model.SQLFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.transaction.Transactional;
import java.io.Serializable;
import java.lang.reflect.*;
import java.util.Collection;
import java.util.List;

/**
 * @param <T>  类型
 * @param <ID> ID
 * @author guodong
 */
@Transactional(rollbackOn = Exception.class)
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

	/**
	 * 获取JpaRepository
	 *
	 * @return JpaRepository
	 */
	public abstract JpaRepository<T, ID> getRepository();

	/**
	 * 获取JpaSpecificationExecutor
	 * @return JpaSpecificationExecutor
	 */
	public abstract JpaSpecificationExecutor<T> getSpecificationExecutor();

	@Override
	public T create(T entry) throws ServiceException, ServiceException {
		entry = getRepository().save(entry);
		return entry;
	}

	@Override
	public PageData<T> find(SQLFilter filter, int start, int limit) {
		Pageable pageable = PageRequest.of(start / limit, limit);
		Specification<T> spec = (root, query, cb) -> JpaUtil.sqlFilterToPredicate(clazz, root, query, cb, filter);
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
	public PageData<T> findByPage(int pageNo, int size) {
		Pageable pageable = PageRequest.of(pageNo, size);
		Page<T> pd = getRepository().findAll(pageable);

		PageData<T> pageData = new PageData<>();
		pageData.setTotal(pd.getTotalElements());
		pageData.setTotalPage(pd.getTotalPages());
		pageData.setData(pd.getContent());
		pageData.setStart(pageNo * size);
		pageData.setLimit(size);
		return pageData;
	}

	@Override
	public PageData<T> findByPage(SQLFilter filter, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		Specification<T> spec = (root, query, cb) -> JpaUtil.sqlFilterToPredicate(clazz, root, query, cb, filter);
		Page<T> pd = getSpecificationExecutor().findAll(spec, pageable);

		PageData<T> pageData = new PageData<>();
		pageData.setTotal(pd.getTotalElements());
		pageData.setTotalPage(pd.getTotalPages());
		pageData.setData(pd.getContent());
		pageData.setStart(page * size);
		pageData.setLimit(size);
		return pageData;
	}

	@Override
	public T get(ID entityid) {
		return getRepository().findById(entityid).orElse(null);
	}


	@SuppressWarnings("unchecked")
	@Override
	public T get(String attrName, Object attrValue) {
		T t = null;
		Field f;
		try {
			f = clazz.getDeclaredField(attrName);
			Method getMethod = getRepository().getClass().getMethod(
					"findBy" + attrName.substring(0, 1).toUpperCase()
							+ attrName.substring(1), f.getType());
			if (getMethod != null) {
				t = (T) getMethod.invoke(getRepository(), attrValue);
			}
		} catch (NoSuchFieldException | SecurityException | NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return t;
	}

	@Override
	public void delete(ID... entityids) throws ServiceException {
		for (ID id : entityids) {
			getRepository().deleteById(id);
		}
	}

	@Override
	public void delete(Collection<T> objs) {
		getRepository().deleteAll(objs);
	}

	@Override
	public void delete(T obj) throws DaoException, ServiceException {
		getRepository().delete(obj);
	}

	@Override
	public void update(T obj) {
		getRepository().save(obj);
	}


	@Override
	public T update(ID id, T obj, List<String> ignored) throws ServiceException {
		T orgObj = getRepository().getOne(id);
		if (orgObj == null) {
			throw new ServiceException("对象不存在");
		}
		ObjectUtil.copy(orgObj, obj, ignored);
		getRepository().save(orgObj);
		return orgObj;
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
		} catch (NoSuchFieldException | SecurityException | NoSuchMethodException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
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
		Specification<T> spec = (root, query, cb) -> JpaUtil.sqlFilterToPredicate(clazz, root, query, cb, filter);
		return getSpecificationExecutor().findAll(spec);
	}

	@Override
	public PageData<T> find(int start, int limit) {
		Pageable pageable = PageRequest.of(start / limit, limit);
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
		Specification<T> spec = (root, query, cb) -> JpaUtil.sqlFilterToPredicate(clazz, root, query, cb, filter);
		return getSpecificationExecutor().count(spec);
	}


	@Override
	public T getByRepoFunc(String funcName, Object... args) throws ServiceException {
		T t;
		try {
			Method method = null;
			Method[] methods = getRepository().getClass().getMethods();
			for (Method m : methods) {
				if (m.getName().equals(funcName)) {
					method = m;
					break;
				}
			}
			if (method != null) {
				t = (T) method.invoke(getRepository(), args);
			} else {
				throw new ServiceException("方法" + funcName + "不存在");
			}
		} catch (SecurityException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			throw new ServiceException(e);
		}
		return t;
	}

	@Override
	public List<T> findByRepoFunc(String funcName, Object... args) throws ServiceException {
		List<T> col;
		try {
			Method method = null;
			Method[] methods = getRepository().getClass().getMethods();
			for (Method m : methods) {
				if (m.getName().equals(funcName)) {
					method = m;
					break;
				}
			}
			if (method != null) {
				col = (List<T>) method.invoke(getRepository(), args);
			} else {
				throw new ServiceException("方法" + funcName + "不存在");
			}
		} catch (SecurityException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			throw new ServiceException(e);
		}
		return col;
	}

	@Override
	public void deleteAll() {
		getRepository().deleteAll();
	}
}
