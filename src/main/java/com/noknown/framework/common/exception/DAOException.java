package com.noknown.framework.common.exception;

import com.noknown.framework.common.web.model.ErrorMsg;


/**
 * 
 * 
 * DAO类抛出异常的包装
 * 
 * 
 * 
 * 
 * JDK版本:
 * 
 * 1.5
 * 
 * @version 1.1
 * @since 1.0
 */


public class DAOException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2240679549488998517L;
	
	private ErrorMsg emsg;

	public DAOException(String message){
		super(message);
		emsg = new ErrorMsg();
	}
	
	public DAOException(String message, int errorNum){
		super(message);
		emsg = new ErrorMsg();
		emsg.setErrorNum(errorNum);
		emsg.setErrorMsg(message);
	}
	
	public DAOException(String message, ErrorMsg msg){
		super(message);
		emsg = msg;
	}
	
	public DAOException(Throwable cause) {
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
	
	public DAOException(Throwable cause, int errorNum) {
		super(cause);
		emsg = new ErrorMsg();
		emsg.setErrorNum(errorNum);
		emsg.setErrorMsg(cause.getMessage());
	}
	
	public DAOException(Throwable cause, ErrorMsg emsg) {
		super(cause);
		this.emsg = emsg;
	}

	public DAOException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public DAOException(String message, Throwable cause, int errorNum) {
		super(message, cause);
		emsg = new ErrorMsg();
		emsg.setErrorNum(errorNum);
		emsg.setErrorMsg(cause.getMessage());
	}
	
	public DAOException(String message, Throwable cause, ErrorMsg emsg) {
		super(message, cause);
		this.emsg = emsg;
	}

	public ErrorMsg getEmsg() {
		if (emsg == null) {
			emsg = new ErrorMsg();
		}
		return emsg;
	}

}
