package com.tiffa.wd.elock.paperless.truckArrivalBoard;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
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
import com.tiffa.wd.elock.paperless.core.repository.CoreRepository;
import com.tiffa.wd.elock.paperless.core.repository.SqlParams;
import com.tiffa.wd.elock.paperless.core.repository.SqlSort;
import com.tiffa.wd.elock.paperless.core.util.CoreUtils;
import com.tiffa.wd.elock.paperless.core.util.SecurityUtils;

@Service
@Transactional(readOnly = true, propagation = Propagation.NEVER)
public class TruckArrvialBoardService {

	@Autowired
	private CoreRepository coreRepository;
	
	@Async("queryTaskExecutor")
	public CompletableFuture<GridData> searchGrid(final TruckArrivalBoardModel model) {
		SqlParams params = SqlParams.createPageParam(model);
		
		StringBuilder sql = new StringBuilder();
		
		sql.append(" SELECT a.* FROM ( ");
		sql.append(" SELECT r.trf_id AS \"tripId\", ");
		sql.append("    r.rel_startdate + NUMTODSINTERVAL(r.rel_travel_time,'MINUTE') AS \"estArrivalTime\", ");
		sql.append("    r.tag_code AS \"tagId\", ");
		sql.append("    t.trf_from AS \"origin\", ");
		sql.append("    t.trf_to AS \"dest\", ");
		sql.append("    r.truck_code AS \"truckId\", ");
		sql.append("    '-' AS \"containerNo\", ");
		sql.append("    r.rel_godate AS \"departureTime\", ");
		sql.append("    r.rel_startdate AS \"submitTime\", ");
		sql.append("    r.rel_enddate AS \"arrivalTime\", ");
		sql.append("    (CASE WHEN r.tampered_time IS NOT NULL AND r.trip_status = 'T' AND r.tampered_flag = 1 THEN 1 ELSE 0 END) AS \"tamper\", ");
		sql.append("    (CASE  ");
		sql.append("        WHEN r.rel_enddate IS NOT NULL AND r.late_flag = 1 THEN 'Late' ");
		sql.append("        WHEN r.rel_enddate IS NOT NULL THEN 'Arrived' ");
		sql.append("        WHEN r.rel_godate IS NULL THEN 'Not Started' ");
		sql.append("        WHEN r.rel_godate IS NOT NULL THEN 'In Transit' ");
		sql.append("    ELSE '-' END) AS \"tripStatus\" ");
		sql.append(" FROM relation r ");
		sql.append("    JOIN transfer t ON t.cpn_id = r.cpn_id AND t.branch_id = r.branch_id AND t.trf_id = r.trf_id ");
		sql.append(" WHERE r.cpn_id = :companyId ");
		sql.append("    AND ( ( r.rel_startdate >= :dateFrom AND r.rel_startdate <= :dateTo ) ");
		sql.append("      OR ( r.rel_enddate IS NULL ) ) ");
		
		params.add("companyId", SecurityUtils.getCompanyId());
		params.add("dateFrom", CoreUtils.atStartOfDay(LocalDate.now()));
		params.add("dateTo", CoreUtils.atEndOfDay(LocalDate.now()));
		
		sql.append(" 	AND EXISTS( ");
		sql.append("  		SELECT 1 "); 
		sql.append("  		FROM user_paperless u "); 
		sql.append("  		WHERE u.cpn_id = r.cpn_id ");
		sql.append("  			AND u.branch_id = r.branch_id "); 
		sql.append("  			AND u.user_login = :username ");
		sql.append(" 	) ");
		
		params.add("username", SecurityUtils.getUsername());
		
		if(CoreUtils.isNotNull(model.getDest())) {
			sql.append("    AND t.trf_to = :dest ");
			params.add("dest", model.getDest());
		}
		sql.append(" ) a ");

		SqlSort sort = SqlSort.create(model, Sort.by("estArrivalTime", Direction.DESC));
		
//		sql.append(" SELECT a.* FROM ( ");
//		sql.append(" SELECT r.trf_id AS \"tripId\", ");
//		sql.append("    r.rel_startdate + NUMTODSINTERVAL(r.rel_travel_time,'MINUTE') AS \"estArrivalTime\", ");
//		sql.append("    t.trf_from AS \"origin\", ");
//		sql.append("    r.truck_code AS \"truckId\", ");
//		sql.append("    '-' AS \"containerNo\", ");
//		sql.append("    r.rel_godate AS \"departureTime\", ");
//		sql.append("    r.rel_enddate AS \"arrivalTime\", ");
//		sql.append("    (CASE WHEN r.tampered_time IS NOT NULL AND r.trip_status = 'T' AND r.tampered_flag = 1 THEN 1 ELSE 0 END) AS \"tamper\", ");
//		sql.append("    (CASE  ");
//		sql.append("        WHEN r.rel_enddate IS NOT NULL AND r.late_flag = 1 THEN 'Late' ");
//		sql.append("        WHEN r.rel_enddate IS NOT NULL THEN 'Arrived' ");
//		sql.append("        WHEN r.rel_godate IS NULL THEN 'Not Started' ");
//		sql.append("        WHEN r.rel_godate IS NOT NULL THEN 'In Transit' ");
//		sql.append("    ELSE '-' END) AS \"tripStatus\" ");
//		sql.append(" FROM relation r ");
//		sql.append("    JOIN transfer t ON t.cpn_id = r.cpn_id AND t.trf_id = r.trf_id ");
//		sql.append(" WHERE r.cpn_id = :companyId ");
//		sql.append("    AND ( ( r.rel_startdate >= :dateFrom AND r.rel_startdate <= :dateTo ) ");
//		sql.append("      OR ( r.rel_enddate IS NULL ) ) ");
//
//		params.add("companyId", SecurityUtils.getCompanyId());
//		params.add("dateFrom", CoreUtils.atStartOfDay(LocalDate.now()));
//		params.add("dateTo", CoreUtils.atEndOfDay(LocalDate.now()));
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
//		if(CoreUtils.isNotNull(model.getDest())) {
//			sql.append("    AND t.trf_to = :dest ");
//			params.add("dest", model.getDest());
//		}
//		sql.append(" ) a ");
//
//		SqlSort sort = SqlSort.create(model, Sort.by("estArrivalTime", Direction.DESC));

		return CompletableFuture.completedFuture(coreRepository.searchPagingGridData(sql.toString(), params, sort));
	}
	
