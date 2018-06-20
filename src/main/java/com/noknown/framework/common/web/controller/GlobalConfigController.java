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
public class GlobalConfigController extends BaseController {

	private final AppInfo appInfo;

	private final GlobalConfigService gcs;

	@Autowired
	public GlobalConfigController(AppInfo appInfo, GlobalConfigService gcs) {
		this.appInfo = appInfo;
		this.gcs = gcs;
	}

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
		if (isFetch) {
			serverInitialized(request.getServletContext());
		}
		return ResponseEntity.ok(gcs.getGlobalConfig(isFetch));
	}

	/**
	 * 获取全局配置对象
	 * @param fetch          是否更新
	 * @return 全局配置对象
	 */
	@RequestMapping(value = "/globalconfig/{configType}", method = RequestMethod.GET)
	public @ResponseBody
	ResponseEntity<?> getConfigRepo(
			HttpServletRequest request,
			@PathVariable("configType") String configType,
			@RequestParam(value = "fetch", required = false, defaultValue = "false") String fetch) {
		boolean isFetch = Boolean.parseBoolean(fetch);
		if (isFetch) {
			serverInitialized(request.getServletContext());
		}
		return ResponseEntity.ok(gcs.getConfigRepo(configType, false));
	}


	@RequestMapping(value = "/globalconfig/{configType}/{domain}/", method = RequestMethod.POST)
	public @ResponseBody
	Object updateConfigKey(
			@PathVariable("domain") String domain,
			@PathVariable("configType") String configType,
			@RequestParam(value = "key") String key,
			@RequestParam(value = "value", required = false) String value) {
		if (value != null) {
			gcs.updateValue(configType, domain, key, value);
		}
		return outActionReturn(HttpStatus.OK, HttpStatus.OK);
	}

	@RequestMapping(value = "/globalconfig/{configType}/{domain}", method = RequestMethod.PUT)
	public @ResponseBody
	Object updateProperties(
			@PathVariable("domain") String domain,
			@PathVariable("configType") String configType,
			@RequestBody Properties properties) {
		gcs.updateProperties(configType, domain, properties);
		return outActionReturn(HttpStatus.OK, HttpStatus.OK);
	}

	@RequestMapping(value = "/globalconfig/{configType}", method = RequestMethod.PUT)
	public @ResponseBody
	Object updateConfigRepo(
			@PathVariable("configType") String configType,
			@RequestBody ConfigRepo configRepo) {
		gcs.updateConfigRepo(configType, configRepo);
		return outActionReturn(HttpStatus.OK, HttpStatus.OK);
	}

	/**
	 * 删除配置库下的domain对应的配置
	 * @param domain         domain名
	 * @param configType     配置类型
	 * @param key            配置名称
	 * @return 空
	 */
	@RequestMapping(value = "/globalconfig/{configType}/{domain}/{key}", method = RequestMethod.DELETE)
	public @ResponseBody
	Object deleteConfigKey(
			@PathVariable String domain,
			@PathVariable String configType,
			@PathVariable String key) {
		gcs.deleteValue(configType, domain, key);
		return outActionReturn(HttpStatus.OK, HttpStatus.OK);
	}

	@RequestMapping(value = "/globalconfig/{configType}/{domain}", method = RequestMethod.DELETE)
	public @ResponseBody
	Object deleteProperties(
			@PathVariable String domain,
			@PathVariable String configType,
			@RequestParam(value = "key", required = false) String key) {
		if (StringUtil.isNotBlank(key)) {
			gcs.deleteValue(configType, domain, key);
		} else {
			gcs.deleteProperties(configType, domain);
		}
		return outActionReturn(HttpStatus.OK, HttpStatus.OK);
	}

	@RequestMapping(value = "/globalconfig/{configType}", method = RequestMethod.DELETE)
	public @ResponseBody
	Object deleteConfigRepo(
			@PathVariable String configType) {
		gcs.deleteConfigRepo(configType);
		return outActionReturn(HttpStatus.OK, HttpStatus.OK);
	}


	private void serverInitialized(ServletContext sc) {
		String appid =  appInfo.getAppId();
		String warProject = appInfo.getWarProject();
		ConfigUtil.serverInitialized(appid, warProject,gcs, sc);
	}
}
