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
@Entity(name = "user_paperless")
@Table(name = "user_paperless")
public class User {

	@Id
	@SequenceGenerator(name = "user_seq", sequenceName = "user_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
	@Column(name = "user_id", precision = 10)
	private Integer userId;

	@Column(name = "cpn_id", precision = 10)
	private Integer companyId;

	@Column(name = "user_login", length = 20)
	private String userLogin;

	@Column(name = "user_pwd", length = 20)
	private String userPwd;

	@Column(name = "user_name", length = 50)
	private String userName;

	@Column(name = "user_startdate")
	private LocalDate userStartDate;

	@Column(name = "user_expiredate")
	private LocalDate userExpireDate;

	@Column(name = "is_officer", precision = 3)
	private Integer isOfficer;

	@Column(name = "branch_id", precision = 10)
	private Integer branchId;
}
