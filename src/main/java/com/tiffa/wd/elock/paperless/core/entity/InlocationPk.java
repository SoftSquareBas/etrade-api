package com.tiffa.wd.elock.paperless.core.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;

@Data
@Embeddable
public class InlocationPk implements Serializable   {
	
	private static final long serialVersionUID = 3020566672725684296L;

	@Column(name = "ou_code", length = 10)
	private String ouCode;

	@Column(name = "ware_code", length = 10)
	private String wareCode;

    @Column(name = "location_code", length = 10)
	private String locationCode;
}
