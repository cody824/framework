/**
 * @Title: RSAUtil.java
 * @Package com.soulinfo.commons.util
 * @Description: 基础工具类
 * CopyRright (c) 2014-2015 SOUL
 * Company:无锡众志和达数据计算股份有限公司
 * 
 * @author xingweiwei
 * @date 2015年5月19日 下午3:05:54
 * @version V0.0.1
 */
package com.noknown.framework.common.util.algo;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RSAUtil {

	/**
	 * 得到公钥
	 * 
	 * @param key
	 *            密钥字符串（经过base64编码）
	 * @throws Exception
	 */
	public static PublicKey getPublicKey(String key) throws Exception {
		byte[] keyBytes;
		keyBytes = Base64.decode(key.toCharArray());

		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PublicKey publicKey = keyFactory.generatePublic(keySpec);
		return publicKey;
	}

	/**
	 * 得到私钥
	 * 
	 * @param key
	 *            密钥字符串（经过base64编码）
	 * @throws Exception
	 */
	public static PrivateKey getPrivateKey(String key) throws Exception {
		byte[] keyBytes;
		keyBytes = Base64.decode(key.toCharArray());

		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
		return privateKey;
	}

	/**
	 * 得到密钥字符串（经过base64编码）
	 * 
	 * @return
	 */
	public static String getKeyString(Key key) throws Exception {
		byte[] keyBytes = key.getEncoded();
		String s = new String(Base64.encode(keyBytes));
		return s;
	}

	/**
	 * 转换字节数组顺序，倒序排列数组，去掉表示字符串长度字节
	 * 
	 * @return
	 */
	private static byte[] transformForDecrypt(byte[] bs) {
		byte[] ret = new byte[bs.length -1 ];
		for (int i = bs.length - 2, j = 0; j < ret.length; j++, i--) {
			ret[j] = bs[i];
		}
		return ret;
	}
	
	/**
	 * 转换字节数组顺序，倒序排列数组，并在末尾加入表示字符串长度字节
	 * 
	 * @return
	 */
	private static byte[] transformForEncrypt(byte[] bs, int size) {
		byte[] ret = new byte[bs.length + 1];
		for (int i = bs.length - 1, j = 0; i >= 0;i--) {
			ret[j++] = bs[i];
		}
		ret[bs.length] = (byte)size;
		return ret;
	}
	
	/**
	 * 转换字节数组顺序，倒序排列数组
	 * 
	 * @return
	 */
	public static byte[] daoxu(byte[] bs){
		byte[] ret = new byte[bs.length];
		for(int i = bs.length - 1, j = 0; i >=0 ;j++, i--){
			ret[j] = bs[i];
		}
		
		return ret;
	}

	/**
	 * 加密plain，密文通过base64编码
	 * 
	 * @return
	 */
	public static String encrypt(Key key, String plain) throws Exception {
		BigInteger e, n;
		if (key instanceof RSAPublicKey) {
			RSAPublicKey pubk = (RSAPublicKey) key;
			e = pubk.getPublicExponent();
			n = pubk.getModulus();
		} else if (key instanceof RSAPrivateKey) {
			RSAPrivateKey prvk = (RSAPrivateKey) key;
			e = prvk.getPrivateExponent();
			n = prvk.getModulus();
		} else {
			throw new Exception("Not support key");
		}

		//明文  
		byte[] plainText = plain.getBytes();  
		BigInteger m = new BigInteger(transformForEncrypt(plainText, plain.length()));
		BigInteger c = m.modPow(e, n);
		return new String(Base64.encode(daoxu(c.toByteArray())));
	}
	
	/**
	 * 解密cipher
	 * 
	 * @return
	 */
	public static String decrypt(Key key, String cipher) throws Exception {
		BigInteger e, n;
		if (key instanceof RSAPublicKey) {
			RSAPublicKey pubk = (RSAPublicKey) key;
			e = pubk.getPublicExponent();
			n = pubk.getModulus();
		} else if (key instanceof RSAPrivateKey) {
			RSAPrivateKey prvk = (RSAPrivateKey) key;
			e = prvk.getPrivateExponent();
			n = prvk.getModulus();
		} else {
			throw new Exception("Not support key");
		}		
		BigInteger m = null;
		try {
			m = new BigInteger(daoxu(Base64.decode(cipher.toCharArray())));
		} catch (Error e1) {
			throw new Exception("License un support!");
		}
		BigInteger c = m.modPow(e, n);
		byte[] plainText = transformForDecrypt(c.toByteArray());
		return new String(plainText);
	}
	
	public static void main(String[] args) throws Exception {

		
	}

}