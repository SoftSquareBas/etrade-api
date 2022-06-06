package com.tiffa.wd.elock.paperless.core.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;

@Data
@Embeddable
public class InlocationPk implements Serializable {

	private static final long serialVersionUID = 3020566672725684296L;

	@Column(name = "oucode", length = 10)
	private String trfId;

	@Column(name = "ware_code", precision = 2)
	private Integer wareCode;

    @Column(name = "location_code", precision = 3)
	private Integer locationCode;
}
