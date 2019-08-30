package com.noknown.framework.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * 超出限制异常，例如：登录用户超出限制用户
 *
 * @author guodong
 */
public class ExceedsLimitException extends AuthenticationException {


	private static final long serialVersionUID = -630026430048869167L;

	public ExceedsLimitException(String msg) {
		super(msg);
	}


	public ExceedsLimitException(String msg, Throwable t) {
		super(msg, t);
	}

}
