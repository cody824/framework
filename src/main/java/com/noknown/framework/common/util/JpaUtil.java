/**
 * @Title: HibernateUtil.java
 * @Package com.soulinfo.commons.util
 * @Description: 基础工具类-cookie操作
 * CopyRright (c) 2014-2015 SOUL
 * Company:无锡众志和达数据计算股份有限公司
 * @author xingweiwei
 * @date 2015年5月19日 下午3:05:54
 * @version V0.0.1
 */
package com.noknown.framework.common.util;

import com.noknown.framework.common.web.model.SQLExpression;
import com.noknown.framework.common.web.model.SQLExpressionSet;
import com.noknown.framework.common.web.model.SQLFilter;
import com.noknown.framework.common.web.model.SQLOrder;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;

import javax.persistence.criteria.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Hibernate相关的工具类
 *
 * @author guodong
 *
 */
@SuppressWarnings("deprecation")
public class JpaUtil {

	private static Class<?> cls;


	public static Predicate sqlFilterToPredicate(Class<?> c, Root<?> root,
	                                             CriteriaQuery<?> query, CriteriaBuilder cb, SQLFilter filter) {
		Predicate predicate = null;
		if (filter != null) {
			// 拼装查询条件时用到，查询属性的类型由查询的实体属性类型决定(当前只处理BigDecimal等带小数的属性)；2014.12.11
			cls = c;
			predicate = sqlFilterToPredicate(root, query, cb, filter);
			List<SQLOrder> sol = filter.getOrderList();
			List<javax.persistence.criteria.Order> list = new ArrayList<>();
			for (SQLOrder order : sol) {
				if (order.getDirection() == null
						|| order.getDirection().equalsIgnoreCase(SQLOrder.ASC)) {
					list.add(cb.asc(root.get(order.getProperty())));
				} else {
					list.add(cb.desc(root.get(order.getProperty())));
				}
			}
			query = query.orderBy(list);
		}
		return predicate;
	}

	/**
	 * 根据SQLFilter对象生成对应的Criterion对象
	 *
	 * @param filter
	 *            SQLFilter SQL查询条件
	 * @return Criterion filter对应的Criterion对象
	 */
	public static Predicate sqlFilterToPredicate(Root<?> root,
	                                             CriteriaQuery<?> query, CriteriaBuilder cb, SQLFilter filter) {
		Predicate predicate = null;
		List<SQLExpressionSet> sel = filter.getSesl();
		for (SQLExpressionSet se : sel) {
			if (se.getLogicalOp() == null) {
				se.setLogicalOp("and");
			}

			if (predicate == null) {
				predicate = sqlFilterToPredicate(root, query, cb, se);
				if ("not".equals(se.getLogicalOp())) {
					predicate = cb.not(predicate);
				}
			} else {
				if ("and".equals(se.getLogicalOp())) {
					predicate = cb.and(predicate, sqlFilterToPredicate(root, query, cb, se));
				} else if ("or".equals(se.getLogicalOp())) {
					predicate = cb.or(predicate, sqlFilterToPredicate(root, query, cb, se));
				} else if ("not".equals(se.getLogicalOp())) {
					predicate = cb.and(predicate, cb.not(sqlFilterToPredicate(root, query, cb, se)));
				}
			}
		}
		return predicate;
	}

	/**
	 * 根据SQLExpressionSet对象生成对应的Criterion对象
	 *
	 * @param ses
	 *            SQLExpressionSet 表达式集合
	 * @return Criterion ses对应的Criterion对象
	 */
	public static Predicate sqlFilterToPredicate(Root<?> root,
	                                             CriteriaQuery<?> query, CriteriaBuilder cb, SQLExpressionSet ses) {
		Predicate predicate = null;
		List<SQLExpression> sel = ses.getSel();
		for (SQLExpression se : sel) {
			if (se.getLogicalOp() == null) {
				se.setLogicalOp("and");
			}

			if (predicate == null) {
				predicate = sqlFilterToPredicate(root, query, cb, se);
				if ("not".equals(se.getLogicalOp())) {
					predicate = cb.not(predicate);
				}
			} else {
				if ("and".equals(se.getLogicalOp())) {
					predicate = cb.and(predicate, sqlFilterToPredicate(root, query, cb, se));
				} else if ("or".equals(se.getLogicalOp())) {
					predicate = cb.or(predicate, sqlFilterToPredicate(root, query, cb, se));
				} else if ("not".equals(se.getLogicalOp())) {
					predicate = cb.and(predicate, cb.not(sqlFilterToPredicate(root, query, cb, se)));
				}
			}
		}
		return predicate;
	}

