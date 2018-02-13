package com.noknown.framework.common.util;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class XmlUtil {

	private static final Logger logger = LoggerFactory.getLogger(XmlUtil.class);

	/**
	 * 写对象到xml文件
	 * 
	 * @param path
	 *            文件路径
	 * @param obj
	 *            对象
	 * @throws IOException 工具类异常
	 */
	public static void writeToXmlFile(String path, Object obj)
			throws IOException {
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
			XmlMapper xmlMapper = new XmlMapper();
			os = new FileOutputStream(file);
			channel = os.getChannel();
			while (true) {
				try {
					lock = channel.tryLock();
					break;
				} catch (Exception e) {
					logger.debug("有其他线程正在操作该文件，当前线程休眠500毫秒");
					try {
						Thread.sleep(500);
					} catch (InterruptedException e1) {
						logger.warn(e.getLocalizedMessage());
					}
				}
			}
			xmlMapper.setSerializationInclusion(Include.NON_EMPTY);
			xmlMapper.writeValue(os, obj);
		} finally {
			try {
				if (lock != null) {
					lock.release();
				}
				if (channel != null) {
					channel.close();
				}
				if (os != null) {
					os.close();
				}
			} catch (IOException e) {
			}
		}
	}

	/**
	 * 格式化xml文件
	 * 
	 * @param path
	 *            文件路径
	 * @throws IOException 
	 *//*
	public static void formatXmlFile(String path) throws IOException {
		File file = new File(path);
		if (!file.exists()) {
			file.createNewFile();
		}
		FileInputStream fis = null;
		InputStreamReader fsr = null;
		BufferedReader br = null;
		FileOutputStream fos = null;
		OutputStreamWriter ow = null;
		FileChannel channel = null;
		FileLock lock = null;

		try {
			fis = new FileInputStream(file);
			channel = fis.getChannel();
			while (true) {
				try {
					lock = channel.tryLock(0, Long.MAX_VALUE, true);
					break;
				} catch (Exception e) {
					logger.debug("有其他线程正在操作该文件，当前线程休眠500毫秒");
					try {
						Thread.sleep(500);
					} catch (InterruptedException e1) {
						logger.warn(e.getLocalizedMessage());
					}
				}
			}
			fsr = new InputStreamReader(fis);
			br = new BufferedReader(fsr);
			String content = "";
			String line = null;
			while ((line = br.readLine()) != null) {
				content = content + line;
			}
			;
			content = format(content);
			if (lock != null) {
				lock.release();
				lock = null;
			}
			if (channel != null) {
				channel.close();
				channel = null;
			}

			fos = new FileOutputStream(file);
			channel = fos.getChannel();
			while (true) {
				try {
					lock = channel.tryLock();
					break;
				} catch (Exception e) {
					logger.debug("有其他线程正在操作该文件，当前线程休眠500毫秒");
					try {
						Thread.sleep(500);
					} catch (InterruptedException e1) {
						logger.warn(e.getLocalizedMessage());
					}
				}
			}
			ow = new OutputStreamWriter(fos);
			ow.write(content, 0, content.length());
			ow.flush();
		} finally {
			try {
				if (lock != null) {
					lock.release();
					lock = null;
				}
				if (channel != null) {
					channel.close();
					channel = null;
				}
				if (br != null)
					br.close();
				if (fsr != null)
					fsr.close();
				if (fis != null)
					fis.close();
				if (ow != null)
					ow.close();
			} catch (IOException e) {
			}
		}
	}*/

	/**
	 * 格式化xml
	 * 
	 * @param unformattedXml
	 *            未格式化的xml字符串
	 * @return 格式化后的字符串
	 *//*
	public static String format(String unformattedXml) {
		Document document = null;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(unformattedXml));
			document = db.parse(is);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
			OutputFormat format = new OutputFormat(document);
			format.setLineWidth(65);
			format.setIndenting(true);
			Writer out = new StringWriter();
			XMLSerializer serializer = new XMLSerializer(out, format);
			serializer.serialize(document);
			return out.toString();
	}*/

	/**
	 * 从xml格式的字符串中读取对象
	 * 
	 * @param str
	 *            xml格式的字符串
	 * @param c
	 *            类
	 * @return 读取的对象
	 * @throws IOException 
	 *             工具类异常
	 */
	public static Object readObjectFromXmlString(String str, Class<?> c) throws IOException {
		XmlMapper xmlMapper = new XmlMapper();
		Object obj = xmlMapper.readValue(str, c);
		return obj;
	}

	/**
	 * 从xml格式的文件中读取对象
	 * 
	 * @param path
	 *            文件路径
	 * @param c
	 *            类
	 * @return 读取的对象
	 * @throws IOException 
	 *             工具类异常
	 */
	public static Object readObjectFromXmlFile(String path, Class<?> c) throws IOException {
		FileInputStream is = null;
		FileChannel channel = null;
		FileLock lock = null;
		try {
			XmlMapper xmlMapper = new XmlMapper();
			is = new FileInputStream(path);
			channel = is.getChannel();
			while (true) {
				try {
					lock = channel.tryLock(0, Long.MAX_VALUE, true);
					break;
				} catch (Exception e) {
					logger.debug("有其他线程正在操作该文件，当前线程休眠500毫秒");
					try {
						Thread.sleep(500);
					} catch (InterruptedException e1) {
						logger.warn(e.getLocalizedMessage());
					}
				}
			}
			Object object = xmlMapper.readValue(is, c);
			return object;
		} catch (FileNotFoundException e) {
			return null;
		} finally {
			try {
				if (lock != null) {
					lock.release();
				}
				if (channel != null) {
					channel.close();
				}
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
			}
		}
	}
}
