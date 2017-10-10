package com.noknown.framework.common.exception;

import com.noknown.framework.common.web.model.ErrorMsg;

/**
 * @(#)UtilException.java 
 * 版权声明 soulinfo 版权所有 违者必究 
 *
 * 修订记录:
 * 1)更改者:郭栋
 * 时　间：2007-12-21　
 * 描　述：创建
 */


/**
 * 
 * 
 * 工具类抛出异常的包装
 * 
 * 
 * 
 * 
 * JDK版本:
 * 
 * 1.5
 * 
 * @author 郭栋
 * @version 1.1
 * @since 1.0
 */


public class UtilException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1895035099104817633L;

	private ErrorMsg emsg;
	
	public UtilException(String message) {
		super(message);
		emsg = new ErrorMsg();
	}
	
	public UtilException(String message, int errorNum) {
		super(message);
		emsg = new ErrorMsg();
		emsg.setErrorNum(errorNum);
		emsg.setErrorMsg(message);
	}
	
	public UtilException(String message, ErrorMsg emsg){
		super(message);
		this.emsg = emsg;
	}

	public UtilException(Throwable cause) {
		super(cause);
		if (cause instanceof DAOException) {
			DAOException e = (DAOException)cause;
			emsg = e.getEmsg();
		} else if (cause instanceof ServiceException) {
			ServiceException e = (ServiceException)cause;
			emsg = e.getEmsg();
		} else if (cause instanceof UtilException) {
			UtilException e = (UtilException)cause;
			emsg = e.getEmsg();
		}
		emsg = new ErrorMsg();
	}
	
	public UtilException(Throwable cause, int errorNum) {
		super(cause);
		emsg = new ErrorMsg();
		emsg.setErrorNum(errorNum);
		emsg.setErrorMsg(cause.getMessage());
	}
	
	public UtilException(Throwable cause, ErrorMsg emsg) {
		super(cause);
		this.emsg = emsg;
	}

	public UtilException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public UtilException(String message, Throwable cause, int errorNum) {
		super(message, cause);
		emsg = new ErrorMsg();
		emsg.setErrorNum(errorNum);
		emsg.setErrorMsg(cause.getMessage());
	}
	
	public UtilException(String message, Throwable cause, ErrorMsg emsg) {
		super(message, cause);
		this.emsg = emsg;
	}

	public ErrorMsg getEmsg() {
		return emsg;
	}

	public void setEmsg(ErrorMsg emsg) {
		this.emsg = emsg;
	}

}
