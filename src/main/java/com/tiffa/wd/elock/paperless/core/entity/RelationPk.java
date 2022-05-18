package com.tiffa.wd.elock.paperless.core.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;

@Data
@Embeddable
public class RelationPk implements Serializable {

	private static final long serialVersionUID = 3020566672725684296L;

	@Column(name = "trf_id", length = 30)
	private String trfId;

	@Column(name = "tag_code", length = 20)
	private String tagCode;

	@Column(name = "drv_code", length = 200)
	private String drvCode;

	@Column(name = "cpn_id", precision = 10)
	private Integer companyId;

	@Column(name = "branch_id", precision = 10)
	private Integer branchId;

}
