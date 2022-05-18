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
@Entity(name = "station")
@Table(name = "station")
public class Location {

	@Id
	@SequenceGenerator(name = "station_seq", sequenceName = "station_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "station_seq")
	@Column(name = "station_id", precision = 20)
	private Integer locationId;

	@Column(name = "cpn_id", precision = 10)
	private Integer companyId;

	@Column(name = "branch_id", precision = 10)
	private Integer branchId;

	@Column(name = "stn_code", length = 20)
	private String locationCode;

	@Column(name = "stn_name", length = 50)
	private String locationName;

	@Column(name = "cust_office_code", length = 50)
	private String customerOfficeCode;

	@Column(name = "cust_office_name", length = 50)
	private String customerOfficeName;

	@Column(name = "station_cpn_area_code", length = 100)
	private String stationCompanyAreaCode;

	@Column(name = "address", length = 200)
	private String address;

	@Column(name = "telephone", length = 20)
	private String telephone;

	@Column(name = "fax", length = 20)
	private String fax;

	@Column(name = "seq_reset", length = 8)
	private String seqReset;

	@Column(name = "seq_inc", precision = 3, scale = 0)
	private Integer seqInc;

	@Column(name = "seq_start", precision = 10, scale = 0)
	private Integer seqStart;

	@Column(name = "seq_next", precision = 10, scale = 0)
	private Integer seqNext;

	@Column(name = "reader1", length = 20)
	private String reader1;

	@Column(name = "reader2", length = 20)
	private String reader2;

	@Column(name = "reader3", length = 20)
	private String reader3;

	@Column(name = "reader4", length = 20)
	private String reader4;

	@Column(name = "reader5", length = 20)
	private String reader5;

	@Column(name = "is_airport", precision = 1, scale = 0)
	private Integer isAirport;

	@Column(name = "taxno", length = 10)
	private String taxNo;

	@Column(name = "cpn_branch", length = 10)
	private String companyBranch;
}
