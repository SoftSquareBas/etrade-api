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
@Entity(name = "transfer_item")
@Table(name = "transfer_item")
public class TransferItem {

	@Id
	@SequenceGenerator(name = "trfitem_seq", sequenceName = "trfitem_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trfitem_seq")
	@Column(name = "trfitem_id", precision = 20)
	private Integer trfItemId;

	@Column(name = "cpn_id", precision = 10)
	private Integer companyId;

	@Column(name = "branch_id", precision = 10)
	private Integer branchId;

	@Column(name = "trf_id", length = 30)
	private String trfId;

	@Column(name = "trfitem_remark", length = 30)
	private String remark;

}
