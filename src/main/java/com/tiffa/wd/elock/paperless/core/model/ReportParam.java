package com.tiffa.wd.elock.paperless.core.model;

import java.util.Map;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class ReportParam {
	
	private String reportName;
	private String exportType;
	private String module;
	private String filename;

	private Map<String, Object> data;
}
