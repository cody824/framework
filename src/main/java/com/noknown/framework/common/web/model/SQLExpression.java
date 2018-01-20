package com.noknown.framework.common.web.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.noknown.framework.common.util.StringUtil;

import java.io.Serializable;



/**
 * 进行查询的独立表达式，SQL类型
 * 		如：name = "admin"
 * @author guodong
 *
 */
public class SQLExpression implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String and = "and";
	public static final String or = "or";
	public static final String not = "not";

	public static final String eq = "=";
	public static final String gt = ">";
	public static final String ge = ">=";
	public static final String lt = "<";
	public static final String le = "<=";
	public static final String between = "between";
	public static final String like = "like";
	public static final String in = "in";
	public static final String isnull = "is null";
	public static final String isnotnul = "is not null";
	
	/**
	 * 表达式逻辑关系符，and，or，not
	 * 	连接该表达式时的逻辑关系符号
	 */
	private String logicalOp;
	/**
	 * 属性名
	 * 		String类型
	 */
	private String property;
	/**
	 * 匹配方式
	 * 		String 类型
	 */
	private String matchMode;
	
	/**
	 * 匹配的值
	 * 		Object[] 对象数组
	 * 		对于matchMode为between和in需要多个值
	 */
	private Object[] value;
	
	/**
	 * 是否区分大写
	 */
	private boolean ignoreCase;

	public SQLExpression() {

	}

	
	/**
	 * 根据基本条件构造简单的查询条件
	 * @param op 逻辑关系符 and，or，not
	 * @param an 属性名
	 * @param mm 匹配表达式 （=，>= 等）
	 * @param obj 匹配的值
	 */
	public SQLExpression(String op, String an, String mm, Object obj) {
		Object[] array;
		if (obj != null && obj.getClass().isArray()) {
			array = (Object[]) obj;
		} else {
			array = new Object[]{obj};
		}
		this.setProperty(an);
		this.setValue(array);
		this.logicalOp = op;
		this.matchMode = mm;
	}
	
	/**
	 * 根据基本条件构造简单的查询条件
	 * @param op 逻辑关系符 and，or，not
	 * @param an 属性名
	 * @param mm 匹配表达式 （=，>= 等）
	 * @param obs 匹配的值
	 */
	public SQLExpression(String op, String an, String mm, Object[] obs) {
		this.setProperty(an);
		this.setValue(obs);
		this.logicalOp = op;
		this.matchMode = mm;
	}


	public String getMatchMode() {
		return matchMode;
	}

	public void setMatchMode(String matchMode) {
		this.matchMode = matchMode;
	}

	public String getLogicalOp() {
		return logicalOp;
	}

	public void setLogicalOp(String logicalOp) {
		this.logicalOp = logicalOp;
	}

	public boolean getIgnoreCase() {
		return ignoreCase;
	}

	public void setIgnoreCase(boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public Object[] getValue() {
		return value;
	}

	public void setValue(Object[] value) {
		this.value = value;
	}

	@Override
	public String toString() {
		String lop = StringUtil.isBlank(logicalOp) ? "and" : logicalOp;
		StringBuffer sb = new StringBuffer(" " + lop + " " + property + " " + matchMode + " ");
		if (matchMode.equals(SQLExpression.between)) {
			if (ignoreCase)
				sb.append("Upper(?) and (?)");
			else
				sb.append("? and ?");
		} else if (matchMode.equals(SQLExpression.in)) {
			sb.append("(");
			for (int i = 0; i < value.length; i++){
				if (value[i] != null && i < value.length - 1) {
					if (ignoreCase)
						sb.append("Upper(?),");
					else
						sb.append("?,");
				} else {
					if (ignoreCase)
						sb.append("Upper(?)");
					else
						sb.append("?");
				}
			}
			sb.append(")");
		} else if (matchMode.equals(SQLExpression.isnull) || matchMode.equals(SQLExpression.isnotnul)) {
			//do nothing
		} else {
			if (ignoreCase)
				sb.append("Upper(?)");
			else
				sb.append("?");
		}
		return sb.toString();
	}

	@JsonIgnore
	public boolean isOk() {
		if (StringUtil.isBlank(property) || StringUtil.isBlank(matchMode))
			return false;
		
		if (matchMode.equals(SQLExpression.between)) {
			if (this.value == null || value.length != 2)
				return false;
			for(Object o : value){
				if (o == null)
					return false;
			}
		}  else if (matchMode.equals(SQLExpression.isnull) || matchMode.equals(SQLExpression.isnotnul)) {
			if (value != null && value.length > 0)
				return false;
		} else if (matchMode.equals(SQLExpression.in))  {
			if (value == null || value.length <= 0)
				return false;
			for(Object o : value){
				if (o == null)
					return false;
			}
		} else {
			if (value == null || value.length != 1)
				return false;
			for(Object o : value){
				if (o == null)
					return false;
			}
		}
		return true;
	}

}
