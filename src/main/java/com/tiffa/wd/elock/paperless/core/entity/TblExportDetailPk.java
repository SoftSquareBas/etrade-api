package com.tiffa.wd.elock.paperless.core.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;

@Data
@Embeddable
public class TblExportDetailPk implements Serializable {

	private static final long serialVersionUID = 7972875856755814907L;

	@Column(name = "cpn_id", precision = 10)
	private Integer companyId;

	@Column(name = "branch_id", precision = 10)
	private Integer branchId;

	@Column(name = "filename", length = 14)
	private String filename;

	@Column(name = "tblexportdetail_id", precision = 19)
	private Integer tblExportDetailId;

}