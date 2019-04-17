package com.noknown.framework.common.util;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.*;

/**
 * @author guodong
 */
public class ObjectUtil {

	private static final Logger logger = LoggerFactory
			.getLogger(ObjectUtil.class);

	private static final int LOCK_RETRY = 50;

	/**
	 * 写对象到对象文件
	 *
	 * @param path
	 *            文件路径
	 * @param obj
	 *            对象
	 * @throws IOException
	 *             工具类异常
	 */
	public static void writeToFile(String path, Object obj) throws IOException {
		File file = BaseUtil.createFile(path);
		if (file == null) {
			throw new IOException("创建文件失败");
		}
		FileOutputStream out;
		ObjectOutputStream objOut = null;
		FileChannel channel = null;
		FileLock lock = null;
		int n = LOCK_RETRY;

		try {
			out = new FileOutputStream(file);
			channel = out.getChannel();
			while(n-- > 0){
				try {
					lock = channel.tryLock();
					logger.debug("获得写文件锁（" + file.getName() + ":" + lock.hashCode() + ")");
					break;
				} catch (Exception e) {
					logger.debug("获得写文件锁失败（" + file.getName() + ")有其他线程正在操作该文件，当前线程休眠500毫秒");
					try {
						Thread.sleep(500);
					} catch (InterruptedException e1) {
						logger.warn(e.getLocalizedMessage());
					}
				}
			}
			objOut = new ObjectOutputStream(out);
			objOut.writeObject(obj);
			objOut.flush();
		} finally {
			try {
				if (lock != null) {
					lock.release();
					logger.debug("释放写文件锁（" + lock.hashCode() + ")");
				}
				IOUtils.closeQuietly(channel);
				IOUtils.closeQuietly(objOut);
			} catch (IOException ignored) {
			}
		}
	}

	/**
	 * 从对象文件中读取对象
	 *
	 * @param path
	 *            文件路径
	 * @return 读取的对象
	 * @throws IOException
	 *             工具类异常
	 */
	public static Object readObjectFromFile(String path) throws IOException {
		Object temp;
		FileInputStream in;
		ObjectInputStream objIn = null;
		FileChannel channel = null;
		FileLock lock = null;
		int n = LOCK_RETRY;
		try {
			File file = new File(path);
			in = new FileInputStream(file);
			channel = in.getChannel();
			while(n-- > 0){
				try {
					lock = channel.tryLock(0,Long.MAX_VALUE,true);
					logger.debug("获得读文件锁（" + file.getName() + ":" + lock.hashCode() + ")");
					break;
				} catch (Exception e) {
					logger.debug("获得读文件锁失败（" + file.getName() + ")有其他线程正在操作该文件，当前线程休眠500毫秒");
					try {
						Thread.sleep(500);
					} catch (InterruptedException e1) {
						logger.warn(e.getLocalizedMessage());
					}
				}
			}
			objIn = new ObjectInputStream(in);
			temp = objIn.readObject();
		} catch (ClassNotFoundException e) {
			return null;
		} finally {
			try {
				if (lock != null) {
					lock.release();
					logger.debug("释放读文件锁（" + lock.hashCode() + ")");
				}
				if (channel != null) {
					channel.close();
				}
				if (objIn != null) {
					objIn.close();
				}
			} catch (IOException ignored) {
			}
		}
		return temp;
	}

