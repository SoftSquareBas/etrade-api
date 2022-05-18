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
@Entity(name = "weight")
@Table(name = "weight")
public class Weight {

	@Id
	@SequenceGenerator(name = "weight_seq", sequenceName = "weight_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "weight_seq")
	@Column(name = "weight_id", precision = 20)
	private Integer weightId;

	@Column(name = "cpn_id", precision = 10)
	private Integer companyId;

	@Column(name = "branch_id", precision = 10)
	private Integer branchId;

	@Column(name = "weight_code", length = 5)
	private String weightCode;

	@Column(name = "weight_name", length = 20)
	private String weightName;

}
