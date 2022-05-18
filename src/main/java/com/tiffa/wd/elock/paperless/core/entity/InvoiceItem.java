package com.tiffa.wd.elock.paperless.core.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity(name = "invoice_item")
@Table(name = "invoice_item")
public class InvoiceItem {

	@Id
	@SequenceGenerator(name = "invitem_seq", sequenceName = "invitem_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "invitem_seq")
	@Column(name = "invitem_id", precision = 10)
	private Integer invItemId;

	@Column(name = "inv_id", precision = 10)
	private Integer invId;

	@Column(name = "cpn_id", precision = 10)
	private Integer companyId;

	@Column(name = "branch_id", precision = 10)
	private Integer branchId;

	@Column(name = "trf_id", length = 30)
	private String trfId;

	@Column(name = "trfitem_id", precision = 10)
	private Integer trfItemId;

	@Column(name = "invitem_partno", length = 50)
	private String invItemPartno;

	@Column(name = "invitem_qty", precision = 10)
	private Integer invItemQty;

	@Column(name = "invitem_uom", length = 5)
	private String invItemUom;

	@Column(name = "invitem_value", precision = 126)
	private BigDecimal invItemValue;

	@Column(name = "invitem_dec", length = 150)
	private String invItemDec;

	@Column(name = "invitem_no", length = 6)
	private String invItemNo;

	@Column(name = "last_user", length = 50)
	private String lastUser;

	@Column(name = "last_update")
	private LocalDateTime lastUpdate;

	@Column(name = "inv_no", length = 50)
	private String invNo;

	@Column(name = "inv_itemno", precision = 10)
	private Integer invItemNumber;

	@Column(name = "declarationnumber", length = 14)
	private String declarationNumber;

	@Column(name = "inv_no_dec", length = 50)
	private String invNoDec;

	@Column(name = "invitem_netweight", precision = 15, scale = 3)
	private BigDecimal invItemNetWeight;

	@Column(name = "invitem_grossweight", precision = 15, scale = 3)
	private BigDecimal invItemGrossWeight;

	@Column(name = "invitem_grossweight_unit", length = 5)
	private String invItemGrossWeightUnit;

	@Column(name = "invitem_noofpack", precision = 15, scale = 3)
	private BigDecimal invItemNoofPack;

	@Column(name = "invitem_noofpack_unit", length = 5)
	private String invItemNoofPackUnit;

	@Column(name = "invitem_netweight_unit", length = 5)
	private String invItemNetWeightUnit;

	@Column(name = "declaration_filename", length = 100)
	private String declarationFileName;

	@Column(name = "invitem_noofpack_packinglist", length = 100)
	private String invItemNoofPackPackingList;

	@Column(name = "id", precision = 10)
	private Integer id;

	@Column(name = "invhdr_id", precision = 10)
	private Integer invHdrId;

	@Column(name = "itemno", precision = 10)
	private Integer itemNo;

	@Column(name = "tblexportdetail_id", precision = 10)
	private Integer tblExportDetailId;

	@Column(name = "item_type", length = 1)
	private String itemType;

	@Column(name = "ref_dec", length = 20)
	private String refDec;

	@Column(name = "product_code", length = 20)
	private String productCode;

	@Column(name = "tblexportdetail_id_assign_pack", precision = 18)
	private Integer tblExportDetailIdAssignPack;
}
