package com.noknown.framework.security.web.controller;

import com.noknown.framework.common.base.BaseController;
import com.noknown.framework.common.exception.WebException;
import com.noknown.framework.common.util.BaseUtil;
import com.noknown.framework.security.authentication.service.TokenAuthService;
import com.noknown.framework.security.model.ApiKey;
import com.noknown.framework.security.service.ApiKeyService;
import com.noknown.framework.security.service.UserService;
import com.noknown.framework.security.service.VerificationCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author guodong
 */
@RestController
@RequestMapping(value = "/security")
public class ApiKeyController extends BaseController {

	private final static String ADMIN = "ROLE_ADMIN";

	private final UserService userService;

	private final VerificationCodeService verificationCodeService;

	private final ApiKeyService apiKeyService;
	private final TokenAuthService authService;
	@Value("${security.jwt.header}")
	private String tokenHeader;
	@Value("${security.login.needAuthcode:false}")
	private boolean needImgAuthcode;
	@Value("${security.login.appWxId:no}")
	private String wxId;

	@Autowired
	public ApiKeyController(UserService userService, VerificationCodeService verificationCodeService, ApiKeyService apiKeyService, TokenAuthService authService) {
		this.userService = userService;
		this.verificationCodeService = verificationCodeService;
		this.apiKeyService = apiKeyService;
		this.authService = authService;
	}

	@RequestMapping(value = "/api/key", method = RequestMethod.POST)
	public ResponseEntity<?> createApiKey(@RequestParam(required = false) Integer userId)
			throws Exception {
		Integer loginId = (Integer) this.loginAuth().getPrincipal();
		boolean canDo = false;
		if (userId == null) {
			userId = loginId;
		}
		if (userId.equals(loginId) || hasRole(ADMIN)) {
			canDo = true;
		}
		if (!canDo) {
			throw new WebException("对不起，您无权获取该信息");
		}
		ApiKey apiKey = apiKeyService.findByUserId(userId);
		if (apiKey != null) {
			apiKey.setSecurityKey(BaseUtil.getUUID());
			apiKeyService.update(apiKey);
		} else {
			apiKey = apiKeyService.create(userId);
		}

		return ResponseEntity.ok(apiKey);
	}

	@RequestMapping(value = "/api/key", method = RequestMethod.GET)
	public ResponseEntity<?> getApiKey(@RequestParam(required = false) Integer userId)
			throws Exception {
		Integer loginId = (Integer) this.loginAuth().getPrincipal();
		boolean canDo = false;
		if (userId == null) {
			userId = loginId;
		}
		if (userId.equals(loginId) || hasRole(ADMIN)) {
			canDo = true;
		}
		if (!canDo) {
			throw new WebException("对不起，您无权获取该信息");
		}
		return ResponseEntity.ok(apiKeyService.findByUserId(userId));
	}

	@RequestMapping(value = "/api/key/security", method = RequestMethod.POST)
	public ResponseEntity<?> updateSecurityKey(@RequestParam(required = false) Integer userId)
			throws Exception {
		Integer loginId = (Integer) this.loginAuth().getPrincipal();
		boolean canDo = false;
		if (userId == null) {
			userId = loginId;
		}
		if (userId.equals(loginId) || hasRole(ADMIN)) {
			canDo = true;
		}
		if (!canDo) {
			throw new WebException("对不起，您无权获取该信息");
		}
		ApiKey apiKey = apiKeyService.findByUserId(userId);
		if (apiKey == null) {
			throw new WebException("用户的Api key不存在");
		}
		apiKey.setSecurityKey(BaseUtil.getUUID());
		apiKeyService.update(apiKey);
		return ResponseEntity.ok(apiKey);
	}

	@RequestMapping(value = "/api/key/enable", method = {RequestMethod.POST, RequestMethod.PUT})
	public ResponseEntity<?> updateStatus(@RequestParam(required = false) Integer userId, @RequestParam boolean enable)
			throws Exception {
		Integer loginId = (Integer) this.loginAuth().getPrincipal();
		boolean canDo = false;
		if (userId == null) {
			userId = loginId;
		}
		if (userId.equals(loginId) || hasRole(ADMIN)) {
			canDo = true;
		}
		if (!canDo) {
			throw new WebException("对不起，您无权获取该信息");
		}
		ApiKey apiKey = apiKeyService.findByUserId(userId);
		if (apiKey == null) {
			throw new WebException("用户的Api key不存在");
		}
		apiKey.setEnable(enable);
		apiKeyService.update(apiKey);
		return ResponseEntity.ok(apiKey);
	}


}
