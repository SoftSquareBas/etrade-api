package com.tiffa.wd.elock.paperless.tripMonitoring;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.tiffa.wd.elock.paperless.core.Data;
import com.tiffa.wd.elock.paperless.core.GridData;
import com.tiffa.wd.elock.paperless.core.Sort;
import com.tiffa.wd.elock.paperless.core.repository.CoreRepository;
import com.tiffa.wd.elock.paperless.core.repository.SqlParams;
import com.tiffa.wd.elock.paperless.core.repository.SqlSort;
import com.tiffa.wd.elock.paperless.core.util.CoreUtils;
import com.tiffa.wd.elock.paperless.core.util.SecurityUtils;

@Service
@Transactional(readOnly = true, propagation = Propagation.NEVER)
public class TripMonitoringService {

	@Autowired
	private CoreRepository coreRepository;
	
	@Async("queryTaskExecutor")
	public CompletableFuture<GridData> searchGrid(final TripMonitoringModel model) {
		SqlParams params = SqlParams.createPageParam(model);
		
		StringBuilder sql = new StringBuilder();
		
		sql.append(" SELECT a.* FROM ( ");
		
		sql.append(" SELECT r.trf_id AS \"tripId\", "); 
		sql.append("    t.trf_from AS \"origin\", ");
		sql.append("    t.trf_to AS \"dest\", ");
		sql.append("    (CASE  ");
		sql.append("        WHEN r.tampered_time IS NOT NULL AND r.trip_status = 'T' AND r.tampered_flag = 1 THEN 'Tamper' ");
		sql.append("        WHEN r.late_flag = 1 THEN 'Late' ");
		sql.append("        WHEN r.rel_startdate IS NOT NULL AND r.rel_godate IS NOT NULL AND r.rel_enddate IS NOT NULL THEN 'Normal' ");
		sql.append("        WHEN r.rel_startdate IS NOT NULL AND r.rel_godate IS NOT NULL THEN 'On Travel' ");
		sql.append("        WHEN r.rel_startdate IS NOT NULL THEN 'Not Start' ");
		sql.append("    ELSE '-' END) AS \"tripStatus\", ");
		sql.append("    r.tag_code AS \"tagId\", ");
		sql.append("    r.truck_code AS \"truckId\", ");
		sql.append("    r.rel_startdate AS \"submitTime\", ");
		sql.append("    r.sealed_time AS \"sealTime\", ");
		sql.append("    r.rel_godate AS \"departureTime\", ");
		sql.append("    r.rel_enddate AS \"arrivalTime\", ");
		sql.append("    r.tampered_time AS \"tamper\", ");
		sql.append("    r.drv_code AS \"driverId\", ");
		sql.append("    r.rel_travel_time AS \"travelTime\", ");
		sql.append("    r.actual_time AS \"actualTime\" ");
		sql.append(" FROM relation r ");
		sql.append("    JOIN transfer t ON t.cpn_id = r.cpn_id AND r.branch_id = t.branch_id AND t.trf_id = r.trf_id ");
		sql.append("    JOIN user_paperless u ON u.cpn_id = t.cpn_id AND u.branch_id = t.branch_id ");
		sql.append(" WHERE r.cpn_id = :companyId ");
		sql.append(" 	AND u.user_login = :username ");

		params.add("companyId", SecurityUtils.getCompanyId());
		params.add("username", SecurityUtils.getUsername());

		if(CoreUtils.isNotNull(model.getDateFrom())) {
			sql.append("    AND r.rel_startdate >= :dateFrom ");
			params.add("dateFrom", CoreUtils.atStartOfDay(model.getDateFrom()));
		}

		if(CoreUtils.isNotNull(model.getDateTo())) {
			sql.append("    AND r.rel_startdate <= :dateTo ");
			params.add("dateTo", CoreUtils.atEndOfDay(model.getDateTo()));
		}
		
		if(CoreUtils.isNotEmpty(model.getTripId())) {
			sql.append("    AND r.trf_id LIKE :tripId ");
			params.add("tripId", "%" + model.getTripId() + "%");
		}
		
		if(CoreUtils.isNotEmpty(model.getOrigin())) {
			sql.append("    AND t.trf_from = :origin ");
			params.add("origin", model.getOrigin());
		}

		if(CoreUtils.isNotEmpty(model.getDest())) {
			sql.append("    AND t.trf_to = :dest ");
			params.add("dest", model.getDest());
		}
		
		if(CoreUtils.isNotEmpty(model.getTagId())) {
			sql.append("    AND r.tag_code LIKE :tagId ");
			params.add("tagId", "%" + model.getTagId() + "%");
		}
		
		if(CoreUtils.isNotEmpty(model.getTruckId())) {
			sql.append("    AND r.truck_code LIKE :truckId ");
			params.add("truckId", "%" + model.getTruckId() + "%");
		}

		sql.append(" ) a ");

		if(CoreUtils.isNotEmpty(model.getTripStatus())) {
			sql.append(" WHERE a.\"tripStatus\" = :tripStatus ");
			params.add("tripStatus", model.getTripStatus());
		}
		
		SqlSort sort = SqlSort.create(model, Sort.by("submitTime", Direction.DESC));

		return CompletableFuture.completedFuture(coreRepository.searchPagingGridData(sql.toString(), params, sort));
	}
	
