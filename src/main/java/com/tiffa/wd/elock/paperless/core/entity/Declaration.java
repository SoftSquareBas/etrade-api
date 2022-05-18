package com.tiffa.wd.elock.paperless.core.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity(name = "declaration")
@Table(name = "declaration")
public class Declaration {

	@Id
	private DeclarationPk pk;

	@Column(name = "trf_id", length = 30, insertable = false, updatable = false)
	private String trfId;

	@Column(name = "cpn_id", precision = 2, insertable = false, updatable = false)
	private Integer companyId;

	@Column(name = "trf_from", length = 7)
	private String trfFrom;

	@Column(name = "trf_to", length = 7)
	private String trfTo;

	@Column(name = "trf_date")
	private LocalDate trfDate;

	@Column(name = "trf_lt", precision = 1)
	private Integer trfLt;

	@Column(name = "trf_type", length = 10)
	private String trfType;

	@Column(name = "remark", length = 100)
	private String remark;

	@Column(name = "trf_kna", length = 255)
	private String trfKna;

	@Column(name = "trf_id_cancel", length = 20)
	private String trfIdCancel;

	@Column(name = "special_remark", length = 255)
	private String specialRemark;

	@Column(name = "last_user", length = 50)
	private String lastUser;

	@Column(name = "amendment_flag", precision = 1)
	private Integer amendmentFlag;

	@Column(name = "canceled_flag", precision = 1)
	private Integer canceledFlag;

	@Column(name = "manual_flag", precision = 1)
	private Integer manualFlag;

	@Column(name = "xml_data", columnDefinition = "CLOB NOT NULL")
	@Lob
	private String xmlData;

	@Column(name = "dct_id", length = 14)
	private String dctId;

}
