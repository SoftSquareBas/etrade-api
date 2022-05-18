package com.tiffa.wd.elock.paperless.core.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity(name = "se_user_privilege")
@Table(name = "se_user_privilege")
public class UserPrivilege implements Serializable {

	private static final long serialVersionUID = 8101023070963459274L;

	@Id
	private UserPrivilegePk pk;
}