	/**
	 * 根据SQLExpression对象生成对应的Criterion对象
	 *
	 * @param se
	 *            SQLExpression 查询表达式
	 * @return Criterion se对应的Criterion对象
	 */
	public static Predicate sqlFilterToPredicate(Root<?> root,
	                                             CriteriaQuery<?> query, CriteriaBuilder cb, SQLExpression se) {
		Predicate predicate = null;
		int i = 0;
		Object[] valueArray = se.getValue();
		Boolean isNeedIgnoreCase = se.getIgnoreCase();// 默认原有的判断条件，如果当前为false，就不会按照参数传递来的是否忽略大小写处理。忽略大小写会将参数转化为字符串。
		// 处理BigDecimal和long，double带小数的属性
		Field f = null;
		Class<?> type = null;
		boolean isDate = false;
		boolean isString = false;
		try {
			f = cls.getDeclaredField(se.getProperty());
			type = f.getType();
		} catch (NoSuchFieldException | SecurityException e1) {
		}
		if (valueArray != null) {
			for (int j = 0; j < valueArray.length; j++) {
				type = type == null || "Serializable".equals(type.getSimpleName()) ? valueArray[j].getClass() : type;
				if (type.equals(long.class) || type.equals(Long.class)) {
					isNeedIgnoreCase = false;
					valueArray[j] = Long.parseLong(valueArray[j].toString());
				} else if (type.equals(BigDecimal.class)) {
					isNeedIgnoreCase = false;
					valueArray[j] = new BigDecimal(valueArray[j].toString());
				} else if (type.equals(BigInteger.class)) {
					isNeedIgnoreCase = false;
					valueArray[j] = new BigInteger(valueArray[j].toString());
				} else if (type.equals(Double.class) || type.equals(double.class)) {
					isNeedIgnoreCase = false;
					valueArray[j] = Double.parseDouble(valueArray[j].toString());
				} else if (type.equals(Float.class) || type.equals(float.class)) {
					isNeedIgnoreCase = false;
					valueArray[j] = Float.parseFloat(valueArray[j].toString());
				} else if (type.equals(Integer.class) || type.equals(int.class)) {
					isNeedIgnoreCase = false;
					valueArray[j] = Integer.parseInt(valueArray[j].toString());
				} else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
					isNeedIgnoreCase = false;
					valueArray[j] = Boolean.parseBoolean(valueArray[j].toString());
				} else if (type.equals(Date.class) || type.equals(java.sql.Date.class)) {
					isNeedIgnoreCase = false;
					isDate = true;
				}
			}

			// 处理日期类型
			SimpleDateFormat formatDate = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			for (Object obj : se.getValue()) {
				if (obj instanceof String) {
					String str = (String) obj;
					// 正则表达式替换“yyyy-MM-ddTHH:mm:ss”为“yyyy-MM-dd HH:mm:ss”
					String regex = "[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}";
					Pattern p = Pattern.compile(regex);
					Matcher m = p.matcher(str);
					boolean dateFlag = m.matches();
					String regex1 = "[0-9]{4}-[0-9]{2}-[0-9]{2}\\s[0-9]{2}:[0-9]{2}:[0-9]{2}";
					Pattern p1 = Pattern.compile(regex1);
					Matcher m1 = p1.matcher(str);
					boolean dateFlag1 = m1.matches();
					if (dateFlag) {
						str = str.replaceAll("T", " ");
						str = str.replaceAll("Z", "");
						try {
							Date date = formatDate.parse(str);
							valueArray[i++] = date;
							isDate = true;
						} catch (ParseException e) {
							System.out.println(e.getLocalizedMessage());
							valueArray[i++] = obj;
							isString = true;
						}
					} else {
						if (dateFlag1) {
							try {
								Date date = formatDate.parse(str);
								valueArray[i++] = date;
								isDate = true;
							} catch (ParseException e) {
								System.out.println(e.getLocalizedMessage());
								valueArray[i++] = obj;
								isString = true;
							}
						} else {
							valueArray[i++] = obj;
							isString = true;
						}
					}
				}
			}
		}
		String[] paths = se.getProperty().split("\\.");

