package com.tiffa.wd.elock.paperless.inspection;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.tiffa.wd.elock.paperless.core.Data;
import com.tiffa.wd.elock.paperless.core.GridData;
import com.tiffa.wd.elock.paperless.core.Sort;
import com.tiffa.wd.elock.paperless.core.exception.BusinessLogicException;
import com.tiffa.wd.elock.paperless.core.repository.CoreRepository;
import com.tiffa.wd.elock.paperless.core.repository.SqlParams;
import com.tiffa.wd.elock.paperless.core.repository.SqlSort;
import com.tiffa.wd.elock.paperless.core.util.CoreUtils;
import com.tiffa.wd.elock.paperless.core.util.SecurityUtils;

@Service
@Transactional(readOnly = true, propagation = Propagation.NEVER)
public class InspectionService {

	@Autowired
	private CoreRepository coreRepository;

	@Async("queryTaskExecutor")
	public CompletableFuture<GridData> searchGrid(final InspectionModel model) {
		SqlParams params = SqlParams.createPageParam(model);
		StringBuilder sql = new StringBuilder();

		sql.append(" SELECT a.*");
		sql.append(" FROM ( ");
		sql.append(" SELECT  ");
		sql.append("    r.cpn_id AS \"companyId\", ");
		sql.append("    r.branch_id AS \"branchId\", ");
		sql.append("    r.trf_id AS \"tripId\", ");
		sql.append("    t.trf_from AS \"origin\", ");
		sql.append("    t.trf_to AS \"dest\", ");
		sql.append("    (CASE  ");
		sql.append("        WHEN r.tampered_time IS NOT NULL AND r.trip_status = 'T' AND r.tampered_flag = 1 THEN 'Tamper' ");
		sql.append("        WHEN r.late_flag = 1 THEN 'Late' ");
		sql.append("        WHEN r.rel_startdate IS NOT NULL AND r.rel_godate IS NOT NULL AND r.rel_enddate IS NOT NULL THEN 'Normal' ");
		sql.append("        WHEN r.rel_startdate IS NOT NULL AND r.rel_godate IS NOT NULL THEN 'On Travel' ");
		sql.append("        WHEN r.rel_startdate IS NOT NULL THEN 'Not Start' ");
		sql.append("    ELSE '-' END) AS \"tripStatus\", ");
		sql.append("    r.rel_startdate AS \"submitTime\", ");
		sql.append("    r.sealed_time AS \"sealTime\", ");
		sql.append("    r.rel_godate AS \"departureTime\", ");
		sql.append("    r.rel_enddate AS \"arrivalTime\", ");
		sql.append("    r.tampered_time AS \"tamper\", ");
		sql.append("    r.tag_code AS \"tagId\", ");
		sql.append("    r.truck_code AS \"truckId\", ");
		sql.append("    r.drv_code AS \"driverId\", ");
		sql.append("    r.rel_travel_time AS \"travelTime\", ");
		sql.append("    r.actual_time AS \"actualTime\", ");
		sql.append("    r.org_customs_requested_name AS \"requesterIdOrg\", ");
		sql.append("    r.org_customs_name AS \"inspecterIdOrg\", ");
		sql.append("    r.rel_inspect_result AS \"resultOrg\", ");
		sql.append("    r.rel_remark AS \"remarkOrg\", ");
		sql.append("    r.dst_customs_requested_name AS \"requesterIdDest\", ");
		sql.append("    r.dst_customs_name AS \"inspecterIdDest\", ");
		sql.append("    r.rel_to_inspect_result AS \"resultDest\", ");
		sql.append("    r.rel_to_remark AS \"remarkDest\", ");
		sql.append("    (CASE   ");
		sql.append("      WHEN NVL(r.rel_inspect,0) = 0 AND (u.is_officer = 1 OR u.is_officer = 2) THEN 'Request' ");
		sql.append("      WHEN NVL(r.rel_inspect,0) = 1 AND r.rel_inspect_result IS NULL AND u.is_officer = 0 THEN 'InspectDisable' ");
		sql.append("      WHEN NVL(r.rel_inspect,0) = 1 AND r.rel_inspect_result IS NULL THEN 'Inspect' ");
		sql.append("      WHEN NVL(r.rel_inspect,0) = 1 AND r.rel_inspect_result = 'PASS' THEN 'Pass' ");
		sql.append("      WHEN NVL(r.rel_inspect,0) = 1 AND r.rel_inspect_result = 'FAIL' THEN 'Fail' ");
		sql.append("    ELSE '-' END) AS \"inspectionOrg\", ");
		sql.append("    (CASE   ");
		sql.append("      WHEN NVL(r.rel_to_inspect,0) = 0 AND (u.is_officer = 1 OR u.is_officer = 2) THEN 'Request' ");
		sql.append("      WHEN NVL(r.rel_to_inspect,0) = 1 AND r.rel_to_inspect_result IS NULL AND u.is_officer = 0 THEN 'InspectDisable' ");
		sql.append("      WHEN NVL(r.rel_to_inspect,0) = 1 AND r.rel_to_inspect_result IS NULL THEN 'Inspect' ");
		sql.append("      WHEN NVL(r.rel_to_inspect,0) = 1 AND r.rel_to_inspect_result = 'PASS' THEN 'Pass' ");
		sql.append("      WHEN NVL(r.rel_to_inspect,0) = 1 AND r.rel_to_inspect_result = 'FAIL' THEN 'Fail' ");
		sql.append("    ELSE '-' END) AS \"inspectionDest\", ");
		sql.append("    (CASE   ");
		sql.append("      WHEN r.rel_startdate > :sysDate THEN 'New' ");
		sql.append("    ELSE '-' END) AS \"tripNew\" ");
		sql.append(" FROM relation r ");
		sql.append("    JOIN transfer t ON t.cpn_id = r.cpn_id AND t.branch_id = r.branch_id AND t.trf_id = r.trf_id ");
		sql.append("    JOIN user_paperless u ON u.cpn_id = t.cpn_id AND u.branch_id = t.branch_id ");
		sql.append(" WHERE r.cpn_id = :companyId ");
		sql.append(" 	AND u.user_login = :username ");
		sql.append("    AND EXISTS(SELECT 1 FROM invoice_item i WHERE i.cpn_id = t.cpn_id AND i.branch_id = t.branch_id AND i.trf_id = t.trf_id) ");
		
		params.add("sysDate", Timestamp.valueOf(LocalDateTime.now().minusMinutes(15)));
		params.add("companyId", SecurityUtils.getCompanyId());
		params.add("username", SecurityUtils.getUsername());

		if (CoreUtils.isNotNull(model.getDateFrom())) {
			sql.append("    AND r.rel_startdate >= :dateFrom ");
			params.add("dateFrom", CoreUtils.atStartOfDay(model.getDateFrom()));
		}

		if (CoreUtils.isNotNull(model.getDateTo())) {
			sql.append("    AND r.rel_startdate <= :dateTo ");
			params.add("dateTo", CoreUtils.atEndOfDay(model.getDateTo()));
		}

		if (CoreUtils.isNotEmpty(model.getOrigin())) {
			sql.append("    AND t.trf_from = :origin ");
			params.add("origin", model.getOrigin());
		}

		if (CoreUtils.isNotEmpty(model.getDest())) {
			sql.append("    AND t.trf_to = :dest ");
			params.add("dest", model.getDest());
		}

		if (CoreUtils.isNotEmpty(model.getDeclarationNo())) {
			sql.append("    AND t.declaration_no LIKE :declarationNo ");
			params.add("declarationNo", "%" + model.getDeclarationNo() + "%");
		}

		sql.append(" ) a ");

		if (CoreUtils.isNotEmpty(model.getTripStatus())) {
			sql.append(" WHERE a.\"tripStatus\" = :tripStatus ");
			params.add("tripStatus", model.getTripStatus());
		}

		String customSort = "(CASE WHEN \"tripStatus\" = 'Tamper' THEN 1 WHEN \"tripStatus\" = 'Late' THEN 2 ELSE 3 END) ASC";
		
		SqlSort sort = SqlSort.create(model, Sort.custom(customSort), Sort.by("tripId", Direction.DESC));
		
		StringBuilder outputSql = new StringBuilder();
		outputSql.append(" SELECT r.* ");
		outputSql.append("   , PKG_STD_UTIL.concatenate_list(CURSOR(SELECT DISTINCT i.declarationnumber FROM invoice_item i WHERE i.cpn_id = r.\"companyId\" AND i.branch_id = r.\"branchId\" AND i.trf_id = r.\"tripId\" ORDER BY 1) , ',') AS \"declarationNo\" ");
		outputSql.append(" FROM ").append(CoreRepository.PAGING_CLAUSE_NAME).append(" r ");
		outputSql.append(" ORDER BY r.").append(CoreRepository.ROWNUM_COLUMN);
		
//		sql.append(" SELECT a.*");
//		sql.append(" FROM ( ");
//		sql.append(" SELECT  ");
//		sql.append("    r.cpn_id AS \"companyId\", ");
//		sql.append("    r.trf_id AS \"tripId\", ");
//		sql.append("    t.trf_from AS \"origin\", ");
//		sql.append("    t.trf_to AS \"dest\", ");
//		sql.append("    (CASE  ");
//		sql.append("        WHEN r.tampered_time IS NOT NULL AND r.trip_status = 'T' AND r.tampered_flag = 1 THEN 'Tamper' ");
//		sql.append("        WHEN r.late_flag = 1 THEN 'Late' ");
//		sql.append("        WHEN r.rel_startdate IS NOT NULL AND r.rel_godate IS NOT NULL AND r.rel_enddate IS NOT NULL THEN 'Normal' ");
//		sql.append("        WHEN r.rel_startdate IS NOT NULL AND r.rel_godate IS NOT NULL THEN 'On Travel' ");
//		sql.append("        WHEN r.rel_startdate IS NOT NULL THEN 'Not Start' ");
//		sql.append("    ELSE '-' END) AS \"tripStatus\", ");
//		sql.append("    r.rel_startdate AS \"submitTime\", ");
//		sql.append("    r.sealed_time AS \"sealTime\", ");
//		sql.append("    r.rel_godate AS \"departureTime\", ");
//		sql.append("    r.rel_enddate AS \"arrivalTime\", ");
//		sql.append("    r.tampered_time AS \"tamper\", ");
//		sql.append("    r.tag_code AS \"tagId\", ");
//		sql.append("    r.truck_code AS \"truckId\", ");
//		sql.append("    r.drv_code AS \"driverId\", ");
//		sql.append("    r.rel_travel_time AS \"travelTime\", ");
//		sql.append("    r.actual_time AS \"actualTime\", ");
//		sql.append("    r.org_customs_requested_name AS \"requesterIdOrg\", ");
//		sql.append("    r.org_customs_name AS \"inspecterIdOrg\", ");
//		sql.append("    r.rel_inspect_result AS \"resultOrg\", ");
//		sql.append("    r.rel_remark AS \"remarkOrg\", ");
//		sql.append("    r.dst_customs_requested_name AS \"requesterIdDest\", ");
//		sql.append("    r.dst_customs_name AS \"inspecterIdDest\", ");
//		sql.append("    r.rel_to_inspect_result AS \"resultDest\", ");
//		sql.append("    r.rel_to_remark AS \"remarkDest\", ");
//		sql.append("    (CASE   ");
//		sql.append("      WHEN NVL(r.rel_inspect,0) = 0 AND (u.is_officer = 1 OR u.is_officer = 2) THEN 'Request' ");
//		sql.append("      WHEN NVL(r.rel_inspect,0) = 1 AND r.rel_inspect_result IS NULL AND u.is_officer = 0 THEN 'InspectDisable' ");
//		sql.append("      WHEN NVL(r.rel_inspect,0) = 1 AND r.rel_inspect_result IS NULL THEN 'Inspect' ");
//		sql.append("      WHEN NVL(r.rel_inspect,0) = 1 AND r.rel_inspect_result = 'PASS' THEN 'Pass' ");
//		sql.append("      WHEN NVL(r.rel_inspect,0) = 1 AND r.rel_inspect_result = 'FAIL' THEN 'Fail' ");
//		sql.append("    ELSE '-' END) AS \"inspectionOrg\", ");
//		sql.append("    (CASE   ");
//		sql.append("      WHEN NVL(r.rel_to_inspect,0) = 0 AND (u.is_officer = 1 OR u.is_officer = 2) THEN 'Request' ");
//		sql.append("      WHEN NVL(r.rel_to_inspect,0) = 1 AND r.rel_to_inspect_result IS NULL AND u.is_officer = 0 THEN 'InspectDisable' ");
//		sql.append("      WHEN NVL(r.rel_to_inspect,0) = 1 AND r.rel_to_inspect_result IS NULL THEN 'Inspect' ");
//		sql.append("      WHEN NVL(r.rel_to_inspect,0) = 1 AND r.rel_to_inspect_result = 'PASS' THEN 'Pass' ");
//		sql.append("      WHEN NVL(r.rel_to_inspect,0) = 1 AND r.rel_to_inspect_result = 'FAIL' THEN 'Fail' ");
//		sql.append("    ELSE '-' END) AS \"inspectionDest\", ");
//		sql.append("    (CASE   ");
//		sql.append("      WHEN r.rel_startdate > :sysDate THEN 'New' ");
//		sql.append("    ELSE '-' END) AS \"tripNew\" ");
//		sql.append(" FROM relation r ");
//		sql.append("    JOIN transfer t ON t.cpn_id = r.cpn_id AND t.trf_id = r.trf_id ");
//		sql.append("    JOIN user_paperless u ON u.cpn_id = t.cpn_id ");
//		sql.append(" WHERE r.cpn_id = :companyId ");
//		sql.append(" 	AND u.user_login = :username ");
//		sql.append("    AND EXISTS(SELECT 1 FROM invoice_item i WHERE i.cpn_id = t.cpn_id AND i.trf_id = t.trf_id) ");
//		
//		params.add("sysDate", Timestamp.valueOf(LocalDateTime.now().minusMinutes(15)));
//		params.add("companyId", SecurityUtils.getCompanyId());
//		params.add("username", SecurityUtils.getUsername());
//
//		if (CoreUtils.isNotNull(model.getDateFrom())) {
//			sql.append("    AND r.rel_startdate >= :dateFrom ");
//			params.add("dateFrom", CoreUtils.atStartOfDay(model.getDateFrom()));
//		}
//
//		if (CoreUtils.isNotNull(model.getDateTo())) {
//			sql.append("    AND r.rel_startdate <= :dateTo ");
//			params.add("dateTo", CoreUtils.atEndOfDay(model.getDateTo()));
//		}
//
//		if (CoreUtils.isNotEmpty(model.getOrigin())) {
//			sql.append("    AND t.trf_from = :origin ");
//			params.add("origin", model.getOrigin());
//		}
//
//		if (CoreUtils.isNotEmpty(model.getDest())) {
//			sql.append("    AND t.trf_to = :dest ");
//			params.add("dest", model.getDest());
//		}
//
//		if (CoreUtils.isNotEmpty(model.getDeclarationNo())) {
//			sql.append("    AND t.declaration_no LIKE :declarationNo ");
//			params.add("declarationNo", "%" + model.getDeclarationNo() + "%");
//		}
//
//		sql.append(" ) a ");
//
//		if (CoreUtils.isNotEmpty(model.getTripStatus())) {
//			sql.append(" WHERE a.\"tripStatus\" = :tripStatus ");
//			params.add("tripStatus", model.getTripStatus());
//		}
//
//		String customSort = "(CASE WHEN \"tripStatus\" = 'Tamper' THEN 1 WHEN \"tripStatus\" = 'Late' THEN 2 ELSE 3 END) ASC";
//		
//		SqlSort sort = SqlSort.create(model, Sort.custom(customSort), Sort.by("tripId", Direction.DESC));
//		
//		StringBuilder outputSql = new StringBuilder();
//		outputSql.append(" SELECT r.* ");
//		outputSql.append("   , PKG_STD_UTIL.concatenate_list(CURSOR(SELECT DISTINCT i.declarationnumber FROM invoice_item i WHERE i.cpn_id = r.\"companyId\" AND i.trf_id = r.\"tripId\" ORDER BY 1) , ',') AS \"declarationNo\" ");
//		outputSql.append(" FROM ").append(CoreRepository.PAGING_CLAUSE_NAME).append(" r ");
//		outputSql.append(" ORDER BY r.").append(CoreRepository.ROWNUM_COLUMN);

		return CompletableFuture.completedFuture(coreRepository.searchPagingGridData(sql.toString(), outputSql.toString(), params, sort));
	}

