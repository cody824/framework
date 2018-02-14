package com.noknown.framework.common.exception;

import com.noknown.framework.common.web.model.ErrorMsg;

/**
 * 
 * 
 * 服务类抛出异常的包装
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
public class ServiceException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1895035099104817633L;

	private ErrorMsg emsg;
	
	public ServiceException(String message) {
		super(message);
		emsg = new ErrorMsg();
		emsg.setErrorMsg(message);
	}
	
	public ServiceException(String message, int errorNum) {
		super(message);
		emsg = new ErrorMsg();
		emsg.setErrorNum(errorNum);
		emsg.setErrorMsg(message);
	}
	
	public ServiceException(String message, ErrorMsg emsg){
		super(message);
		this.emsg = emsg;
	}

	public ServiceException(Throwable cause) {
		super(cause);
		if (cause instanceof DaoException) {
			DaoException e = (DaoException) cause;
			emsg = e.getEmsg();
		} else if (cause instanceof ServiceException) {
			ServiceException e = (ServiceException)cause;
			emsg = e.getEmsg();
		} else if (cause instanceof UtilException) {
			UtilException e = (UtilException)cause;
			emsg = e.getEmsg();
		} else{
			emsg = new ErrorMsg();
		}
	}
	
	public ServiceException(Throwable cause, int errorNum) {
		super(cause);
		emsg = new ErrorMsg();
		emsg.setErrorNum(errorNum);
		emsg.setErrorMsg(cause.getMessage());
	}
	
	public ServiceException(Throwable cause, ErrorMsg emsg) {
		super(cause);
		this.emsg = emsg;
	}

	public ServiceException(String message, Throwable cause) {
		super(message, cause);
		emsg = new ErrorMsg();
		emsg.setErrorMsg(message);
	}
	
	public ServiceException(String message, Throwable cause, int errorNum) {
		super(message, cause);
		emsg = new ErrorMsg();
		emsg.setErrorNum(errorNum);
		emsg.setErrorMsg(cause.getMessage());
	}
	
	public ServiceException(String message, Throwable cause, ErrorMsg emsg) {
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
