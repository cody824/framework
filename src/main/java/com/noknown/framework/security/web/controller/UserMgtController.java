package com.noknown.framework.security.web.controller;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import com.noknown.framework.common.base.BaseController;
import com.noknown.framework.common.exception.DAOException;
import com.noknown.framework.common.util.JsonUtil;
import com.noknown.framework.common.web.model.PageData;
import com.noknown.framework.common.web.model.SQLExpression;
import com.noknown.framework.common.web.model.SQLFilter;
import com.noknown.framework.common.web.model.SQLOrder;
import com.noknown.framework.security.model.User;
import com.noknown.framework.security.service.RoleService;
import com.noknown.framework.security.service.UserService;

@Controller
@RequestMapping(value = "/security/")
public class UserMgtController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    /**
     * 获取所有用户
     *
     * @param request
     * @param response
     * @param filter   过滤器
     * @param sort     排序
     * @param start    起始位置
     * @param limit    限制大小
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/user/", method = RequestMethod.GET)
    public
    @ResponseBody
    Object getAllUsers(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(value = "filter", required = false) String filter,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "start", required = false, defaultValue = "0") int start,
            @RequestParam(value = "limit", required = false, defaultValue = "-1") int limit)
            throws Exception {
        filter = HtmlUtils.htmlUnescape(filter);
        sort = HtmlUtils.htmlUnescape(sort);
        PageData<?> users;
        SQLFilter sqlFilter = null;
        if (filter != null) {
            sqlFilter = JsonUtil.toObject(filter, SQLFilter.class);
        }
        if (sort != null) {
            if (sqlFilter == null)
                sqlFilter = new SQLFilter();

            List<SQLOrder> sortL = JsonUtil.toList(sort, SQLOrder.class);
            for (SQLOrder order : sortL) {
                sqlFilter.addSQLOrder(order);
            }
        }
        users = userService.findBySQLFilter(sqlFilter, start, limit);
        return users;
    }

    /**
     * 重置单个用户密码
     *
     * @param request
     * @param response
     * @param userId
     * @return
     * @throws Exception
     * @throws DAOException
     */
    @RequestMapping(value = "/user/{userId}/password/reset", method = RequestMethod.PUT)
    public
    @ResponseBody
    Object resetUserPasswd(HttpServletRequest request,
                           HttpServletResponse response, @PathVariable Integer userId)
            throws Exception {
        List<Integer> userIds = Arrays.asList(userId);
        userService.resetUsersPasswd(userIds);
        return outActionReturn(null, HttpStatus.OK);
    }

    @RequestMapping(value = "/user/password/reset", method = RequestMethod.PUT)
    public
    @ResponseBody
    Object resetUsersPasswd(HttpServletRequest request,
                            HttpServletResponse response, @RequestBody List<Integer> userIds)
            throws Exception {
        if (userIds == null || userIds.size() <= 0) {
            return outActionReturn(null, HttpStatus.OK);
        }
        userService.resetUsersPasswd(userIds);
        return outActionReturn(null, HttpStatus.OK);
    }

    /**
     * 批量删除用户
     *
     * @param request
     * @param response
     * @param userNames 用户名
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/user/", method = RequestMethod.DELETE)
    public
    @ResponseBody
    Object deleteUsers(HttpServletRequest request,
                       HttpServletResponse response, @RequestBody List<String> userNames)
            throws Exception {
        userService.deleteUsersByName(userNames);
        return outActionReturn(null, HttpStatus.OK);
    }


    /**
     * 通过用户名查找用户
     *
     * @param request
     * @param response
     * @param name     用户名
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/user/name/{name}", method = RequestMethod.GET)
    public
    @ResponseBody
    Object getUserByUserName(HttpServletRequest request,
                             HttpServletResponse response, @PathVariable("name") String name)
            throws Exception {
        User user = userService.findByNick(name);
        return outActionReturn(user, HttpStatus.OK);
    }

    /**
     * 获取所有锁定的用户
     *
     * @param request
     * @param response
     * @param filter   过滤器
     * @param start    起始位置
     * @param limit    限制大小
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/lockedUser/", method = RequestMethod.GET)
    public
    @ResponseBody
    Object getLockUsers(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(value = "filter", required = false, defaultValue = "0") String filter,
            @RequestParam(value = "start", required = false, defaultValue = "0") int start,
            @RequestParam(value = "limit", required = false, defaultValue = "-1") int limit)
            throws Exception {
        filter = HtmlUtils.htmlUnescape(filter);
        PageData<?> users;

        SQLFilter sqlFilter = (SQLFilter) JsonUtil.toObject(filter,
                SQLFilter.class);
        SQLExpression se = new SQLExpression("and", "status", SQLExpression.eq,
                new Object[]{2});
        if (sqlFilter == null) {
            sqlFilter = new SQLFilter(se);
        } else {
            sqlFilter.addSQLExpression(se);
        }
        users = userService.findBySQLFilter(sqlFilter, start, limit);
        return outActionReturn(
        		users,
                HttpStatus.OK);

    }

    /**
     * 批量解锁用户
     *
     * @param request
     * @param response
     * @param userNames 用户名集合
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/lockedUser/", method = RequestMethod.DELETE)
    public
    @ResponseBody
    Object unlockUsers(HttpServletRequest request,
                       HttpServletResponse response, @RequestBody List<String> userNames)
            throws Exception {
        userService.unlockUsersByName(userNames);
        return outActionReturn(null, HttpStatus.OK);
    }

    /**
     * 批量锁定用户
     *
     * @param request
     * @param response
     * @param userNames 用户名集合
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/lockedUser/", method = RequestMethod.PUT)
    public
    @ResponseBody
    Object lockUsers(HttpServletRequest request, HttpServletResponse response,
                     @RequestBody List<String> userNames) throws Exception {
        userService.lockUsersByName(userNames);
        return outActionReturn(null, HttpStatus.OK);
    }

    /**
     * 锁定单个用户
     *
     * @param request
     * @param response
     * @param name     用户名
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/lockedUser/{userId}", method = RequestMethod.PUT)
    public
    @ResponseBody
    Object lockUser(HttpServletRequest request, HttpServletResponse response,
                    @PathVariable("userId") Integer userId) throws Exception {
        List<Integer> userIds = Arrays.asList(userId);
        userService.lockUsersById(userIds);
        return outActionReturn(null, HttpStatus.OK);
    }

    /**
     * 解锁单个用户
     *
     * @param request
     * @param response
     * @param name     用户名
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/lockedUser/{userId}", method = RequestMethod.DELETE)
    public
    @ResponseBody
    Object unLockUser(HttpServletRequest request, HttpServletResponse response,
                      @PathVariable("userId") Integer userId) throws Exception {
        List<Integer> userIds = Arrays.asList(userId);
        userService.unlockUsersById(userIds);
        return outActionReturn(null, HttpStatus.OK);
    }

    /**
     * 通过用户ID获取用户
     *
     * @param request
     * @param response
     * @param userId   用户ID
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/user/{userId}", method = RequestMethod.GET)
    public
    @ResponseBody
    Object getUserById(HttpServletRequest request,
                       HttpServletResponse response, @PathVariable("userId") Integer userId)
            throws Exception {
        User user;
        user = userService.findById(userId);
        return outActionReturn(user,
                HttpStatus.OK);
    }

    /**
     * 为用户批量赋予角色
     *
     * @param request
     * @param response
     * @param userId   用户ID
     * @param roleIds  角色ID集合
     * @return
     * @throws Exception
     * @throws
     */
    @RequestMapping(value = "/user/{userId}/role", method = RequestMethod.PUT)
    public
    @ResponseBody
    Object addUsersToRole(HttpServletRequest request,
                          HttpServletResponse response,
                          @PathVariable("userId") Integer userId,
                          @RequestBody List<Integer> roleIds) throws Exception {
        for (Integer roleId : roleIds) {
            roleService.attachRoleForUser(userId, roleId);
        }
        return outActionReturn(null, HttpStatus.OK);
    }

    /**
     * 批量删除用户角色
     *
     * @param request
     * @param response
     * @param userId   用户ID
     * @param roleIds  角色ID集合
     * @return
     * @throws Exception
     * @throws NumberFormatException
     */
    @RequestMapping(value = "/user/{userId}/role", method = RequestMethod.DELETE)
    public
    @ResponseBody
    Object removeRoleByUserId(HttpServletRequest request,
                              HttpServletResponse response,
                              @PathVariable("userId") Integer userId,
                              @RequestBody List<Integer> roleIds) throws Exception {
        for (Integer roleId : roleIds) {
            roleService.detachRoleFromUser(userId, roleId);
        }
        return outActionReturn(null, HttpStatus.OK);
    }

    /**
     * 修改用户密码
     *
     * @param request
     * @param response
     * @param userId
     * @param oldPassword
     * @param newPassword
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/user/{userId}/password", method = RequestMethod.PUT, headers = "Accept=*")
    public
    @ResponseBody
    Object updateUserPasswd(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable("userId") Integer userId,
            @RequestParam String oldPassword,
            @RequestParam String newPassword
    ) throws Exception {

        userService.updateUserPasswd(userId, oldPassword, newPassword);
        return outActionReturn(HttpStatus.OK, HttpStatus.OK);
    }

    /**
     * 修改来自第三方账号的用户名
     *
     * @param request
     * @param response
     * @param userId
     * @param name
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/user/{userId}/name", method = RequestMethod.PUT, headers = "Accept=*")
    public
    @ResponseBody
    Object updateOauthUsername(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable("userId") Integer userId,
            @RequestParam String name
    ) throws Exception {

        userService.updateNick(userId, name);

        return outActionReturn(HttpStatus.OK, HttpStatus.OK);
    }

}