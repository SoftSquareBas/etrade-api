package com.tiffa.wd.elock.paperless.core.entity;

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
@Entity(name = "tag")
@Table(name = "tag")
public class Tag {

	@Id
	@SequenceGenerator(name = "tag_seq", sequenceName = "tag_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tag_seq")
	@Column(name = "tag_id", precision = 20)
	private Integer tagId;

	@Column(name = "cpn_id", precision = 10)
	private Integer companyId;

	@Column(name = "branch_id", precision = 10)
	private Integer branchId;

	@Column(name = "tag_code", length = 20)
	private String tagCode;

	@Column(name = "tag_name", length = 20)
	private String tagName;

	@Column(name = "tag_lastupdate")
	private LocalDateTime lastUpdate;

	@Column(name = "tag_status", length = 25)
	private String tagStatus;

}
