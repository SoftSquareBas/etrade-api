package com.tiffa.wd.elock.paperless.preOperation.declarationItem;

import java.time.LocalDate;

import com.tiffa.wd.elock.paperless.core.CommonRequest;
import com.tiffa.wd.elock.paperless.core.PageRequest;
import com.tiffa.wd.elock.paperless.core.ValidateRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=true)
@ToString(callSuper = true)
public class DeclarationItemModel extends CommonRequest implements PageRequest, ValidateRequest {

	private LocalDate dateFrom;
	private LocalDate dateTo;
	
	private String declarationNo;
	private Boolean displayOnlyRemainItem;
	
	private String referenceNo;

}
