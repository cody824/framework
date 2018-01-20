package com.noknown.framework.common.base;

import com.noknown.framework.common.web.model.ErrorMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashMap;
import java.util.Map;

public class BaseController {
	
	public final Logger logger = LoggerFactory.getLogger(getClass());

	public static Map<String, Object> okRet = new HashMap<>();

	static {
		okRet.put("success", true);
	}
	
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
     * 返回错误
     * @param msg
     * @param status
     * @return
     */
	public ResponseEntity<?> outActionError(String msg, HttpStatus status) {
		HttpStatus sendStatus = HttpStatus.OK;
        if (httpStatus)
        	sendStatus = status;
        ErrorMsg errorMsg = new ErrorMsg(msg, false);
        return new ResponseEntity<Object>(errorMsg, sendStatus);
	}
    
    /**
	 * 返回对象
	 * @param obj		要返回的对象
	 * @param status	响应状态
	 * @return
	 */
	public ResponseEntity<?> outActionReturn(Object obj, HttpStatus status) {
		HttpStatus sendStatus = HttpStatus.OK;
        if (httpStatus)
        	sendStatus = status;
	        
        return new ResponseEntity<Object>(obj, sendStatus);
	}
	
	
    public ResponseEntity<?> outActionReturn(Object obj, int status) {
    	HttpStatus sendStatus = HttpStatus.OK;
        try {
        	sendStatus = HttpStatus.valueOf(status); 
        } catch (IllegalArgumentException  e) {
        	e.printStackTrace();
        }
        return outActionReturn(obj, sendStatus);
    }

}
