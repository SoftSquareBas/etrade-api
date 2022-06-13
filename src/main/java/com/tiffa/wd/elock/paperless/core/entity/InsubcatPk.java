package com.tiffa.wd.elock.paperless.core.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;

@Data
@Embeddable
public class InsubcatPk implements Serializable   {
	
	//private static final long serialVersionUID = 3020566672725684296L;

	@Column(name = "category_code", length = 10)
	private String categoreyCode;

	@Column(name = "sub_cat_code", length = 10)
	private String subCategorey;

    
}
