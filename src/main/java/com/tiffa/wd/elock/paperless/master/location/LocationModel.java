package com.tiffa.wd.elock.paperless.master.location;

import com.tiffa.wd.elock.paperless.core.CommonRequest;
import com.tiffa.wd.elock.paperless.core.PageRequest;
import com.tiffa.wd.elock.paperless.core.ValidateRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=true)
@ToString(callSuper = true)
public class LocationModel extends CommonRequest implements PageRequest, ValidateRequest {

	private Integer locationId;
	
	private String locationCode;
	private String locationName;
	
	private String customerOfficeCode;
	private String customerOfficeName;
	
	private String stationCompanyAreaCode;
	
	private String address;
	
	private String companyBranch;
	private String telephone;
	private String fax;
	
	private String reader1;
	private String reader2;
	private String reader3;
	private String reader4;
	private String reader5;

}
