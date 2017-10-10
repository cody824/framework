package com.noknown.framework.common.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.noknown.framework.common.web.model.ErrorMsg;

public class BaseController {
	
	public final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Value("${ajaxHttpStatus:true}")
	private boolean httpStatus = true;
	
	public Authentication loginAuth(){
    	return SecurityContextHolder.getContext().getAuthentication();
    }
	
    
    public boolean hasRole(String role){
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    	if (auth != null){
    		for (GrantedAuthority ga : auth.getAuthorities()){
    			if (ga.getAuthority().equals(role)){
    				return true;
    			}
    		}
    	}
    	return false;
    }
    
    /**
	 * 返回对象
	 * @param obj		要返回的对象
	 * @param status	响应状态
	 * @return
	 */
	public Object outActionReturn(Object obj, HttpStatus status) {
		return outActionReturn(obj, status.value());
	}
	
	/**
	 * 返回错误信息
	 * @param errorMsg	错误信息
	 * @param status	响应状态
	 * @return
	 */
	public Object outActionError(String errorMsg, HttpStatus status){
		ErrorMsg eM = new ErrorMsg(errorMsg);
		return outActionReturn(eM, status);
	}
	
	
    public Object outActionReturn(Object obj, int status) {
        try {
            HttpStatus.valueOf(status);
        } catch (IllegalArgumentException  e) {
            status = HttpStatus.INTERNAL_SERVER_ERROR.value();
        }
        int sendStatus = HttpStatus.OK.value();
        if (httpStatus)
        	sendStatus = status;
        
        return new ResponseEntity<Object>(obj, HttpStatus.valueOf(sendStatus));
    }

}
