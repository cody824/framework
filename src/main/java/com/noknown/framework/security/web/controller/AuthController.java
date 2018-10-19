package com.noknown.framework.security.web.controller;

import com.noknown.framework.common.base.BaseController;
import com.noknown.framework.common.exception.WebException;
import com.noknown.framework.common.util.BaseUtil;
import com.noknown.framework.common.util.JsonUtil;
import com.noknown.framework.common.util.RegexValidateUtil;
import com.noknown.framework.common.util.StringUtil;
import com.noknown.framework.security.authentication.JwtAuthenticationRequest;
import com.noknown.framework.security.authentication.JwtAuthenticationResponse;
import com.noknown.framework.security.authentication.service.TokenAuthService;
import com.noknown.framework.security.model.BaseUserDetails;
import com.noknown.framework.security.model.ThirdPartyAccount;
import com.noknown.framework.security.model.User;
import com.noknown.framework.security.pojo.UserWarpForReg;
import com.noknown.framework.security.service.UserService;
import com.noknown.framework.security.service.VerificationCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author guodong
 */
@RestController
@RequestMapping(value = "/security")
public class AuthController  extends BaseController {

	private final UserService userService;

	private final VerificationCodeService verificationCodeService;

	@Value("${security.jwt.header:jwtheader}")
	private String tokenHeader;

	@Value("${security.login.needAuthcode:false}")
	private boolean needImgAuthcode;

	@Value("${security.login.appWxId:no}")
	private String wxId;

	@Value("${security.login.emailAuthcode:false}")
	private boolean emailAuthcode;


	private final TokenAuthService authService;

	@Autowired
	public AuthController(UserService userService, VerificationCodeService verificationCodeService, TokenAuthService authService) {
		this.userService = userService;
		this.verificationCodeService = verificationCodeService;
		this.authService = authService;
	}

