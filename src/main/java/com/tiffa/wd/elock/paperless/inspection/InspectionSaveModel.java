package com.tiffa.wd.elock.paperless.inspection;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper=true)
public class InspectionSaveModel extends InspectionRequestModel {

	private String requesterId;
	private String inspecterId;
	private String result;
	private String remark;

}
