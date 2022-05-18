package com.tiffa.wd.elock.paperless.core;

import java.util.List;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public abstract class CommonRequest {

	private Integer companyId;
	private Integer branchId;
	
	// For must Implement PageRequest
	private Integer pageNumber;
	private Integer pageSize;
	private List<Sort> sorts;
	
	// For must Implement ValidateRequest
	private String field;
	private Object value;
	
	public CommonRequest() {
		log.debug("create {}", this.getClass());
	}
	
	public void setValidateModel(String field, Object value) {
		this.field = field;
		this.value = value;
	}
}