	@Async("queryTaskExecutor")
	public CompletableFuture<Data> searchSummaryBar(final TruckArrivalBoardModel model) {
		SqlParams params = SqlParams.createPageParam(model);
		StringBuilder sql = new StringBuilder();
		
		sql.append(" SELECT COUNT(1) AS \"totalTrip\", ");
		sql.append("   SUM(CASE WHEN a.\"tripStatus\" = 'Not Started' THEN 1 ELSE 0 END) AS \"notStartedStatus\", ");
		sql.append("   SUM(CASE WHEN a.\"tripStatus\" = 'In Transit' THEN 1 ELSE 0 END) AS \"inTransitStatus\", ");
		sql.append("   SUM(CASE WHEN a.\"tripStatus\" = 'Arrived' THEN 1 ELSE 0 END) AS \"arrivedStatus\", ");
		sql.append("   SUM(CASE WHEN a.\"tripStatus\" = 'Late' OR a.\"tamper\" = 1 THEN 1 ELSE 0 END) AS \"exceptionStatus\" ");
		sql.append(" FROM ( ");

		sql.append(" SELECT ");
		sql.append("    (CASE WHEN r.tampered_time IS NOT NULL AND r.trip_status = 'T' AND r.tampered_flag = 1 THEN 1 ELSE 0 END) AS \"tamper\", ");
		sql.append("    (CASE  ");
		sql.append("        WHEN r.rel_enddate IS NOT NULL AND r.late_flag = 1 THEN 'Late' ");
		sql.append("        WHEN r.rel_enddate IS NOT NULL THEN 'Arrived' ");
		sql.append("        WHEN r.rel_godate IS NULL THEN 'Not Started' ");
		sql.append("        WHEN r.rel_godate IS NOT NULL THEN 'In Transit' ");
		sql.append("    ELSE '-' END) AS \"tripStatus\" ");
		sql.append(" FROM relation r ");
		sql.append("    JOIN transfer t ON t.cpn_id = r.cpn_id AND t.branch_id = r.branch_id AND t.trf_id = r.trf_id ");
		sql.append(" WHERE r.cpn_id = :companyId ");
		sql.append("    AND ( ( r.rel_startdate >= :dateFrom AND r.rel_startdate <= :dateTo ) ");
		sql.append("      OR ( r.rel_enddate IS NULL ) ) ");

		params.add("companyId", SecurityUtils.getCompanyId());
		params.add("dateFrom", CoreUtils.atStartOfDay(LocalDate.now()));
		params.add("dateTo", CoreUtils.atEndOfDay(LocalDate.now()));
		
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
		
		if(CoreUtils.isNotNull(model.getDest())) {
			sql.append("    	AND t.trf_to = :dest ");
			params.add("dest", model.getDest());
		}
		
		sql.append("    ) ");
		
		sql.append(" ) a ");
		
//		sql.append(" SELECT COUNT(1) AS \"totalTrip\", ");
//		sql.append("   SUM(CASE WHEN a.\"tripStatus\" = 'Not Started' THEN 1 ELSE 0 END) AS \"notStartedStatus\", ");
//		sql.append("   SUM(CASE WHEN a.\"tripStatus\" = 'In Transit' THEN 1 ELSE 0 END) AS \"inTransitStatus\", ");
//		sql.append("   SUM(CASE WHEN a.\"tripStatus\" = 'Arrived' THEN 1 ELSE 0 END) AS \"arrivedStatus\", ");
//		sql.append("   SUM(CASE WHEN a.\"tripStatus\" = 'Late' OR a.\"tamper\" = 1 THEN 1 ELSE 0 END) AS \"exceptionStatus\" ");
//		sql.append(" FROM ( ");
//
//		sql.append(" SELECT ");
//		sql.append("    (CASE WHEN r.tampered_time IS NOT NULL AND r.trip_status = 'T' AND r.tampered_flag = 1 THEN 1 ELSE 0 END) AS \"tamper\", ");
//		sql.append("    (CASE  ");
//		sql.append("        WHEN r.rel_enddate IS NOT NULL AND r.late_flag = 1 THEN 'Late' ");
//		sql.append("        WHEN r.rel_enddate IS NOT NULL THEN 'Arrived' ");
//		sql.append("        WHEN r.rel_godate IS NULL THEN 'Not Started' ");
//		sql.append("        WHEN r.rel_godate IS NOT NULL THEN 'In Transit' ");
//		sql.append("    ELSE '-' END) AS \"tripStatus\" ");
//		sql.append(" FROM relation r ");
//		sql.append("    JOIN transfer t ON t.cpn_id = r.cpn_id AND t.trf_id = r.trf_id ");
//		sql.append(" WHERE r.cpn_id = :companyId ");
//		sql.append("    AND ( ( r.rel_startdate >= :dateFrom AND r.rel_startdate <= :dateTo ) ");
//		sql.append("      OR ( r.rel_enddate IS NULL ) ) ");
//
//		params.add("companyId", SecurityUtils.getCompanyId());
//		params.add("dateFrom", CoreUtils.atStartOfDay(LocalDate.now()));
//		params.add("dateTo", CoreUtils.atEndOfDay(LocalDate.now()));
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
//		if(CoreUtils.isNotNull(model.getDest())) {
//			sql.append("    	AND t.trf_to = :dest ");
//			params.add("dest", model.getDest());
//		}
//		
//		sql.append("    ) ");
//		
//		sql.append(" ) a ");
		
		return CompletableFuture.completedFuture(coreRepository.getData(sql.toString(), params));
		
	}
	
