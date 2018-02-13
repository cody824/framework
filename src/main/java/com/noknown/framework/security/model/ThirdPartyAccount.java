package com.noknown.framework.security.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "security_tpa")
public class ThirdPartyAccount implements Serializable {
	
	private static final long serialVersionUID = -2129093964399945648L;
	
	/**
	 * 主键 第三方账号ID
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	
	/**
	 * 关联的用户ID
	 */
	private Integer userId;
	
	/**
	 * 第三方的账号类型
	 */
	private String accountType;
	
	/**
	 * 第三方账号的ID
	 */
	private String openId;
	
	/**
	 * 头像
	 */
	private String avatar;
	
	/**
	 * 高清头像
	 */
	private String avatarHd;
	
	/**
	 * 
	 * 昵称
	 */
	private String nickname;
	
	/**
	 * 访问token
	 */
	private String accessToken;
	
	/**
	 * 
	 */
	private String appId;
	
	/**
	 * 
	 */
	private String unionId;

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the userId
	 */
	public Integer getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	/**
	 * @return the accountType
	 */
	public String getAccountType() {
		return accountType;
	}

	/**
	 * @param accountType the accountType to set
	 */
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	

	/**
	 * @return the openId
	 */
	public String getOpenId() {
		return openId;
	}

	/**
	 * @param openId the openId to set
	 */
	public void setOpenId(String openId) {
		this.openId = openId;
	}

	/**
	 * @return the avatar
	 */
	public String getAvatar() {
		return avatar;
	}

	/**
	 * @param avatar the avatar to set
	 */
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	/**
	 * @return the avatar_hd
	 */
	public String getAvatarHd() {
		return avatarHd;
	}

	/**
	 * @param avatarHd the avatar_hd to set
	 */
	public void setAvatarHd(String avatarHd) {
		this.avatarHd = avatarHd;
	}

	/**
	 * @return the nickname
	 */
	public String getNickname() {
		return nickname;
	}

	/**
	 * @param nickname the nickname to set
	 */
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	/**
	 * @return the accessToken
	 */
	public String getAccessToken() {
		return accessToken;
	}

	/**
	 * @param accessToken the accessToken to set
	 */
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	/**
	 * @return the appId
	 */
	public String getAppId() {
		return appId;
	}

	/**
	 * @param appId the appId to set
	 */
	public void setAppId(String appId) {
		this.appId = appId;
	}

	/**
	 * @return the unionId
	 */
	public String getUnionId() {
		return unionId;
	}

	/**
	 * @param unionId the unionId to set
	 */
	public void setUnionId(String unionId) {
		this.unionId = unionId;
	}

}
