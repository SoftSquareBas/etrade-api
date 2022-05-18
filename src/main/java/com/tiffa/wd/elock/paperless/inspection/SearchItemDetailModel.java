package com.tiffa.wd.elock.paperless.inspection;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class SearchItemDetailModel {

	private String trfId;
	private String tagCode;
	private String drvCode;

}
