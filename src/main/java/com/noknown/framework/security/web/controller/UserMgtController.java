package com.noknown.framework.security.web.controller;

import com.noknown.framework.common.base.BaseController;
import com.noknown.framework.common.exception.DaoException;
import com.noknown.framework.common.exception.ServiceException;
import com.noknown.framework.common.exception.WebException;
import com.noknown.framework.common.util.JsonUtil;
import com.noknown.framework.common.util.StringUtil;
import com.noknown.framework.common.web.model.PageData;
import com.noknown.framework.common.web.model.SQLExpression;
import com.noknown.framework.common.web.model.SQLFilter;
import com.noknown.framework.security.authentication.SureAuthenticationInfo;
import com.noknown.framework.security.model.BaseUserDetails;
import com.noknown.framework.security.model.Role;
import com.noknown.framework.security.model.User;
import com.noknown.framework.security.pojo.UserWarpForReg;
import com.noknown.framework.security.service.RoleService;
import com.noknown.framework.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * @author guodong
 */
@Controller
@RequestMapping(value = "/security/")
public class UserMgtController extends BaseController {

	private final UserService userService;

	private final RoleService roleService;

	private final boolean initAdmin;

	@Autowired
	public UserMgtController(UserService userService,
	                         RoleService roleService,
	                         @Value("${security.admin.auto-create:true}")boolean initAdmin) {
		this.userService = userService;
		this.roleService = roleService;
		this.initAdmin = initAdmin;
	}

