package com.noknown.framework.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 使用正则表达式验证输入格式
 * 
 * @author guodong
 * 
 */
public class RegexValidateUtil {
	/**
	 * 验证邮箱
	 * 
	 * @param email
	 * @return
	 */
	public static boolean checkEmail(String email) {
		boolean flag = false;
		try {
			Pattern regex = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
			Matcher matcher = regex.matcher(email);
			flag = matcher.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	/**
	 * 验证手机号码
	 * 
	 * @param mobileNumber
	 * @return
	 */
	public static boolean checkMobile(String mobileNumber) {
		boolean flag = false;
		try {
			Pattern p = Pattern.compile("^(0|86|17951)?(17[0-9]|13[0-9]|15[012356789]|18[0-9]|14[57])[0-9]{8}$");  
			Matcher m = p.matcher(mobileNumber);  
			flag = m.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}
	
	/**
	 * 验证版本号（maven）
	 *  1.0.0-SNAPSHOT
	 *  1.0.0
	 *  1.0
	 *  1
	 * 
	 * @param version
	 * @return
	 */
	//FIXME 不支持1.11.0
	public static boolean checkVersion(String version) {
		boolean flag = false;
		try {
			Pattern regex = Pattern
					.compile("(^[0-9])(\\.([0-9]){1,2}){0,2}(-.+)*$");
			Matcher matcher = regex.matcher(version);
			flag = matcher.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}
}
