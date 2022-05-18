package com.tiffa.wd.elock.paperless.core.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;

@Data
@Embeddable
public class UserRolePk implements Serializable {

	private static final long serialVersionUID = -3753607443357282598L;

	@Column(name = "user_id", precision = 10)
	private Integer userId;

	@Column(name = "role_id", precision = 10)
	private Integer roleId;

}
