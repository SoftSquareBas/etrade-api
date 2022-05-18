package com.tiffa.wd.elock.paperless.core.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;

@Data
@Embeddable
public class RolePrivilegePk implements Serializable {

	private static final long serialVersionUID = 5137978069162533292L;

	@Column(name = "role_id", precision = 10)
	private Integer roleId;

	@Column(name = "privilege_id", length = 50)
	private Integer privilegeId;

}
