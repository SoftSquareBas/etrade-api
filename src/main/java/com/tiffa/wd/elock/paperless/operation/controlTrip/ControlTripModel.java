package com.tiffa.wd.elock.paperless.operation.controlTrip;

import java.util.List;

import com.tiffa.wd.elock.paperless.core.CommonRequest;
import com.tiffa.wd.elock.paperless.core.PageRequest;
import com.tiffa.wd.elock.paperless.core.ValidateRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=true)
@ToString(callSuper = true)
public class ControlTripModel extends CommonRequest implements PageRequest, ValidateRequest {
	
	private String tagCode;
	private String truckCode;
	private String vehicleType;
	private String driver;
	private Integer intervalTime;
	private Boolean lastTruck;
	private String route;
	private String containerNumber;
	
	private List<TripRecord> tripRecords;
	
	private Integer maxRow;
	
}
