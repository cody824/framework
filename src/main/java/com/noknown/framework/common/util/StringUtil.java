/**
 * @Title: StringUtil.java
 * @Package com.soulinfo.commons.util
 * @Description: 基础工具类
 * CopyRright (c) 2014-2015 SOUL
 * Company:无锡众志和达数据计算股份有限公司
 * 
 * @author xingweiwei
 * @date 2015年5月19日 下午3:05:54
 * @version V0.0.1
 */
package com.noknown.framework.common.util;

import java.util.Collection;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.noknown.framework.common.util.algo.Encrypt;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;


/**
 * @author wangyao
 * @email yao.wang@soulinfo.com
 * @date 2015年4月8日 下午2:41:56
 * @description
 */
public class StringUtil extends StringUtils {
	
	/**
	 * 将数字字符串+默认加入值（当前默认加入值为1，默认返回值为0） 后重新转换成字符串 如 0099 +1=0100
	 * 
	 * @param str
	 *            需要转换的字符串
	 * @return 转换后的字符串
	 * @author flymz
	 */
	public final static String transformString(String str) {
		String defaultResult = "0";
		int defaultAddValue = 1;
		return transformString(str, defaultResult, defaultAddValue);
	}

	/**
	 * 将数字字符串加上默认加入值后转换成字符串 如 0099 +1=0100
	 * 
	 * @param str
	 *            需要转换的字符串
	 * @param defaultResult
	 *            字符串默认值
	 * @param defaultAddValue
	 *            字符串默认需要加入值
	 * @return 转换后的字符串
	 * @author flymz
	 */
	public static final String transformString(String str,
			String defaultResult, int defaultAddValue) {
		int flag = 0;
		if (isBlank(str)) {
			return defaultResult;
		}
		try {
			Long.parseLong(str);
		} catch (Exception e) {
			return defaultResult;
		}
		for (int i = 0; i < str.length(); i++) {
			String indexValue = str.substring(i, i + 1);
			if (indexValue.equals("0")) {
				flag++;
			} else {
				break;
			}
		}
		long lastAddStr = 0;
		try {
			lastAddStr = Long.parseLong(str.substring(flag));
		} catch (Exception e) {
			lastAddStr = 0;
		}
		defaultResult = str.substring(0, flag)
				+ String.valueOf((lastAddStr + defaultAddValue));
		return defaultResult;
	}

	/**
	 * Trim String，如果String为null，就返回字符串""
	 * 
	 * @param s
	 * @return
	 */
	public static String trim(String s) {
		if (s == null)
			return "";
		return s.trim();
	}

	/**
	 * 去除String中的特殊字符和不可见字符
	 * 
	 * @param s
	 * @return
	 */
	public static String cleanString(String in) {
		if (isBlank(in))
			return "";

		StringBuffer out = new StringBuffer(); // Used to hold the output.

		for (int i = 0; i < in.length(); i++) {
			char current = in.charAt(i); // NOTE: No IndexOutOfBoundsException
											// caught here; it should not
											// happen.
			if ((current == 0x9) || (current == 0xA) || (current == 0xD)
					|| ((current >= 0x20) && (current <= 0x7E))
					|| ((current >= 0xA1) && (current <= 0xD7FF))
					|| ((current >= 0xE000) && (current <= 0xFFFD))
					|| ((current >= 0x10000) && (current <= 0x10FFFF)))
				out.append(current);
		}
		return out.toString().trim();
	}

	public static String asString(Object obj) {
		if (obj == null) {
			return "";
		}
		if (obj.getClass().isArray()) {
			// 是数组
			Object[] objs = (Object[]) obj;
			StringBuilder sb = new StringBuilder();
			for (Object object : objs) {
				sb.append(object.toString() + ",");
			}
			return (sb.length() > 0) ? (sb.substring(0, sb.length() - 1)) : (sb
					.toString());
		} else if (obj instanceof Collection) {
			Collection<?> objs = (Collection<?>) obj;
			StringBuilder sb = new StringBuilder();
			for (Object object : objs) {
				sb.append(object.toString() + ",");
			}
			return (sb.length() > 0) ? (sb.substring(0, sb.length() - 1)) : (sb
					.toString());
		} else {
			return String.valueOf(obj);
		}
	}

