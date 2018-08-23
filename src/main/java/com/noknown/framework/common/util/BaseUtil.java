package com.noknown.framework.common.util;

import com.noknown.framework.common.exception.UtilException;
import com.noknown.framework.common.util.algo.RandomString;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

/**
 * 基础工具类
 * 
 * @author guodong
 * 
 */
public class BaseUtil{

	/**
	 * 生成新的UUID字符串 没有中间的“-”符号
	 * 
	 * @return String UUID字符串
	 */
	public static String getUUID() {
		String s = UUID.randomUUID().toString();
		// 去掉“-”符号
		return s.substring(0, 8) + s.substring(9, 13) + s.substring(14, 18)
				+ s.substring(19, 23) + s.substring(24);
	}

	/**
	 * 获取10位的随机码
	 * 
	 */
	@SuppressWarnings("deprecation")
	public static String getIdentifyCode() {
		String identifyCode;
		Date date = new Date();
		int year = date.getYear() + (new Random().nextInt(1100));
		int month = date.getMonth() * (new Random().nextInt(10));
		int hou = date.getHours() + (new Random().nextInt(10));
		int min = date.getMinutes() + (new Random().nextInt(10));
		int sec = date.getSeconds() * (new Random().nextInt(10));
		int num = new Random().nextInt(1000);
		StringBuffer buffer = new StringBuffer();
		buffer = buffer.append(year).append(month).append(hou)
				.append(min).append(sec).append(num);
		String code = buffer.toString();
		Long long1 = Long.parseLong(code);
		identifyCode = Long.toHexString(long1);
		return identifyCode;
	}

	/**
	 * 获取指定时间的随机码
	 * 
	 */
	public static String getTimeCode(Date time) {
		String timesplit = Long.toString(time.getTime()).substring(8, 11);
		return DateUtil.toString(time, DateUtil.ACCOUNT_DATE_FORMAT)
				+ "00"
				+ timesplit
				+ RandomString.randomNumber(2);
	}

	/**
	 * 获取进程号
	 */
	public static int getPid() {
		RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
		String name = runtime.getName();
		try {
			return Integer.parseInt(name.substring(0, name.indexOf('@')));
		} catch (Exception e) {
			return -1;
		}
	}

	/**
	 * 获取路径
	 * 
	 * @return 路径
	 */
	public static String getClassPath() {
		try {
			return Thread.currentThread().getContextClassLoader()
					.getResource("").toURI().getPath();
		} catch (URISyntaxException e) {
			return Thread.currentThread().getContextClassLoader()
					.getResource("").getFile();
		}
	}

