package com.noknown.framework.security.authentication.oauth2.handler;

import com.noknown.framework.common.config.AppInfo;
import com.noknown.framework.common.service.GlobalConfigService;
import com.noknown.framework.common.util.StringUtil;
import com.noknown.framework.security.authentication.oauth2.Oauth2Handler;
import com.noknown.framework.security.model.ThirdPartyAccount;
import com.noknown.framework.security.service.UserService;
import com.qq.connect.api.OpenID;
import com.qq.connect.api.qzone.UserInfo;
import com.qq.connect.javabeans.AccessToken;
import com.qq.connect.utils.http.HttpClient;
import com.qq.connect.utils.http.PostParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Component;

import java.util.Properties;


/**
 * @author guodong
 */
@Component("QQOauth2Handler")
public class QQOauth2Handler implements Oauth2Handler {

	public final Logger logger = LoggerFactory.getLogger(getClass());

	private final AppInfo baseAppIdUtil;

	private final GlobalConfigService gcs;

	private final UserService userService;

	@Autowired
	public QQOauth2Handler(AppInfo baseAppIdUtil, GlobalConfigService gcs, UserService userService) {
		this.baseAppIdUtil = baseAppIdUtil;
		this.gcs = gcs;
		this.userService = userService;
	}

	@Override
	public ThirdPartyAccount doAuth(String code, String state) {
		String openId, avatar, avatarHd, nickname;
		try {
			AccessToken qqAccessToken;
			HttpClient client = new HttpClient();
			Properties qqP =  gcs.getProperties("qq", baseAppIdUtil.getAppId(), false);

			qqAccessToken = new AccessToken(client.post(qqP.getProperty("accessTokenURL"),
					new PostParameter[]{new PostParameter("client_id", qqP.getProperty("app_ID")),
							new PostParameter("client_secret", qqP.getProperty("app_KEY")),
							new PostParameter("grant_type", "authorization_code"),
							new PostParameter("code", code),
							new PostParameter("redirect_uri", qqP.getProperty("redirect_URI"))}, Boolean.FALSE));

			OpenID userOpenID = new OpenID(qqAccessToken.getAccessToken());
			openId = userOpenID.getUserOpenID();

			UserInfo  qqUserInfo = new UserInfo(qqAccessToken.getAccessToken(), openId);
			nickname = qqUserInfo.getUserInfo().getNickname();
			avatar = qqUserInfo.getUserInfo().getAvatar().getAvatarURL50();
			avatarHd = qqUserInfo.getUserInfo().getAvatar().getAvatarURL100();

			ThirdPartyAccount tpa = userService.getThirdPartyAccountByOpenId(openId, "qq");
			if (tpa == null) {
				tpa = new ThirdPartyAccount();
			}
			tpa.setOpenId(openId);
			tpa.setAccountType("qq");
			tpa.setAvatar(avatar);
			tpa.setAvatarHd(avatarHd);
			tpa.setNickname(nickname);
			return tpa;

		} catch (Exception e) {
			String trackStr = gcs.getConfig("runConfig", baseAppIdUtil.getAppId(), "serviceTrack", false);
			boolean track = !StringUtil.isBlank(trackStr) && Boolean.parseBoolean(trackStr);
			if (track) {
				e.printStackTrace();
			}
			logger.error("处理绑定QQ失败, 错误原因:{}", e.getLocalizedMessage());
			throw new AuthenticationServiceException(e.getLocalizedMessage());
		}
	}
}
