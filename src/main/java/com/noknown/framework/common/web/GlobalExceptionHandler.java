package com.noknown.framework.common.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import com.noknown.framework.common.exception.DAOException;
import com.noknown.framework.common.exception.ServiceException;
import com.noknown.framework.common.exception.WebException;
import com.noknown.framework.common.web.model.ErrorMsg;

@ControllerAdvice
public class GlobalExceptionHandler {
	

	@Value("${ajaxHttpStatus:true}")
	private boolean httpStatus = true;
	
	public final Logger logger = LoggerFactory.getLogger(getClass());
	
	  @ExceptionHandler  
	    public Object  exp(HttpServletRequest request, HttpServletResponse response, Exception ex) { 
		     HttpStatus responseStatus = HttpStatus.BAD_REQUEST;
		     ErrorMsg msg;
		  	 if(ex instanceof WebException) {  
	        	WebException we = (WebException) ex;
	        	responseStatus = we.getHttpStatus();
	        	msg = we.getEmsg();
	        } else if(ex instanceof ServiceException) {
	        	ServiceException se = (ServiceException) ex;
	        	responseStatus = HttpStatus.BAD_REQUEST;
	        	msg = se.getEmsg();
	        } else if(ex instanceof DAOException) {
	        	logger.error(ex.getLocalizedMessage());
	        	ex.printStackTrace();
	        	msg = new ErrorMsg("服务错误：" + ex.getLocalizedMessage());
	        	responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
	        }  else {  
	        	logger.error(ex.getLocalizedMessage());
	        	ex.printStackTrace();
	        	msg = new ErrorMsg("服务错误：" + ex.getLocalizedMessage());
	        	responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
	        }  
		  	request.setAttribute("errorMsg", msg);
			request.setAttribute("errorEx", ex);
			if (isAjaxRequest(request)) {
				if (!httpStatus) responseStatus = HttpStatus.OK;
				return new ResponseEntity<ErrorMsg>(msg, responseStatus);
			} else {
				if (httpStatus)
					response.setStatus(responseStatus.value());
				ModelAndView view = new ModelAndView();
				view.addObject("errorMsg", msg);
				view.addObject("errorEx", ex);
				System.out.println("+++++++++错误++++++++");
				System.out.println(responseStatus.value());
				view.setViewName("error/" + responseStatus.value());
				return view;
			}
	    } 
	  
	  private boolean isAjaxRequest(HttpServletRequest request) {
			boolean check = false;
			if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
				check = true;
			} else if ("application/json".equalsIgnoreCase(request.getHeader("Accept"))) {
				check = true;
			}
			return check;
		}

}