		Path<?> path = null;
		for (String p : paths) {
			if (path == null) {
				path = root.get(p);
			} else {
				path = path.get(p);
			}
		}
		if ("=".equals(se.getMatchMode())) {
			if (isString && isNeedIgnoreCase) {
				predicate = cb.equal(cb.upper(path.as(String.class)), valueArray[0].toString().toUpperCase());
			} else {
				predicate = cb.equal(path.as(type), valueArray[0]);
			}

		} else if (">".equals(se.getMatchMode())) {
			predicate = cb.gt(path.as(Number.class), (Number) valueArray[0]);
		} else if (">=".equals(se.getMatchMode())) {
			predicate = cb.ge(path.as(Number.class), (Number) valueArray[0]);
		} else if ("<".equals(se.getMatchMode())) {
			predicate = cb.lt(path.as(Number.class), (Number) valueArray[0]);
		} else if ("<=".equals(se.getMatchMode())) {
			predicate = cb.le(path.as(Number.class), (Number) valueArray[0]);
		} else if ("<>".equals(se.getMatchMode())) {
			predicate = cb.notEqual(path.as(type), valueArray[0]);
		} else if ("between".equals(se.getMatchMode())) {
			if (isDate) {
				predicate = cb.between(path.as(Date.class), Date.class.cast(valueArray[0]), Date.class.cast(valueArray[1]));
			} else {
				if (type.equals(long.class) || type.equals(Long.class)) {
					predicate = cb.between(path.as(Long.class), Long.class.cast(valueArray[0]), Long.class.cast(valueArray[1]));
				} else if (type.equals(BigDecimal.class)) {
					predicate = cb.between(path.as(BigDecimal.class), BigDecimal.class.cast(valueArray[0]), BigDecimal.class.cast(valueArray[1]));
				} else if (type.equals(BigInteger.class)) {
					predicate = cb.between(path.as(BigInteger.class), BigInteger.class.cast(valueArray[0]), BigInteger.class.cast(valueArray[1]));
				} else if (type.equals(Double.class) || type.equals(double.class)) {
					predicate = cb.between(path.as(Double.class), Double.class.cast(valueArray[0]), Double.class.cast(valueArray[1]));
				} else if (type.equals(Float.class) || type.equals(float.class)) {
					predicate = cb.between(path.as(Float.class), Float.class.cast(valueArray[0]), Float.class.cast(valueArray[1]));
				} else if (type.equals(Integer.class) || type.equals(int.class)) {
					predicate = cb.between(path.as(Integer.class), Integer.class.cast(valueArray[0]), Integer.class.cast(valueArray[1]));
				}
			}
		} else if ("like".equals(se.getMatchMode())) {
			if (isNeedIgnoreCase) {
				predicate = cb.like(cb.upper(path.as(String.class)), ((String) valueArray[0]).toUpperCase());
			} else {
				predicate = cb.like(path.as(String.class), (String) valueArray[0]);
			}
		} else if ("in".equals(se.getMatchMode())) {
			predicate = path.in(valueArray);
		} else if (se.getMatchMode().equals(SQLExpression.isnull)) {
			predicate = path.isNull();
		} else if (se.getMatchMode().equals(SQLExpression.isnotnul)) {
			predicate = path.isNotNull();
		} else {
			predicate = cb.equal(path.as(type), valueArray[0]);
		}
		return predicate;
	}


	/**
	 * 根据SQLFilter对象生成对应的Hql Query条件语句
	 *
	 * @param c
	 *            Class查询对象的类型
	 * @param filter
	 *            SQLFilter SQL查询条件
	 * @return DetachedCriteria filter对应的DetachedCriteria对象
	 */
	public static String sqlFilterToQuery(Class<?> c, SQLFilter filter) {
		String query = "";
		if (filter != null && filter.getSesl() != null && filter.getSesl().size() > 0) {
			StringBuilder sb = new StringBuilder("from " + c.getSimpleName()
					+ " where ");
			sb.append(filter.toString());
		}
		return query;
	}

	/**
	 * 根据SQLFilter对象生成对应的Criterion对象
	 *
	 * @param filter
	 *            SQLFilter SQL查询条件
	 * @return Criterion filter对应的Criterion对象
	 */
	public static Criterion sqlFilterToDc(SQLFilter filter) {
		Criterion criterion = null;
		List<SQLExpressionSet> sel = filter.getSesl();
		for (SQLExpressionSet se : sel) {
			if (se.getLogicalOp() == null) {
				se.setLogicalOp("and");
			}

			if (criterion == null) {
				criterion = sqlFilterToDc(se);
				if ("not".equals(se.getLogicalOp())) {
					criterion = Restrictions.not(criterion);
				}
			} else {
				if ("and".equals(se.getLogicalOp())) {
					criterion = Restrictions.and(criterion, sqlFilterToDc(se));
				} else if ("or".equals(se.getLogicalOp())) {
					criterion = Restrictions.or(criterion, sqlFilterToDc(se));
				} else if ("not".equals(se.getLogicalOp())) {
					criterion = Restrictions.and(criterion,
							Restrictions.not(sqlFilterToDc(se)));
				}
			}
		}
		return criterion;
	}

	/**
	 * 根据SQLExpressionSet对象生成对应的Criterion对象
	 *
	 * @param ses
	 *            SQLExpressionSet 表达式集合
	 * @return Criterion ses对应的Criterion对象
	 */
	public static Criterion sqlFilterToDc(SQLExpressionSet ses) {
		Criterion criterion = null;
		List<SQLExpression> sel = ses.getSel();
		for (SQLExpression se : sel) {
			if (se.getLogicalOp() == null) {
				se.setLogicalOp("and");
			}

			if (criterion == null) {
				criterion = sqlFilterToDc(se);
				if ("not".equals(se.getLogicalOp())) {
					criterion = Restrictions.not(criterion);
				}
			} else {
				if ("and".equals(se.getLogicalOp())) {
					criterion = Restrictions.and(criterion, sqlFilterToDc(se));
				} else if ("or".equals(se.getLogicalOp())) {
					criterion = Restrictions.or(criterion, sqlFilterToDc(se));
				} else if ("not".equals(se.getLogicalOp())) {
					criterion = Restrictions.and(criterion,
							Restrictions.not(sqlFilterToDc(se)));
				}
			}
		}
		return criterion;
	}

	/**
	 * 根据SQLExpression对象生成对应的Criterion对象
	 *
	 * @param se
	 *            SQLExpression 查询表达式
	 * @return Criterion se对应的Criterion对象
	 */
	public static Criterion sqlFilterToDc(SQLExpression se) {
		Criterion criterion = null;
		SimpleExpression exp = null;
		int i = 0;
		Object[] valueArray = se.getValue();
		Boolean isNeedIgnoreCase = true;// 默认原有的判断条件，如果当前为false，就不会按照参数传递来的是否忽略大小写处理。忽略大小写会将参数转化为字符串。
		// 处理BigDecimal和long，double带小数的属性
		Field f = null;
		Class<?> type = null;
		try {
			f = cls.getDeclaredField(se.getProperty());
			type = f.getType();
		} catch (NoSuchFieldException | SecurityException e1) {
		}
		if (valueArray != null) {
			for (int j = 0; j < valueArray.length; j++) {
				type = type == null || "Serializable".equals(type.getSimpleName()) ? valueArray[j].getClass() : type;
				if (type.equals(long.class) || type.equals(Long.class)) {
					isNeedIgnoreCase = false;
					valueArray[j] = Long.parseLong(valueArray[j].toString());
				} else if (type.equals(BigDecimal.class)) {
					isNeedIgnoreCase = false;
					valueArray[j] = new BigDecimal(valueArray[j].toString());
				} else if (type.equals(BigInteger.class)) {
					isNeedIgnoreCase = false;
					valueArray[j] = new BigInteger(valueArray[j].toString());
				} else if (type.equals(Double.class) || type.equals(double.class)) {
					isNeedIgnoreCase = false;
					valueArray[j] = Double.parseDouble(valueArray[j].toString());
				} else if (type.equals(Float.class) || type.equals(float.class)) {
					isNeedIgnoreCase = false;
					valueArray[j] = Float.parseFloat(valueArray[j].toString());
				} else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
					isNeedIgnoreCase = false;
					valueArray[j] = Boolean.parseBoolean(valueArray[j].toString());
				} else if (type.equals(Integer.class) || type.equals(int.class)) {
					isNeedIgnoreCase = false;
					valueArray[j] = Integer.parseInt(valueArray[j].toString());
				} else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
					isNeedIgnoreCase = false;
					valueArray[j] = Boolean.parseBoolean(valueArray[j].toString());
				}
			}

			// 处理日期类型
			SimpleDateFormat formatDate = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			for (Object obj : se.getValue()) {
				if (obj instanceof String) {
					String str = (String) obj;
					// 正则表达式替换“yyyy-MM-ddTHH:mm:ss”为“yyyy-MM-dd HH:mm:ss”
					String regex = "[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}";
					Pattern p = Pattern.compile(regex);
					Matcher m = p.matcher(str);
					boolean dateFlag = m.matches();
					String regex1 = "[0-9]{4}-[0-9]{2}-[0-9]{2}\\s[0-9]{2}:[0-9]{2}:[0-9]{2}";
					Pattern p1 = Pattern.compile(regex1);
					Matcher m1 = p1.matcher(str);
					boolean dateFlag1 = m1.matches();
					if (dateFlag) {
						str = str.replaceAll("T", " ");
						try {
							Date date = formatDate.parse(str);
							valueArray[i++] = date;
						} catch (ParseException e) {
							System.out.println(e.getLocalizedMessage());
							valueArray[i++] = obj;
						}
					} else {

						if (dateFlag1) {
							try {
								Date date = formatDate.parse(str);
								valueArray[i++] = date;
							} catch (ParseException e) {
								System.out.println(e.getLocalizedMessage());
								valueArray[i++] = obj;
							}
						} else {

							valueArray[i++] = obj;
						}
					}
				}
			}
		}


		if ("=".equals(se.getMatchMode())) {
			exp = Expression.eq(se.getProperty(), valueArray[0]);
		} else if (">".equals(se.getMatchMode())) {
			exp = Expression.gt(se.getProperty(), valueArray[0]);
		} else if (">=".equals(se.getMatchMode())) {
			exp = Expression.ge(se.getProperty(), valueArray[0]);
		} else if ("<".equals(se.getMatchMode())) {
			exp = Expression.lt(se.getProperty(), valueArray[0]);
		} else if ("<=".equals(se.getMatchMode())) {
			exp = Expression.le(se.getProperty(), valueArray[0]);
		} else if ("<>".equals(se.getMatchMode())) {
			exp = Expression.ne(se.getProperty(), valueArray[0]);
		} else if ("between".equals(se.getMatchMode())) {
			criterion = Expression.between(se.getProperty(), valueArray[0],
					valueArray[1]);
		} else if ("like".equals(se.getMatchMode())) {
			exp = Expression.like(se.getProperty(), valueArray[0]);
		} else if ("in".equals(se.getMatchMode())) {
			criterion = Expression.in(se.getProperty(), valueArray);
		} else if (se.getMatchMode().equals(SQLExpression.isnull)) {
			criterion = Expression.isNull(se.getProperty());
		} else if (se.getMatchMode().equals(SQLExpression.isnotnul)) {
			criterion = Expression.isNotNull(se.getProperty());
		} else {
			exp = Expression.eq(se.getProperty(), valueArray[0]);
		}
		if (exp != null) {
			if (isNeedIgnoreCase) {
				criterion = se.getIgnoreCase() ? exp.ignoreCase() : exp;
			} else {
				criterion = exp;
			}
		}

		return criterion;
	}

}
