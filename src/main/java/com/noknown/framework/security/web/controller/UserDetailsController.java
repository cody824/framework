package com.noknown.framework.security.web.controller;

import com.noknown.framework.common.base.BaseController;
import com.noknown.framework.common.exception.ServiceException;
import com.noknown.framework.common.exception.WebException;
import com.noknown.framework.security.Constants;
import com.noknown.framework.security.model.BaseUserDetails;
import com.noknown.framework.security.model.factory.UserDetailsFactory;
import com.noknown.framework.security.service.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.Map;

import static com.noknown.framework.security.Constants.ROLE_ADMIN;

/**
 * @author guodong
 */
@RestController
@RequestMapping(value = "/security")
public class UserDetailsController  extends BaseController {

	private final UserDetailsFactory udFactory;

	private final UserDetailsService udService;

	@Autowired
	public UserDetailsController(UserDetailsFactory udFactory, UserDetailsService udService) {
		this.udFactory = udFactory;
		this.udService = udService;
	}

	@RequestMapping(value = "/ud/{userId}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateUserDetails(HttpSession session, @PathVariable Integer userId, @RequestBody String text)
			throws WebException, ServiceException {
		boolean canDo = false;
		Authentication user = loginAuth();
		if (user == null) {
			throw new WebException("对不起，需要登录后才能操作");
		}
		if (hasRole(ROLE_ADMIN)) {
			canDo = true;
		}
		if (user.getPrincipal().equals(userId)){
			canDo = true;
		}
		if (!canDo) {
			throw new WebException("对不起，您无权修改该用户信息");
		}
		BaseUserDetails udDetails;
		udDetails = udFactory.parseUD(text);
		if (udDetails == null) {
			throw new WebException("用户对象错误");
		}
		udDetails.setId(userId);
		udDetails = udService.updateUserDetails(udDetails);
		if (user.getPrincipal().equals(userId)) {
			session.setAttribute(Constants.SURE_LOGIN_USER_DETAIL, udDetails);
			session.setAttribute(Constants.SURE_LOGIN_USER_NAME, udDetails.getFullName());
		}
		return ResponseEntity.ok(udDetails);
	}

	@RequestMapping(value = "/ud/{userId}", method = RequestMethod.GET)
	public ResponseEntity<?> getUD(@PathVariable Integer userId)
			throws ServiceException {
		BaseUserDetails ud = udService.getUserDetail(userId);
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
		BaseUserDetails udDetails = udService.getUserDetail((Integer) user.getPrincipal());
		if (udDetails == null) {
			throw new WebException("用户信息不存在");
		}
		return ResponseEntity.ok(udDetails);
	}

	@RequestMapping(value = "/ud/fullInfo", method = RequestMethod.GET)
	public ResponseEntity<?> getUserAccess()
			throws WebException, ServiceException {
		Authentication user = loginAuth();
		if (user == null){
			throw new WebException("没有登录");
		}
		Map<String, Object> ret = new HashMap<>();
		BaseUserDetails udDetails = udService.getUserDetail((Integer) user.getPrincipal());
		if (udDetails == null) {
			throw new WebException("用户信息不存在");
		}
		ret.put("access", user.getAuthorities());
		ret.put("userInfo", udDetails);
		return ResponseEntity.ok(ret);
	}

}
