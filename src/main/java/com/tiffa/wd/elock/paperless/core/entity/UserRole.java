package com.tiffa.wd.elock.paperless.core.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity(name = "se_user_role")
@Table(name = "se_user_role")
public class UserRole implements Serializable {

	private static final long serialVersionUID = -1490648903642998936L;

	@Id
	private UserRolePk pk;
}
