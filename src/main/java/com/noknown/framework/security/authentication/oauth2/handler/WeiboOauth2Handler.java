package com.noknown.framework.security.authentication.oauth2.handler;

import com.noknown.framework.common.config.AppInfo;
import com.noknown.framework.common.service.GlobalConfigService;
import com.noknown.framework.common.util.StringUtil;
import com.noknown.framework.security.authentication.oauth2.Oauth2Handler;
import com.noknown.framework.security.model.ThirdPartyAccount;
import com.noknown.framework.security.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import weibo4j.util.WeiboConfig;

@Component("WeiboOauth2Handler")
public class WeiboOauth2Handler implements Oauth2Handler {
	
	public final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private AppInfo baseAppIdUtil;
	
	@Autowired
	private GlobalConfigService gcs;
	
	@Autowired
	private UserService userService;

	@Override
	public ThirdPartyAccount doAuth(String code, String state) {
		String openId, avatar, avatar_hd, nickname;
		ThirdPartyAccount tpa = null;

		String appId = null;
		String[]  stateParam;


		if (StringUtil.isBlank(state)){
			throw new BadCredentialsException("state参数错误");
		} else {
			stateParam = state.split(",");
			if (stateParam.length < 3)
				throw new BadCredentialsException("state参数错误");
			appId = stateParam[2];
		}
		if (appId == null)
			appId = baseAppIdUtil.getAppId();

		try {
			WeiboConfig weiboConfig = new WeiboConfig(gcs.getProperties("weibo", appId, false));
			weibo4j.Oauth oauth = new weibo4j.Oauth(weiboConfig);
			weibo4j.http.AccessToken accessToken;
			accessToken = oauth.getAccessTokenByCode(code);
			openId = accessToken.getUid();
			
			weibo4j.Users weiUser = new weibo4j.Users(weiboConfig);
			weiUser.client.setToken(accessToken.getAccessToken());
			weibo4j.model.User wUser = weiUser.showUserById(openId);
			nickname = wUser.getScreenName();
			avatar = wUser.getProfileImageUrl();
			avatar_hd = wUser.getAvatarLarge();
			tpa = userService.getThirdPartyAccountByOpenId(openId, "weibo");
			if (tpa == null) {
				tpa = new ThirdPartyAccount();
			}
			tpa.setOpenId(openId);
			tpa.setAccountType("weibo");
			tpa.setAvatar(avatar);
			tpa.setAvatar_hd(avatar_hd);
			tpa.setNickname(nickname);
			return tpa;
		} catch (Exception e) {
			String trackStr = gcs.getConfig("runConfig", baseAppIdUtil.getAppId(), "serviceTrack", false);
			boolean track = !StringUtil.isBlank(trackStr) && Boolean.parseBoolean(trackStr);
			if (track) e.printStackTrace();
			logger.error("处理绑定weibo失败, 错误原因:{}", e.getLocalizedMessage());
			throw new AuthenticationServiceException(e.getLocalizedMessage());
		} 
	}

}
