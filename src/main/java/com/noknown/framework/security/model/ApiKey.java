package com.noknown.framework.security.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @author guodong
 * @date 2018/4/2
 */
@Entity
@Table(name = "security_api_key")
public class ApiKey implements Serializable {

	private static final long serialVersionUID = 4095725661950696203L;
	@Id
	@Column(length = 64)
	private String accessKey;

	private String securityKey;

	private Integer userId;

	private Boolean enable;

	private Date createTime;

	public String getAccessKey() {
		return accessKey;
	}

	public ApiKey setAccessKey(String accessKey) {
		this.accessKey = accessKey;
		return this;
	}

	public String getSecurityKey() {
		return securityKey;
	}

	public ApiKey setSecurityKey(String securityKey) {
		this.securityKey = securityKey;
		return this;
	}

	public Integer getUserId() {
		return userId;
	}

	public ApiKey setUserId(Integer userId) {
		this.userId = userId;
		return this;
	}

	public Boolean getEnable() {
		return enable;
	}

	public ApiKey setEnable(Boolean enable) {
		this.enable = enable;
		return this;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public ApiKey setCreateTime(Date createTime) {
		this.createTime = createTime;
		return this;
	}
}