	public static void updateObjectInFile(String path, Object obj) throws IOException {
		Object src;
		RandomAccessFile raf = null;
		FileChannel channel = null;
		FileLock lock = null;
		int n = LOCK_RETRY;
		try {
			File file = new File(path);
			if (!file.exists()) {
				ObjectUtil.writeToFile(path, obj);
				return;
			}
			raf = new RandomAccessFile(file, "rw");
			channel = raf.getChannel();
			while(n-- > 0){
				try {
					lock = channel.tryLock();
					logger.debug("获得读写文件锁（" + file.getName() + ":" + lock.hashCode() + ")");
					break;
				} catch (Exception e) {
					logger.debug("获得读写文件锁失败（" + file.getName() + ")有其他线程正在操作该文件，当前线程休眠500毫秒");
					try {
						Thread.sleep(500);
					} catch (InterruptedException e1) {
						logger.warn(e.getLocalizedMessage());
					}
				}
			}
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			while((raf.read(buf))!=-1){
				bos.write(buf);
				buf = new byte[1024];
			}
			byte[] objBs = bos.toByteArray();
			src = ObjectUtil.toObject(objBs);
			copy(src, obj, null);
			byte[] newObjBs = ObjectUtil.toByteArray(src);
			raf.seek(0);
			raf.setLength(newObjBs.length);
			raf.write(newObjBs);
		} catch (FileNotFoundException ignored) {
		} finally {
			try {
				if (lock != null) {
					lock.release();
					logger.debug("释放读写文件锁（" + lock.hashCode() + ")");
				}
				if (channel != null) {
					channel.close();
				}
				if (raf != null) {
					raf.close();
				}
			} catch (IOException ignored) {
			}
		}
	}


	public static byte[] toByteArray (Object obj) {
		byte[] bytes = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(obj);
			oos.flush();
			bytes = bos.toByteArray ();
			oos.close();
			bos.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return bytes;
	}

	public static Object toObject (byte[] bytes) {
		Object obj = null;
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream (bytes);
			ObjectInputStream ois = new ObjectInputStream (bis);
			obj = ois.readObject();
			ois.close();
			bis.close();
		} catch (IOException | ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		return obj;
	}

	/**
	 * 通过get，set方法拷贝对象
	 * 		要求对象中属性不能有基本类型，通过null判断是否对属性进行copy
	 * @param target    目标
	 * @param src    源
	 * @param ignoreFs 强制忽略的属性
	 * @return 拷贝完成的对象
	 */
	public static Object copy(Object target, Object src, List<String> ignoreFs) {
		Class<?> type = target.getClass();
		Class<?> type2 = src.getClass();
		List<Field> fields = new ArrayList<>();

		for (Class<?> clazz = target.getClass(); clazz != Object.class; clazz = clazz
				.getSuperclass()) {
			Collections.addAll(fields, clazz.getDeclaredFields());
		}

		String fname;
		Method getMethod;
		Method setMethod;
		Object value = null;
		for (Field field : fields) {
			try {
				fname = field.getName();
				if (ignoreFs != null && ignoreFs.contains(fname)) {
					continue;
				}

				getMethod = type2.getMethod(
						"get" + fname.substring(0, 1).toUpperCase()
								+ fname.substring(1));
				if (getMethod != null) {
					value = getMethod.invoke(src);
				}

				if (value != null) {
					setMethod = type.getMethod(
							"set" + fname.substring(0, 1).toUpperCase()
									+ fname.substring(1), field
									.getType());
					setMethod.invoke(target, value);
				}
			} catch (NoSuchMethodException ignored) {
			} catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				logger.warn(e.getLocalizedMessage());
			}
		}
		return target;
	}

	/**
	 * 将map内的数据添加到对象中
	 *
	 * @param object 需要操作的对象
	 * @param map    存放的map
	 */
	public static void assignByMap(Object object, Map<String, ?> map) {
		Class<?> type = object.getClass();
		Class<?> attrType;
		List<Field> fields = new ArrayList<>();

		for (Class<?> clazz = object.getClass(); clazz != Object.class; clazz = clazz
				.getSuperclass()) {
			fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
		}

		String fname;
		Method method;
		for (Field field : fields) {
			fname = field.getName();
			if (map.containsKey(fname)) {
				Object obj = map.get(fname);
				attrType = field.getType();
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
									+ fname.substring(1), field
									.getType());
					method.invoke(object, obj);
				} catch (Exception e) {
					logger.warn(e.getLocalizedMessage());
				}
			}
		}

	}
}
