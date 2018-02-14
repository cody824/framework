package com.noknown.framework.security.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * 用户角色信息
 * 
 * @author guodong
 * 
 */
@Entity
@Table(name = "security_role")
public class Role implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1043578378399795034L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(unique=true)
	private String name;
	
	private String comment;
	

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
	 * @return the name
	 */
	public String getName() {
		return name;
	}



	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}



	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}



	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}


	@Override
	public String toString() {
		return "Role [id=" + id + ", name=" + name + ", comment=" + comment + "]";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Role role = (Role) o;
		return Objects.equals(id, role.id) &&
				Objects.equals(name, role.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name);
	}
}
