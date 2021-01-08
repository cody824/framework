
package com.noknown.framework.common.util.algo;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {

	protected static char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
			'f'};

	/**
	 * 获取File文件的MD5值
	 * 
	 * @param file
	 * @return
	 */
	public static String getSignature(File file) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);

			byte[] buffer = new byte[8192];
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			int len;
			while ((len = fis.read(buffer)) != -1) {
				md5.update(buffer, 0, len);
			}
			return bufferToHex(md5.digest());
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(fis);
		}
		return null;
	}
	
	/**
	 * 获取File文件的MD5值
	 * 
	 * @param bytes
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static String getSignature(byte[] bytes) throws NoSuchAlgorithmException {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(bytes);
			return bufferToHex(md.digest());
	}

	private static String bufferToHex(byte[] bytes, int m, int n) {
		StringBuffer stringbuffer = new StringBuffer(2 * n);
		int k = m + n;
		for (int l = m; l < k; l++) {
			appendHexPair(bytes[l], stringbuffer);
		}
		return stringbuffer.toString();
	}

	private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
		char c0 = hexDigits[(bt & 0xf0) >> 4];
		char c1 = hexDigits[bt & 0xf];
		stringbuffer.append(c0);
		stringbuffer.append(c1);
	}

	private static String bufferToHex(byte[] bytes) {
		return bufferToHex(bytes, 0, bytes.length);
	}
}