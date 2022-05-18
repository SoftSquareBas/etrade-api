package com.tiffa.wd.elock.paperless.core.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity(name = "transfer")
@Table(name = "transfer")
public class Transfer {

	@Id
	private TransferPk pk;
	
	@Column(name = "trf_id", length = 30, insertable = false, updatable = false)
	private String trfId;

	@Column(name = "cpn_id", precision = 10, insertable = false, updatable = false)
	private Integer companyId;
	
	@Column(name = "branch_id", precision = 10, insertable = false, updatable = false)
	private Integer branchId;

	@Column(name = "trf_from", length = 35)
	private String trfFrom;

	@Column(name = "trf_to", length = 35)
	private String trfTo;

	@Column(name = "trf_date")
	private LocalDate trfDate;

	@Column(name = "trf_lt", precision = 3)
	private Integer trfLt;

	@Column(name = "trf_type", length = 10)
	private String trfType;

	@Column(name = "remark", length = 100)
	private String remark;

	@Column(name = "trf_kna", length = 255)
	private String trfKna;

	@Column(name = "trf_id_cancel", length = 20)
	private String trfIdCancel;

	@Column(name = "special_remark", length = 255)
	private String specialRemark;

	@Column(name = "last_user", length = 255)
	private String lastUser;

	@Column(name = "amendment_flag", precision = 3)
	private Integer amendmentFlag;

	@Column(name = "canceled_flag", precision = 3)
	private Integer canceledFlag;

	@Column(name = "manual_flag", precision = 3)
	private Integer manualFlag;

	@Column(name = "trfid_flag", precision = 3)
	private Integer trfidFlag;

	@Column(name = "description", length = 1000)
	private String description;

	@Column(name = "quantity", length = 1000)
	private String quantity;

	@Column(name = "bathvalue", length = 1000)
	private String bathvalue;

	@Column(name = "trf_travel_time", precision = 10)
	private Integer trfTravelTime;

	@Column(name = "corridor_code", length = 20)
	private String corridorCode;

	@Column(name = "trf_status", length = 20)
	private String trfStatus;

	@Column(name = "declaration_filename", length = 100)
	private String declarationFilename;

	@Column(name = "declaration_no", length = 20)
	private String declarationNo;

	@Column(name = "invoice_no", length = 500)
	private String invoiceNo;
	
	@Column(name = "truck_code", length = 20)
	private String truckCode;
	
	@Column(name = "track_id", length = 6)
	private String trackId;
	
	@Column(name = "shipment_status", length = 200)
	private String shipmentStatus;

}
