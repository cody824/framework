/**
 * 
 */
package com.noknown.framework.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.List;

public class ObjectUtil {
	
	private static final Logger logger = LoggerFactory
			.getLogger(ObjectUtil.class);
	
	public static final int lockRetry = 50;

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
		File file = new File(path);
		if (!file.exists()) {
			File parentFile = file.getParentFile();
			if (!parentFile.exists()) {  
				parentFile.mkdirs();  
            } 
			file.createNewFile();
		}
		FileOutputStream out;
		ObjectOutputStream objOut = null;
		FileChannel channel = null;  
		FileLock lock = null; 
		int n = lockRetry;

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
				if (channel != null)
					channel.close();
				if (objOut != null)
					objOut.close();
			} catch (IOException e) {
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
		Object temp = null;
		FileInputStream in;
		ObjectInputStream objIn = null;
		FileChannel channel = null;  
		FileLock lock = null;  
		int n = lockRetry;
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
				if (channel != null)
					channel.close();
				if (objIn != null)
					objIn.close();
			} catch (IOException e) {
			}
		}
		return temp;
	}
	
	public static void updateObjectInFile(String path, Object obj) throws IOException{
		Object src = null;
		RandomAccessFile raf = null;
		FileChannel channel = null;  
		FileLock lock = null;  
		int n = lockRetry;
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
		} catch (FileNotFoundException e) {
		} finally {
			try {
				if (lock != null) {
					lock.release();
					logger.debug("释放读写文件锁（" + lock.hashCode() + ")");  
				}
				if (channel != null)
					channel.close();
				if (raf != null)
					raf.close();
			} catch (IOException e) {
			}
		}
	}
	
	
	   /**  
     * 对象转数组  
     * @param obj  
     * @return  
     */  
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
       
    /**  
     * 数组转对象  
     * @param bytes  
     * @return  
     */  
    public static Object toObject (byte[] bytes) {      
        Object obj = null;      
        try {        
            ByteArrayInputStream bis = new ByteArrayInputStream (bytes);        
            ObjectInputStream ois = new ObjectInputStream (bis);        
            obj = ois.readObject();      
            ois.close();   
            bis.close();   
        } catch (IOException ex) {        
            ex.printStackTrace();   
        } catch (ClassNotFoundException ex) {        
            ex.printStackTrace();   
        }      
        return obj;    
    }   
	
	/**
	 * 通过get，set方法拷贝对象
	 * 		要求对象中属性不能有基本类型，通过null判断是否对属性进行copy
	 * @param obj1	目标
	 * @param obj2	源
	 * @param ignoreFs 强制忽略的属性
	 * @return
	 */
	public static Object copy(Object obj1, Object obj2, List<String> ignoreFs) {
		Class<?> type = obj1.getClass();
		Class<?> type2 = obj2.getClass();
		List<Field> fields = new ArrayList<Field>();

		for (Class<?> clazz = obj1.getClass(); clazz != Object.class; clazz = clazz
				.getSuperclass()) {
			for (Field f : clazz.getDeclaredFields()) {
				fields.add(f);
			}
		}

		String fname = null;
		Method getMethod = null;
		Method setMethod = null;
		Object value = null;
		for (int i = 0; i < fields.size(); i++) {
			try {
				fname = fields.get(i).getName();
				if(ignoreFs != null && ignoreFs.contains(fname)){
					continue;
				}

				getMethod = type2.getMethod(
						"get" + fname.substring(0, 1).toUpperCase()
								+ fname.substring(1));
				if (getMethod != null)
					value = getMethod.invoke(obj2);

				if (value != null) {
					setMethod = type.getMethod(
							"set" + fname.substring(0, 1).toUpperCase()
									+ fname.substring(1), fields.get(i)
									.getType());
					setMethod.invoke(obj1, new Object[] { value });
				}
			} catch (NoSuchMethodException e) {
			} catch (SecurityException e) {
				logger.warn(e.getLocalizedMessage());
			} catch (IllegalAccessException e) {
				logger.warn(e.getLocalizedMessage());
			} catch (IllegalArgumentException e) {
				logger.warn(e.getLocalizedMessage());
			} catch (InvocationTargetException e) {
				logger.warn(e.getLocalizedMessage());
			}
		}
		return obj1;
	}
}
