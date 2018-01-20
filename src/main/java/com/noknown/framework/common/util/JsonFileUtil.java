/**
 * @Title: XmlUtil.java
 * @Package com.soulinfo.commons.util
 * @Description: 基础工具类
 * CopyRright (c) 2014-2015 SOUL
 * Company:无锡众志和达数据计算股份有限公司
 * 
 * @author xingweiwei
 * @date 2015年5月19日 下午3:05:54
 * @version V0.0.1
 */
package com.noknown.framework.common.util;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class JsonFileUtil {

	private static final Logger logger = LoggerFactory.getLogger(JsonFileUtil.class);

	/**
	 * 写对象到xml文件
	 * 
	 * @param path
	 *            文件路径
	 * @param obj
	 *            对象
	 * @throws IOException 工具类异常
	 */
	public static void writeToJsonFile(String path, Object obj) throws IOException {
		File file = new File(path);
		if (!file.exists()) {
				File parentFile = file.getParentFile();
				if (!parentFile.exists()) {  
					parentFile.mkdirs();  
	            } 
				file.createNewFile();
		}
		FileOutputStream os = null;
		FileChannel channel = null;
		FileLock lock = null;
		try {
			ObjectMapper om = new ObjectMapper();
			os = new FileOutputStream(file);
			channel = os.getChannel();
			while (true) {
				try {
					lock = channel.tryLock();
					break;
				} catch (Exception e) {
					logger.debug("有其他线程正在操作" + path + "文件，当前写线程休眠500毫秒");
					try {
						Thread.sleep(500);
					} catch (InterruptedException e1) {
						logger.warn(e.getLocalizedMessage());
					}
				}
			}
			om.writeValue(os, obj);
		} finally {
			try {
				if (lock != null)
					lock.release();
				if (channel != null)
					channel.close();
				if (os != null)
					os.close();
			} catch (IOException e) {
			}
		}
	}

	/**
	 * 从json格式的文件中读取对象
	 * 
	 * @param path
	 *            文件路径
	 * @param c
	 *            类
	 * @return 读取的对象
	 * @throws IOException 工具类异常
	 */
	public static Object readObjectFromJsonFile(String path, Class<?> c) throws JsonParseException, JsonMappingException, IOException {
		FileInputStream is = null;
		FileChannel channel = null;
		FileLock lock = null;
		
		try {
			ObjectMapper om = new ObjectMapper();
			is = new FileInputStream(path);
			channel = is.getChannel();
			while (true) {
				try {
					lock = channel.tryLock(0, Long.MAX_VALUE, true);
					break;
				} catch (Exception e) {
					logger.debug("有其他线程正在操作" + path + "文件，当前读线程休眠500毫秒");
					try {
						Thread.sleep(500);
					} catch (InterruptedException e1) {
						logger.warn(e1.getLocalizedMessage());
					}
				}
			}
			om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			return om.readValue(is, c);
		} catch (FileNotFoundException e) {
			return null;
		} finally {
			try {
				if (lock != null)
					lock.release();
				if (channel != null)
					channel.close();
				if (is != null)
					is.close();
			} catch (IOException e) {
			}
		}
	}
}
