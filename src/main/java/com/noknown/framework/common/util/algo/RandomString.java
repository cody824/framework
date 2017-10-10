/**
 * @Title: RandomString.java
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

import java.util.Random;

public class RandomString {
	
	private static final char[] readomNumber=new char[]{'1','2','0','3','5','6','4','7','8','9'};
	private static final char[] readomSequence=new char[]{'A','a','B','b','C','c','D','d'
        ,'E','e','F','f','G','g','H','h'
        ,'I','i','J','j','K','k','L','l'
        ,'M','m','N','n','O','o','P','p'
        ,'Q','q','R','r','S','s','T','t'
        ,'U','u','V','v','W','w','X','x'
        ,'Y','y','Z','z','1','2','0','3'
        ,'4','5','6','7','8','9'};


	/**
	 * 产生指定长度的字符串
	 * @param length
	 * @return
	 */
	public final static String RandomStr(int length )
	{
		Random random=new Random();
		
		StringBuffer sb=new StringBuffer();
		 for (int i=0;i< length;i++){
			 String StrRand=String.valueOf(readomSequence[random.nextInt(61)]);
			 sb.append(StrRand);
		 }
		return sb.toString();
	}
	
	/**
	 *  产生指定长度的字符串数字
	 * @param length
	 * @return
	 */
	public final static String RandomNumber(int length )
	{
		Random random=new Random();
		
		StringBuffer sb=new StringBuffer();
		 for (int i=0;i< length;i++){
			 String StrRand=String.valueOf(readomNumber[random.nextInt(9)]);
			 sb.append(StrRand);
		 }
		return sb.toString();
	}
}