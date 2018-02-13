package com.noknown.framework.security.web.controller;

import com.noknown.framework.common.base.BaseController;
import com.noknown.framework.common.web.model.SQLFilter;
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

	@RequestMapping(value = "/role/{roleName}/user", method = RequestMethod.GET)
	public
	@ResponseBody
	Object getRoleUser(@PathVariable("roleName") String roleName) throws Exception {
		List<User> userList = userService.findUserByRoleName(roleName);
		return outActionReturn(userList, HttpStatus.OK);
	}

}