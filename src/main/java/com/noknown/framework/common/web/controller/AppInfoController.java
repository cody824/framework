package com.noknown.framework.common.web.controller;

import com.noknown.framework.common.base.BaseController;
import com.noknown.framework.common.config.AppInfo;
import com.noknown.framework.common.model.ConfigRepo;
import com.noknown.framework.common.service.GlobalConfigService;
import com.noknown.framework.common.util.ConfigUtil;
import com.noknown.framework.common.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.Properties;

/**
 * @author guodong
 */
@Controller
public class AppInfoController extends BaseController {

	private final AppInfo appInfo;

	@Autowired
	public AppInfoController(AppInfo appInfo) {
		this.appInfo = appInfo;
	}


	@RequestMapping(value = "/appInfo", method = RequestMethod.GET)
	public @ResponseBody
	Object appConfig() {
		return appInfo;
	}

}
