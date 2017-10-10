/**
 * @Title: Encrypt.java
 * @Package com.soulinfo.commons.util
 * @Description: 基础工具类-cookie操作
 * CopyRright (c) 2014-2015 SOUL
 * Company:无锡众志和达数据计算股份有限公司
 * 
 * @author xingweiwei
 * @date 2015年5月19日 下午3:05:54
 * @version V0.0.1
 */
package com.noknown.framework.common.util.algo;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encrypt {
	/**
	 * 加密
	 * @param strSrc        原始字符串
	 * @param encType       加密类型
	 * @return              加密后的字符串
	 */
	public static String hashEncrypt(String strSrc,String encType){
		MessageDigest md = null;
		String strDes = null;

		byte[] bt = strSrc.getBytes();
		try {
			if (encType == null || encType.equals("")) {
				encType = "MD5";
			}
			md = MessageDigest.getInstance(encType);
			md.update(bt);
			strDes = bytes2Hex(md.digest()); // to HexString
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
		return strDes;
	}
	
	public static String bytes2Hex(byte[] bts) {
		String des = "";
		String tmp = null;
		for (int i = 0; i < bts.length; i++) {
			tmp = (Integer.toHexString(bts[i] & 0xFF));
			if (tmp.length() == 1) {
				des += "0";
			}
			des += tmp;
		}
		return des;
	}
	
	@SuppressWarnings("static-access")
	public static void main(String[] args) {
		Encrypt te=new Encrypt();
        String strSrc="123456";
        System.out.println("Source String:"+strSrc);
        System.out.println("Encrypted String:");
        System.out.println("Use Def:"+te.hashEncrypt(strSrc,null));
        System.out.println("Use MD5:"+te.hashEncrypt(strSrc,"MD5"));
        System.out.println("Use SHA:"+te.hashEncrypt(strSrc,"SHA-1"));
        System.out.println("Use SHA-256:"+te.hashEncrypt(strSrc,"SHA-256"));
	}
}
