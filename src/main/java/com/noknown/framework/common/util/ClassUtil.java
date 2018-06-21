package com.noknown.framework.common.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author guodong
 */
public class ClassUtil {


	/**
	 * 通过URLClassLoader加载类，并初始化实例
	 */
	public static Object loadInstance(String path, String name){
		Object instance = null;
		URLClassLoader classLoader = null;
		URL url;
		try {
			url = new URL(path);
			classLoader = new URLClassLoader(new URL[]{url}, Thread.currentThread()
					.getContextClassLoader());
			Class<?> myClass = classLoader.loadClass(name);
			instance = myClass.newInstance();
		} catch (MalformedURLException | ClassNotFoundException | InstantiationException | IllegalAccessException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if (classLoader != null) {
					classLoader.close();
				}
			} catch (IOException ignore) {
			}
		}
		return instance;
	}

	/**
	 * 通过URLClassLoader加载类
	 */
	public static Class<?> loadClass(String path, String name){
		Class<?> myClass = null;
		URLClassLoader classLoader = null;
		URL url;
		try {
			url = new URL(path);
			classLoader = new URLClassLoader(new URL[]{url}, Thread.currentThread()
					.getContextClassLoader());
			myClass = classLoader.loadClass(name);
		} catch (MalformedURLException | ClassNotFoundException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if (classLoader != null) {
					classLoader.close();
				}
			} catch (IOException ignore) {
			}
		}
		return myClass;
	}

	/**
	 * 从jar文件中读取指定目录下面的所有的class文件
	 *
	 * @param jarPaht  jar文件存放的位置
	 * @param filePaht 指定的文件目录
	 * @return 所有的的class的对象
	 */
	public static List<Class> getClasssFromJarFile(String jarPaht, String filePaht) {
		List<Class> clazzs = new ArrayList<>();

		URLClassLoader classLoader = null;
		JarFile jarFile = null;
		try {
			URL url = new URL(jarPaht);
			String filePath = URLDecoder.decode(url.getFile(), "UTF-8");

			classLoader = new URLClassLoader(new URL[]{url}, Thread.currentThread()
					.getContextClassLoader());

			jarFile = new JarFile(filePath);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		List<JarEntry> jarEntryList = new ArrayList<>();

		if (jarFile != null) {
			Enumeration<JarEntry> ee = jarFile.entries();
			while (ee.hasMoreElements()) {
				JarEntry entry = ee.nextElement();
				// 过滤我们出满足我们需求的东西
				if (entry.getName().startsWith(filePaht) && entry.getName().endsWith(".class")) {
					jarEntryList.add(entry);
				}
			}
		}

		if (classLoader != null) {
			for (JarEntry entry : jarEntryList) {
				String className = entry.getName().replace('/', '.');
				className = className.substring(0, className.length() - 6);

				try {
					clazzs.add(classLoader.loadClass(className));
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		try {
			if (classLoader != null) {
				classLoader.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return clazzs;
	}
}
