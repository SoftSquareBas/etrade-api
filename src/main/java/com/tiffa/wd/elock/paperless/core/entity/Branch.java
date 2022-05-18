package com.tiffa.wd.elock.paperless.core.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity(name = "branch")
@Table(name = "branch")
public class Branch {

	@Id
	@SequenceGenerator(name = "branch_seq", sequenceName = "branch_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "branch_seq")
	@Column(name = "branch_id", precision = 10)
	private Integer branchId;

	@Column(name = "cpn_id", precision = 10)
	private Integer companyId;

	@Column(name = "branch_code", precision = 10)
	private String branchCode;

	@Column(name = "branch_name", length = 100)
	private String branchName;

	@Column(name = "branch_status", precision = 3)
	private Integer branchStatus;

	@Column(name = "user_id", precision = 10)
	private Integer userId;

	@Column(name = "created_date")
	private LocalDateTime createdDate;

	@Column(name = "created_user", length = 20)
	private String createdUser;

	@Column(name = "updated_date")
	private LocalDateTime updatedDate;

	@Column(name = "updated_user", length = 20)
	private String updatedUser;

	@Column(name = "declarationcost")
	private BigDecimal declarationCost;

	@Column(name = "webappcost")
	private BigDecimal webAppCost;

	@Column(name = "pertripcost")
	private BigDecimal perTripCost;

	@Column(name = "currency", precision = 20)
	private String currency;

	@Column(name = "exchangerate")
	private BigDecimal exchangeRate;

}
