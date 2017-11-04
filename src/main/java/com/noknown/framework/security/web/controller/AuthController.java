package com.noknown.framework.security.web.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.noknown.framework.common.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.noknown.framework.common.base.BaseController;
import com.noknown.framework.common.exception.WebException;
import com.noknown.framework.common.util.BaseUtil;
import com.noknown.framework.common.util.JsonUtil;
import com.noknown.framework.common.util.RegexValidateUtil;
import com.noknown.framework.security.authentication.JwtAuthenticationRequest;
import com.noknown.framework.security.authentication.JwtAuthenticationResponse;
import com.noknown.framework.security.authentication.service.TokenAuthService;
import com.noknown.framework.security.model.ThirdPartyAccount;
import com.noknown.framework.security.model.User;
import com.noknown.framework.security.model.UserDetails;
import com.noknown.framework.security.service.AuthcodeService;
import com.noknown.framework.security.service.UserService;

@RestController
@RequestMapping(value = "/security")
public class AuthController  extends BaseController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private AuthcodeService authcodeService;

	@Value("${security.jwt.header}")
	private String tokenHeader;
	
	@Value("${security.login.needAuthcode:false}")
	private boolean needImgAuthcode;
	
	@Value("${security.login.appWxId:no}")
	private String wxId;
	

	@Autowired
	private TokenAuthService authService;

	@RequestMapping(value = "/auth/getToken/{tpaType}", method = RequestMethod.POST)
	public ResponseEntity<?> createAuthenticationTokenFromTpa(@PathVariable String tpaType, @RequestBody String text)
			throws Exception {
		String token = null;
		if ("weibo".equals(tpaType)) {
			token = getTaken(tpaType, text, "avatar", "avatar_hd", "name");
		} else if ("qq".equals(tpaType)) {
			token = getTaken(tpaType, text, "figureurl_qq_1", "figureurl_qq_2", "nickname");
		}  else if ("wechat".equals(tpaType)) {
			Map<String, Object> map = JsonUtil.toMap(text);
			String openId = (String) map.get("openId");
			ThirdPartyAccount tpa = userService.getWxAccoutByOpenId(openId);
			User user = null;
			if (tpa != null) {//已经绑定，重新登录
				user = userService.findById(tpa.getUserId());
			} else {//注册
				String avatar = (String) map.get("headimgurl");
				String avatar_hd = (String) map.get("headimgurl");
				String nickname = (String) map.get("nickname");
				String unionId = (String) map.get("unionid");
				UserDetails uDetails = userService.addUserFromWx(wxId, unionId, openId, avatar, avatar_hd, nickname);
				user = userService.findById(uDetails.getId());
			}
			if (user != null) {
				token = authService.login(user);
			} else {
				throw new WebException("用户信息不存在");
			}
		} else {
			throw new WebException("不支持该类型的第三方登录");
		}
		return ResponseEntity.ok(new JwtAuthenticationResponse(token));
	}
	
	private String getTaken(String tpaType, String text, String avatarKey, String avatarHdKey, String nicknameKey) throws Exception{
		String token = null;
		Map<String, Object> map = JsonUtil.toMap(text);
		String openId = (String) map.get("idstr");
		if (openId != null) {
			ThirdPartyAccount tpa = userService.getThirdPartyAccountByOpenId(openId, tpaType);
			User user = null;
			if (tpa != null) {//已经绑定，重新登录
				user = userService.findById(tpa.getUserId());
			} else {//注册
				String avatar = (String) map.get(avatarKey);
				String avatar_hd = (String) map.get(avatarHdKey);
				String nickname = (String) map.get(nicknameKey);
				UserDetails uDetails = userService.addUserFromTpa(tpaType, openId, avatar, avatar_hd, nickname);
				user = userService.findById(uDetails.getId());
			}
			if (user != null) {
				token = authService.login(user);
			} else {
				throw new WebException("用户信息不存在");
			}
		} else {
			throw new WebException("登录信息错误，没有ID信息");
		}
		return token;
	}
 	
	
	@RequestMapping(value = "/auth/getToken", method = RequestMethod.POST)
	public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtAuthenticationRequest authenticationRequest)
			throws AuthenticationException {
		final String token = authService.login(authenticationRequest.getUsername(),
				authenticationRequest.getPassword());
		return ResponseEntity.ok(new JwtAuthenticationResponse(token));
	}
	
	@RequestMapping(value = "/auth/getToken", method = RequestMethod.DELETE)
	public ResponseEntity<?> removeToken()
			throws AuthenticationException {
		SecurityContextHolder.getContext().setAuthentication(null);
		return ResponseEntity.ok(null);
	}

	@RequestMapping(value = "/auth/refresToken", method = RequestMethod.GET)
	public ResponseEntity<?> refreshAndGetAuthenticationToken(HttpServletRequest request)
			throws AuthenticationException {
		String token = request.getHeader(tokenHeader);
		String refreshedToken = authService.refresh(token);
		if (refreshedToken == null) {
			return ResponseEntity.badRequest().body(null);
		} else {
			return ResponseEntity.ok(new JwtAuthenticationResponse(refreshedToken));
		}
	}
	
	@RequestMapping(value = "/auth/register/nick", method = RequestMethod.POST, consumes = "application/json")
	public ResponseEntity<?>  registerFromNick(@RequestParam(required = false) String clientId, @RequestParam(required = false)String authcode, @RequestBody User user) throws Exception {
		if (StringUtil.isNotBlank(user.getNick())){
			boolean isMobile = RegexValidateUtil.checkMobile(user.getNick());
			boolean isEmail = RegexValidateUtil.checkEmail(user.getNick());
			if (isMobile) {
				throw new WebException("不能使用手机号作为用户昵称");
			} else if (isEmail) {
				throw new WebException("不能使用邮箱作为用户昵称");
			} 
		} else {
			throw new WebException("请输入用户昵称");
		}
		if (StringUtil.isBlank(user.getPassword()) || user.getPassword().trim().length() == 0){
			throw new WebException("请输入密码");
		}
		
		UserDetails udDetails  = userService.addUser(user);
		return ResponseEntity.ok(udDetails);
	}
	
	@RequestMapping(value = "/auth/register/mobile", method = RequestMethod.POST, consumes = "application/json")
	public ResponseEntity<?>  registerFromMobile(@RequestParam(required = false) String clientId, @RequestParam String authcode, @RequestBody User user) throws Exception {
		if (StringUtil.isBlank(user.getMobile()) ||  !RegexValidateUtil.checkMobile(user.getMobile())){
			throw new WebException("请输入正确的手机号");
		} 
		user.setEmail(null);
		if (StringUtil.isBlank(user.getNick())){
			user.setNick("m" + BaseUtil.getUUID());
		} 
		boolean	checkOk = authcodeService.checkAuthCode(user.getMobile(), authcode);
		if (!checkOk){
			throw new WebException("验证码错误");
		}
		UserDetails udDetails  = userService.addUser(user);
		return ResponseEntity.ok(udDetails);
	}

	@RequestMapping(value = "/auth/resetpsd/mobile", method = RequestMethod.POST)
	public ResponseEntity<?>  resetPsdFromMobile(@RequestParam(required = false) String clientId, @RequestParam String authcode, @RequestParam String mobile, @RequestParam String password) throws Exception {
		if (StringUtil.isBlank(mobile) ||  !RegexValidateUtil.checkMobile(mobile)){
			throw new WebException("请输入正确的手机号");
		}
		boolean	checkOk = authcodeService.checkAuthCode(mobile, authcode);
		if (!checkOk){
			throw new WebException("验证码错误");
		}
		userService.resetUserPasswd(mobile, password);
		return ResponseEntity.ok(okRet);
	}
	
	@RequestMapping(value = "/auth/register/email", method = RequestMethod.POST, consumes = "application/json")
	public ResponseEntity<?>  registerFromEmail(@RequestParam String clientId, @RequestParam String authcode, @RequestBody User user) throws Exception {
		if (StringUtil.isBlank(user.getEmail()) ||  !RegexValidateUtil.checkEmail(user.getEmail())){
			throw new WebException("请输入正确的邮箱");
		} 
		user.setMobile(null);
		if (StringUtil.isBlank(user.getNick())){
			user.setNick("e" + BaseUtil.getUUID());
		} 
		boolean	checkOk = authcodeService.checkAuthCode(user.getEmail(), authcode);
		if (!checkOk){
			throw new WebException("验证码错误");
		}
		if (!checkOk){
			throw new WebException("验证码错误");
		}
		UserDetails udDetails  = userService.addUser(user);
		return ResponseEntity.ok(udDetails);
	}

	@RequestMapping(value = "/auth/register/tpa", method = RequestMethod.POST, consumes = "application/json")
	public ResponseEntity<?>  registerFromTpa(@RequestParam(required = false) String uuid, @RequestParam String openId, @RequestParam String tapType, @RequestBody String text) throws Exception {
		User user = (User) JsonUtil.toObject(text, User.class);
		UserDetails udDetails  = userService.addUser(user);
		return ResponseEntity.ok(udDetails);
	}
}
