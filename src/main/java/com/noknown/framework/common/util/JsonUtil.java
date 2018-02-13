package com.noknown.framework.common.util;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 处理Json String的工具类，解析字符串生成对象，把对象输出JSON字符串等
 * 
 * @author guodong
 */
public class JsonUtil {

	private static final Logger logger = LoggerFactory
			.getLogger(JsonUtil.class);

	/**
	 * Json 转成 Map对象
	 * 
	 * @param body
	 *            Json字符串
	 * @return Map对象
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> toMap(String body) {
		try {
			body = URLDecoder.decode(body, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		ObjectMapper om = new ObjectMapper();
		Map<String, Object> map = null;
		try {
			map = om.readValue(body, Map.class);
		} catch (JsonParseException e) {
			logger.warn(e.getLocalizedMessage());
		} catch (JsonMappingException e) {
			logger.warn(e.getLocalizedMessage());
		} catch (IOException e) {
			logger.warn(e.getLocalizedMessage());
		}
		return map;
	}


	/**
	 * 解析Json string生成c类型的对象 只支持UTF-8编码的字符串
	 * 
	 * @param body
	 *            String json字符串
	 * @param clazz
	 *            Class要生成的对象类型
	 * @return Object 解析生成的对象，解析不成功返回null，错误记录在日志中。
	 */
	public static <T> T toObject(String body, Class<T> clazz) {
		try {
			body = URLDecoder.decode(body, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			logger.warn(e1.getLocalizedMessage());
		}
		ObjectMapper om = new ObjectMapper();
		om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			return om.readValue(body, clazz);
		} catch (JsonParseException e) {
			logger.warn(e.getLocalizedMessage());
		} catch (JsonMappingException e) {
			logger.warn(e.getLocalizedMessage());
		} catch (IOException e) {
			logger.error(e.getLocalizedMessage());
		}
		return null;
	}

