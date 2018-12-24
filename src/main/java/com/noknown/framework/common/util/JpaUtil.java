/**
 * @Title: HibernateUtil.java
 * @Package com.soulinfo.commons.util
 * @Description: 基础工具类-cookie操作
 * CopyRright (c) 2014-2015 SOUL
 * Company:无锡众志和达数据计算股份有限公司
 * 
 * @author xingweiwei
 * @date 2015年5月19日 下午3:05:54
 * @version V0.0.1
 */
package com.noknown.framework.common.util;

import com.noknown.framework.common.web.model.SQLExpression;
import com.noknown.framework.common.web.model.SQLExpressionSet;
import com.noknown.framework.common.web.model.SQLFilter;
import com.noknown.framework.common.web.model.SQLOrder;
import org.hibernate.criterion.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
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
			
			// 创建关联查询别名
			sqlAliasToPredicate(root, filter);
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


		if ("=".equals(se.getMatchMode())) {
			if (isString && isNeedIgnoreCase) {
				predicate = cb.equal(cb.upper(root.get(se.getProperty()).as(String.class)), valueArray[0].toString().toUpperCase());
			} else {
				predicate = cb.equal(root.get(se.getProperty()).as(type), valueArray[0]);
			}

		} else if (">".equals(se.getMatchMode())) {
			predicate = cb.gt(root.get(se.getProperty()).as(Number.class), (Number) valueArray[0]);
		} else if (">=".equals(se.getMatchMode())) {
			predicate = cb.ge(root.get(se.getProperty()).as(Number.class), (Number) valueArray[0]);
		} else if ("<".equals(se.getMatchMode())) {
			predicate = cb.lt(root.get(se.getProperty()).as(Number.class), (Number) valueArray[0]);
		} else if ("<=".equals(se.getMatchMode())) {
			predicate = cb.le(root.get(se.getProperty()).as(Number.class), (Number) valueArray[0]);
		} else if ("<>".equals(se.getMatchMode())) {
			predicate = cb.notEqual(root.get(se.getProperty()).as(type), valueArray[0]);
		} else if ("between".equals(se.getMatchMode())) {
			if (isDate) {
				predicate = cb.between(root.get(se.getProperty()).as(Date.class), Date.class.cast(valueArray[0]), Date.class.cast(valueArray[1]));
			} else {
				if (type.equals(long.class) || type.equals(Long.class)) {
					predicate = cb.between(root.get(se.getProperty()), Long.class.cast(valueArray[0]), Long.class.cast(valueArray[1]));
				} else if (type.equals(BigDecimal.class)) {
					predicate = cb.between(root.get(se.getProperty()), BigDecimal.class.cast(valueArray[0]), BigDecimal.class.cast(valueArray[1]));
				} else if (type.equals(BigInteger.class)) {
					predicate = cb.between(root.get(se.getProperty()), BigInteger.class.cast(valueArray[0]), BigInteger.class.cast(valueArray[1]));
				} else if (type.equals(Double.class) || type.equals(double.class)) {
					predicate = cb.between(root.get(se.getProperty()), Double.class.cast(valueArray[0]), Double.class.cast(valueArray[1]));
				} else if (type.equals(Float.class) || type.equals(float.class)) {
					predicate = cb.between(root.get(se.getProperty()), Float.class.cast(valueArray[0]), Float.class.cast(valueArray[1]));
				} else if (type.equals(Integer.class) || type.equals(int.class)) {
					predicate = cb.between(root.get(se.getProperty()), Integer.class.cast(valueArray[0]), Integer.class.cast(valueArray[1]));
				}
			}
		} else if ("like".equals(se.getMatchMode())) {
			if (isNeedIgnoreCase){
				predicate = cb.like(cb.upper(root.get(se.getProperty()).as(String.class)), ((String) valueArray[0]).toUpperCase());
			} else {
				predicate = cb.like(root.get(se.getProperty()).as(String.class), (String) valueArray[0]);
			}
		} else if ("in".equals(se.getMatchMode())) {
			predicate = root.get(se.getProperty()).in(valueArray);
		} else if (se.getMatchMode().equals(SQLExpression.isnull)) {
			predicate = root.get(se.getProperty()).isNull();
		}  else if (se.getMatchMode().equals(SQLExpression.isnotnul)) {
			predicate = root.get(se.getProperty()).isNotNull();
		} else {
			predicate = cb.equal(root.get(se.getProperty()).as(type), valueArray[0]);
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
	 * 根据SQLFilter对象生成对应的DetachedCriteria对象
	 * 
	 * @param c
	 *            Class查询对象的类型
	 * @param filter
	 *            SQLFilter SQL查询条件
	 * @return DetachedCriteria filter对应的DetachedCriteria对象
	 */
	public static DetachedCriteria sqlFilterToDc(Class<?> c, SQLFilter filter) {
		DetachedCriteria dc = DetachedCriteria.forClass(c);
		if (filter != null && filter.getSesl() != null) {
			// 拼装查询条件时用到，查询属性的类型由查询的实体属性类型决定(当前只处理BigDecimal等带小数的属性)；2014.12.11
			cls = c;
			// 创建关联查询别名
			sqlAliasToDc(dc, filter);
			Criterion criterion = sqlFilterToDc(filter);
			if (criterion != null) {
				dc.add(criterion);
			}
			List<SQLOrder> sol = filter.getOrderList();
			for (SQLOrder order : sol) {
				if (order.getDirection() == null
						|| order.getDirection().equalsIgnoreCase(SQLOrder.ASC)) {
					dc.addOrder(Order.asc(order.getProperty()));
				} else {
					dc.addOrder(Order.desc(order.getProperty()));
				}
			}
		}
		return dc;
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
		}  else if (se.getMatchMode().equals(SQLExpression.isnotnul)) {
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

	private static void sqlAliasToPredicate(Root<?> root, SQLFilter filter) {
		// a.b.c.para==>a别名a a.b别名b b.c别名c ===》查询属性修改为c.para
		// 每个级别的别名都为对应点的前一个值
		// 所有的查询属性最终将只保留最近的一个xxx.para
		// 按上述的规则。理论上支持无限级别的 关联查询
		// 查询条件整理:按不同的查询关联级别，将不同关联对象的查询属性 ，按对象名整合到一起，避免创建多个相同别名 而报错
		List<List<String>> propertys = new ArrayList<List<String>>();
		propertys = settlePropertyInfo(filter);
		// 创建别名
		for (List<String> list : propertys) {
			// 确保是重第一级别的关联 往 高级别的关联创建别名
			for (String pro : list) {
				if (pro.indexOf(".") > 0) {
					String[] keys = pro.split("\\.");
					
					root.get(pro).alias(keys[1]);
				} else {
					root.get(pro).alias(pro);
				}
			}
		}
		// 修改查询属性，替换成由别名指向查询属性
		converpara(filter);
	}

	/**
	 * 根据SQLFilter数据， 创建DetachedCriteria 关联查询的相关别名
	 * 
	 * @param criteria
	 * @param filter
	 */
	private static void sqlAliasToDc(DetachedCriteria criteria, SQLFilter filter) {
		// a.b.c.para==>a别名a a.b别名b b.c别名c ===》查询属性修改为c.para
		// 每个级别的别名都为对应点的前一个值
		// 所有的查询属性最终将只保留最近的一个xxx.para
		// 按上述的规则。理论上支持无限级别的 关联查询
		// 查询条件整理:按不同的查询关联级别，将不同关联对象的查询属性 ，按对象名整合到一起，避免创建多个相同别名 而报错
		List<List<String>> propertys = new ArrayList<List<String>>();
		propertys = settlePropertyInfo(filter);
		// 创建别名
		for (List<String> list : propertys) {
			// 确保是重第一级别的关联 往 高级别的关联创建别名
			for (String pro : list) {
				if (pro.indexOf(".") > 0) {
					String[] keys = pro.split("\\.");
					criteria.createAlias(pro, keys[1]);
				} else {
					criteria.createAlias(pro, pro);
				}
			}
		}
		// 修改查询属性，替换成由别名指向查询属性
		converpara(filter);
	}

	private static void converpara(SQLFilter filter) {
		List<SQLExpressionSet> sel = filter.getSesl();
		for (SQLExpressionSet sqlExpressionSet : sel) {
			List<SQLExpression> selEx = sqlExpressionSet.getSel();
			for (SQLExpression sqlExpression : selEx) {
				String pro = sqlExpression.getProperty();
				String[] keys = pro.split("\\.");
				if (keys.length > 1) {
					int i = keys.length;
					String newPara = keys[i - 2] + "." + keys[i - 1];
					sqlExpression.setProperty(newPara);
				}
			}
		}

		List<SQLOrder> orders = filter.getOrderList();
		for (SQLOrder sqlOrder : orders) {
			String pro = sqlOrder.getProperty();
			String[] keys = pro.split("\\.");
			if (keys.length > 1) {
				int i = keys.length;
				String newPara = keys[i - 2] + "." + keys[i - 1];
				sqlOrder.setProperty(newPara);
			}
		}

	}

	private static List<List<String>> settlePropertyInfo(SQLFilter filter) {
		List<List<String>> lis = new ArrayList<List<String>>();
		List<SQLExpressionSet> sel = filter.getSesl();
		for (SQLExpressionSet sqlExpressionSet : sel) {
			List<SQLExpression> selEx = sqlExpressionSet.getSel();
			for (SQLExpression sqlExpression : selEx) {
				checkProperty(lis, sqlExpression.getProperty());
			}
		}
		List<SQLOrder> orders = filter.getOrderList();
		for (SQLOrder sqlOrder : orders) {
			checkProperty(lis, sqlOrder.getProperty());
		}

		return lis;
	}

	private static void checkProperty(List<List<String>> lis, String name) {
		int ind = 0;
		String[] level = name.split("\\.");
		ind = level.length - 1;
		for (int i = 0; i < ind; i++) {
			// 当前的lis中包含了几个级别的关联 别名
			int lisNub = lis.size();
			if (lisNub < i + 1) {
				// 当lis中的保持的关联级别少于当前的查询属性时，创建关联级别
				List<String> newL = new ArrayList<String>();
				lis.add(newL);
			}

			// 当前关联级别 的别名
			String nameB = "";
			if (i == 0) {
				nameB = level[i];
			} else {
				nameB = level[i - 1] + "." + level[i];
			}
			// lis中 当前关联级别的别名中是否有 满足当前查询属性的别名，没有就创建
			Boolean hasExist = false;
			List<String> lisI = lis.get(i);
			for (String map : lisI) {
				if (map.contains(nameB)) {
					hasExist = true;
				}
			}
			if (!hasExist && nameB != "") {
				lis.get(i).add(nameB);
			}

		}

	}

}
