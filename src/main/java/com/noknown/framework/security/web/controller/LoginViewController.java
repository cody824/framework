package com.noknown.framework.security.web.controller;

import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.noknown.framework.common.base.BaseController;
import com.noknown.framework.common.config.AppInfo;
import com.noknown.framework.common.service.GlobalConfigService;


@Controller
public class LoginViewController extends BaseController {

	protected String defaultView = "login";

	@Autowired
	private GlobalConfigService gcs;

	@Autowired
	private AppInfo appIdUtil;
	
	  
    @Value("${security.login.qq:false}")
    private boolean supportQQLogin;
    
    @Value("${security.login.weibo:false}")
    private boolean supportWeiboLogin;
    
    @Value("${security.login.wechat:false}")
    private boolean supportWechatLogin;
    
    @Value("${security.login.wechatOS:false}")
    private boolean supportWechatOSLogin;

	/**
	 * 登录页面
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param appId
	 * @param view
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/gotoLoginView", method = { RequestMethod.GET, RequestMethod.POST })
	public String gotoLoginView(HttpSession session, HttpServletRequest request, HttpServletResponse response,
			/* @PathVariable("tpType") String tpType, */
			@RequestParam(required = false) String appId, @RequestParam(required = false) String view)
			throws Exception {
		String loginAction;// 登录请求
		Map<String, String> urlMap = new HashMap<>();// 第三方登录url链接
		// 支持的第三方登录类型，默认为"qq,wechat,weibo,wechatOS"，
		// wechat只PC端微信二维码登录，wechatOS指微信环境授权登录
		List<String> supportTpaType = new ArrayList<>();
		String loginServer;// 登录回调服务器
		String redirectUri;// 验证回调uri

		Properties loginConfig = gcs.getProperties("loginConfig", appIdUtil.getAppId(), true);
		if (loginConfig == null)
			loginConfig = new Properties();

		String avString = loginConfig.getProperty(request.getServerName());
		if (avString != null && appId == null) {
			String[] av = avString.split(":");
			appId = av[0];
			if (view == null && av.length > 1)
				view = av[1];
		}
		appId = appId == null ? "default" : appId;
		if (view == null)
			view = "/" + defaultView;

		loginAction = loginConfig.getProperty("loginAction", "/base/auth");
		if (supportQQLogin)
			supportTpaType.add("qq");
		if (supportWeiboLogin)
			supportTpaType.add("weibo");
		if (supportWechatLogin)
			supportTpaType.add("wechat");
		if (supportWechatOSLogin)
			supportTpaType.add("wechatOS");
		
		

		for (String type : supportTpaType) {
			String url = loginConfig.getProperty(type + "OauthBaseUrl_" + appId);
			String clientId = loginConfig.getProperty(type + "ClientId_" + appId);
			if (url == null) {
				url = loginConfig.getProperty(type + "OauthBaseUrl_default");
			}
			if (clientId == null) {
				clientId = loginConfig.getProperty(type + "ClientId_default");
			}
			loginServer = loginConfig.getProperty(type + "RedirectDomain_" + appId);
			if (loginServer == null) {
				loginServer = loginConfig.getProperty("loginServer");
			}
			if (loginServer == null) {
				loginServer = request.getScheme() + "://" + request.getServerName();
			}
			if (!loginServer.endsWith("/")) {
				loginServer = loginServer + "/";
			}
			if (loginAction.startsWith("/")) {
				loginAction = loginAction.substring(1);
			}
			redirectUri = loginServer + loginAction;
			redirectUri = URLEncoder.encode(redirectUri, "utf-8");

			if (url != null && clientId != null) {
				url = MessageFormat.format(url, clientId, redirectUri, session.getId());
				urlMap.put(type, url);
			}
		}
		request.setAttribute("loginAction", loginAction);
		request.setAttribute("urlMap", urlMap);
		return view;
	}

}