	public static String asString(boolean b) {
		return String.valueOf(b);
	}

	public static String asString(char c) {
		return String.valueOf(c);
	}

	public static String asString(char[] data) {
		return String.valueOf(data);
	}

	public static String asString(double d) {
		return String.valueOf(d);
	}

	public static String asString(float f) {
		return String.valueOf(f);
	}

	public static String asString(int i) {
		return String.valueOf(i);
	}

	public static String asString(long l) {
		return String.valueOf(l);
	}

	public static String asString(char[] data, int offset, int count) {
		return String.valueOf(data, offset, count);
	}
	
	public static String[] shortUrl(String url) {

		// 可以自定义生成 MD5 加密字符传前的混合 KEY
		String key = "soulinfo";

		// 要使用生成 URL 的字符
		String[] chars = new String[] { "a", "b", "c", "d", "e", "f", "g", "h",
		"i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t",
		"u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
		"6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H",
		"I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
		"U", "V", "W", "X", "Y", "Z"
		};

		// 对传入网址进行 MD5 加密
		String sMD5EncryptResult = Encrypt.hashEncrypt(key + url, "MD5");
		String hex = sMD5EncryptResult;
		String[] resUrl = new String[4];
		for (int i = 0; i < 4; i++) {
			// 把加密字符按照 8 位一组 16 进制与 0x3FFFFFFF 进行位与运算
			String sTempSubString = hex.substring(i * 8, i * 8 + 8);
			// 这里需要使用 long 型来转换，因为 Inteper .parseInt() 只能处理 31 位 , 首位为符号位 , 如果不用
			// long ，则会越界
			long lHexLong = 0x3FFFFFFF & Long.parseLong(sTempSubString, 16);
			String outChars = "";
			
			for (int j = 0; j < 6; j++) {
				// 把得到的值与 0x0000003D 进行位与运算，取得字符数组 chars 索引
				long index = 0x0000003D & lHexLong;
				// 把取得的字符相加
				outChars += chars[(int) index];
				// 每次循环按位右移 5 位
				lHexLong = lHexLong >> 5;
			}

			// 把字符串存入对应索引的输出数组
			resUrl[i] = outChars;
		}
		return resUrl;
	}
	
	/**
	 * 格式化手机号，即隐藏部分手机号码。13388882222 --> 133****2222
	 * (前提需要保证手机号位数正确，不正确则直接返回原字符串)
	 * @param phoneNumber
	 * @return
	 */
	public static String formatPhoneNumber(String phoneNumber) {
		if(isBlank(phoneNumber) || isMobile(phoneNumber) == false) {
			return phoneNumber;
		}
		
		return phoneNumber.substring(0, 3) + "****" + phoneNumber.substring(7);
	}


	/**
	 * 隐藏部分字符串
	 * @param str 需要处理的字符串
	 * @param ratio 显示比例（如 2 表示显示前2分之1的字符，不显示的动"***"替代 ）
     * @return
     */
	public static String transformString(String str,int ratio ) {
		if(isBlank(str)) {
			return str;
		}
		return str.substring(0, (int) Math.ceil(str.length()/ratio))+"***";
	}


	/**
	 * 格式化邮箱地址，即隐藏部分信息 yao.wang@soulinfo.com --> ya***ng@sou***.com
	 * (前提需要保证邮箱格式正确，不正确则直接返回原字符串)
	 * @param email
	 * @return
	 */
	public static String formatEmail(String email) {
		if(isBlank(email) || isEmail(email) == false) {
			return email;
		}
		
		String[] splits = email.split("@");
		if(splits.length < 2) {
			return email;
		}
		
		String after = splits[1];
		if(after.length() > 4){
			after = after.substring(0, 3) + "***";
		}
		
		String before = splits[0];
		if(before.length() > 4){
			before = before.substring(0, 2) + "***" + before.substring(before.length() - 2);
		}
		
		return before + "@" + after;
	}
	
	public static boolean isMobile( String mobile) {
		Pattern p = Pattern.compile("^(0|86|17951)?(17[0-9]|13[0-9]|15[012356789]|18[0-9]|14[57])[0-9]{8}$");  
		Matcher m = p.matcher(mobile);  
		return m.matches();
	}
	
