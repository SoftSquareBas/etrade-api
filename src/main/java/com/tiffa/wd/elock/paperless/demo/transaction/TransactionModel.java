package com.tiffa.wd.elock.paperless.demo.transaction;

import com.tiffa.wd.elock.paperless.core.CommonRequest;
import com.tiffa.wd.elock.paperless.core.PageRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TransactionModel extends CommonRequest implements PageRequest {

	private String ouCode;
	private String receiveTypeFrom;
	private String receiveNoFrom;
	private String receiveNoTo;
	private String receiveDateFrom;
	private String warehouseFrom;
	private String warehouseTo;
	private String staffFrom;
	private String staffTo;
	private String status;
	
}
