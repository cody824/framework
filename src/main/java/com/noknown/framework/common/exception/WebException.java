package com.noknown.framework.common.exception;

import org.springframework.http.HttpStatus;

import com.noknown.framework.common.web.model.ErrorMsg;

/**
 * @(#)WebException.java 
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
 * web层抛出异常的包装
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


public class WebException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1895035099104817633L;

	private ErrorMsg emsg;
	
	private HttpStatus httpStatus;
	
	public WebException(String message) {
		super(message);
		emsg = new ErrorMsg();
		emsg.setErrorMsg(message);
		httpStatus = HttpStatus.BAD_REQUEST;
	}
	
	public WebException(String message, HttpStatus status) {
		super(message);
		emsg = new ErrorMsg();
		emsg.setErrorMsg(message);
		httpStatus = status;
	}
	
	public WebException(String message, int errorNum) {
		super(message);
		emsg = new ErrorMsg();
		emsg.setErrorNum(errorNum);
		emsg.setErrorMsg(message);
		this.httpStatus = HttpStatus.BAD_REQUEST;
	}
	
	public WebException(String message, int errorNum, HttpStatus status) {
		super(message);
		emsg = new ErrorMsg();
		emsg.setErrorNum(errorNum);
		emsg.setErrorMsg(message);
		this.httpStatus = status;
	}
	
	public WebException(String message, ErrorMsg emsg){
		super(message);
		this.emsg = emsg;
		this.httpStatus = HttpStatus.BAD_REQUEST;
	}
	
	public WebException(String message, ErrorMsg emsg, HttpStatus status){
		super(message);
		this.emsg = emsg;
		this.httpStatus = status;
	}

	public WebException(Throwable cause) {
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
		} else if (cause instanceof WebException) {
			WebException e = (WebException)cause;
			emsg = e.getEmsg();
		} else{
			emsg = new ErrorMsg();
		}
		this.httpStatus = HttpStatus.BAD_REQUEST;
	}
	
	public WebException(Throwable cause, HttpStatus status) {
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
		} else if (cause instanceof WebException) {
			WebException e = (WebException)cause;
			emsg = e.getEmsg();
		} else{
			emsg = new ErrorMsg();
		}
		this.httpStatus = status;
	}
	
	public WebException(Throwable cause, int errorNum) {
		super(cause);
		emsg = new ErrorMsg();
		emsg.setErrorNum(errorNum);
		emsg.setErrorMsg(cause.getMessage());
		this.httpStatus = HttpStatus.BAD_REQUEST;
	}
	
	public WebException(Throwable cause, int errorNum, HttpStatus status) {
		super(cause);
		emsg = new ErrorMsg();
		emsg.setErrorNum(errorNum);
		emsg.setErrorMsg(cause.getMessage());
		this.httpStatus = status;
	}
	
	public WebException(Throwable cause, ErrorMsg emsg) {
		super(cause);
		this.emsg = emsg;
		this.httpStatus = HttpStatus.BAD_REQUEST;
	}
	
	public WebException(Throwable cause, ErrorMsg emsg, HttpStatus status) {
		super(cause);
		this.emsg = emsg;
		this.httpStatus = status;
	}

	public WebException(String message, Throwable cause) {
		super(message, cause);
		emsg = new ErrorMsg();
		emsg.setErrorMsg(message);
		this.httpStatus = HttpStatus.BAD_REQUEST;
	}
	
	public WebException(String message, Throwable cause, HttpStatus status) {
		super(message, cause);
		emsg = new ErrorMsg();
		emsg.setErrorMsg(message);
		this.httpStatus = status;
	}
	
	public WebException(String message, Throwable cause, int errorNum) {
		super(message, cause);
		emsg = new ErrorMsg();
		emsg.setErrorNum(errorNum);
		emsg.setErrorMsg(cause.getMessage());
	}
	
	public WebException(String message, Throwable cause, int errorNum, HttpStatus status) {
		super(message, cause);
		emsg = new ErrorMsg();
		emsg.setErrorNum(errorNum);
		emsg.setErrorMsg(cause.getMessage());
		this.httpStatus = status;
	}
	
	public WebException(String message, Throwable cause, ErrorMsg emsg) {
		super(message, cause);
		this.emsg = emsg;
		this.httpStatus = HttpStatus.BAD_REQUEST;
	}
	
	public WebException(String message, Throwable cause, ErrorMsg emsg, HttpStatus status) {
		super(message, cause);
		this.emsg = emsg;
		this.httpStatus = status;
	}

	public ErrorMsg getEmsg() {
		return emsg;
	}

	public void setEmsg(ErrorMsg emsg) {
		this.emsg = emsg;
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(HttpStatus httpStatus) {
		this.httpStatus = httpStatus;
	}

}