	public static boolean isEmail( String email) {
		Pattern p = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");  
		Matcher m = p.matcher(email);  
		return m.matches();
	}

	public static String decodeUnicode(String theString) {
		char aChar;
		int len = theString.length();
		StringBuffer outBuffer = new StringBuffer(len);
		for (int x = 0; x < len;) {
			aChar = theString.charAt(x++);
			if (aChar == '\\') {
				aChar = theString.charAt(x++);
				if (aChar == 'u') {
					// Read the xxxx
					int value = 0;
					for (int i = 0; i < 4; i++) {
						aChar = theString.charAt(x++);
						switch (aChar) {
							case '0':
							case '1':
							case '2':
							case '3':
							case '4':
							case '5':
							case '6':
							case '7':
							case '8':
							case '9':
								value = (value << 4) + aChar - '0';
								break;
							case 'a':
							case 'b':
							case 'c':
							case 'd':
							case 'e':
							case 'f':
								value = (value << 4) + 10 + aChar - 'a';
								break;
							case 'A':
							case 'B':
							case 'C':
							case 'D':
							case 'E':
							case 'F':
								value = (value << 4) + 10 + aChar - 'A';
								break;
							default:
								throw new IllegalArgumentException(
										"Malformed   \\uxxxx   encoding.");
						}

					}
					outBuffer.append((char) value);
				} else {
					if (aChar == 't')
						aChar = '\t';
					else if (aChar == 'r')
						aChar = '\r';
					else if (aChar == 'n')
						aChar = '\n';
					else if (aChar == 'f')
						aChar = '\f';
					outBuffer.append(aChar);
				}
			} else
				outBuffer.append(aChar);
		}
		return outBuffer.toString();
	}
	

	/**
	 * 返回中文拼音
	 * @param src
	 * @return
	 */
	  public static String getPingYin(String src) {  
   	  
	        char[] t1 = null;  
	        t1 = src.toCharArray();  
	        String[] t2 = new String[t1.length];  
	        HanyuPinyinOutputFormat t3 = new HanyuPinyinOutputFormat();  
	          
	        t3.setCaseType(HanyuPinyinCaseType.LOWERCASE);  
	        t3.setToneType(HanyuPinyinToneType.WITHOUT_TONE);  
	        t3.setVCharType(HanyuPinyinVCharType.WITH_V);  
	        String t4 = "";  
	        int t0 = t1.length;  
	        try {  
	            for (int i = 0; i < t0; i++) {  
	                // 判断是否为汉字字符  
	                if (java.lang.Character.toString(t1[i]).matches(  
	                        "[\\u4E00-\\u9FA5]+")) {  
	                    t2 = PinyinHelper.toHanyuPinyinStringArray(t1[i], t3);  
	                    t4 += t2[0];  
	                } else  
	                    t4 += java.lang.Character.toString(t1[i]);  
	            }  
	            // System.out.println(t4);  
	            return t4;  
	        } catch (BadHanyuPinyinOutputFormatCombination e1) {  
	            e1.printStackTrace();  
	        }  
	        return t4;  
	    }  

  /**
   * 返回中文的首字母  
   * @param str
   * @return
   */
    public static String getPinYinHeadChar(String str) {  
  
        String convert = "";  
        for (int j = 0; j < str.length(); j++) {  
            char word = str.charAt(j);  
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);  
            if (pinyinArray != null) {  
                convert += pinyinArray[0].charAt(0);  
            } else {  
                convert += word;  
            }  
        }  
        return convert;  
    }  
	  
    /**
     * 将字符串转移为ASCII码  
     * @param cnStr
     * @return
     */
    public static String getCnASCII(String cnStr) {  
        StringBuffer strBuf = new StringBuffer();  
        byte[] bGBK = cnStr.getBytes();  
        for (int i = 0; i < bGBK.length; i++) {  
            strBuf.append(Integer.toHexString(bGBK[i] & 0xff));  
        }  
        return strBuf.toString();  
    }

	public static String getRandomString(int length) {
		String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

		Random rd = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = rd.nextInt(str.length());
			sb.append(str.charAt(number));
		}
		return sb.toString();
	}
	
}