	@Async("queryTaskExecutor")
	public CompletableFuture<Data> searchSummaryBar(final InspectionModel model) {
		SqlParams params = SqlParams.createPageParam(model);
		StringBuilder sql = new StringBuilder();
		
		sql.append(" SELECT COUNT(1) AS \"totalTrip\", ");
		sql.append("   SUM(CASE WHEN a.\"tripStatus\" = 'Not Start' THEN 1 ELSE 0 END) AS \"notStartStatus\", ");
		sql.append("   SUM(CASE WHEN a.\"tripStatus\" = 'Normal' THEN 1 ELSE 0 END) AS \"normalStatus\", ");
		sql.append("   SUM(CASE WHEN a.\"tripStatus\" = 'Late' THEN 1 ELSE 0 END) AS \"lateStatus\", ");
		sql.append("   SUM(CASE WHEN a.\"tripStatus\" = 'Tamper' THEN 1 ELSE 0 END) AS \"tamperStatus\", ");
		sql.append("   SUM(CASE WHEN a.\"tripStatus\" = 'On Travel' THEN 1 ELSE 0 END) AS \"onTravelStatus\", ");
		sql.append("   SUM(CASE WHEN a.\"tripInspectionPass\" = 'Pass' THEN 1 ELSE 0 END) AS \"inspectionPass\", ");
		sql.append("   SUM(CASE WHEN a.\"tripInspectionFail\" = 'Fail' THEN 1 ELSE 0 END) AS \"inspectionNoPass\", ");
		sql.append("   SUM(CASE WHEN a.\"tripNew\" = 'New' THEN 1 ELSE 0 END) AS \"tripNew\" ");
		sql.append(" FROM ( ");

		sql.append(" SELECT ");
		sql.append("    (CASE  ");
		sql.append("        WHEN r.tampered_time IS NOT NULL AND r.trip_status = 'T' AND r.tampered_flag = 1 THEN 'Tamper' ");
		sql.append("        WHEN r.late_flag = 1 THEN 'Late' ");
		sql.append("        WHEN r.rel_startdate IS NOT NULL AND r.rel_godate IS NOT NULL AND r.rel_enddate IS NOT NULL THEN 'Normal' ");
		sql.append("        WHEN r.rel_startdate IS NOT NULL AND r.rel_godate IS NOT NULL THEN 'On Travel' ");
		sql.append("        WHEN r.rel_startdate IS NOT NULL THEN 'Not Start' ");
		sql.append("    ELSE '-' END) AS \"tripStatus\", ");
		sql.append("    (CASE   ");
		sql.append("        WHEN r.rel_inspect = 1 AND r.rel_inspect_result = 'PASS' THEN 'Pass' ");
		sql.append("        WHEN r.rel_to_inspect = 1 AND r.rel_to_inspect_result = 'PASS' THEN 'Pass' ");
		sql.append("    ELSE '-' END) AS \"tripInspectionPass\", ");
		sql.append("    (CASE   ");
		sql.append("        WHEN r.rel_inspect = 1 AND r.rel_inspect_result = 'FAIL' THEN 'Fail' ");
		sql.append("        WHEN r.rel_to_inspect = 1 AND r.rel_to_inspect_result = 'FAIL' THEN 'Fail' ");
		sql.append("    ELSE '-' END) AS \"tripInspectionFail\", ");
		sql.append("    (CASE   ");
		sql.append("        WHEN r.rel_startdate > :sysDate THEN 'New' ");
		sql.append("    ELSE '-' END) AS \"tripNew\" ");
		sql.append(" FROM relation r ");
		sql.append(" WHERE r.cpn_id = :companyId ");
		
		params.add("companyId", SecurityUtils.getCompanyId());
		params.add("sysDate", Timestamp.valueOf(LocalDateTime.now().minusMinutes(15)));
		
		sql.append(" 	AND EXISTS( ");
		sql.append("  		SELECT 1 "); 
		sql.append("  		FROM user_paperless u "); 
		sql.append("  		WHERE u.cpn_id = r.cpn_id ");
		sql.append("  			AND u.branch_id = r.branch_id "); 
		sql.append("  			AND u.user_login = :username ");
		sql.append(" 	) ");
		
		params.add("username", SecurityUtils.getUsername());
		
		sql.append(" 	AND EXISTS( ");
		sql.append("     	SELECT 1  ");
		sql.append("     	FROM transfer t ");
		sql.append("     	WHERE t.cpn_id = r.cpn_id "); 
		sql.append("     		AND t.branch_id = r.branch_id "); 
		sql.append("     		AND t.trf_id = r.trf_id ");
		
		if (CoreUtils.isNotEmpty(model.getOrigin())) {
			sql.append("    	AND t.trf_from = :origin ");
			params.add("origin", model.getOrigin());
		}

		if (CoreUtils.isNotEmpty(model.getDest())) {
			sql.append("    	AND t.trf_to = :dest ");
			params.add("dest", model.getDest());
		}

		if (CoreUtils.isNotEmpty(model.getDeclarationNo())) {
			sql.append("    	AND t.declaration_no LIKE :declarationNo ");
			params.add("declarationNo", model.getDeclarationNo() + "%");
		}

		sql.append("    ) ");

		if (CoreUtils.isNotNull(model.getDateFrom())) {
			sql.append("    AND r.rel_startdate >= :dateFrom ");
			params.add("dateFrom", CoreUtils.atStartOfDay(model.getDateFrom()));
		}

		if (CoreUtils.isNotNull(model.getDateTo())) {
			sql.append("    AND r.rel_startdate <= :dateTo ");
			params.add("dateTo", CoreUtils.atEndOfDay(model.getDateTo()));
		}

		sql.append(" ) a ");

		if (CoreUtils.isNotEmpty(model.getTripStatus())) {
			sql.append(" WHERE a.\"tripStatus\" = :tripStatus ");
			params.add("tripStatus", model.getTripStatus());
		}
		
//		sql.append(" SELECT COUNT(1) AS \"totalTrip\", ");
//		sql.append("   SUM(CASE WHEN a.\"tripStatus\" = 'Not Start' THEN 1 ELSE 0 END) AS \"notStartStatus\", ");
//		sql.append("   SUM(CASE WHEN a.\"tripStatus\" = 'Normal' THEN 1 ELSE 0 END) AS \"normalStatus\", ");
//		sql.append("   SUM(CASE WHEN a.\"tripStatus\" = 'Late' THEN 1 ELSE 0 END) AS \"lateStatus\", ");
//		sql.append("   SUM(CASE WHEN a.\"tripStatus\" = 'Tamper' THEN 1 ELSE 0 END) AS \"tamperStatus\", ");
//		sql.append("   SUM(CASE WHEN a.\"tripStatus\" = 'On Travel' THEN 1 ELSE 0 END) AS \"onTravelStatus\", ");
//		sql.append("   SUM(CASE WHEN a.\"tripInspectionPass\" = 'Pass' THEN 1 ELSE 0 END) AS \"inspectionPass\", ");
//		sql.append("   SUM(CASE WHEN a.\"tripInspectionFail\" = 'Fail' THEN 1 ELSE 0 END) AS \"inspectionNoPass\", ");
//		sql.append("   SUM(CASE WHEN a.\"tripNew\" = 'New' THEN 1 ELSE 0 END) AS \"tripNew\" ");
//		sql.append(" FROM ( ");
//
//		sql.append(" SELECT ");
//		sql.append("    (CASE  ");
//		sql.append("        WHEN r.tampered_time IS NOT NULL AND r.trip_status = 'T' AND r.tampered_flag = 1 THEN 'Tamper' ");
//		sql.append("        WHEN r.late_flag = 1 THEN 'Late' ");
//		sql.append("        WHEN r.rel_startdate IS NOT NULL AND r.rel_godate IS NOT NULL AND r.rel_enddate IS NOT NULL THEN 'Normal' ");
//		sql.append("        WHEN r.rel_startdate IS NOT NULL AND r.rel_godate IS NOT NULL THEN 'On Travel' ");
//		sql.append("        WHEN r.rel_startdate IS NOT NULL THEN 'Not Start' ");
//		sql.append("    ELSE '-' END) AS \"tripStatus\", ");
//		sql.append("    (CASE   ");
//		sql.append("        WHEN r.rel_inspect = 1 AND r.rel_inspect_result = 'PASS' THEN 'Pass' ");
//		sql.append("        WHEN r.rel_to_inspect = 1 AND r.rel_to_inspect_result = 'PASS' THEN 'Pass' ");
//		sql.append("    ELSE '-' END) AS \"tripInspectionPass\", ");
//		sql.append("    (CASE   ");
//		sql.append("        WHEN r.rel_inspect = 1 AND r.rel_inspect_result = 'FAIL' THEN 'Fail' ");
//		sql.append("        WHEN r.rel_to_inspect = 1 AND r.rel_to_inspect_result = 'FAIL' THEN 'Fail' ");
//		sql.append("    ELSE '-' END) AS \"tripInspectionFail\", ");
//		sql.append("    (CASE   ");
//		sql.append("        WHEN r.rel_startdate > :sysDate THEN 'New' ");
//		sql.append("    ELSE '-' END) AS \"tripNew\" ");
//		sql.append(" FROM relation r ");
//		sql.append(" WHERE r.cpn_id = :companyId ");
//		
//		params.add("sysDate", Timestamp.valueOf(LocalDateTime.now().minusMinutes(15)));
//		params.add("companyId", SecurityUtils.getCompanyId());
//		
//		sql.append(" 	AND EXISTS( ");
//		sql.append("  		SELECT 1 "); 
//		sql.append("  		FROM user_paperless u "); 
//		sql.append("  		WHERE u.cpn_id = r.cpn_id ");
//		sql.append("  			AND u.user_login = :username ");
//		sql.append(" 	) ");
//		
//		params.add("username", SecurityUtils.getUsername());
//		
//		sql.append(" 	AND EXISTS( ");
//		sql.append("     	SELECT 1  ");
//		sql.append("     	FROM transfer t ");
//		sql.append("     	WHERE t.cpn_id = r.cpn_id "); 
//		sql.append("     		AND t.trf_id = r.trf_id ");
//		
//		if (CoreUtils.isNotEmpty(model.getOrigin())) {
//			sql.append("    	AND t.trf_from = :origin ");
//			params.add("origin", model.getOrigin());
//		}
//
//		if (CoreUtils.isNotEmpty(model.getDest())) {
//			sql.append("    	AND t.trf_to = :dest ");
//			params.add("dest", model.getDest());
//		}
//
//		if (CoreUtils.isNotEmpty(model.getDeclarationNo())) {
//			sql.append("    	AND t.declaration_no LIKE :declarationNo ");
//			params.add("declarationNo", model.getDeclarationNo() + "%");
//		}
//
//		sql.append("    ) ");
//
//		if (CoreUtils.isNotNull(model.getDateFrom())) {
//			sql.append("    AND r.rel_startdate >= :dateFrom ");
//			params.add("dateFrom", CoreUtils.atStartOfDay(model.getDateFrom()));
//		}
//
//		if (CoreUtils.isNotNull(model.getDateTo())) {
//			sql.append("    AND r.rel_startdate <= :dateTo ");
//			params.add("dateTo", CoreUtils.atEndOfDay(model.getDateTo()));
//		}
//
//		sql.append(" ) a ");
//
//		if (CoreUtils.isNotEmpty(model.getTripStatus())) {
//			sql.append(" WHERE a.\"tripStatus\" = :tripStatus ");
//			params.add("tripStatus", model.getTripStatus());
//		}
		
		return CompletableFuture.completedFuture(coreRepository.getData(sql.toString(), params));

	}

