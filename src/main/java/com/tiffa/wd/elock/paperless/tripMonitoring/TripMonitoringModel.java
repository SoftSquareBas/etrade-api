package com.tiffa.wd.elock.paperless.tripMonitoring;

import java.time.LocalDate;

import com.tiffa.wd.elock.paperless.core.CommonRequest;
import com.tiffa.wd.elock.paperless.core.PageRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=true)
@ToString(callSuper = true)
public class TripMonitoringModel extends CommonRequest implements PageRequest {

	private LocalDate dateFrom;
	private LocalDate dateTo;
	private String tripStatus;
	private String tripId;
	private String origin;
	private String dest;
	private String tagId;
	private String truckId;
	
}
