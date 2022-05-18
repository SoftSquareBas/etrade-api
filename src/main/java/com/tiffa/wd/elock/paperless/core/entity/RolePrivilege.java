package com.tiffa.wd.elock.paperless.core.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity(name = "se_role_privilege")
@Table(name = "se_role_privilege")
public class RolePrivilege {

	@Id
	private RolePrivilegePk pk;

}