	@Async("queryTaskExecutor")
	public CompletableFuture<Data> searchItemDetail(TruckArrivalBoardItemDetailModel model) {
		SqlParams params = SqlParams.create();
		StringBuilder sql = new StringBuilder();

		sql.append(" SELECT t.trf_id AS \"tripId\" ");
		sql.append("   , r.tag_code AS \"tagCode\" ");
		sql.append("   , PKG_STD_UTIL.concatenate_list(CURSOR(SELECT DISTINCT i.declarationnumber FROM invoice_item i WHERE i.cpn_id = r.cpn_id AND i.branch_id = r.branch_id AND i.trf_id = r.trf_id ORDER BY 1) , ',') AS \"declarationNo\" ");
		sql.append("   , t.invoice_no AS \"invoiceNo\" ");
		sql.append("   , r.to_quantity AS \"quantity\" ");
		sql.append("   , r.to_bathvalue AS \"valueBaht\" ");
		sql.append(" FROM relation r ");
		sql.append("   	JOIN transfer t ON t.trf_id = r.trf_id AND t.cpn_id = r.cpn_id AND t.branch_id = r.branch_id ");
		sql.append(" WHERE r.cpn_id = :companyId ");
		sql.append("   	AND r.trf_id = :trfId ");

		params.add("companyId", SecurityUtils.getCompanyId());
		params.add("trfId", model.getTrfId());
		
		return CompletableFuture.completedFuture(coreRepository.getData(sql.toString(), params));
	}
	
	@Async("queryTaskExecutor")
	public CompletableFuture<String> getToDescription(TruckArrivalBoardItemDetailModel model) {
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
