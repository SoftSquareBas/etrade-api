package com.tiffa.wd.elock.paperless.core.model;

import java.time.LocalDate;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@Deprecated
public class InspectionReportModel {

	private String tripId;
	private LocalDate dateFrom;
	private LocalDate dateTo;
	private String tripStatus;
	private String origin;
	private String dest;
	private String declarationNo;
	private Integer pageNumber;
	private Integer pageSize;

}
