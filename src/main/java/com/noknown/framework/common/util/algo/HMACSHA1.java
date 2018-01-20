/**
 * @Title: HMACSHA1.java
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

import java.security.InvalidKeyException;  
import java.security.NoSuchAlgorithmException;  
  
import javax.crypto.Mac;  
import javax.crypto.spec.SecretKeySpec;  
  
public class HMACSHA1 {  
  
    private static final String HMAC_SHA1 = "HmacSHA1";  
  
    /** 
     * 生成签名数据 
     *  
     * @param data 待加密的数据 
     * @param key  加密使用的key 
     * @return 生成MD5编码的字符串  
     * @throws InvalidKeyException 
     * @throws NoSuchAlgorithmException 
     */  
    public static String getSignature(byte[] data, byte[] key) throws InvalidKeyException, NoSuchAlgorithmException {  
        SecretKeySpec signingKey = new SecretKeySpec(key, HMAC_SHA1);  
        Mac mac = Mac.getInstance(HMAC_SHA1);  
        mac.init(signingKey);  
        byte[] rawHmac = mac.doFinal(data);  
        return new String(Base16.encode(rawHmac));  
    }  
    
    public static void main(String[] args) throws Exception {
		// 加密成base64
		String strSrc = "aaaaa";
		String key = "bbbb";
		String strOut = new String(getSignature(strSrc.getBytes(), key.getBytes() ));
		System.out.println(strOut);
	}
      
} 