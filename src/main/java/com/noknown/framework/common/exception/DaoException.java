package com.noknown.framework.common.exception;

import com.noknown.framework.common.web.model.ErrorMsg;


/**
 * DAO类抛出异常的包装
 *
 * @author guodong
 * <p>
 * JDK版本:
 * <p>
 * 1.5
 * @version 1.1
 * @since 1.0
 */
public class DaoException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = -2240679549488998517L;

	private ErrorMsg emsg;

	public DaoException(String message) {
		super(message);
		emsg = new ErrorMsg();
	}

	public DaoException(String message, int errorNum) {
		super(message);
		emsg = new ErrorMsg();
		emsg.setErrorNum(errorNum);
		emsg.setErrorMsg(message);
	}

	public DaoException(String message, ErrorMsg msg) {
		super(message);
		emsg = msg;
	}

	public DaoException(Throwable cause) {
		super(cause);
		if (cause instanceof DaoException) {
			DaoException e = (DaoException) cause;
			emsg = e.getEmsg();
		} else if (cause instanceof ServiceException) {
			ServiceException e = (ServiceException) cause;
			emsg = e.getEmsg();
		} else if (cause instanceof UtilException) {
			UtilException e = (UtilException) cause;
			emsg = e.getEmsg();
		}
		emsg = new ErrorMsg();
	}

	public DaoException(Throwable cause, int errorNum) {
		super(cause);
		emsg = new ErrorMsg();
		emsg.setErrorNum(errorNum);
		emsg.setErrorMsg(cause.getMessage());
	}

	public DaoException(Throwable cause, ErrorMsg emsg) {
		super(cause);
		this.emsg = emsg;
	}

	public DaoException(String message, Throwable cause) {
		super(message, cause);
	}

	public DaoException(String message, Throwable cause, int errorNum) {
		super(message, cause);
		emsg = new ErrorMsg();
		emsg.setErrorNum(errorNum);
		emsg.setErrorMsg(cause.getMessage());
	}

	public DaoException(String message, Throwable cause, ErrorMsg emsg) {
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
