package com.noknown.framework.security.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.noknown.framework.common.base.BaseController;
import com.noknown.framework.common.exception.ServiceException;
import com.noknown.framework.common.exception.WebException;
import com.noknown.framework.security.model.UserDetails;
import com.noknown.framework.security.model.factory.UserDetailsFactory;
import com.noknown.framework.security.service.UserDetailsService;

@RestController
@RequestMapping(value = "/security")
public class UserDetailsController  extends BaseController {
	
	@Autowired
	private UserDetailsFactory udFactory;
	
	@Autowired
	private UserDetailsService udService;

	@RequestMapping(value = "/ud/{userId}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateUserDetails(@PathVariable Integer userId, @RequestBody String text)
			throws WebException, ServiceException {
		boolean canDo = false;;
		Authentication user = loginAuth();
		if (user == null)
			throw new WebException("对不起，需要登录后才能操作");
		if (hasRole("ROLE_ADMIN")){
			canDo = true;
		}
		if (user.getPrincipal().equals(userId)){
			canDo = true;
		}
		UserDetails udDetails;
		if (canDo){
			udDetails = udFactory.parseUD(text);
			if (udDetails == null){
				throw new WebException("用户对象错误");
			}
			udDetails.setId(userId);
			udService.updateUserDetails(udDetails);
		}
		return ResponseEntity.ok().build();
	}

	@RequestMapping(value = "/ud/{userId}", method = RequestMethod.GET)
	public ResponseEntity<?> getUD(@PathVariable Integer userId)
			throws ServiceException {
		UserDetails ud = udService.get(userId);
		if (ud == null) {
			return ResponseEntity.notFound().build();
		} else {
			return ResponseEntity.ok(ud);
		}
	}
	
	@RequestMapping(value = "/ud/loginInfo", method = RequestMethod.GET)
	public ResponseEntity<?> getLoginUD()
			throws WebException, ServiceException {
		Authentication user = loginAuth();
		if (user == null){
			throw new WebException("没有登录");
		}
		UserDetails udDetails = udService.get((Integer)user.getPrincipal());
		if (udDetails == null)
			throw new WebException("用户信息不存在");
		return ResponseEntity.ok(udDetails);
	}
	
}
