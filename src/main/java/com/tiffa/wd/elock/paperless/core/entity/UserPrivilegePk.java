package com.tiffa.wd.elock.paperless.core.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;

@Data
@Embeddable
public class UserPrivilegePk implements Serializable {

	private static final long serialVersionUID = 1860870908104037403L;

	@Column(name = "user_id", precision = 10)
	private Integer userId;

	@Column(name = "privilege_id", precision = 10)
	private Integer privilegeId;

}
