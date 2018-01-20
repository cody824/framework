package com.noknown.framework.security.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import libs.fastjson.com.alibaba.fastjson.annotation.JSONField;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "security_user")
@JsonIgnoreProperties({ "handler","hibernateLazyInitializer", "authorities" }) 
public class User implements Serializable, Authentication, UserDetails {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6026489601310615286L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(length = 32)
	private String nick;

	@Column(length = 32)
	private String password;

	private String email;

	@Column(length = 16)
	private String mobile;
	
	private Boolean enable = true;
	
	private Boolean nickOk = false;
	
	private Date createDate;
	
	private Date lastPasswordResetDate;
	
	@Transient
	private boolean isAuth;

	@Transient
	private Object details;

	@ManyToMany(cascade = { CascadeType.REFRESH }, fetch = FetchType.EAGER)
	@JSONField(serialize = false)
	private List<Role> roles = new ArrayList<>();


	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> auths = new ArrayList<>();
        List<Role> roles = this.getRoles();
        for (Role role : roles) {
            auths.add(new SimpleGrantedAuthority(role.getName()));
        }
        return auths;
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
	 * @return the enable
	 */
	public Boolean getEnable() {
		return enable;
	}


	/**
	 * @param enable the enable to set
	 */
	public void setEnable(Boolean enable) {
		this.enable = enable;
	}


	/**
	 * @return the createDate
	 */
	public Date getCreateDate() {
		return createDate;
	}


	/**
	 * @param createDate the createDate to set
	 */
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}


	/**
	 * @return the lastPasswordResetDate
	 */
	public Date getLastPasswordResetDate() {
		return lastPasswordResetDate;
	}


	/**
	 * @param lastPasswordResetDate the lastPasswordResetDate to set
	 */
	public void setLastPasswordResetDate(Date lastPasswordResetDate) {
		this.lastPasswordResetDate = lastPasswordResetDate;
	}


	/**
	 * @return the roles
	 */
	public List<Role> getRoles() {
		return roles;
	}


	/**
	 * @param roles the roles to set
	 */
	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
	
	public void addRole(Role role) {
		if (roles == null) roles = new ArrayList<>();
		if (!roles.contains(role))
			roles.add(role);
	}
	
	public void removeRole(Role role) {
		if (roles != null){
			roles.remove(role);
		} 
	}


	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}



	@Override
	public String getName() {
		return "id";
	}



	@Override
	public Object getCredentials() {
		return password;
	}



	@Override
	@JsonIgnore
	public  Object getDetails() {
		return details;
	}


	public void setDetails(Object details) {
		this.details = details;
	}

	@Override
	public Object getPrincipal() {
		return id;
	}



	@Override
	public boolean isAuthenticated() {
		return isAuth;
	}



	@Override
	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
		isAuth = isAuthenticated;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return nick;
	}



	@Override
	public boolean isAccountNonExpired() {
		return true;
	}



	@Override
	public boolean isAccountNonLocked() {
		return true;
	}



	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}



	@Override
	public boolean isEnabled() {
		return enable;
	}



	/**
	 * @return the nickOk
	 */
	public Boolean getNickOk() {
		return nickOk;
	}



	/**
	 * @param nickOk the nickOk to set
	 */
	public void setNickOk(Boolean nickOk) {
		this.nickOk = nickOk;
	}

}
