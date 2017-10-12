package com.noknown.framework.common.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;


public class ClassUtil {

	/**
	 * 通过URLClassLoader加载类，并初始化实例
	 * @param path
	 * @param name
	 * @return
	 */
	public static Object loadInstance(String path, String name){
		Object instance = null;
		URLClassLoader classLoader = null; 
		URL url;
		try {
			url = new URL(path);
			classLoader = new URLClassLoader(new URL[] { url }, Thread.currentThread()  
	                .getContextClassLoader());  
	        Class<?> myClass = classLoader.loadClass(name);
	        instance = myClass.newInstance();
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} finally {
			try {
				if (classLoader != null)
				classLoader.close();
			} catch (IOException e) {
			}
		}
        return instance;
	}
	
	/**
	 * 通过URLClassLoader加载类
	 * @param path
	 * @param name
	 * @return
	 */
	public static Class<?> loadClass(String path, String name){
		Class<?> myClass = null;
		URLClassLoader classLoader = null; 
		URL url;
		try {
			url = new URL(path);
			classLoader = new URLClassLoader(new URL[] { url }, Thread.currentThread()  
	                .getContextClassLoader());  
			myClass = classLoader.loadClass(name);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (classLoader != null)
				classLoader.close();
			} catch (IOException e) {
			}
		}
        return myClass;
	}
}
