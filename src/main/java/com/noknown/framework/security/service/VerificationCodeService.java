package com.noknown.framework.security.service;

import com.noknown.framework.common.exception.DaoException;
import com.noknown.framework.common.exception.ServiceException;

/**
 * @author guodong
 */
public interface VerificationCodeService {

	/**
	 * 生成验证码
	 * @param to : 手机号码 or 邮箱地址 or clientId
	 * @param len : 验证码长度
	 * @param timeout : 验证码过期时间（分钟）
	 * @return 验证码
	 * @throws ServiceException 异常信息
	 * @throws DaoException     异常信息
	 */
	String generate(String to, int len, int timeout) throws DaoException, ServiceException;

	/**
	 * 验证验证码（不存在，错误，过期都返回false）
	 * @param to    手机号码 or 邮箱地址 or clientId
	 * @param code  验证码
	 * @return 是否正确
	 * @throws ServiceException 异常信息
	 * @throws DaoException     异常信息
	 */
	boolean check(String to, String code) throws DaoException, ServiceException;

	/**
	 * 生成并发送验证码
	 * @param type : phone or email
	 * @param to : 手机号码 or 邮箱地址
	 * @param len : 验证码长度
	 * @param timeout : 验证码过期时间（分钟）
	 * @throws ServiceException 异常信息
	 * @throws DaoException     异常信息
	 */
	void send(String type, String to, int len, int timeout) throws DaoException, ServiceException;

	/**
	 * 使验证码失效(即删除缓存中的验证码)
	 * @param type  phone or email
	 * @param to    手机号码 or 邮箱地址
	 * @throws ServiceException 异常信息
	 * @throws DaoException     异常信息
	 */
	void expire(String type, String to) throws DaoException, ServiceException;

}
