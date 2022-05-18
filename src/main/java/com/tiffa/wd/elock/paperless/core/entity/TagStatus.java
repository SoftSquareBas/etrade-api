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
@Entity(name = "tag_status")
@Table(name = "tag_status")
public class TagStatus {

	@Id
	@SequenceGenerator(name = "tag_status_seq", sequenceName = "tag_status_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tag_status_seq")
	@Column(name = "id", precision = 20)
	private Integer id;

	@Column(name = "cpn_id", precision = 10)
	private Integer companyId;

	@Column(name = "branch_id", precision = 10)
	private Integer branchId;

	@Column(name = "tag_id", precision = 20)
	private String tagId;

	@Column(name = "truck_code", precision = 20)
	private String truckCode;

	@Column(name = "trf_ids", precision = 200)
	private String trfIds;

	@Column(name = "trf_from", precision = 20)
	private String trfFrom;

	@Column(name = "trf_to", precision = 20)
	private String trfTo;

	@Column(name = "state", precision = 1)
	private String state;
}
