package com.tiffa.wd.elock.paperless.core.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;

@Data
@Embeddable
public class DeclarationPk implements Serializable {

	private static final long serialVersionUID = 3020566672725684296L;

	@Column(name = "trf_id", length = 30)
	private String trfId;

	@Column(name = "cpn_id", precision = 2)
	private Integer companyId;

}
