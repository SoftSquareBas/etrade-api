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
@Entity(name = "driver")
@Table(name = "driver")
public class Driver {

	@Id
	@SequenceGenerator(name = "driver_seq", sequenceName = "driver_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "driver_seq")
	@Column(name = "driver_id", precision = 20)
	private Integer driverId;

	@Column(name = "cpn_id", precision = 10)
	private Integer companyId;

	@Column(name = "branch_id", precision = 10)
	private Integer branchId;

	@Column(name = "drv_code", length = 50)
	private String driverCode;

	@Column(name = "drv_name", length = 50)
	private String driverName;

}
