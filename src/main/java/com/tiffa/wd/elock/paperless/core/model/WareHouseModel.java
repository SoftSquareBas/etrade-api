package com.tiffa.wd.elock.paperless.core.model;

import com.tiffa.wd.elock.paperless.core.CommonRequest;
import com.tiffa.wd.elock.paperless.core.PageRequest;
import com.tiffa.wd.elock.paperless.core.ValidateRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class WareHouseModel extends CommonRequest implements PageRequest, ValidateRequest {

	private String wareCode;

	private String wareName;
	private String active;
}
