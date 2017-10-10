package com.noknown.framework.security.service;

import com.noknown.framework.common.exception.DAOException;
import com.noknown.framework.common.exception.ServiceException;

public interface AuthcodeService{
	
	
    /**
	 * 生成验证码
	 * @param to : 手机号码 or 邮箱地址 or clientId
	 * @param len : 验证码长度
	 * @param timeout : 验证码过期时间（分钟）
	 * @return
	 * @throws ServiceException
	 */
	public String generateAuthCode(String to, int len, int timeout) throws DAOException, ServiceException;
	
	/**
	 * 验证验证码（不存在，错误，过期都返回false）
	 * @param to
	 * @param authcode
	 * @return
	 * @throws ServiceException
	 */
	public boolean checkAuthCode(String to, String authcode) throws DAOException, ServiceException;
	
	/**
	 * 生成并发送验证码
	 * @param type : phone or email
	 * @param to : 手机号码 or 邮箱地址
	 * @param len : 验证码长度
	 * @param timeout : 验证码过期时间（分钟）
	 * @throws ServiceException
	 */
	public void sendAuthCode(String type, String to, int len, int timeout) throws DAOException, ServiceException;
	
	/**
	 * 使验证码失效(即删除缓存中的验证码)
	 * @param type
	 * @param to
	 * @throws ServiceException
	 */
	public void expireAuthCode(String type, String to) throws DAOException, ServiceException;

}