	@Async("queryTaskExecutor")
	public CompletableFuture<Data> searchSummaryBar(final TripMonitoringModel model) {
		SqlParams params = SqlParams.createPageParam(model);
		StringBuilder sql = new StringBuilder();
		
		sql.append(" SELECT COUNT(1) AS \"totalTrip\", ");
		sql.append("   SUM(CASE WHEN a.\"tripStatus\" = 'Not Start' THEN 1 ELSE 0 END) AS \"notStartStatus\", ");
		sql.append("   SUM(CASE WHEN a.\"tripStatus\" = 'Normal' OR a.\"tripStatus\" = 'On Travel' THEN 1 ELSE 0 END) AS \"normalStatus\", ");
		sql.append("   SUM(CASE WHEN a.\"tripStatus\" = 'Late' THEN 1 ELSE 0 END) AS \"lateStatus\", ");
		sql.append("   SUM(CASE WHEN a.\"tripStatus\" = 'Tamper' THEN 1 ELSE 0 END) AS \"tamperStatus\" ");
		sql.append(" FROM ( ");

		sql.append(" SELECT "); 
		sql.append("    (CASE  ");
		sql.append("        WHEN r.tampered_time IS NOT NULL AND r.trip_status = 'T' AND r.tampered_flag = 1 THEN 'Tamper' ");
		sql.append("        WHEN r.late_flag = 1 THEN 'Late' ");
		sql.append("        WHEN r.rel_startdate IS NOT NULL AND r.rel_godate IS NOT NULL AND r.rel_enddate IS NOT NULL THEN 'Normal' ");
		sql.append("        WHEN r.rel_startdate IS NOT NULL AND r.rel_godate IS NOT NULL THEN 'On Travel' ");
		sql.append("        WHEN r.rel_startdate IS NOT NULL THEN 'Not Start' ");
		sql.append("    ELSE '-' END) AS \"tripStatus\" ");
		sql.append(" FROM relation r ");
		sql.append(" WHERE r.cpn_id = :companyId ");
		params.add("companyId", SecurityUtils.getCompanyId());
		
		sql.append(" 	AND EXISTS( ");
		sql.append("  		SELECT 1 ");
		sql.append("  		FROM user_paperless u "); 
		sql.append("  		WHERE u.cpn_id = r.cpn_id "); 
		sql.append("  			AND u.branch_id = r.branch_id "); 
		sql.append("  			AND u.user_login = :username "); 
		sql.append("  	) "); 
		params.add("username", SecurityUtils.getUsername());
		
		sql.append(" 	AND EXISTS( ");
		sql.append("     	SELECT 1  ");
		sql.append("     	FROM transfer t ");
		sql.append("     	WHERE t.cpn_id = r.cpn_id "); 
		sql.append("     		AND t.branch_id = r.branch_id "); 
		sql.append("     		AND t.trf_id = r.trf_id ");
		
		if(CoreUtils.isNotEmpty(model.getOrigin())) {
			sql.append("    	AND t.trf_from = :origin ");
			params.add("origin", model.getOrigin());
		}

		if(CoreUtils.isNotEmpty(model.getDest())) {
			sql.append("    	AND t.trf_to = :dest ");
			params.add("dest", model.getDest());
		}
		
		sql.append("    ) ");

		if(CoreUtils.isNotNull(model.getDateFrom())) {
			sql.append("    AND r.rel_startdate >= :dateFrom ");
			params.add("dateFrom", CoreUtils.atStartOfDay(model.getDateFrom()));
		}

		if(CoreUtils.isNotNull(model.getDateTo())) {
			sql.append("    AND r.rel_startdate <= :dateTo ");
			params.add("dateTo", CoreUtils.atEndOfDay(model.getDateTo()));
		}
		
		if(CoreUtils.isNotEmpty(model.getTripId())) {
			sql.append("    AND r.trf_id LIKE :tripId ");
			params.add("tripId", model.getTripId() + "%");
		}
		
		if(CoreUtils.isNotEmpty(model.getTagId())) {
			sql.append("    AND r.tag_code LIKE :tagId ");
			params.add("tagId", model.getTagId() + "%");
		}
		
		if(CoreUtils.isNotEmpty(model.getTruckId())) {
			sql.append("    AND r.truck_code LIKE :truckId ");
			params.add("truckId", model.getTruckId() + "%");
		}

		sql.append(" ) a ");

		if(CoreUtils.isNotEmpty(model.getTripStatus())) {
			sql.append(" WHERE a.\"tripStatus\" = :tripStatus ");
			params.add("tripStatus", model.getTripStatus());
		}
		
		return CompletableFuture.completedFuture(coreRepository.getData(sql.toString(), params));
		
	}

}
