package com.noknown.framework.fss.service.impl;

import com.noknown.framework.common.exception.UtilException;
import com.noknown.framework.common.util.BaseUtil;
import com.noknown.framework.fss.service.FileStoreService;
import org.aspectj.util.FileUtil;

import java.io.*;


public class FileFS implements FileStoreService {

	private String basePath;
	
	private String baseUrl;
	
	public FileFS() {
	}
	
	public FileFS(String basePath, String baseUrl) {
		this.basePath = basePath;
		this.baseUrl = baseUrl;
	}
	
	@Override
	public String put(byte[] data, String key) throws IOException {
		return put(new ByteArrayInputStream(data), key);
	}

	@Override
	public String put(InputStream inputStream, String key) throws IOException {
		File file = new File(BaseUtil.getPath(basePath) + key);
		if (!file.exists())
			try {
				BaseUtil.createFile(BaseUtil.getPath(basePath) + key);
			} catch (UtilException e) {
				e.printStackTrace();
			}
		FileOutputStream fos = new FileOutputStream(BaseUtil.getPath(basePath) + key);
		final int MAX = 4096;
		byte[] buf = new byte[MAX];
		for (int bytesRead = inputStream.read(buf, 0, MAX); bytesRead != -1; bytesRead = inputStream.read(buf, 0, MAX)) {
			fos.write(buf, 0, bytesRead);
		} 
		fos.flush();
		fos.close();
		return baseUrl + key;
	}

	@Override
	public String put(String path, String key) throws IOException {
		File file = new File(path);
		File toFile = new File(BaseUtil.getPath(basePath) + key);
		FileUtil.copyFile(file, toFile);
		return baseUrl + key;
	}

	@Override
	public void del(String key) throws IOException {
		File file = new File(BaseUtil.getPath(basePath) + key);
		FileUtil.deleteContents(file);
	}
	

	@Override
	public void get(String path, String key) throws IOException {
		File fromFile = new File(BaseUtil.getPath(basePath) + key);
		File toFile = new File(path);
		FileUtil.copyFile(fromFile, toFile);
	}

	@Override
	public void get(OutputStream outputStream, String key) throws IOException {
		File file = new File(BaseUtil.getPath(basePath) + key);
		if (!file.exists())
			throw new IOException(BaseUtil.getPath(basePath) + key + "不存在");
		FileInputStream fis = new FileInputStream(file);
		final int MAX = 4096;
		byte[] buf = new byte[MAX];
		for (int bytesRead = fis.read(buf, 0, MAX); bytesRead != -1; bytesRead = fis.read(buf, 0, MAX)) {
			outputStream.write(buf, 0, bytesRead);
		} 
		fis.close();
	}
	

	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

}
