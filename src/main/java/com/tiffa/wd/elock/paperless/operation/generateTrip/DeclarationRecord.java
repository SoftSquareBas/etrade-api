package com.tiffa.wd.elock.paperless.operation.generateTrip;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class DeclarationRecord {
	
	private String filename;
	private Integer tblExportDetailId;
	
	private Integer invoiceHdrId;
	private Integer invoiceDtlId;
	
	private String itemType;
	
    private BigDecimal assignQty;
    private BigDecimal assignPackageAmount;
}
