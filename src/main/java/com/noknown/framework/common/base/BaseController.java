package com.noknown.framework.common.base;

import com.noknown.framework.common.util.JsonUtil;
import com.noknown.framework.common.web.model.ErrorMsg;
import com.noknown.framework.common.web.model.SQLFilter;
import com.noknown.framework.common.web.model.SQLOrder;
import com.noknown.framework.security.authentication.SureAuthenticationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.util.HtmlUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author guodong
 */
public class BaseController {

	protected static Map<String, Object> okRet = new HashMap<>();
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	static {
		okRet.put("success", true);
	}

	@Value("${ajaxHttpStatus:true}")
	private boolean httpStatus = true;

	protected Authentication loginAuth() {
		return SecurityContextHolder.getContext().getAuthentication();
	}

	protected SureAuthenticationInfo loginUser() {
		Authentication authentication = loginAuth();
		if (authentication != null && authentication instanceof SureAuthenticationInfo) {
			return ((SureAuthenticationInfo) authentication);
		}
		return null;
	}

	protected Integer loginId() {
		Authentication authentication = loginAuth();
		if (authentication != null) {
			return (Integer) authentication.getPrincipal();
		}
		return null;
	}

	protected boolean hasRole(String role) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			for (GrantedAuthority ga : auth.getAuthorities()) {
				if (ga.getAuthority().equals(role)) {
					return true;
				}
			}
		}
		return false;
	}

	protected ResponseEntity<?> outActionError(String msg, HttpStatus status) {
		HttpStatus sendStatus = HttpStatus.OK;
		if (httpStatus) {
			sendStatus = status;
		}
		ErrorMsg errorMsg = new ErrorMsg(msg, false);
		return new ResponseEntity<Object>(errorMsg, sendStatus);
	}

	public ResponseEntity<?> outActionReturn(Object obj, HttpStatus status) {
		HttpStatus sendStatus = HttpStatus.OK;
		if (httpStatus) {
			sendStatus = status;
		}
		return new ResponseEntity<>(obj, sendStatus);
	}


	public ResponseEntity<?> outActionReturn(Object obj, int status) {
		HttpStatus sendStatus = HttpStatus.OK;
		try {
			sendStatus = HttpStatus.valueOf(status);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return outActionReturn(obj, sendStatus);
	}

	public SQLFilter buildFilter(String filter, String sort) {
		SQLFilter sqlFilter = null;
		if (filter != null) {
			filter = HtmlUtils.htmlUnescape(filter);
			sqlFilter = JsonUtil.toObject(filter, SQLFilter.class);
		}
		if (sort != null) {
			sort = HtmlUtils.htmlUnescape(sort);
			if (sqlFilter == null) {
				sqlFilter = new SQLFilter();
			}
			List<SQLOrder> sortL = JsonUtil.toList(sort, SQLOrder.class);
			if (sortL != null) {
				for (SQLOrder order : sortL) {
					sqlFilter.addSQLOrder(order);
				}
			}
		}
		if (sqlFilter == null) {
			sqlFilter = new SQLFilter();
		}
		return sqlFilter;
	}

}
