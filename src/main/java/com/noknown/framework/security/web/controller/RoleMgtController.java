package com.noknown.framework.security.web.controller;

import com.noknown.framework.common.base.BaseController;
import com.noknown.framework.common.exception.WebException;
import com.noknown.framework.common.util.RegexValidateUtil;
import com.noknown.framework.common.web.model.SQLFilter;
import com.noknown.framework.security.model.Role;
import com.noknown.framework.security.model.User;
import com.noknown.framework.security.service.RoleService;
import com.noknown.framework.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author guodong
 */
@Controller
@RequestMapping(value = "/security/")
public class RoleMgtController extends BaseController {

	private final RoleService roleService;

	private final UserService userService;

	@Autowired
	public RoleMgtController(RoleService roleService, UserService userService) {
		this.roleService = roleService;
		this.userService = userService;
	}

	@RequestMapping(value = "/role/", method = RequestMethod.GET)
	public
	@ResponseBody
	Object getAllRoles(
			@RequestParam(value = "filter", required = false) String filter,
			@RequestParam(value = "sort", required = false) String sort,
			@RequestParam(value = "start", required = false, defaultValue = "0") int start,
			@RequestParam(value = "limit", required = false, defaultValue = "-1") int limit)
			throws Exception {
		SQLFilter sqlFilter = this.buildFilter(filter, sort);
		return roleService.find(sqlFilter, start, limit);
	}

	@RequestMapping(value = "/role/", method = RequestMethod.POST)
	public
	@ResponseBody
	Object addRole(
			@RequestParam(value = "name") String name,
			@RequestParam(value = "comment") String comment)
			throws Exception {
		Role role = roleService.createRole(name, comment);
		return outActionReturn(role, HttpStatus.OK);
	}

	@RequestMapping(value = "/role/{id}", method = RequestMethod.PUT)
	public
	@ResponseBody
	Object modifyRole(
			@PathVariable Integer id,
			@RequestParam(value = "comment") String comment)
			throws Exception {
		Role role = roleService.get(id);
		role.setComment(comment);
		roleService.modifyRole(role);
		return outActionReturn(role, HttpStatus.OK);
	}

	@RequestMapping(value = "/role/{id}/user/", method = RequestMethod.PUT)
	public
	@ResponseBody
	Object attachUserToRole(
			@PathVariable Integer id,
			@RequestParam String userKey)
			throws Exception {
		User user;
		if (RegexValidateUtil.checkEmail(userKey)) {
			user = userService.findByEmail(userKey);
		} else if (RegexValidateUtil.checkMobile(userKey)) {
			user = userService.findByMobile(userKey);
		} else {
			user = userService.findByNick(userKey);
		}
		if (user == null) {
			throw new WebException("用户不存在");
		}
		roleService.attachRoleForUser(user.getId(), id);
		return outActionReturn(null, HttpStatus.OK);
	}

	@RequestMapping(value = "/role/", method = RequestMethod.DELETE)
	public
	@ResponseBody
	Object delRoles(
			@RequestBody List<Integer> roleIds)
			throws Exception {
		roleService.destroyRole(roleIds);
		return outActionReturn(null, HttpStatus.OK);
	}

	@RequestMapping(value = "/role/{roleName}/user", method = RequestMethod.GET)
	public
	@ResponseBody
	Object getRoleUser(@PathVariable("roleName") String roleName) throws Exception {
		List<User> userList = userService.findUserByRoleName(roleName);
		return outActionReturn(userList, HttpStatus.OK);
	}

}