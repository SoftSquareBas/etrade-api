package com.tiffa.wd.elock.paperless.operation.generateTrip;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.tiffa.wd.elock.paperless.core.CommonRequest;
import com.tiffa.wd.elock.paperless.core.PageRequest;
import com.tiffa.wd.elock.paperless.core.ValidateRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class GenerateTripModel extends CommonRequest implements PageRequest, ValidateRequest {

	/**
	 * 1 = By Invoice, 2 = Allow Extra Item, 3 = Manual
	 */
	private Integer entryOption;

	private LocalDate tripDate;
	private String tripFrom;
	private String tripTo;
	private String tripId;

	private Integer invoiceItemId;

	private String trackId;

	private String shipmentStatus;
	private String remark;

	private String declarationNo;
	private String declarationDesc;
	private Integer tblExportDetailId;
	private String filename;

	private String invoiceNo;
	private BigDecimal grossWeight;
	private String grossWeightUnit;
	private String noOfPackagePackingList;

	private List<DeclarationRecord> records;

	private BigDecimal noOfPackage;
	private String noOfPackageUnit;

	private List<InvoiceItemRecord> invoiceItems;

	private String query;
}
