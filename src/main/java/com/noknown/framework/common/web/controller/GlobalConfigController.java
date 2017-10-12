package com.noknown.framework.common.web.controller;

import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.noknown.framework.common.base.BaseController;
import com.noknown.framework.common.config.AppInfo;
import com.noknown.framework.common.service.GlobalConfigService;
import com.noknown.framework.common.util.ConfigUtil;

@Controller
public class GlobalConfigController extends BaseController {
	
	@Autowired
	private AppInfo appInfo;

	@Autowired
	private GlobalConfigService gcs;

	/**
	 * 获取domain对应配置
	 * @param domain         domain名
	 * @param configType     配置类型
	 * @param fetch          是否更新
	 * @return               domain对应配置
	 */
	@RequestMapping(value = "/globalconfig/{configType}/{domain}", method = RequestMethod.GET)
	public @ResponseBody
	Object getBaseConfig(
			@PathVariable("domain") String domain,
			@PathVariable("configType") String configType,
			@RequestParam(value = "fetch", required = false, defaultValue = "false") String fetch) {
		boolean isFetch = Boolean.parseBoolean(fetch);
		Properties pros = gcs.getProperties(configType, domain, isFetch);
		return outActionReturn(pros, HttpStatus.OK);
	}

	/**
	 * 获取全局配置对象
	 * @param fetch          是否更新
	 * @return               全局配置对象
	 */
	@RequestMapping(value = "/globalconfig/", method = RequestMethod.GET)
	public @ResponseBody
	ResponseEntity<?> getGrobalConfig(
			HttpServletRequest request,
			@RequestParam(value = "fetch", required = false, defaultValue = "false") String fetch) {
		boolean isFetch = Boolean.parseBoolean(fetch);
		if (isFetch)
			serverInitialized(request.getServletContext());
		return ResponseEntity.ok(gcs.getGlobalConfig(isFetch));
	}
	
	/**
	 * 更新配置库下的domain对应的配置
	 * @param domain         domain名
	 * @param configType     配置类型
	 * @param key            配置名称
	 * @param value          配置别名
	 * @return
	 */
	@RequestMapping(value = "/globalconfig/{configType}/{domain}", method = RequestMethod.PUT)
	public @ResponseBody
	Object updateBaseConfig(
			@PathVariable("domain") String domain,
			@PathVariable("configType") String configType,
			@RequestParam(value = "key", required = false) String key,
			@RequestParam(value = "value", required = false) String value) {
		if (key != null) {
			gcs.updateValue(configType, domain, key, value);
		}
		return outActionReturn(HttpStatus.OK, HttpStatus.OK);
	}
	
	/**
	 * 删除配置库下的domain对应的配置
	 * @param domain         domain名
	 * @param configType     配置类型
	 * @param key            配置名称
	 * @param value          配置别名
	 * @return
	 */
	@RequestMapping(value = "/globalconfig/{configType}/{domain}", method = RequestMethod.DELETE)
	public @ResponseBody
	Object deleteBaseConfig(
			@PathVariable("domain") String domain,
			@PathVariable("configType") String configType,
			@RequestParam(value = "key", required = false) String key) {
		if (key != null) {
			gcs.deleteValue(configType, domain, key);
		}
		return outActionReturn(HttpStatus.OK, HttpStatus.OK);
	}
	
	/**
	 * 获取全局配置别名
	 * @return               全局配置别名
	 */
	@RequestMapping(value = "/globalconfig/nameConfig", method = RequestMethod.GET)
	public @ResponseBody
	Object getNameConfig() {
		return outActionReturn(gcs.getNameConfig(), HttpStatus.OK);
	}
	
	/**
	 * 更新配置库下的配置名称对应的别名
	 * @param key            配置名称
	 * @param paramName      配置别名
	 * @return
	 */
	@RequestMapping(value = "/globalconfig/nameConfig", method = RequestMethod.PUT)
	public @ResponseBody
	Object updateNameConfig(
			@RequestParam(value = "key", required = false) String key,
			@RequestParam(value = "paramName", required = false) String paramName) {
		if (key != null)
			gcs.updateNameConfig(key, paramName);
		return outActionReturn(HttpStatus.OK, HttpStatus.OK);
	}
	
	/**
	 * 删除配置库下的配置名称对应的别名
	 * @param key            配置名称
	 * @return
	 */
	@RequestMapping(value = "/globalconfig/nameConfig", method = RequestMethod.DELETE)
	public @ResponseBody
	Object deleteNameConfig(
			@RequestParam(value = "key", required = false) String key) {
		if (key != null)
			gcs.deleteNameConfig(key);
		return outActionReturn(HttpStatus.OK, HttpStatus.OK);
	}
	
	
	private void serverInitialized(ServletContext sc) {
		String appid =  appInfo.getAppId();
		String warProject = appInfo.getWarProject();
		ConfigUtil.serverInitialized(appid, warProject,gcs, sc);
	}
}
