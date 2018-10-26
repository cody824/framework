package com.noknown.framework.security.web.controller;

import com.noknown.framework.common.base.BaseController;
import com.noknown.framework.common.exception.DaoException;
import com.noknown.framework.common.exception.ServiceException;
import com.noknown.framework.common.exception.WebException;
import com.noknown.framework.common.util.RegexValidateUtil;
import com.noknown.framework.common.web.model.SQLFilter;
import com.noknown.framework.security.model.Group;
import com.noknown.framework.security.model.User;
import com.noknown.framework.security.service.GroupService;
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
public class GroupMgtController extends BaseController {

	private final UserService userService;

	private final GroupService groupService;

	@Autowired
	public GroupMgtController(UserService userService, GroupService groupService) {
		this.userService = userService;
		this.groupService = groupService;
	}

	@RequestMapping(value = "/group/", method = RequestMethod.GET)
	public
	@ResponseBody
	Object find(
			@RequestParam(value = "filter", required = false) String filter,
			@RequestParam(value = "sort", required = false) String sort,
			@RequestParam(value = "start", required = false, defaultValue = "0") int start,
			@RequestParam(value = "limit", required = false, defaultValue = "-1") int limit)
			throws Exception {
		SQLFilter sqlFilter = this.buildFilter(filter, sort);
		return groupService.find(sqlFilter, start, limit);
	}

	@RequestMapping(value = "/group/", method = RequestMethod.POST)
	public
	@ResponseBody
	Object add(
			@RequestParam(value = "name") String name,
			@RequestParam(value = "comment") String comment,
			@RequestParam(required = false) Integer parentId)
			throws Exception {
		Group group = groupService.create(name, comment, parentId);
		return outActionReturn(group, HttpStatus.OK);
	}

	@RequestMapping(value = "/group/{id}", method = RequestMethod.PUT)
	public
	@ResponseBody
	Object modify(
			@PathVariable Integer id,
			@RequestParam(value = "comment") String comment)
			throws Exception {
		Group group = groupService.get(id);
		group.setComment(comment);
		groupService.update(group);
		return outActionReturn(group, HttpStatus.OK);
	}

	@RequestMapping(value = "/group/{id}/user/", method = RequestMethod.PUT)
	public
	@ResponseBody
	Object addUserToGroup(
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
		groupService.addUser(user.getId(), id);
		return outActionReturn(null, HttpStatus.OK);
	}

	@RequestMapping(value = "/group/{id}", method = RequestMethod.DELETE)
	public
	@ResponseBody
	Object delGroup(@PathVariable Integer id) throws ServiceException, DaoException {
		groupService.delete(new Integer[]{id});
		return outActionReturn(null, HttpStatus.OK);
	}

	@RequestMapping(value = "/group/{groupId}/user", method = RequestMethod.GET)
	public
	@ResponseBody
	Object getRoleUser(@PathVariable Integer groupId) throws Exception {
		List<User> userList = userService.findUserByGroupId(groupId);
		return outActionReturn(userList, HttpStatus.OK);
	}

}