	@PostConstruct
	private void initAdmin(){
		if (!initAdmin){
			return;
		}
		Integer userId = null;
		User user = userService.findByNick("admin");
		if (user == null){
			UserWarpForReg userWarpForReg = new UserWarpForReg();
			userWarpForReg.setNick("admin");
			userWarpForReg.setNickOk(true);
			userWarpForReg.setPassword("123456");
			Map<String, String> params = new HashMap<>(1);
			params.put("fullName", "管理员");
			userWarpForReg.setParams(params);
			try {
				BaseUserDetails userDetails = userService.addUser(userWarpForReg);
				userId = userDetails.getId();
			} catch (ServiceException e) {
				e.printStackTrace();
			}
		} else {
			userId = user.getId();
		}
		try {
			Role role = roleService.getRoleByName("ROLE_ADMIN");
			if (role == null){
				roleService.createRole("ROLE_ADMIN", "系统管理员");
				roleService.attachRoleForUser(userId, "ROLE_ADMIN");
			}
		} catch (DaoException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/user/", method = RequestMethod.GET)
	public
	@ResponseBody
	Object getAllUsers(
			@RequestParam(value = "filter", required = false) String filter,
			@RequestParam(value = "sort", required = false) String sort,
			@RequestParam(value = "start", required = false, defaultValue = "0") int start,
			@RequestParam(value = "limit", required = false, defaultValue = "-1") int limit)
			throws Exception {
		SQLFilter sqlFilter = this.buildFilter(filter, sort);
		return userService.find(sqlFilter, start, limit);
	}


	@RequestMapping(value = "/user/{userId}/password/reset", method = RequestMethod.PUT)
	public
	@ResponseBody
	Object resetUserPasswd(@PathVariable Integer userId)
			throws Exception {
		List<Integer> userIds = Collections.singletonList(userId);
		userService.resetUsersPasswd(userIds);
		return outActionReturn(null, HttpStatus.OK);
	}

	@RequestMapping(value = "/user/password/reset", method = RequestMethod.PUT)
	public
	@ResponseBody
	Object resetUsersPasswd(@RequestBody List<Integer> userIds)
			throws Exception {
		if (userIds == null || userIds.size() <= 0) {
			return outActionReturn(null, HttpStatus.OK);
		}
		userService.resetUsersPasswd(userIds);
		return outActionReturn(null, HttpStatus.OK);
	}

	@RequestMapping(value = "/user/", method = RequestMethod.DELETE)
	public
	@ResponseBody
	Object deleteUsers(@RequestBody List<String> userNames)
			throws Exception {
		userService.deleteUsersByName(userNames);
		return outActionReturn(null, HttpStatus.OK);
	}

	@RequestMapping(value = "/user/{userName}", method = RequestMethod.DELETE)
	public
	@ResponseBody
	Object deleteUser(@PathVariable String userName)
			throws Exception {
		userService.deleteUsersByName(Arrays.asList(userName));
		return outActionReturn(null, HttpStatus.OK);
	}

	@RequestMapping(value = "/user/name/{name}", method = RequestMethod.GET)
	public
	@ResponseBody
	Object getUserByUserName(@PathVariable("name") String name) {
		User user = userService.findByNick(name);
		return outActionReturn(user, HttpStatus.OK);
	}

	@RequestMapping(value = "/lockedUser/", method = RequestMethod.GET)
	public
	@ResponseBody
	Object getLockUsers(
			@RequestParam(value = "filter", required = false, defaultValue = "0") String filter,
			@RequestParam(value = "start", required = false, defaultValue = "0") int start,
			@RequestParam(value = "limit", required = false, defaultValue = "-1") int limit)
			throws Exception {
		filter = HtmlUtils.htmlUnescape(filter);
		PageData<?> users;

		SQLFilter sqlFilter = JsonUtil.toObject(filter,
				SQLFilter.class);
		SQLExpression se = new SQLExpression("and", "status", SQLExpression.eq,
				new Object[]{2});
		if (sqlFilter == null) {
			sqlFilter = new SQLFilter(se);
		} else {
			sqlFilter.addSQLExpression(se);
		}
		users = userService.find(sqlFilter, start, limit);
		return outActionReturn(
				users,
				HttpStatus.OK);

	}

	@RequestMapping(value = "/lockedUser/", method = RequestMethod.DELETE)
	public
	@ResponseBody
	Object unlockUsers(@RequestBody List<String> userNames)
			throws Exception {
		userService.unlockUsersByName(userNames);
		return outActionReturn(null, HttpStatus.OK);
	}

	@RequestMapping(value = "/lockedUser/", method = RequestMethod.PUT)
	public
	@ResponseBody
	Object lockUsers(@RequestBody List<String> userNames) throws Exception {
		userService.lockUsersByName(userNames);
		return outActionReturn(null, HttpStatus.OK);
	}

	@RequestMapping(value = "/lockedUser/{userId}", method = RequestMethod.PUT)
	public
	@ResponseBody
	Object lockUser(@PathVariable("userId") Integer userId) throws Exception {
		List<Integer> userIds = Collections.singletonList(userId);
		userService.lockUsersById(userIds);
		return outActionReturn(null, HttpStatus.OK);
	}

	@RequestMapping(value = "/lockedUser/{userId}", method = RequestMethod.DELETE)
	public
	@ResponseBody
	Object unLockUser(@PathVariable("userId") Integer userId) throws Exception {
		List<Integer> userIds = Collections.singletonList(userId);
		userService.unlockUsersById(userIds);
		return outActionReturn(null, HttpStatus.OK);
	}

	@RequestMapping(value = "/user/{userId}", method = RequestMethod.GET)
	public
	@ResponseBody
	Object getUserById(@PathVariable("userId") Integer userId)
			throws Exception {
		User user;
		user = userService.get(userId);
		return outActionReturn(user,
				HttpStatus.OK);
	}

	@RequestMapping(value = "/user/{userId}/role", method = RequestMethod.PUT)
	public
	@ResponseBody
	Object addUsersToRole(@PathVariable("userId") Integer userId,
	                      @RequestParam(required = false) String roleName,
	                      @RequestBody List<Integer> roleIds) throws Exception {
		for (Integer roleId : roleIds) {
			roleService.attachRoleForUser(userId, roleId);
		}
		if (StringUtil.isNotBlank(roleName)) {
			roleService.attachRoleForUser(userId, roleName);
		}
		return outActionReturn(null, HttpStatus.OK);
	}

	@RequestMapping(value = "/user/{userId}/role", method = RequestMethod.DELETE)
	public
	@ResponseBody
	Object removeRoleByUserId(@PathVariable("userId") Integer userId,
	                          @RequestParam(required = false) String roleName,
	                          @RequestBody List<Integer> roleIds) throws Exception {
		for (Integer roleId : roleIds) {
			roleService.detachRoleFromUser(userId, roleId);
		}
		if (StringUtil.isNotBlank(roleName)) {
			roleService.detachRoleFromUser(userId, roleName);
		}
		return outActionReturn(null, HttpStatus.OK);
	}

	@RequestMapping(value = "/user/{userId}/role", method = RequestMethod.GET)
	public
	@ResponseBody
	Object getUserRole(@PathVariable("userId") Integer userId) throws Exception {
		User user = userService.get(userId);
		if (user != null) {
			return outActionReturn(user.getAuthorities(), HttpStatus.OK);
		}
		return outActionReturn(null, HttpStatus.OK);
	}

	@RequestMapping(value = "/user/role", method = RequestMethod.GET)
	public
	@ResponseBody
	Object getUserRole() throws Exception {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null) {
			return outActionError("请登录", HttpStatus.UNAUTHORIZED);
		}

		Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
		return outActionReturn(authorities, HttpStatus.OK);
	}

	@RequestMapping(value = "/user/{userId}/password", method = RequestMethod.PUT, headers = "Accept=*")
	public
	@ResponseBody
	Object updateUserPasswd(@PathVariable("userId") Integer userId,
	                        @RequestParam String oldPassword,
	                        @RequestParam String newPassword
	) throws Exception {

		userService.updateUserPasswd(userId, oldPassword, newPassword);
		return outActionReturn(HttpStatus.OK, HttpStatus.OK);
	}

	@RequestMapping(value = "/user/password", method = RequestMethod.PUT, headers = "Accept=*")
	public
	@ResponseBody
	Object updateUserPasswd(@RequestParam String oldPassword,
	                        @RequestParam String newPassword
	) throws Exception {
		SureAuthenticationInfo sai = this.loginUser();
		if (sai == null) {
			throw new WebException("请登录", 401);
		}
		userService.updateUserPasswd(sai.getUser().getId(), oldPassword, newPassword);
		return outActionReturn(sai.getUser(), HttpStatus.OK);
	}

	@RequestMapping(value = "/user/{userId}/name", method = RequestMethod.PUT, headers = "Accept=*")
	public
	@ResponseBody
	Object updateOauthUsername(@PathVariable("userId") Integer userId,
	                           @RequestParam String name
	) throws Exception {

		userService.updateNick(userId, name);

		return outActionReturn(HttpStatus.OK, HttpStatus.OK);
	}

}