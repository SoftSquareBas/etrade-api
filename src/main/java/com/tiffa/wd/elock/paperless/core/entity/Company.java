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
@Entity(name = "company")
@Table(name = "company")
public class Company {

	@Id
	@SequenceGenerator(name = "cpn_seq", sequenceName = "cpn_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cpn_seq")
	@Column(name = "cpn_id", precision = 10)
	private Integer companyId;

	@Column(name = "cpn_code", length = 4)
	private String companyCode;

	@Column(name = "cpn_name", length = 50)
	private String companyName;

	@Column(name = "cpn_fullname", length = 50)
	private String companyFullName;

	@Column(name = "cpn_address", length = 200)
	private String cpnAddress;

	@Column(name = "cpn_telephone", length = 20)
	private String cpnTelephone;

	@Column(name = "cpn_fax", length = 20)
	private String cpnFax;

	@Column(name = "cpn_status", precision = 3)
	private Integer cpnStatus;

	@Column(name = "seq_reset", length = 8)
	private String seqReset;

	@Column(name = "seq_inc", precision = 3)
	private Integer seqInc;

	@Column(name = "seq_start", precision = 10)
	private Integer seqStart;

	@Column(name = "seq_next", precision = 10)
	private Integer seqNext;

	@Column(name = "cpn_taxid", length = 25)
	private String cpnTaxId;

	@Column(name = "cpn_establishno", length = 25)
	private String cpnEstablishNo;

	@Column(name = "cpn_rep_fullname", length = 100)
	private String cpnRepFullname;

	@Column(name = "cpn_rep_address", length = 100)
	private String cpnRepAddress;

	@Column(name = "cpn_rep_telephone", length = 100)
	private String cpnRepTelephone;

	@Column(name = "cpn_rep_fax", length = 100)
	private String cpnRepFax;

	@Column(name = "cpn_rep_taxid", length = 100)
	private String cpnRepTaxId;

	@Column(name = "prefix_code", length = 50)
	private String prefixCode;

	@Column(name = "no_digit")
	private Integer noDigit;

	@Column(name = "ops_year", length = 20)
	private String opsYear;

	@Column(name = "ops_year_format", length = 20)
	private String opsYearFormat;

	@Column(name = "ops_month", length = 20)
	private String opsMonth;

	@Column(name = "ops_day", length = 20)
	private String opsDay;

	@Column(name = "current_running")
	private Long currentRunning;

	@Column(name = "col_begin_journey", length = 20)
	private String colBeginJourney;

	@Column(name = "col_end_journey", length = 20)
	private String colEndJourney;

	@Column(name = "col_disarm", length = 20)
	private String colDisarm;

	@Column(name = "auto_begin_journey", length = 20)
	private String autoBeginJourney;

	@Column(name = "auto_end_journey", length = 20)
	private String autoEndJourney;

	@Column(name = "auto_disarm", length = 20)
	private String autoDisarm;

	@Column(name = "user_id")
	private Integer userId;

	@Column(name = "cpn_tax_incentive_id", length = 20)
	private String cpnTaxIncentiveId;
}
