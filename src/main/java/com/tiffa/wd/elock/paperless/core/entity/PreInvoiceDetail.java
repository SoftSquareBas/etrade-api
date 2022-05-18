package com.tiffa.wd.elock.paperless.core.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity(name = "pre_invoice_dtl")
@Table(name = "pre_invoice_dtl")
public class PreInvoiceDetail {

	@Id
	@SequenceGenerator(name = "preinvoicedetail_seq", sequenceName = "preinvoicedetail_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "preinvoicedetail_seq")
	@Column(name = "id")
	private Integer id;

	@Column(name = "cpn_id", precision = 10)
	private Integer companyId;

	@Column(name = "branch_id", precision = 10)
	private Integer branchId;

	@Column(name = "invhdr_id", precision = 15)
	private Integer invoiceHeaderId;

	@Column(name = "itemno", precision = 10, scale = 5)
	private BigDecimal itemNumber;

	@Column(name = "partnumber", length = 255)
	private String partNumber;

	@Column(name = "partdescription", length = 255)
	private String partDescription;

	@Column(name = "invoicequantity", precision = 10, scale = 5)
	private BigDecimal invoiceQty;

	@Column(name = "unitofqty", length = 25)
	private String unitOfQty;

	@Column(name = "unitpricefext", precision = 10, scale = 5)
	private BigDecimal unitPriceFext;

	@Column(name = "unitpricecurrencycode", length = 25)
	private String untiPriceCurrencyCode;

	@Column(name = "amountforeignsubtotal", precision = 10, scale = 5)
	private BigDecimal amountForeignSubTotal;

	@Column(name = "amountcurrencycode", length = 25)
	private String amountCurrencyCode;

	@Column(name = "palletno", length = 25)
	private String palletNo;

	@Column(name = "palletdimension", length = 255)
	private String palletDimension;

	@Column(name = "cartonno", length = 255)
	private String cartonNo;

	@Column(name = "cartondimention", length = 255)
	private String cartonDimention;

	@Column(name = "netweightunitcode", length = 25)
	private String netWeightUnitCode;

	@Column(name = "netweight", length = 25)
	private String netWeight;

	@Column(name = "grossweightunitcode", length = 25)
	private String grossWeightUnitCode;

	@Column(name = "grossweight", length = 25)
	private String grossWeight;

	@Column(name = "invoicestatus", length = 25)
	private String invoiceStatus;

	@Column(name = "used_flag", length = 25)
	private String usedFlag;

	@Column(name = "partno", length = 6)
	private String partNo;

	@Column(name = "remain_invqty", precision = 10, scale = 5)
	private BigDecimal remainInvoiceQty;

	@Column(name = "usage_status", length = 1)
	private String usageStatus;

	@Column(name = "tblexportdetail_id", precision = 10)
	private Integer tblExportDetailId;

	@Column(name = "declarationnumber", length = 14)
	private String declarationNumber;

	@Column(name = "referenceno", length = 35)
	private String referenceNo;

	@Column(name = "item_no", precision = 10)
	private Integer itemNo;

	@Column(name = "invoiceitemno", precision = 10)
	private Integer invoiceItemNo;

	@Column(name = "engdescofgoods", length = 512)
	private String engDescOfGoods;

	@Column(name = "total_noofpackage", precision = 10)
	private Integer totalNoOfPackage;

	@Column(name = "total_noofpackageunit", length = 10)
	private String totalNoOfPackageUnit;

}
