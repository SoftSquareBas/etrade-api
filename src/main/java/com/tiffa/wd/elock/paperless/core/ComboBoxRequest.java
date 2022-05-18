package com.tiffa.wd.elock.paperless.core;

import java.io.Serializable;

import lombok.Data;

@Data
public class ComboBoxRequest implements Serializable {

	private static final long serialVersionUID = -6681254479746553386L;

	private Integer companyId;
	private Integer branchId;
	
	private String query;
	private String from;
	private String to;

}
