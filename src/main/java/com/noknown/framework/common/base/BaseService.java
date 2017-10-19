package com.noknown.framework.common.base;

import java.io.Serializable;
import java.util.Collection;

import com.noknown.framework.common.exception.DAOException;
import com.noknown.framework.common.exception.ServiceException;
import com.noknown.framework.common.web.model.PageData;
import com.noknown.framework.common.web.model.SQLFilter;

public interface BaseService<T, ID extends Serializable> {
	
	/**
	 * 获取对象
	 * @param entityid 对象ID
	 * @return 返回T对象
	 * @throws DAOException, ServiceException 操作失败抛出异常
	 */
	T get(ID  entityid) throws DAOException, ServiceException;

	/**
	 * 根据对象属性获取对象
	 * 		该属性的值必须唯一，否则只返回第一个匹配的对象
	 * @param attrName 对象属性名
	 * @param attrValue 对象属性值
	 * @return <T> 返回attrName值为attrValue的对象，如果不存在返回NULL
	 * @throws DAOException ServiceException 操作失败抛出异常
	 */
	public T get(String attrName, Object attrValue)throws DAOException, ServiceException;

	/**
	 * 删除对象
	 * @param entityids 对象ID集合
	 * @return 无返回值
	 * @throws DAOException, ServiceException 操作失败抛出异常
	 */
	void delete(@SuppressWarnings("unchecked") ID  ... entityids) throws DAOException, ServiceException;
	
	/**
	 * 删除对象
	 * @param objs 对象集合
	 * @return 无返回值
	 * @throws DAOException 操作失败抛出异常
	 * @throws DAOException, ServiceException 操作失败抛出异常 
	 */
	void delete(Collection<T> objs) throws DAOException, ServiceException;
	

	/**
	 * 删除对象
	 * @param obj 对象
	 * @return 无返回值
	 * @throws DAOException 操作失败抛出异常
	 * @throws DAOException, ServiceException 操作失败抛出异常 
	 */
	void delete(T obj) throws DAOException, ServiceException;
	
	/**
	 * 更新对象
	 * @param obj 要更新的对象
	 * @return 无返回值
	 * @throws DAOException, ServiceException 操作失败抛出异常 
	 */
	void update(T  obj) throws DAOException, ServiceException;
	
	
	/**
	 * 根据对象属性获取对象
	 * 		
	 * @param attrName 对象属性名
	 * @param attrValue 对象属性值
	 * @return Collection<T> 返回attrName值为attrValue的集合，如果不存在返回NULL
	 * @throws DAOException ServiceException 操作失败抛出异常
	 */
	public Collection<T> find(String attrName, Object attrValue)throws DAOException, ServiceException;
	
	/**
	 * 获得所有的<T>对象
	 * @return Collection<T> 返回所有的<T>对象
	 * @throws DAOException ServiceException 操作失败抛出异常
	 */
	public Collection<T> find()  throws DAOException, ServiceException;
	
	/**
	 * 获取SQLFilter相匹配的的所有对象
	 * @param filter SQLFilter对象
	 * @return Collection<T> 返回符合条件的对象
	 * @throws DAOException ServiceException 操作失败抛出异常
	 */
	public Collection<T> find(SQLFilter filter) throws DAOException, ServiceException;

	/**
	 * 分页查询
	 * @param start 数据索引
	 * @param limit  显示几条数据
	 * @return PageData 返回符合条件的分页数据
	 * @throws DAOException ServiceException 操作失败抛出异常
	 */
	public PageData<T> find(int start,int limit)  throws DAOException, ServiceException;

	
    /**
	 * 获取SQLFilter相匹配的分页对象
	 * @param filter SQLFilter对象
	 * @param start 数据索引
	 * @param limit  显示几条数据
	 * @return PageData 返回符合条件的分页数据
	 * @throws DAOException ServiceException 操作失败抛出异常
	 */
	PageData<T> find(SQLFilter filter, int start, int limit) throws DAOException, ServiceException;
	
	/** 
     * 获取SQLFilter相匹配的对象总数
     * @param filter SQLFilter对象
     * @return 符合条件的对象个数
     */  
    public long getCount(SQLFilter filter); 
}
