package com.tiffa.wd.elock.paperless.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity(name = "package")
@Table(name = "package")
public class Package {

	@Id
	@SequenceGenerator(name = "package_seq", sequenceName = "package_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "package_seq")
	@Column(name = "package_id", precision = 20)
	private Integer packageId;

	@Column(name = "cpn_id", precision = 10)
	private Integer companyId;

	@Column(name = "branch_id", precision = 10)
	private Integer branchId;

	@Column(name = "pkg_code", length = 5)
	private String packageCode;

	@Column(name = "pkg_name", length = 20)
	private String packageName;

}
