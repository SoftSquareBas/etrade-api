package com.tiffa.wd.elock.paperless.core.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity(name = "se_role")
@Table(name = "se_role")
public class Role {

	@Id
	@SequenceGenerator(name = "role_seq", sequenceName = "role_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_seq")
	@Column(name = "role_id", precision = 10)
	private Integer roleId;

	@Column(name = "rolelevel", length = 50)
	private String roleLevel;

	@Column(name = "rolecode", length = 50)
	private String roleCode;

	@Column(name = "rolename", length = 50)
	private String roleName;

	@Column(name = "description", length = 50)
	private String description;

	@Column(name = "inactive")
	private LocalDate inactive;

	@Column(name = "cpn_id", precision = 10)
	private Integer companyId;

	@Column(name = "branch_id", precision = 10)
	private Integer branchId;

}
