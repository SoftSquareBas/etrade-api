package com.tiffa.wd.elock.paperless.core.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;

@Data
@Embeddable
public class InTranHeadPk implements Serializable {

	private static final long serialVersionUID = 1860870908104037403L;

	@Column(name = "doc_no", precision = 10)
	private String docNo;

	@Column(name = "doc_type", precision = 10)
	private String docType;

	@Column(name = "ou_code", precision = 10)
	private String ouCode;

}
