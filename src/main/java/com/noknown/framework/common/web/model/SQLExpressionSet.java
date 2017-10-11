package com.noknown.framework.common.web.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.noknown.framework.common.util.StringUtil;


/**
 * 进行查询的表达式集合，SQL类型
 * 		如：name = "admin" and age > 10 
 * @author guodong
 *
 */
public class SQLExpressionSet implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 表达式逻辑关系符，and，or，not
	 * 	连接该表达式集合时的逻辑关系符号
	 */
	private String logicalOp;
	
	/**
	 * 该集合中的表达式列表
	 * 		String类型
	 */
	private List<SQLExpression> sel;

	public SQLExpressionSet() {
	}
	
	/**
	 * 根据基本条件构造集合，以and相连
	 * @param name          匹配名
	 * @param mathmode      匹配模式, ">=", ">", "<=" "<", "=", "<>", "like"
	 * @param value         匹配值
	 */
	public SQLExpressionSet(String name, String mathmode, Object value) {
		this.sel = new ArrayList<SQLExpression>();
		SQLExpression se = new SQLExpression("and", name, mathmode, value);
		this.logicalOp = "and";
		this.sel.add(se);
	}
	
	/**
	 * 根据基本条件构造集合，以and相连
	 * @param name          匹配名
	 * @param mathmode      匹配模式, ">=", ">", "<=" "<", "=", "<>", "like"
	 * @param value         匹配值
	 */
	public SQLExpressionSet(String name, String mathmode, Object[] value) {
		this.sel = new ArrayList<SQLExpression>();
		SQLExpression se = new SQLExpression("and", name, mathmode, value);
		this.logicalOp = "and";
		this.sel.add(se);
	}
	
	/**
	 * 根据基本条件构造集合
	 * @param logicalOp     表达式逻辑关系符，and，or，not
	 * @param name          匹配名
	 * @param mathmode      匹配模式, ">=", ">", "<=" "<", "=", "<>", "like"
	 * @param value         匹配值
	 */
	public SQLExpressionSet(String logicalOp, String name, String mathmode, Object value) {
		this.sel = new ArrayList<SQLExpression>();
		SQLExpression se = new SQLExpression("and", name, mathmode, value);
		this.logicalOp = logicalOp;
		this.sel.add(se);
	}
	
	/**
	 * 根据基本条件构造集合
	 * @param logicalOp     表达式逻辑关系符，and，or，not
	 * @param name          匹配名
	 * @param mathmode      匹配模式, ">=", ">", "<=" "<", "=", "<>", "like"
	 * @param value         匹配值
	 */
	public SQLExpressionSet(String logicalOp, String name, String mathmode, Object[] value) {
		this.sel = new ArrayList<SQLExpression>();
		SQLExpression se = new SQLExpression("and", name, mathmode, value);
		this.logicalOp = logicalOp;
		this.sel.add(se);
	}

	/**
	 * 根据条件表达式构造集合
	 * @param se            条件表达式
	 */
	public SQLExpressionSet(SQLExpression se) {
		this.sel = new ArrayList<SQLExpression>();
		this.logicalOp = "and";
		this.sel.add(se);
	}

	public List<SQLExpression> getSel() {
		return sel;
	}

	public void setSel(List<SQLExpression> expressionSet) {
		this.sel = expressionSet;
	}

	public String getLogicalOp() {
		return logicalOp;
	}

	public void setLogicalOp(String logicalOp) {
		this.logicalOp = logicalOp;
	}
	
	@Override
	public String toString() {
		String query = "";
		String lop = StringUtil.isBlank(logicalOp) ? "and" : logicalOp;
		if (sel != null && sel.size() > 0) {
			StringBuffer sb = new  StringBuffer(" " + lop + " (");
			int i = 0;
			for (SQLExpression se : sel){
				String seStr = se.toString();
				if ( i++ == 0)
					sb.append(seStr.substring(seStr.indexOf(" ", 1)));
				else {
					sb.append(se.toString());
				}
			}
			sb.append(")");
			query = sb.toString();
		}
		return query;
		
	}
	
	@JsonIgnore
	public boolean isOk() {
		if (sel == null || sel.size() == 0)
			return false;
		else {
			for(SQLExpression se : sel) {
				if (se == null)
					return false;
				if (!se.isOk())
					return false;
			}
			if (sel.get(0).getLogicalOp() != null && sel.get(0).getLogicalOp().equals("or"))
				return false;
		}
		return true;
	}

	@JsonIgnore
	public Object[] getParamValue() {
		List<Object> list = new ArrayList<Object>();
		if (sel != null && sel.size() > 0) {
			for (SQLExpression se : sel){
				for (Object o : se.getValue()){
					list.add(o);
				}
			}
		}
		return list.toArray();
	}
}
