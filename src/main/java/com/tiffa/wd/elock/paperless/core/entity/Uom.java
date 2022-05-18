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
@Entity(name = "uom")
@Table(name = "uom")
public class Uom {

	@Id
	@SequenceGenerator(name = "uom_seq", sequenceName = "uom_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "uom_seq")
	@Column(name = "uom_id", precision = 20)
	private Integer uomId;

	@Column(name = "cpn_id", precision = 10)
	private Integer companyId;

	@Column(name = "branch_id", precision = 10)
	private Integer branchId;

	@Column(name = "uom_code", length = 5)
	private String uomCode;

	@Column(name = "uom_name", length = 20)
	private String uomName;

	@Column(name = "uom_customs_code", length = 10)
	private String uomCustomsCode;

}
