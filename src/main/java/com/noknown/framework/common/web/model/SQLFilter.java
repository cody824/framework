package com.noknown.framework.common.web.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.noknown.framework.common.util.StringUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 进行查询的表达式集合，SQL类型，包括排序方式
 * 		如：(name like "admin%" and age > 10) and( name like "user%" and age > 20)
 * 		排序方式 name asc
 * 			   age desc	
 * @author guodong
 *
 */
public class SQLFilter implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 定义结果集的排序方式
	 * 		按照列表中顺序处理
	 */
	private List<SQLOrder> orderList;
	
	/**
	 * 定义结果集的排序方式
	 * 		按照列表中顺序处理
	 */
	private List<SQLExpressionSet> sesl;
	
	public SQLFilter() {
	}
	
	/**
	 * 根据基本条件构造简单的查询条件
	 * @param name 属性名
	 * @param mathmode 匹配表达式 （=，>= 等）
	 * @param value 匹配的值
	 */
	public SQLFilter(String name, String mathmode, Object value) {
		this.sesl = new ArrayList<SQLExpressionSet>();
		SQLExpressionSet ses = new SQLExpressionSet("and", name, mathmode, value);
		this.sesl.add(ses);
	}
	
	/**
	 * 根据基本条件构造简单的查询条件
	 * @param name 属性名
	 * @param mathmode 匹配表达式 （=，>= 等）
	 * @param value 匹配的值
	 */
	public SQLFilter(String name, String mathmode, Object[] value) {
		this.sesl = new ArrayList<SQLExpressionSet>();
		SQLExpressionSet ses = new SQLExpressionSet("and", name, mathmode, value);
		this.sesl.add(ses);
	}
	
	/**
	 * 根据基本条件构造简单的查询条件
	 * @param relation      表达式逻辑关系符，and，or，not
	 * @param name          匹配名
	 * @param mathmode      匹配模式, ">=", ">", "<=" "<", "=", "<>", "like"
	 * @param value         匹配值
	 */
	public SQLFilter(String relation, String name, String mathmode, Object value) {
		this.sesl = new ArrayList<SQLExpressionSet>();
		SQLExpressionSet ses = new SQLExpressionSet(relation, name, mathmode, value);
		this.sesl.add(ses);
	}
	
	/**
	 * 根据基本条件构造简单的查询条件
	 * @param relation      表达式逻辑关系符，and，or，not
	 * @param name          匹配名
	 * @param mathmode      匹配模式, ">=", ">", "<=" "<", "=", "<>", "like"
	 * @param value         匹配值
	 */
	public SQLFilter(String relation, String name, String mathmode, Object[] value) {
		this.sesl = new ArrayList<SQLExpressionSet>();
		SQLExpressionSet ses = new SQLExpressionSet(relation, name, mathmode, value);
		this.sesl.add(ses);
	}
	
	/**
	 * 根据List<SQLExpressionSet>构造查询条件
	 * @param sesl          条件表达式集合列表
	 */
	public SQLFilter(List<SQLExpressionSet> sesl) {
		this.sesl = sesl;
	}
	
	/**
	 * 根据SQLExpressionSet对象构造查询条件
	 * @param ses           条件表达式集合
	 */
	public SQLFilter(SQLExpressionSet ses) {
		this.sesl = new ArrayList<SQLExpressionSet>();
		this.sesl.add(ses);
	}
	
	/**
	 * 根据SQLExpression对象构造查询条件
	 * @param se            条件表达式
	 */
	public SQLFilter(SQLExpression se) {
		this.sesl = new ArrayList<SQLExpressionSet>();
		SQLExpressionSet ses = new SQLExpressionSet(se);
		this.sesl.add(ses);
	}
		
	/**
	 * 添加表达式，新建一个表达式集合，以and相连
	 * @param se            条件表达式
	 */
	public void addSQLExpressionSet(SQLExpression se){
		SQLExpressionSet ses = new SQLExpressionSet(se);
		addSQLExpressionSet(ses);
	}
	
	/**
	 * 添加表达式，新建一个表达式集合，以and相连
	 * @param name			属性名称
	 * @param matchMode		匹配模式
	 * @param value			匹配值
	 */
	public void addSQLExpressionSet(String name, String matchMode, Object value){
		addSQLExpressionSet("and", name, matchMode, value);
	}
	
	/**
	 * 添加表达式，新建一个表达式集合，以and相连
	 * @param name			属性名称
	 * @param matchMode		匹配模式
	 * @param value			匹配值
	 */
	public void addSQLExpressionSet(String name, String matchMode, Object[] value){
		addSQLExpressionSet("and", name, matchMode, value);
	}
	
	/**
	 * 添加表达式，新建一个表达式集合
	 * @param relation      表达式逻辑关系符，and，or，not
	 * @param name			属性名称
	 * @param matchMode		匹配模式
	 * @param value			匹配值
	 */
	public void addSQLExpressionSet(String relation, String name, String matchMode, Object value){
		SQLExpressionSet ses = new SQLExpressionSet(relation, name, matchMode, value);
		addSQLExpressionSet(ses);
	}
	
	/**
	 * 添加表达式，新建一个表达式集合
	 * @param relation      表达式逻辑关系符，and，or，not
	 * @param name			属性名称
	 * @param matchMode		匹配模式
	 * @param value			匹配值
	 */
	public void addSQLExpressionSet(String relation, String name, String matchMode, Object[] value){
		SQLExpressionSet ses = new SQLExpressionSet(relation, name, matchMode, value);
		addSQLExpressionSet(ses);
	}
	
	/**
	 * 添加一个表达式集合
	 * @param ses         条件表达式集合
	 */
	public void addSQLExpressionSet(SQLExpressionSet ses){
		if (sesl == null)
			sesl = new ArrayList<SQLExpressionSet>();
		sesl.add(ses);
	}
	
	/**
	 * 添加表达式，把表达式添加到最后一个表达式集中，以and相连
	 * @param name			属性名称
	 * @param matchMode		匹配模式
	 * @param value			匹配值
	 */
	public void addSQLExpression(String name, String matchMode, Object value){
		addSQLExpression("and", name, matchMode, value);
	}
	
	/**
	 * 添加表达式，把表达式添加到最后一个表达式集中，以and相连
	 * @param name			属性名称
	 * @param matchMode		匹配模式
	 * @param value			匹配值
	 */
	public void addSQLExpression(String name, String matchMode, Object[] value){
		addSQLExpression("and", name, matchMode, value);
	}
	
	/**
	 * 添加表达式，把表达式添加到最后一个表达式集中
	 * @param relation      表达式逻辑关系符，and，or，not
	 * @param name			属性名称
	 * @param matchMode		匹配模式
	 * @param value			匹配值
	 */
	public void addSQLExpression(String relation, String name, String matchMode, Object value){
		SQLExpression se = new SQLExpression(relation, name, matchMode, value);
		addSQLExpression(se);
	}
	
	/**
	 * 添加表达式，把表达式添加到最后一个表达式集中
	 * @param relation      表达式逻辑关系符，and，or，not
	 * @param name			属性名称
	 * @param matchMode		匹配模式
	 * @param value			匹配值
	 */
	public void addSQLExpression(String relation, String name, String matchMode, Object[] value){
		SQLExpression se = new SQLExpression(relation, name, matchMode, value);
		addSQLExpression(se);
	}
	
	/**
	 * 添加表达式，把表达式添加到最后一个表达式集中
	 * @param se            条件表达式
	 */
	public void addSQLExpression(SQLExpression se){
		SQLExpressionSet ses;
		if (sesl == null || sesl.size() == 0){
			sesl = new ArrayList<SQLExpressionSet>();
			ses = new SQLExpressionSet(se);
			sesl.add(ses);
		} else {
			ses = sesl.get(sesl.size() - 1);
			if (ses.getSel() == null){
				ses.setSel(new ArrayList<SQLExpression>());
			}
			ses.getSel().add(se);
		}
	}

	
	/**
	 * 增加一个排序规则
	 * @param order		  排序规则
	 */
	public void addSQLOrder(SQLOrder order){
		if (orderList == null)
			orderList = new ArrayList<SQLOrder>();
		orderList.add(order);
	}
	
	/**
	 * 增加一个排序规则，默认升序排列
	 * @param property  属性名称
	 */
	public void addSQLOrder(String property){
		if (orderList == null)
			orderList = new ArrayList<SQLOrder>();
		orderList.add(new SQLOrder(property));
	}
	
	/**
	 * 增加一个排序规则，默认升序排列
	 * @param property		属性名称
	 * @param direction		排序规则		
	 */
	public void addSQLOrder(String property, String direction){
		if (orderList == null)
			orderList = new ArrayList<SQLOrder>();
		orderList.add(new SQLOrder(property, direction));
	}

	public List<SQLExpressionSet> getSesl() {	
		if (sesl == null)
		{	
			sesl=new ArrayList<SQLExpressionSet>();
			this.setSesl(sesl);
		}
		return sesl;
	}

	public void setSesl(List<SQLExpressionSet> filterList) {
		this.sesl = filterList;
	}

	public List<SQLOrder> getOrderList() {
		if (orderList == null)
			orderList = new ArrayList<SQLOrder>();
		return orderList;
	}

	public void setOrderList(List<SQLOrder> orderList) {
		this.orderList = orderList;
	}

	@JsonIgnore
	public boolean isOk(){
		if (sesl == null || sesl.size() == 0)
			return false;
		else {
			for(SQLExpressionSet ses : sesl) {
				if (ses == null)
					return false;
				if (!ses.isOk())
					return false;
			}
			
			if (sesl.get(0).getLogicalOp() != null && sesl.get(0).getLogicalOp().equals("or"))
				return false;
			
		}
		return true;
	}
	
	@JsonIgnore
	public Object[] getParamValue() {
		List<Object> list = new ArrayList<Object>();
		if (sesl != null && sesl.size() > 0) {
			for (SQLExpressionSet ses : sesl){
				for (Object o : ses.getParamValue()){
					list.add(o);
				}
			}
		}
		return list.toArray();
	}
	
	@Override
	public String toString() {
		String query = "";
		if (sesl != null && sesl.size() > 0) {
			StringBuffer sb = new  StringBuffer(" 1 = 1");
			for (SQLExpressionSet ses : sesl){
				if (!StringUtil.isBlank(ses.toString()))
					sb.append(ses.toString());
			}
			if (orderList != null && orderList.size() > 0) {
				sb.append(" order by ");
				for (int i = 0; i < orderList.size(); i++){
					if (i > 0)
						sb.append(",");
					sb.append(orderList.get(i).getProperty() + " " + orderList.get(i).getDirection());
				}
			}
			query = sb.toString();
		}
		return query;
	}
	
	
	
}
