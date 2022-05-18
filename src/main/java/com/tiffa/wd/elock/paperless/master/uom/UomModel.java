package com.tiffa.wd.elock.paperless.master.uom;

import com.tiffa.wd.elock.paperless.core.CommonRequest;
import com.tiffa.wd.elock.paperless.core.PageRequest;
import com.tiffa.wd.elock.paperless.core.ValidateRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=true)
@ToString(callSuper = true)
public class UomModel extends CommonRequest implements PageRequest, ValidateRequest {

	private Integer uomId;
	private String uomCode;
	private String uomName;
	private String uomCustomsCode;
	
}