	/**
	 * Map 转换为 对象
	 * 
	 * @param cla
	 *            类
	 * @param map
	 *            需要转换的map对象
	 * @return Object 对象
	 */
	public static Object mapToBean(Class<?> cla, Map<String, Object> map) {
		Field[] fields = cla.getDeclaredFields();
		String fname = null;
		Method method = null;
		Object obj = null;
		Object bean = null;

		try {
			bean = Class.forName(cla.getName()).newInstance();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		if (bean != null) {
			for (int i = 0; i < fields.length; i++) {
				fname = fields[i].getName();
				if (map.containsKey(fname)) {
					obj = map.get(fname);
					try {
						if (obj == null) {
							method = cla.getMethod("set"
									+ fname.substring(0, 1).toUpperCase()
									+ fname.substring(1), fields[i].getType());
						} else {
							method = cla.getMethod("set"
									+ fname.substring(0, 1).toUpperCase()
									+ fname.substring(1),
									new Class[] { obj.getClass() });
						}
						method.invoke(bean, new Object[] { obj });
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			}
		}

		return bean;

	}

	/**
	 * 将map内的数据添加到对象中
	 * 
	 * @param object
	 *            需要操作的对象
	 * @param map
	 *            存放的map
	 */
	public static void assignByMap(Object object, Map<String, Object> map) {
		Class<?> type = object.getClass();
		Class<?> attrType = null;
		List<Field> fields = new ArrayList<Field>();

		for (Class<?> clazz = object.getClass(); clazz != Object.class; clazz = clazz
				.getSuperclass()) {
			for (Field f : clazz.getDeclaredFields()) {
				fields.add(f);
			}
		}

		String fname = null;
		Method method = null;
		Object obj = null;
		for (int i = 0; i < fields.size(); i++) {
			fname = fields.get(i).getName();
			if (map.containsKey(fname)) {
				obj = map.get(fname);
				//FIXME obj == null
				attrType = fields.get(i).getType();
				if (attrType.equals(long.class) || attrType.equals(Long.class)) {
					if (obj != null) {
						obj = Long.parseLong(obj.toString());
					}
				} else if (attrType.equals(BigDecimal.class)) {
					if (obj != null) {
						obj = new BigDecimal(obj.toString());
					}
				} else if (attrType.equals(BigInteger.class)) {
					if (obj != null) {
						obj = new BigInteger(obj.toString());
					}
				} else if (attrType.equals(Double.class) || attrType.equals(double.class)) {
					if (obj != null) {
						obj = Double.parseDouble(obj.toString());
					}
				} else if (attrType.equals(Float.class) || attrType.equals(float.class)) {
					if (obj != null) {
						obj = Float.parseFloat(obj.toString());
					}
				} else if (attrType.equals(Boolean.class) || attrType.equals(boolean.class)) {
					if (obj != null) {
						obj = Boolean.parseBoolean(obj.toString());
					}
				} else if (attrType.equals(Integer.class) || attrType.equals(int.class)) {
					if (obj != null) {
						obj = Integer.parseInt(obj.toString());
					}
				} else if (attrType.equals(Boolean.class) || attrType.equals(boolean.class)) {
					if (obj != null) {
						obj = Boolean.parseBoolean(obj.toString());
					}
				} 
				try {
					method = type.getMethod(
							"set" + fname.substring(0, 1).toUpperCase()
									+ fname.substring(1), fields.get(i)
									.getType());
					method.invoke(object, new Object[] { obj });
				} catch (NoSuchMethodException e) {
					logger.warn(e.getLocalizedMessage());
				} catch (SecurityException e) {
					logger.warn(e.getLocalizedMessage());
				} catch (IllegalAccessException e) {
					logger.warn(e.getLocalizedMessage());
				} catch (IllegalArgumentException e) {
					logger.warn(e.getLocalizedMessage());
				} catch (InvocationTargetException e) {
					logger.warn(e.getLocalizedMessage());
				} catch (java.lang.NullPointerException e) {
					logger.warn(e.getLocalizedMessage());
				}
			}
		}

	}

	/**
	 * 解析JSON 生成字符串数组
	 * 
	 * @Param body
	 * @return String[]
	 */
	public static String[] toStringArray(String body) {
		try {
			body = URLDecoder.decode(body, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		body = body.replace("[", "");
		body = body.replace("]", "");
		body = body.replaceAll("\"", "");
		String[] strs = body.split(",");

		return strs;
	}


	/**
	 * 解析Json string生成c类型的对象列表 只支持UTF-8编码的字符串
	 * 
	 * @param body
	 *            String json字符串
	 * @param clazz
	 *            Class要生成的对象类型
	 * @return <T> List<T> 解析生成的对象，解析不成功返回null，错误记录在日志中。
	 */
	public static <T> List<T> toList(String body, Class<?> clazz) {
		try {
			body = URLDecoder.decode(body, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			logger.warn(e1.getLocalizedMessage());
		}
		ObjectMapper om = new ObjectMapper();
		TypeFactory t = TypeFactory.defaultInstance();
		try {
			List<T> list = om.readValue(body,
					t.constructCollectionType(ArrayList.class, clazz));
			return list;
		} catch (JsonParseException e) {
			logger.warn(e.getLocalizedMessage());
		} catch (JsonMappingException e) {
			logger.warn(e.getLocalizedMessage());
		} catch (IOException e) {
			logger.error(e.getLocalizedMessage());
		}
		return null;
	}

	/**
	 * 把JavaBean转换为json字符串 (1)普通对象转换：toJson(Student) (2)List转换：toJson(List)
	 * (3)Map转换:toJson(Map) 我们发现不管什么类型，都可以直接传入这个方法
	 * 
	 * @param object
	 *            JavaBean对象
	 * @return json字符串
	 */
	public static String toJson(Object object) {
		ObjectMapper om = new ObjectMapper();
		try {
			return om.writeValueAsString(object);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
