package com.noknown.framework.common.web.model;

import java.io.Serializable;

/**
 * 查询的排序方式
 * @author guodong
 *
 */
public class SQLOrder implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 升序排列
	 */
	public static final String asc = "asc";
	
	/**
	 * 降序排列
	 */
	public static final String desc = "desc";
	/**
	 * 要排序的属性
	 *
	 */
	private String property;
	/**
	 * 升序/降序设置
	 *
	 */
	private String direction;

	public SQLOrder() {

	}
	
	/**
	 * 根据排序属性构造排序
	 * @param property         排序属性
	 */
	public SQLOrder(String property) {
		this.property = property;
		this.direction = SQLOrder.desc;
	}
	
	/**
	 * 根据排序属性及排序设置构造排序
	 * @param property         排序属性
	 * @param direction        排序设置
	 */
	public SQLOrder(String property, String direction) {
		this.property = property;
		this.direction = direction;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

}
