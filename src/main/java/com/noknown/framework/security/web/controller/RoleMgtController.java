package com.noknown.framework.security.web.controller;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping(value = "/security/")
public class RoleMgtController extends BaseController {

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
    @RequestMapping(value = "/role/", method = RequestMethod.GET)
    public
    @ResponseBody
    Object getAllRoles(
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
        users = roleService.find(sqlFilter, start, limit);
        return users;
    }

}