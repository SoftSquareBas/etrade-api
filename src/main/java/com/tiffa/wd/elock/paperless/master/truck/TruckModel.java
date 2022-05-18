package com.tiffa.wd.elock.paperless.master.truck;

import com.tiffa.wd.elock.paperless.core.CommonRequest;
import com.tiffa.wd.elock.paperless.core.PageRequest;
import com.tiffa.wd.elock.paperless.core.ValidateRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=true)
@ToString(callSuper = true)
public class TruckModel extends CommonRequest implements PageRequest, ValidateRequest {

	private Integer truckId;
	private String truckCode;
	private String truckName;
	
}