	/**
	 * 根据相对路径获取配置
	 * 
	 * @param filePath
	 *            路径名
	 * @return 配置
	 * @throws UtilException
	 *             工具类异常
	 */
	public static Properties loadProperties(String filePath)
			throws UtilException {
		String path;
		path = getClassPath() + "/" + filePath;
		String dirPath = path.substring(0, path.lastIndexOf("/"));
		File dir = new File(dirPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return loadPropertiesFromRealPath(path);
	}

	/**
	 * 根据相对路径保存配置
	 * 
	 * @param pro
	 *            配置
	 * @param filePath
	 *            路径名
	 * @throws UtilException
	 *             工具类异常
	 */
	public static void saveProperties(Properties pro, String filePath)
			throws UtilException {
		String path = getClassPath() + "/" + filePath;
		File file = new File(path);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				throw new UtilException(e);
			}
		}
		try {
			Writer fw = new FileWriter(file);
			pro.store(fw, null);
			fw.close();
		} catch (IOException e) {
			throw new UtilException(e);
		}
	}

	/**
	 * 根据文件绝对路径读取配置
	 * 
	 * @param filePath
	 *            文件路径
	 * @return 配置
	 * @throws UtilException
	 *             工具类异常
	 */
	public static Properties loadPropertiesFromRealPath(String filePath)
			throws UtilException {
		File file = new File(filePath);
		if (!file.exists()) {
			try {
				BaseUtil.createFile(filePath);
			} catch (IOException e) {
				throw new UtilException(e);
			}
		}
		return loadPropertiesFromFile(file);
	}

	public static Properties loadPropertiesFromRealPath(String filePath, boolean isCreate)
			throws UtilException {
		File file = new File(filePath);
		if (!file.exists() && isCreate) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				throw new UtilException(e);
			}
		}
		return loadPropertiesFromFile(file);
	}

	/**
	 * 从文件读取配置
	 * 
	 * @param file
	 *            文件
	 * @return 配置
	 * @throws UtilException
	 *             工具类异常
	 */
	public static Properties loadPropertiesFromFile(File file)
			throws UtilException {
		Properties pro = new OrderProperties();
		InputStreamReader reader = null;
		try {
			reader = new InputStreamReader(new FileInputStream(file), "utf8");
			pro.load(reader);
		} catch (FileNotFoundException e) {
			return pro;
		} catch (IOException e) {
			throw new UtilException(e);
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return pro;
	}

	/**
	 * 根据文件绝对路径保存配置到文件
	 * 
	 * @param pro
	 *            配置
	 * @param filePath
	 *            文件路径
	 * @throws UtilException
	 *             工具类异常
	 */
	public static void savePropertiesToRealPath(Properties pro, String filePath)
			throws UtilException {
		File file = new File(filePath);
		if (!file.exists()) {
			try {
				BaseUtil.createFile(filePath);
			} catch (IOException e) {
				throw new UtilException(e);
			}
		}
		try {
			Writer fw = new FileWriter(file);
			pro.store(fw, null);
			fw.close();
		} catch (IOException e) {
			throw new UtilException(e);
		}
	}

	/**
	 * 写数据到文件
	 * 
	 * @param filePath
	 *            文件路径
	 * @param data
	 *            所要写的数据
	 * @throws UtilException
	 *             工具类异常
	 */
	public static void writeDateToFile(String filePath, String data)
			throws UtilException {
		File file = new File(filePath);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				throw new UtilException(e);
			}
		}
		try {
			Writer fw = new FileWriter(file);
			fw.write(data);
			fw.flush();
			fw.close();
		} catch (IOException e) {
			throw new UtilException(e);
		}
	}

	/**
	 * 创建目标文件
	 *
	 */
	public static File createFile(String destFileName) throws IOException {
		File file = new File(destFileName);
		if (file.exists()) {
			return file;
		}
		if (destFileName.endsWith(File.separator)) {
			throw new IOException("创建单个文件" + destFileName + "失败，目标文件不能为目录！");
		}
		// 判断目标文件所在的目录是否存在
		if (!file.getParentFile().exists()) {
			// 如果目标文件所在的目录不存在，则创建父目录
			file.getParentFile().mkdirs();
		}
		if (!file.getParentFile().exists()) {
			throw new IOException("创建目标文件所在目录失败！");
		}
		// 创建目标文件
		if (file.createNewFile()) {
			return file;
		} else {
			return null;
		}
	}

	/**
	 * 递归删除目录下的所有文件及子目录下所有文件
	 * 
	 * @param dir
	 *            将要删除的文件目录
	 * @return boolean Returns "true" if all deletions were successful. If a
	 *         deletion fails, the method stops attempting to delete and returns
	 *         "false".
	 */
	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			// 递归删除目录中的子目录下
			if (children != null) {
				for (String child : children) {
					boolean success = deleteDir(new File(dir, child));
					if (!success) {
						return false;
					}
				}
			}
		}
		// 目录此时为空，可以删除
		return dir.delete();
	}

	/**
	 * 仿深度克隆效果 复制对象
	 */
	public static Object cloneObject(Object obj) throws Exception {
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(byteOut);
		out.writeObject(obj);
		ByteArrayInputStream byteIn = new ByteArrayInputStream(
				byteOut.toByteArray());
		ObjectInputStream in = new ObjectInputStream(byteIn);
		return in.readObject();
	}

	/**
	 * 获取路径解析是否以classpath开头，以classpath开头返回以classpath为起点的真实路劲
	 * 
	 */
	public static String getPath(String path) {
		String ret = path;
		String classpath = "classpath";
		if (path != null && path.startsWith(classpath)) {
			ret = BaseUtil.getClassPath()
					+ path.substring(path.indexOf(":") + 1);
		}
		return ret;
	}

	/**
	 * 读取文件内容到 字符串中
	 *
	 * @param filePath  文件路径
	 * @return 读取失败返回null
	 */
	public static String file2String(String filePath) {
		File file = new File(filePath);
		BufferedReader reader = null;
		StringBuilder result = new StringBuilder();
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString;
			while ((tempString = reader.readLine()) != null) {
				if (result.length() == 0) {
					result.append(tempString);
				} else {
					result.append("\n").append(tempString);
				}
			}
			reader.close();
			return result.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ignore) {
				}
			}
		}
	}
}