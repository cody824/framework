/**
 * @Title: Base16.java
 * @Package com.soulinfo.commons.util
 * @Description: 算法类基础库
 * CopyRright (c) 2014-2015 SOUL
 * Company:无锡众志和达数据计算股份有限公司
 * 
 * @author xingweiwei
 * @date 2015年5月19日 下午3:05:54
 * @version V0.0.1
 */
package com.noknown.framework.common.util.algo;

/**
 * Base16编码解码类
 */
public class Base16 {

    /**
     * 对字节数据进行Base16编码。
     * @param src 源字节数组
     * @return 编码后的字符串
     */
    public static String encode(byte src[]){
        StringBuffer strbuf = new StringBuffer(src.length * 2);
        int i;

        for (i = 0; i < src.length; i++) {
            if (((int) src[i] & 0xff) < 0x10)
                strbuf.append("0");

            strbuf.append(Long.toString((int) src[i] & 0xff, 16));
        }

        return strbuf.toString();
    }
    
    /**
     * 对Base16编码的字符串进行解码。
     * @param src 源字串
     * @return 解码后的字节数组
     */
    public  static byte[] decode(String hexString){
        byte[] bts = new byte[hexString.length() / 2];
        for (int i = 0; i < bts.length; i++) {
            bts[i] = (byte) Integer.parseInt(hexString.substring(2 * i, 2 * i + 2), 16);
        }
        return bts;
    }

}