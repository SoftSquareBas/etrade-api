package com.tiffa.wd.elock.paperless.core.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;

@Data
@Embeddable
public class InperiodPk implements Serializable   {
	
	//private static final long serialVersionUID = 3020566672725684296L;

	@Column(name = "ou_code", length = 15)
	private String ouCode;

	@Column(name = "year", length = 4)
	private Integer yeAr;

    @Column(name = "period", length = 2)
	private Integer perIod;
}
