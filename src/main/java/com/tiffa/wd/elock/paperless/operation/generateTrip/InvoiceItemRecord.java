package com.tiffa.wd.elock.paperless.operation.generateTrip;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class InvoiceItemRecord {
	
	private String productCode;
	private String description;
	private BigDecimal qty;
	private String qtyUnit;
	private BigDecimal netWeight;
	private String netWeightUnit;
	private BigDecimal value;
}
