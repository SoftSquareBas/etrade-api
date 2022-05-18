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
@Entity(name = "truck")
@Table(name = "truck")
public class Truck {

	@Id
	@SequenceGenerator(name = "truck_seq", sequenceName = "truck_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "truck_seq")
	@Column(name = "truck_id", precision = 20)
	private Integer truckId;

	@Column(name = "cpn_id", precision = 10)
	private Integer companyId;

	@Column(name = "branch_id", precision = 10)
	private Integer branchId;

	@Column(name = "truck_code", length = 20)
	private String truckCode;

	@Column(name = "truck_name", length = 20)
	private String truckName;

}
