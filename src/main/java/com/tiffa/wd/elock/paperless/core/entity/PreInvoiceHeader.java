package com.tiffa.wd.elock.paperless.core.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity(name = "pre_invoice_hdr")
@Table(name = "pre_invoice_hdr")
public class PreInvoiceHeader {

	@Id
	@SequenceGenerator(name = "preinvoiceheader_seq", sequenceName = "preinvoiceheader_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "preinvoiceheader_seq")
	@Column(name = "id", precision = 10)
	private Integer id;

	@Column(name = "cpn_id", precision = 10)
	private Integer companyId;

	@Column(name = "branch_id", precision = 10)
	private Integer branchId;

	@Column(name = "invoiceno", length = 10)
	private String invoiceNo;

	@Column(name = "invoicedate")
	private LocalDate invoiceDate;

	@Column(name = "shipto", length = 256)
	private String shipTo;

	@Column(name = "shipfrom", length = 256)
	private String shipFrom;

	@Column(name = "companytax", length = 256)
	private String companyTax;

	@Column(name = "companybranno", length = 256)
	private String companyBranchNo;

	@Column(name = "cosineename", length = 256)
	private String cosineeName;

	@Column(name = "purchasecountry", length = 256)
	private String purchaseCountry;

	@Column(name = "destinationcountry", length = 256)
	private String destinationCountry;

	@Column(name = "termpayment", length = 256)
	private String termPayment;

	@Column(name = "invoicestatus", length = 25)
	private String invoiceStatus;

	@Column(name = "torefno", length = 25)
	private String toRefNo;

	@Column(name = "partno", length = 25)
	private String partNo;

	@Column(name = "datastatus", length = 100)
	private String dataStatus;

	@Column(name = "truck_code", length = 20)
	private String truckCode;

	@Column(name = "declarationnumber", length = 14)
	private String declarationNumber;
}