	@RequestMapping(value = "/auth/getToken/{tpaType}", method = RequestMethod.POST)
	public ResponseEntity<?> createAuthenticationTokenFromTpa(@PathVariable String tpaType, @RequestBody String text)
			throws Exception {
		String token;
		if ("weibo".equals(tpaType)) {
			token = getTaken(tpaType, text, "avatar", "avatar_hd", "name");
		} else if ("qq".equals(tpaType)) {
			token = getTaken(tpaType, text, "figureurl_qq_1", "figureurl_qq_2", "nickname");
		}  else if ("wechat".equals(tpaType)) {
			Map<String, Object> map = JsonUtil.toMap(text);
			String openId = (String) map.get("openid");
			ThirdPartyAccount tpa = userService.getWxAccoutByOpenId(openId);
			User user;
			if (tpa != null) {
				//已经绑定，重新登录
				user = userService.get(tpa.getUserId());
			} else {
				//注册
				String avatar = (String) map.get("headimgurl");
				String avatarHd = (String) map.get("headimgurl");
				String nickname = (String) map.get("nickname");
				String unionId = (String) map.get("unionid");
				BaseUserDetails uDetails = userService.addUserFromWx(wxId, unionId, openId, avatar, avatarHd, nickname);
				user = userService.get(uDetails.getId());
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
		String token;
		Map<String, Object> map = JsonUtil.toMap(text);
		String openId = (String) map.get("idstr");
		if (openId != null) {
			ThirdPartyAccount tpa = userService.getThirdPartyAccountByOpenId(openId, tpaType);
			User user;
			if (tpa != null) {
				//已经绑定，重新登录
				user = userService.get(tpa.getUserId());
			} else {
				//注册
				String avatar = (String) map.get(avatarKey);
				String avatarHd = (String) map.get(avatarHdKey);
				String nickname = (String) map.get(nicknameKey);
				BaseUserDetails uDetails = userService.addUserFromTpa(tpaType, openId, avatar, avatarHd, nickname);
				user = userService.get(uDetails.getId());
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
	public ResponseEntity<?> registerFromNick(@RequestBody UserWarpForReg user) throws Exception {
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

		BaseUserDetails udDetails = userService.addUser(user);
		return ResponseEntity.ok(udDetails);
	}

	@RequestMapping(value = "/auth/register/mobile", method = RequestMethod.POST, consumes = "application/json")
	public ResponseEntity<?> registerFromMobile(@RequestParam String authcode, @RequestBody UserWarpForReg user) throws Exception {
		if (StringUtil.isBlank(user.getMobile()) ||  !RegexValidateUtil.checkMobile(user.getMobile())){
			throw new WebException("请输入正确的手机号");
		}
		if (StringUtil.isBlank(user.getNick())){
			user.setNick("m" + BaseUtil.getUUID());
		}
		boolean checkOk = verificationCodeService.check(user.getMobile(), authcode);
		if (!checkOk){
			throw new WebException("验证码错误");
		}
		BaseUserDetails udDetails = userService.addUser(user);
		return ResponseEntity.ok(udDetails);
	}

	@RequestMapping(value = "/auth/resetpsd/mobile", method = RequestMethod.POST)
	public ResponseEntity<?> resetPsdFromMobile(@RequestParam String authcode, @RequestParam String mobile, @RequestParam String password) throws Exception {
		if (StringUtil.isBlank(mobile) ||  !RegexValidateUtil.checkMobile(mobile)){
			throw new WebException("请输入正确的手机号");
		}
		boolean checkOk = verificationCodeService.check(mobile, authcode);
		if (!checkOk){
			throw new WebException("验证码错误");
		}
		userService.resetUserPasswd(mobile, password);
		return ResponseEntity.ok(okRet);
	}

	@RequestMapping(value = "/auth/resetpsd/email", method = RequestMethod.POST)
	public ResponseEntity<?> resetPsdFromEmail(@RequestParam String authcode, @RequestParam String email, @RequestParam String password) throws Exception {
		if (StringUtil.isBlank(email) || !RegexValidateUtil.checkEmail(email)) {
			throw new WebException("请输入正确的邮件地址");
		}
		boolean checkOk = verificationCodeService.check(email, authcode);
		if (!checkOk) {
			throw new WebException("验证码错误");
		}
		userService.resetUserPasswd(email, password);
		return ResponseEntity.ok(okRet);
	}

	@RequestMapping(value = "/auth/register/email", method = RequestMethod.POST, consumes = "application/json")
	public ResponseEntity<?> registerFromEmail(@RequestParam String authcode, @RequestBody UserWarpForReg user) throws Exception {
		if (StringUtil.isBlank(user.getEmail()) ||  !RegexValidateUtil.checkEmail(user.getEmail())){
			throw new WebException("请输入正确的邮箱");
		}
		if (StringUtil.isBlank(user.getNick())){
			user.setNick("e" + BaseUtil.getUUID());
		}
		if (emailAuthcode) {
			boolean checkOk = verificationCodeService.check(user.getEmail(), authcode);
			if (!checkOk) {
				throw new WebException("验证码错误");
			}
		}
		BaseUserDetails udDetails = userService.addUser(user);
		return ResponseEntity.ok(udDetails);
	}

	@RequestMapping(value = "/auth/register/tpa", method = RequestMethod.POST, consumes = "application/json")
	public ResponseEntity<?> registerFromTpa(@RequestParam(required = false) String wxId, @RequestParam(required = false) String uuid, @RequestParam String openId, @RequestParam String tapType, @RequestBody String text) throws Exception {
		UserWarpForReg user = JsonUtil.toObject(text, UserWarpForReg.class);
		BaseUserDetails udDetails = userService.addUser(user);
		if (StringUtil.isNotBlank(wxId)) {
			userService.bindWxAccout(udDetails.getId(), wxId, uuid, openId, udDetails.getAvatar(), udDetails.getAvatarHd(), udDetails.getNick());
		} else {
			userService.bindTpaAccout(udDetails.getId(), tapType, openId, udDetails.getAvatar(), udDetails.getAvatarHd(), udDetails.getNick());
		}
		return ResponseEntity.ok(udDetails);
	}
}