	@Transactional(readOnly = false)
	public Data requestInspect(InspectionRequestModel model) {
		
		if ("Org".equals(model.getType())) {
			SqlParams params = SqlParams.create();
			StringBuilder sql = new StringBuilder();
			sql.append(" UPDATE relation ");
			sql.append(" SET rel_inspect = 1 ");
			sql.append(" , org_customs_requested_name = :username ");
			
			sql.append(" WHERE trf_id = :trfId ");
			sql.append("   AND tag_code = :tagCode ");
			sql.append("   AND drv_code = :drvCode ");
			sql.append("   AND cpn_id = :companyId ");

			params.add("companyId", SecurityUtils.getCompanyId());
			params.add("username", SecurityUtils.getUsername());
			params.add("trfId", model.getTrfId());
			params.add("tagCode", model.getTagCode());
			params.add("drvCode", model.getDrvCode());

			Integer status = coreRepository.update(sql.toString(), params);
			if (status == 0) {
				throw BusinessLogicException.error("Update Failed.");
			}
		} else if("Dest".equals(model.getType())) {
			SqlParams params = SqlParams.create();
			StringBuilder sql = new StringBuilder();
			sql.append(" UPDATE relation ");
			sql.append(" SET rel_to_inspect = 1 ");
			sql.append(" , dst_customs_requested_name = :username ");
			
			sql.append(" WHERE trf_id = :trfId ");
			sql.append("   AND tag_code = :tagCode ");
			sql.append("   AND drv_code = :drvCode ");
			sql.append("   AND cpn_id = :companyId ");

			params.add("companyId", SecurityUtils.getCompanyId());
			params.add("username", SecurityUtils.getUsername());
			params.add("trfId", model.getTrfId());
			params.add("tagCode", model.getTagCode());
			params.add("drvCode", model.getDrvCode());

			Integer status = coreRepository.update(sql.toString(), params);
			if (status == 0) {
				throw BusinessLogicException.error("Update Failed.");
			}
		}
		return Data.of();
	}

