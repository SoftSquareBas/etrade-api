package com.tiffa.wd.elock.paperless.core.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity(name = "tblexportdetail")
@Table(name = "tblexportdetail")
public class TblExportDetail {

	@Id
	private TblExportDetailPk pk;

	@Column(name = "cpn_id", precision = 10, insertable = false, updatable = false)
	private Integer companyId;

	@Column(name = "branch_id", precision = 10, insertable = false, updatable = false)
	private Integer branchId;

	@Column(name = "filename", length = 150, insertable = false, updatable = false)
	private String filename;

	@Column(name = "tblexportdetail_id", precision = 19, insertable = false, updatable = false)
	private Integer tblExportDetailId;

	@Column(name = "declarationnumber", length = 14, insertable = false, updatable = false)
	private String declarationNumber;

	@Column(name = "referenceno", length = 35)
	private String referenceNo;

	@Column(name = "itemno", precision = 10)
	private Integer itemNo;

	@Column(name = "invoiceno", length = 35)
	private String invoiceNo;

	@Column(name = "invoiceitemno", precision = 10)
	private Integer invoiceItemNo;

	@Column(name = "tariffcode", length = 12)
	private String tariffCode;

	@Column(name = "tariffsequence", length = 5)
	private String tariffSequence;

	@Column(name = "statisticalcode", length = 3)
	private String statisticalCode;

	@Column(name = "exporttariff", length = 3)
	private String exportTariff;

	@Column(name = "privilegecode", length = 3)
	private String privilegeCode;

	@Column(name = "ahtncode", length = 12)
	private String ahtnCode;

	@Column(name = "depositreason", length = 12)
	private String depositReason;

	@Column(name = "natureoftransaction", length = 2)
	private String natureOfTransaction;

	@Column(name = "undgnumber", length = 4)
	private String undgNumber;

	@Column(name = "productcode", length = 35)
	private String productCode;

	@Column(name = "thaidescofgoods", length = 512)
	private String thaiDescOfGoods;

	@Column(name = "engdescofgoods", length = 512)
	private String engDescOfGoods;

	@Column(name = "customsproductcode", length = 35)
	private String customsProductCode;

	@Column(name = "productattribute1", length = 35)
	private String productAttribute1;

	@Column(name = "productattribute2", length = 35)
	private String productAttribute2;

	@Column(name = "productyear", length = 4)
	private String productYear;

	@Column(name = "brandname", length = 35)
	private String brandName;

	@Column(name = "Remark", length = 35)
	private String remark;

	@Column(name = "origincountry", length = 2)
	private String originCountry;

	@Column(name = "netweight", precision = 15, scale = 3)
	private BigDecimal netWeight;

	@Column(name = "netweightunit", length = 3)
	private String netWeightUnit;

	@Column(name = "quantity", precision = 15, scale = 3)
	private BigDecimal quantity;

	@Column(name = "quantityunit", length = 3)
	private String quantityUnit;

	@Column(name = "currency", length = 3)
	private String currency;

	@Column(name = "exchangerate", precision = 15, scale = 3)
	private BigDecimal exchangeRate;

	@Column(name = "priceforeign", precision = 15, scale = 3)
	private BigDecimal priceForeign;

	@Column(name = "pricebaht", precision = 16, scale = 2)
	private BigDecimal priceBaht;

	@Column(name = "invoicequantity", precision = 15, scale = 3)
	private BigDecimal invoiceQuantity;

	@Column(name = "invoicequantityunit", length = 3)
	private String invoiceQuantityUnit;

	@Column(name = "invoiceamountforeign", precision = 16, scale = 2)
	private BigDecimal invoiceAmountForeign;

	@Column(name = "invoiceamountbaht", precision = 16, scale = 2)
	private BigDecimal invoiceAmountBaht;

	@Column(name = "increasedpriceforeign", precision = 16, scale = 2)
	private BigDecimal increasedPriceForeign;

