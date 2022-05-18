package com.tiffa.wd.elock.paperless.dashboard.documentPerformance;

import com.tiffa.wd.elock.paperless.core.CommonRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=true)
@ToString(callSuper = true)
public class DocumentPerformanceModel extends CommonRequest {

	private Integer period;
	
}