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

/**
 * @author guodong
 */
public class RsaUtil {

	public final static String RSA_ALGORITHM = "RSA";

	public final static String PKCS1_ALGORITHM = "RSA/ECB/PKCS1Padding";

	/**
	 * 得到公钥
	 *
	 * @param key 密钥字符串（经过base64编码）
	 * @throws Exception 异常
	 */
	public static PublicKey getPublicKey(String key, String algoType) throws Exception {
		byte[] keyBytes;
		keyBytes = Base64.decode(key.toCharArray());

		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(algoType);
		return keyFactory.generatePublic(keySpec);
	}


	/**
	 * 得到公钥
	 *
	 * @param key 密钥字符串（经过base64编码）
	 * @throws Exception 异常
	 */
	public static PublicKey getPublicKey(String key) throws Exception {
		byte[] keyBytes;
		keyBytes = Base64.decode(key.toCharArray());

		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
		return keyFactory.generatePublic(keySpec);
	}

	/**
	 * 得到私钥
	 *
	 * @param key 密钥字符串（经过base64编码）
	 * @throws Exception 异常
	 */
	public static PrivateKey getPrivateKey(String key) throws Exception {
		byte[] keyBytes;
		keyBytes = Base64.decode(key.toCharArray());

		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
		return keyFactory.generatePrivate(keySpec);
	}

	/**
	 * 得到密钥字符串（经过base64编码）
	 *
	 * @return 结果
	 */
	public static String getKeyString(Key key) {
		byte[] keyBytes = key.getEncoded();
		return new String(Base64.encode(keyBytes));
	}


	/**
	 * 加密，密文通过base64编码
	 *
	 * @param key   key
	 * @param plain 明文
	 * @return 密文
	 * @throws Exception 异常
	 */
	public static String encrypt(Key key, String plain) throws Exception {
		BigInteger[] en = getEn(key);

		//明文  
		byte[] plainText = plain.getBytes();
		BigInteger m = new BigInteger(transformForEncrypt(plainText, plain.length()));
		BigInteger c = m.modPow(en[0], en[1]);
		return new String(Base64.encode(daoxu(c.toByteArray())));
	}

	/**
	 * 解密
	 *
	 * @param key    key
	 * @param cipher 密文
	 * @return 明文
	 * @throws Exception 异常
	 */
	public static String decrypt(Key key, String cipher) throws Exception {
		BigInteger[] en = getEn(key);
		BigInteger m;
		try {
			m = new BigInteger(daoxu(Base64.decode(cipher.toCharArray())));
		} catch (Error e1) {
			throw new Exception("License un support!");
		}
		BigInteger c = m.modPow(en[0], en[1]);
		byte[] plainText = transformForDecrypt(c.toByteArray());
		return new String(plainText);
	}

	private static BigInteger[] getEn(Key key) throws Exception {
		BigInteger[] en = new BigInteger[2];
		if (key instanceof RSAPublicKey) {
			RSAPublicKey pubk = (RSAPublicKey) key;
			en[0] = pubk.getPublicExponent();
			en[1] = pubk.getModulus();
		} else if (key instanceof RSAPrivateKey) {
			RSAPrivateKey prvk = (RSAPrivateKey) key;
			en[0] = prvk.getPrivateExponent();
			en[1] = prvk.getModulus();
		} else {
			throw new Exception("Not support key");
		}
		return en;
	}


	/**
	 * 转换字节数组顺序，倒序排列数组，去掉表示字符串长度字节
	 *
	 * @return 结果
	 */
	private static byte[] transformForDecrypt(byte[] bs) {
		byte[] ret = new byte[bs.length - 1];
		for (int i = bs.length - 2, j = 0; j < ret.length; j++, i--) {
			ret[j] = bs[i];
		}
		return ret;
	}

	/**
	 * 转换字节数组顺序，倒序排列数组，并在末尾加入表示字符串长度字节
	 *
	 * @return 结果
	 */
	private static byte[] transformForEncrypt(byte[] bs, int size) {
		byte[] ret = new byte[bs.length + 1];
		for (int i = bs.length - 1, j = 0; i >= 0; i--) {
			ret[j++] = bs[i];
		}
		ret[bs.length] = (byte) size;
		return ret;
	}

	/**
	 * 转换字节数组顺序，倒序排列数组
	 *
	 * @return 结果
	 */
	private static byte[] daoxu(byte[] bs) {
		byte[] ret = new byte[bs.length];
		for (int i = bs.length - 1, j = 0; i >= 0; j++, i--) {
			ret[j] = bs[i];
		}

		return ret;
	}

}