	@Column(name = "increasedpricebaht", precision = 16, scale = 2)
	private BigDecimal increasedPriceBaht;

	@Column(name = "fobvalueforeign", precision = 16, scale = 2)
	private BigDecimal fobValueForeign;

	@Column(name = "fobvaluebath", precision = 16, scale = 2)
	private BigDecimal fobValueBaht;

	@Column(name = "fobvalueassess", precision = 16, scale = 2)
	private BigDecimal fobValueAssess;

	@Column(name = "shippingmark", length = 512)
	private String shippingMark;

	@Column(name = "packageamount", precision = 18, scale = 0)
	private Integer packageAmount;

	@Column(name = "packageunit", length = 3)
	private String packageUnit;

	@Column(name = "packageunitname", length = 35)
	private String packageUnitName;

	@Column(name = "reimpcert", length = 1)
	private String reimpcert;

	@Column(name = "boi", length = 1)
	private String boi;

	@Column(name = "bandformula", length = 1)
	private String bandFormula;

	@Column(name = "formula", length = 1)
	private String formula;

	@Column(name = "reexport", length = 1)
	private String reExport;

	@Column(name = "freezone", length = 1)
	private String freeZone;

	@Column(name = "epz", length = 1)
	private String epz;

	@Column(name = "compensationindicator", length = 1)
	private String compensationIndicator;

	@Column(name = "impdeclno", length = 14)
	private String impdeclNo;

	@Column(name = "impdecllinenumber", length = 4)
	private String impdeclLineNumber;

	@Column(name = "formulano", length = 35)
	private String formulaNo;

	@Column(name = "bis19", length = 35)
	private String bis19;

	@Column(name = "boilicenseno", length = 35)
	private String boiLicenseNo;

	@Column(name = "bondlicenseno", length = 35)
	private String bondLicenseNo;

	@Column(name = "purchasecountry", length = 2)
	private String purchaseCountry;

	@Column(name = "lastentry", length = 1)
	private String lastEntry;

	@Column(name = "shfobvaluebaht", precision = 10, scale = 3)
	private BigDecimal shFobValueBaht;

	@Column(name = "shfobvalueforeign", precision = 15, scale = 3)
	private BigDecimal shFobValueForeign;

	@Column(name = "shquantityunit", length = 3)
	private String shQuantityUnit;

	@Column(name = "shquantity", precision = 15, scale = 3)
	private BigDecimal shQuantity;

	@Column(name = "shnetweightunit", length = 3)
	private String shNetWeightUnit;

	@Column(name = "shnetweight", precision = 15, scale = 3)
	private BigDecimal shNetWeight;

	@Column(name = "shpackageunit", length = 3)
	private String shPackageUnit;

	@Column(name = "shpackageamount", precision = 18, scale = 0)
	private Integer shPackageAmount;

	@Column(name = "shstatisticalcode", length = 100)
	private String shStatisticalCode;

	@Column(name = "shtariffcode", length = 100)
	private String shTariffCode;

	@Column(name = "shreferenceno", length = 20)
	private String shReferenceNo;

	@Column(name = "flagcancel", length = 1)
	private String flagCancel;

	@Column(name = "invoicedate")
	private LocalDate invoiceDate;

	@Column(name = "declarationdate")
	private LocalDate declarationDate;

	@Column(name = "remainqty", precision = 15, scale = 3)
	private BigDecimal remainQty;

	@Column(name = "deliveryqty", precision = 15, scale = 3)
	private BigDecimal deliveryQty;

	@Column(name = "remainvalue", precision = 16, scale = 2)
	private BigDecimal remainValue;

	@Column(name = "declarationacceptdate")
	private LocalDate declarationAcceptDate;

	@Column(name = "recvdatetime")
	private LocalDateTime recvDateTime;

	@Column(name = "remainnetweight", precision = 15, scale = 3)
	private BigDecimal remainNetWeight;

	@Column(name = "remaintotalpackageamount", precision = 15, scale = 3)
	private BigDecimal remainTotalPackageAmount;
}
