package com.noknown.framework.security.model;

import org.springframework.context.annotation.Lazy;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Date;


@MappedSuperclass
@Lazy(value = false)
public abstract class BaseUserDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7464655669064879252L;
	
	@Id
	private Integer id;
	
	/**
	 * 用户账号
	 */
	@Column(length = 32)
	private String nick;
	
	/**
	 * 全名
	 */
	@Column(length = 32)
	private String fullName="";
	
	/**
	 * 生日
	 */
/*	@JsonSerialize(using = CustomDateSerializer.class)  */
	private Date birthDay;
	
	/**
	 * 邮箱
	 */
	private String email="";
	
	/**
	 * 性别
	 */
	private Integer sex;
	
	
	/**
	 * 电话
	 */
	@Column(length = 16)
	private String phone="";
	
	/**
	 * 籍贯
	 */
	@Column(length = 16)
	private String nativePlace;
	
	/**
	 * 爱好
	 */
	private String hobby;
	
	/**
	 * 座右铭
	 */
	private String motto;
	
	/**
	 * 手机
	 */
	@Column(length = 16)
	private String mobile;
	

	/**
	 * 头像
	 */
	private String avatar;
	
	/**
	 * 高清头像
	 */
	private String avatarHd;


	
	/**
	 * @return the nick
	 */
	public String getNick() {
		return nick;
	}

	/**
	 * @param nick the nick to set
	 */
	public void setNick(String nick) {
		this.nick = nick;
	}

	/**
	 * @return the fullName
	 */
	public String getFullName() {
		return fullName;
	}

	/**
	 * @param fullName the fullName to set
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	/**
	 * @return the birthDay
	 */
	public Date getBirthDay() {
		return birthDay;
	}

	/**
	 * @param birthDay the birthDay to set
	 */
	public void setBirthDay(Date birthDay) {
		this.birthDay = birthDay;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the sex
	 */
	public Integer getSex() {
		return sex;
	}

	/**
	 * @param sex the sex to set
	 */
	public void setSex(Integer sex) {
		this.sex = sex;
	}

	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @param phone the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * @return the nativePlace
	 */
	public String getNativePlace() {
		return nativePlace;
	}

	/**
	 * @param nativePlace the nativePlace to set
	 */
	public void setNativePlace(String nativePlace) {
		this.nativePlace = nativePlace;
	}

	/**
	 * @return the hobby
	 */
	public String getHobby() {
		return hobby;
	}

	/**
	 * @param hobby the hobby to set
	 */
	public void setHobby(String hobby) {
		this.hobby = hobby;
	}

	/**
	 * @return the motto
	 */
	public String getMotto() {
		return motto;
	}

	/**
	 * @param motto the motto to set
	 */
	public void setMotto(String motto) {
		this.motto = motto;
	}

	/**
	 * @return the mobile
	 */
	public String getMobile() {
		return mobile;
	}

	/**
	 * @param mobile the mobile to set
	 */
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

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
	 * @return the avatarHd
	 */
	public String getAvatarHd() {
		return avatarHd;
	}

	/**
	 * @param avatarHd the avatarHd to set
	 */
	public void setAvatarHd(String avatarHd) {
		this.avatarHd = avatarHd;
	}

	
}
