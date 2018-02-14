package com.noknown.framework.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author guodong
 */
public class AuthodeErrorException  extends AuthenticationException  {

	/**
	 *
	 */
	private static final long serialVersionUID = 7698514645872433712L;


	public AuthodeErrorException(String msg) {
		super(msg);
	}


	public AuthodeErrorException(String msg, Throwable t) {
		super(msg, t);
	}

}
