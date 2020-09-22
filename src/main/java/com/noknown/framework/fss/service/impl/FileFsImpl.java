package com.noknown.framework.fss.service.impl;

import com.noknown.framework.common.util.BaseUtil;
import com.noknown.framework.fss.service.FileStoreService;
import org.aspectj.util.FileUtil;

import java.io.*;

/**
 * @author guodong
 */
public class FileFsImpl implements FileStoreService {

	private String basePath;
	
	private String baseUrl;

	public FileFsImpl(String basePath, String baseUrl) {
		this.basePath = basePath;
		this.baseUrl = baseUrl;
	}
	
	@Override
	public String put(byte[] data, String key) throws IOException {
		return put(new ByteArrayInputStream(data), key);
	}

	@Override
	public String put(InputStream inputStream, String key) throws IOException {
		BaseUtil.createFile(BaseUtil.getPath(basePath) + key);
		FileOutputStream fos = new FileOutputStream(BaseUtil.getPath(basePath) + key);
		final int max = 4096;
		byte[] buf = new byte[max];
		for (int bytesRead = inputStream.read(buf, 0, max); bytesRead != -1; bytesRead = inputStream.read(buf, 0, max)) {
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
	public void del(String key) {
		File file = new File(BaseUtil.getPath(basePath) + key);
		FileUtil.deleteContents(file);
	}

	@Override
	public String copy(String srcKey, String targetKey) throws IOException {
		File fromFile = new File(BaseUtil.getPath(basePath), srcKey);
		File toFile = new File(BaseUtil.getPath(basePath), targetKey);
		FileUtil.copyFile(fromFile, toFile);
		return baseUrl + targetKey;
	}

	@Override
	public boolean exist(String key) {
		File file = new File(BaseUtil.getPath(basePath), key);
		return file.exists() && file.isFile();
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
		if (!file.exists()) {
			throw new IOException(BaseUtil.getPath(basePath) + key + "不存在");
		}
		FileInputStream fis = new FileInputStream(file);
		final int max = 4096;
		byte[] buf = new byte[max];
		for (int bytesRead = fis.read(buf, 0, max); bytesRead != -1; bytesRead = fis.read(buf, 0, max)) {
			outputStream.write(buf, 0, bytesRead);
		} 
		fis.close();
	}

	public String getPath(String key) {
		File file = new File(BaseUtil.getPath(basePath) + key);
		return file.getAbsolutePath();
	}

	public String getUrl(String key) {
		return baseUrl + key;
	}

	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

}
