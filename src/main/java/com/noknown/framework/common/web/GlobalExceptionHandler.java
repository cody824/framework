package com.noknown.framework.common.web;

import com.noknown.framework.common.exception.DaoException;
import com.noknown.framework.common.exception.ServiceException;
import com.noknown.framework.common.exception.WebException;
import com.noknown.framework.common.util.StringUtil;
import com.noknown.framework.common.web.model.ErrorMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

/**
 * @author guodong
 */
@ControllerAdvice
public class GlobalExceptionHandler {


	@Value("${ajaxHttpStatus:true}")
	private boolean httpStatus = true;

	public final Logger logger = LoggerFactory.getLogger(getClass());

	private final MessageSource messageSource;

	public GlobalExceptionHandler(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@ExceptionHandler
	public Object exp(HttpServletRequest request, HttpServletResponse response, Exception ex) {
		HttpStatus responseStatus;

		ErrorMsg msg;
		if (ex instanceof WebException) {
			WebException we = (WebException) ex;
			responseStatus = we.getHttpStatus();
			msg = we.getEmsg();
		} else if (ex instanceof ServiceException) {
			ServiceException se = (ServiceException) ex;
			responseStatus = HttpStatus.BAD_REQUEST;
			msg = se.getEmsg();
		} else if (ex instanceof DaoException) {
			logger.error(ex.getLocalizedMessage(), ex);
			msg = new ErrorMsg("服务错误：" + ex.getLocalizedMessage(), true);
			responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		} else if (ex instanceof BadCredentialsException) {
			logger.error(ex.getLocalizedMessage());
			ex.printStackTrace();
			msg = new ErrorMsg("验证错误：" + ex.getLocalizedMessage(), false);
			responseStatus = HttpStatus.UNAUTHORIZED;
		} else {
			logger.error(ex.getLocalizedMessage(), ex);
			msg = new ErrorMsg("服务错误：" + ex.getLocalizedMessage(), true);
			responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		return returnError(request, response, ex, responseStatus, msg);
	}

	@ExceptionHandler(value = MethodArgumentNotValidException.class)
	public Object handleMethodArgumentNotValidException(HttpServletRequest request, HttpServletResponse response, MethodArgumentNotValidException ex) {
		HttpStatus responseStatus = HttpStatus.BAD_REQUEST;
		ErrorMsg msg = new ErrorMsg();
		BindingResult bindingResult = ex.getBindingResult();
		String errorMesssage = "参数错误:\n";
		for (FieldError fieldError : bindingResult.getFieldErrors()) {
			String field = messageSource.getMessage(fieldError.getObjectName() + "." + fieldError.getField(), null, fieldError.getField(), request.getLocale());
			errorMesssage += field + ":" + fieldError.getDefaultMessage() + "\n";
		}
		msg.setErrorMsg(errorMesssage);
		logger.error(errorMesssage);
		return returnError(request, response, ex, responseStatus, msg);
	}

	@ExceptionHandler(value = DataIntegrityViolationException.class)
	public Object handleSQLIntegrityConstraintViolationException(HttpServletRequest request, HttpServletResponse response, DataIntegrityViolationException ex) {
		HttpStatus responseStatus = HttpStatus.BAD_REQUEST;
		ErrorMsg msg = new ErrorMsg();
		msg.setErrorMsg(ex.getRootCause().getLocalizedMessage());
		logger.error(ex.getLocalizedMessage());
		return returnError(request, response, ex, responseStatus, msg);
	}

	@ExceptionHandler(value = ConstraintViolationException.class)
	public Object handleConstraintViolationException(HttpServletRequest request, HttpServletResponse response, ConstraintViolationException ex) {
		HttpStatus responseStatus = HttpStatus.BAD_REQUEST;
		ErrorMsg msg = new ErrorMsg();
		String errorMesssage = "数据错误:\n";
		for (ConstraintViolation constraintViolation : ex.getConstraintViolations()) {
			String field = messageSource.getMessage(StringUtil.lowerFirstCase(constraintViolation.getRootBeanClass().getSimpleName()) + "." + constraintViolation.getPropertyPath(), null, constraintViolation.getPropertyPath().toString(), request.getLocale());
			errorMesssage += field + ":" + constraintViolation.getMessage() + "\n";
		}
		msg.setErrorMsg(errorMesssage);
		logger.error(ex.getLocalizedMessage());
		return returnError(request, response, ex, responseStatus, msg);
	}

	@ExceptionHandler(value = TransactionSystemException.class)
	public Object handleTransactionSystemException(HttpServletRequest request, HttpServletResponse response, TransactionSystemException ex) {
		HttpStatus responseStatus = HttpStatus.BAD_REQUEST;
		ErrorMsg msg = new ErrorMsg();
		Throwable cause = ex;
		boolean constrainError = false;
		while (true) {
			cause = cause.getCause();
			if (cause == null) {
				break;
			}
			if (cause instanceof ConstraintViolationException) {
				constrainError = true;
				break;
			}
		}
		if (constrainError) {
			ConstraintViolationException ce = (ConstraintViolationException) cause;
			String errorMesssage = "数据错误:\n";
			for (ConstraintViolation constraintViolation : ce.getConstraintViolations()) {
				String field = messageSource.getMessage(StringUtil.lowerFirstCase(constraintViolation.getRootBeanClass().getSimpleName()) + "." + constraintViolation.getPropertyPath(), null, constraintViolation.getPropertyPath().toString(), request.getLocale());
				errorMesssage += field + ":" + constraintViolation.getMessage() + "\n";
			}
			msg.setErrorMsg(errorMesssage);
		} else {
			msg.setErrorMsg(ex.getLocalizedMessage());
		}
		logger.error(ex.getLocalizedMessage());
		return returnError(request, response, ex, responseStatus, msg);
	}


	private Object returnError(HttpServletRequest request, HttpServletResponse response, Exception ex, HttpStatus responseStatus, ErrorMsg msg) {
		request.setAttribute("errorMsg", msg);
		request.setAttribute("errorEx", ex);
		if (isAjaxRequest(request)) {
			if (!httpStatus) {
				responseStatus = HttpStatus.OK;
			}
			return new ResponseEntity<>(msg, responseStatus);
		} else {
			if (httpStatus) {
				response.setStatus(responseStatus.value());
			}
			ModelAndView view = new ModelAndView();
			view.addObject("errorMsg", msg);
			view.addObject("errorEx", ex);
			view.setViewName("error/" + responseStatus.value());
			return view;
		}
	}

	private boolean isAjaxRequest(HttpServletRequest request) {
		boolean check = false;
		String accept = request.getHeader("Accept");
		if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
			check = true;
		} else if (StringUtil.isNotBlank(accept) && accept.contains("application/json")) {
			check = true;
		}
		return check;
	}

}
