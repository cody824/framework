package com.noknown.framework.fss.service.impl;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.OSSObject;
import com.noknown.framework.fss.config.OSSConfig;
import com.noknown.framework.fss.service.FileStoreService;

import java.io.*;

/**
 * @author guodong
 */
public class AliFsImpl implements FileStoreService {
	

	private OSSConfig config;
	
	private OSSClient client;
	
	private boolean isInit;

	public void init(OSSConfig newConfig) {
		if (newConfig == null && isInit) {
			return;
		}
		if (newConfig != null || !isInit) {
			config = newConfig == null ? config : newConfig;
			if (config != null) {
				client = new OSSClient(config.endpoint, config.accessKeyId, config.secretAccessKey);
				isInit = true;
			}
		}
	}

	@Override
	public String put(byte[] data, String key) {
		return put(new ByteArrayInputStream(data), key);
	}

	@Override
	public String put(InputStream inputStream, String key) {
		init(null);
		client.putObject(config.getBucketName(), key, inputStream);
		return config.getDomain() + key;
	}

	@Override
	public String put(String path, String key) {
		init(null);
		File file = new File(path);
		client.putObject(config.getBucketName(), key, file);
		return config.getDomain() + key;
	}

	@Override
	public void get(String path, String key) throws IOException {
		init(null);
		File file = new File(path);
		OutputStream outputStream = new FileOutputStream(file);
		get(outputStream, key);
		outputStream.close();
	}

	@Override
	public void get(OutputStream outputStream, String key) throws IOException {
		init(null);
		InputStream is;
		OSSObject ossObject = client.getObject(config.getBucketName(), key);
		if (ossObject != null){
			is = ossObject.getObjectContent();
			byte[] buffer = new byte[1204];
			int byteread;
			while ((byteread = is.read(buffer)) != -1) {
				outputStream.write(buffer, 0, byteread);
			}
			is.close();
		}
	}

	@Override
	public void del(String key) {
		client.deleteObject(config.getBucketName(), key);
	}

	@Override
	public String copy(String srcKey, String targetKey) {
		client.copyObject(config.getBucketName(), srcKey, config.getBucketName(), targetKey);
		return config.getDomain() + targetKey;
	}

	@Override
	public boolean exist(String key) {
		return client.doesObjectExist(config.getBucketName(), key);
	}

	/**
	 * @return the config
	 */
	public OSSConfig getConfig() {
		return config;
	}

	/**
	 * @param config the config to set
	 */
	public void setConfig(OSSConfig config) {
		this.config = config;
	}

}
