package com.tiffa.wd.elock.paperless.core.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity(name = "relation")
@Table(name = "relation")
public class Relation implements Serializable {

	private static final long serialVersionUID = 4460155681154593524L;

	@Id
	private RelationPk pk;

	@Column(name = "trf_id", insertable = false, updatable = false)
	private String trfId;

	@Column(name = "tag_code", insertable = false, updatable = false)
	private String tagCode;

	@Column(name = "drv_code", insertable = false, updatable = false)
	private String drvCode;

	@Column(name = "cpn_id", precision = 10, insertable = false, updatable = false)
	private Integer companyId;

	@Column(name = "branch_id", precision = 10, insertable = false, updatable = false)
	private Integer branchId;

	@Column(name = "truck_code", length = 20)
	private String truckCode;

	@Column(name = "rel_startdate")
	private LocalDateTime relStartDate;

	@Column(name = "rel_godate")
	private LocalDateTime relGoDate;

	@Column(name = "rel_enddate")
	private LocalDateTime relEndDate;

	@Column(name = "rel_travel_time", precision = 10)
	private Integer relTravelTime;

	@Column(name = "rel_inspect", precision = 3)
	private Integer relInspect;

	@Column(name = "rel_inspect_result", length = 10)
	private String relInspectResult;

	@Column(name = "rel_remark", length = 200)
	private String relRemark;

	@Column(name = "rel_last_truck", precision = 3)
	private Integer relLastTruck;

	@Column(name = "status", length = 10)
	private String status;

	@Column(name = "book_id_from", length = 50)
	private String bookIdFrom;

	@Column(name = "book_id_to", length = 50)
	private String bookIdTo;

	@Column(name = "rel_to_inspect", precision = 3)
	private Integer relToInspect;

	@Column(name = "rel_to_inspect_result", length = 10)
	private String relToInspectResult;

	@Column(name = "rel_to_remark", length = 200)
	private String relToRemark;

	@Column(name = "trip_status", length = 1)
	private String tripStatus;

	@Column(name = "expected_time", precision = 11)
	private Long exprectedTime;

	@Column(name = "org_customs_name", length = 255)
	private String orgCustomsName;

	@Column(name = "dst_customs_name", length = 255)
	private String dstCustomsName;

	@Column(name = "special_remark", length = 255)
	private String specialRemark;

	@Column(name = "special_remark_name", length = 255)
	private String specialRemarkName;

	@Column(name = "special_result", length = 10)
	private String specialResult;

	@Column(name = "org_customs_requested_name", length = 255)
	private String orgCustomsRequestedName;

	@Column(name = "dst_customs_requested_name", length = 255)
	private String dstCustomsRequestedName;

	@Column(name = "actual_time", precision = 11)
	private Long actualTime;

	@Column(name = "sealed_time")
	private LocalDateTime sealedTime;

	@Column(name = "tampered_time")
	private LocalDateTime tamperedTime;

	@Column(name = "doc_status_type", length = 15)
	private String docStatusType;

	@Column(name = "doc_status_reqno", length = 40)
	private String docStatusReqno;

	@Column(name = "doc_status_date")
	private LocalDateTime docStatusDate;

	@Column(name = "doc_status_remark", length = 255)
	private String docStatusRemark;

	@Column(name = "doc_status_name", length = 60)
	private String docStatusName;

	@Column(name = "truck_status_type", length = 15)
	private String truckStatusType;

	@Column(name = "truck_status_reqno", length = 40)
	private String truckStatusReqno;

	@Column(name = "truck_status_date")
	private LocalDateTime truckStatusDate;

	@Column(name = "truck_status_remark", length = 255)
	private String truckStatusRemark;

	@Column(name = "truck_status_name", length = 60)
	private String truckStatusName;

	@Column(name = "last_user", length = 255)
	private String lastUser;

	@Column(name = "tampered_flag", precision = 3)
	private Integer tamperedFlag;

	@Column(name = "late_flag", precision = 3)
	private Integer lateFlag;

	@Column(name = "type", length = 3)
	private String type;

	@Column(name = "tag_status_from", length = 10)
	private String tagStatusFrom;

	@Column(name = "tag_status_to", length = 10)
	private String tagStatusTo;

	@Column(name = "open_time")
	private LocalDateTime openTime;

	@Column(name = "dct_id", length = 14)
	private String dctId;

	@Column(name = "corridoralert_flag", precision = 6)
	private Integer corridoralertFlag;

	@Column(name = "corridoralert_time")
	private LocalDateTime corridoralertTime;

	@Column(name = "to_quantity", length = 1000)
	private String toQuantity;

	@Column(name = "to_bathvalue", length = 20)
	private String toBathvalue;

	@Column(name = "corridor_code", length = 20)
	private String corridorCode;

	@Column(name = "to_description")
	private String toDescription;

	@Column(name = "containernumber", length = 20)
	private String containerNumber;

	@Column(name = "vehicle_type", length = 20)
	private String vehicleType;

}
