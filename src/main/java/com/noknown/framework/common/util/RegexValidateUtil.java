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


	public final static Pattern MOBILE_PATTERN = Pattern.compile("^(0|86|17951)?(17[0-9]|13[0-9]|15[012356789]|18[0-9]|14[57])[0-9]{8}$");

	public final static Pattern EMAIL_PATTERN = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");

	public final static Pattern VERSION_PATTERN = Pattern.compile("(^[0-9])(\\.([0-9]){1,2}){0,2}(-.+)*$");

	/**
	 * 验证邮箱
	 *
	 * @param email 邮箱
	 * @return 是否是电子邮箱
	 */
	public static boolean checkEmail(String email) {
		boolean flag;
		try {
			Matcher matcher = EMAIL_PATTERN.matcher(email);
			flag = matcher.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	/**
	 * 验证手机号码
	 *
	 * @param mobileNumber 电话号码
	 * @return 是否是电话号码
	 */
	public static boolean checkMobile(String mobileNumber) {
		boolean flag;
		try {
			Matcher m = MOBILE_PATTERN.matcher(mobileNumber);
			flag = m.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}
	
	/**
	 * 验证版本号（maven） 	 FIXME 不支持1.11.0
	 *  1.0.0-SNAPSHOT
	 *  1.0.0
	 *  1
	 * @param version  版本
	 * @return 是否是版本
	 */
	public static boolean checkVersion(String version) {
		boolean flag;
		try {
			Matcher matcher = VERSION_PATTERN.matcher(version);
			flag = matcher.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}
}