	@Transactional(readOnly = false)
	public Data saveInspect(InspectionSaveModel model) {
		if ("Origin".equals(model.getType())) {
			SqlParams params = SqlParams.create();
			StringBuilder sql = new StringBuilder();
			sql.append(" UPDATE relation ");
			sql.append(" SET org_customs_name = :username ");
			sql.append(" , rel_inspect_result = :result ");
			sql.append(" , rel_remark = :remark ");
			
			sql.append(" WHERE trf_id = :trfId ");
			sql.append("   AND tag_code = :tagCode ");
			sql.append("   AND drv_code = :drvCode ");
			sql.append("   AND cpn_id = :companyId ");

			params.add("companyId", SecurityUtils.getCompanyId());
			params.add("username", SecurityUtils.getUsername());
			params.add("result", model.getResult());
			params.add("remark", model.getRemark());
			params.add("trfId", model.getTrfId());
			params.add("tagCode", model.getTagCode());
			params.add("drvCode", model.getDrvCode());
			
			Integer status = coreRepository.update(sql.toString(), params);
			if (status == 0) {
				throw BusinessLogicException.error("Update Fail.");
			}
			
		} else if("Destination".equals(model.getType())) {
			SqlParams params = SqlParams.create();
			StringBuilder sql = new StringBuilder();
			sql.append(" UPDATE relation ");
			sql.append(" SET dst_customs_name = :username ");
			sql.append(" , rel_to_inspect_result = :result ");
			sql.append(" , rel_to_remark = :remark ");
			
			sql.append(" WHERE trf_id = :trfId ");
			sql.append("   AND tag_code = :tagCode ");
			sql.append("   AND drv_code = :drvCode ");
			sql.append("   AND cpn_id = :companyId ");

			params.add("companyId", SecurityUtils.getCompanyId());
			params.add("username", SecurityUtils.getUsername());
			params.add("result", model.getResult());
			params.add("remark", model.getRemark());
			params.add("trfId", model.getTrfId());
			params.add("tagCode", model.getTagCode());
			params.add("drvCode", model.getDrvCode());
			
			Integer status = coreRepository.update(sql.toString(), params);
			if (status == 0) {
				throw BusinessLogicException.error("Update Fail.");
			}
		}

		return Data.of();
	}

