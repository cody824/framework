package com.noknown.framework.fss.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface FileStoreService {

	String put(final byte[] data, String key) throws IOException;

	String put(InputStream inputStream, String key) throws IOException;

	String put(String path, String key) throws IOException;
	
	void get(String path, String key) throws IOException;

	void get(OutputStream outputStream, String key) throws IOException;

	void del(String key) throws IOException;
}
