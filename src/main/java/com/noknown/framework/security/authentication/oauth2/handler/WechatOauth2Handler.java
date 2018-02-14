package com.noknown.framework.security.authentication.oauth2.handler;

import com.noknown.framework.common.config.AppInfo;
import com.noknown.framework.common.service.GlobalConfigService;
import com.noknown.framework.common.util.StringUtil;
import com.noknown.framework.security.authentication.oauth2.Oauth2Handler;
import com.noknown.framework.security.model.ThirdPartyAccount;
import com.noknown.framework.security.service.UserService;
import com.noknown.framework.wechat.config.bean.WxMpServiceRepo;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

/**
 * @author guodong
 */
@Component("WechatOauth2Handler")
public class WechatOauth2Handler implements Oauth2Handler {

	public final Logger logger = LoggerFactory.getLogger(getClass());

	private final AppInfo baseAppIdUtil;

	private final GlobalConfigService gcs;

	private final UserService userService;

	private final WxMpServiceRepo wxRepo;

	@Autowired
	public WechatOauth2Handler(AppInfo baseAppIdUtil, GlobalConfigService gcs, UserService userService, WxMpServiceRepo wxRepo) {
		this.baseAppIdUtil = baseAppIdUtil;
		this.gcs = gcs;
		this.userService = userService;
		this.wxRepo = wxRepo;
	}


	@Override
	public ThirdPartyAccount doAuth(String code, String state) {
		String openId, avatar, avatarHd, nickname;
		ThirdPartyAccount tpa;
		String appId;
		String[]  stateParam;


		if (StringUtil.isBlank(state)){
			throw new BadCredentialsException("state参数错误");
		} else {
			stateParam = state.split(",");
			if (stateParam.length < 3) {
				throw new BadCredentialsException("state参数错误");
			}
			appId = stateParam[2];
		}
		try {
			WxMpUser wechatUser;
			WxMpService ws = wxRepo.getService(appId);
			if (ws == null) {
				throw new BadCredentialsException("无效的APPID");
			}


			WxMpOAuth2AccessToken accessToken = ws.oauth2getAccessToken(code);
			wechatUser = ws.oauth2getUserInfo(accessToken, null);

			openId = wechatUser.getOpenId();
			nickname = wechatUser.getNickname();
			avatar = wechatUser.getHeadImgUrl();
			avatarHd = wechatUser.getHeadImgUrl();
			tpa = userService.getWxAccoutByOpenId(openId);
			if (tpa == null) {
				tpa = new ThirdPartyAccount();
			}
			tpa.setUnionId(wechatUser.getUnionId());
			tpa.setOpenId(openId);
			tpa.setAccountType("wechat");
			tpa.setAppId(appId);
			tpa.setAvatar(avatar);
			tpa.setAvatarHd(avatarHd);
			tpa.setNickname(nickname);
			tpa.setAccessToken(accessToken.getAccessToken());
			return tpa;
		} catch (Exception e) {
			String trackStr = gcs.getConfig("runConfig", baseAppIdUtil.getAppId(), "serviceTrack", false);
			boolean track = !StringUtil.isBlank(trackStr) && Boolean.parseBoolean(trackStr);
			if (track) {
				e.printStackTrace();
			}
			logger.error("处理绑定微信失败, 错误原因:{}", e.getLocalizedMessage());
			throw new AuthenticationServiceException(e.getLocalizedMessage());
		}
	}

}
