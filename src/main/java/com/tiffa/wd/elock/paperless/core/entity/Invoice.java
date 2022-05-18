package com.tiffa.wd.elock.paperless.core.entity;

import java.math.BigDecimal;
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
@Entity(name = "invoice")
@Table(name = "invoice")
public class Invoice {

	@Id
	@SequenceGenerator(name = "inv_seq", sequenceName = "inv_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inv_seq")
	@Column(name = "inv_id")
	private Integer invId;

	@Column(name = "cpn_id", precision = 10)
	private Integer companyId;

	@Column(name = "branch_id", precision = 10)
	private Integer branchId;

	@Column(name = "trf_id", length = 30)
	private String trfId;

	@Column(name = "trfitem_id", precision = 10)
	private Integer trfItemId;

	@Column(name = "inv_no", length = 50)
	private String invNo;

	@Column(name = "inv_date")
	private LocalDate invDate;

	@Column(name = "inv_gross_weight", precision = 15, scale = 5)
	private BigDecimal invGrossWeight;

	@Column(name = "inv_gross_weight_unit", length = 5)
	private String invGrossWeightUnit;

	@Column(name = "inv_package", precision = 10)
	private Integer invPackage;

	@Column(name = "inv_package_unit", length = 5)
	private String invPackageUnit;

}
