package com.noknown.framework.security.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 组信息
 *
 * @author guodong
 */
@Entity
@Table(name = "security_group")
@JsonIgnoreProperties({"handler", "hibernateLazyInitializer", "authorities"})
public class Group implements Serializable {
	private static final long serialVersionUID = 395214174702401936L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(length = 32)
	private String name;

	private Integer parentGroupId;

	private String comment;

	public Integer getId() {
		return id;
	}

	public Group setId(Integer id) {
		this.id = id;
		return this;
	}

	public String getName() {
		return name;
	}

	public Group setName(String name) {
		this.name = name;
		return this;
	}

	public Integer getParentGroupId() {
		return parentGroupId;
	}

	public Group setParentGroupId(Integer parentGroupId) {
		this.parentGroupId = parentGroupId;
		return this;
	}

	public String getComment() {
		return comment;
	}

	public Group setComment(String comment) {
		this.comment = comment;
		return this;
	}
}