	@Async("queryTaskExecutor")
	public CompletableFuture<Data> searchItemDetail(SearchItemDetailModel model) {
		SqlParams params = SqlParams.create();
		StringBuilder sql = new StringBuilder();
		
		sql.append(" SELECT t.invoice_no AS \"invoiceNo\", r.to_quantity AS \"quantity\", r.to_bathvalue AS \"valueBaht\" ");
		sql.append(" FROM relation r JOIN transfer t ON r.cpn_id = t.cpn_id AND r.branch_id = t.branch_id AND r.trf_id = t.trf_id  ");
		sql.append(" WHERE t.trf_id = :trfId ");
		sql.append("   AND t.cpn_id = :companyId ");
		
		params.add("trfId", model.getTrfId());
		params.add("companyId", SecurityUtils.getCompanyId());

		return CompletableFuture.completedFuture(coreRepository.getData(sql.toString(), params));
	}
	
	@Async("queryTaskExecutor")
	public CompletableFuture<String> getToDescription(SearchItemDetailModel model) {
		SqlParams params = SqlParams.create();
		StringBuilder sql = new StringBuilder();

		sql.append(" SELECT i.invitem_dec, SUM(i.invitem_qty) AS invitem_qty, i.invitem_uom");
		sql.append(" FROM invoice_item i");
		sql.append(" WHERE i.trf_id = :trfId");
		sql.append(" 	AND i.cpn_id = :companyId ");
		sql.append(" GROUP BY i.invitem_dec, i.invitem_uom"); 
		sql.append(" ORDER BY i.invitem_uom");
		
		params.add("trfId", model.getTrfId());
		params.add("companyId", SecurityUtils.getCompanyId());

		GridData gridData = coreRepository.searchGridData(sql.toString(), params);
		
		NumberFormat nf = NumberFormat.getNumberInstance();
		
		List<String> descriptions = new ArrayList<>();
		for (Map<String, Object> record : gridData.getRecords()) {
			String desc = (String)record.get("invitem_dec");
			BigDecimal qty = (BigDecimal) record.get("invitem_qty");
			String uom = (String)record.get("invitem_uom");
			
			descriptions.add(String.format("%s = %s %s", desc, nf.format(qty), uom));
		}
		return CompletableFuture.completedFuture(StringUtils.join(descriptions, ", "));
	}

}
