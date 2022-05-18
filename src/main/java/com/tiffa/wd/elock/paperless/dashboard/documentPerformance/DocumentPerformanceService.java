package com.tiffa.wd.elock.paperless.dashboard.documentPerformance;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.tiffa.wd.elock.paperless.core.GridData;
import com.tiffa.wd.elock.paperless.core.repository.CoreRepository;
import com.tiffa.wd.elock.paperless.core.repository.SqlParams;
import com.tiffa.wd.elock.paperless.core.util.CoreUtils;
import com.tiffa.wd.elock.paperless.core.util.SecurityUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true, propagation = Propagation.NEVER)
public class DocumentPerformanceService {

	@Autowired
	private CoreRepository coreRepository;
	
	private boolean test = false;
	
	private LocalDate getProcessDate(LocalDate testDate) {
		return (test) ? testDate : LocalDate.now();
	}
	
	private BigDecimal getData(BigDecimal realData) {
		return (test) ? new BigDecimal(Math.floor(Math.random() * 10000)) : realData;
	}
	
	@SuppressWarnings("unchecked")
	@Async("queryTaskExecutor")
	public CompletableFuture<GridData> searchTotalDeclaration() {
		log.info("[{}] searchTotalDeclaration", Thread.currentThread().getName());
		LocalDate processDate = getProcessDate(LocalDate.parse("17/06/2021", DateTimeFormatter.ofPattern("dd/MM/yyyy")));
		
		SqlParams params = SqlParams.create();
		
		GridData branchGridData = this.getTotalBranchRoute(params);
		
		log.info("TotalBranchRoute : {}", branchGridData);
		
		Map<String, Map<String, Object>> branchMap = new LinkedHashMap<>();
		for (Map<String, Object> record : branchGridData.getRecords()) {
			String branch = (String)record.get("branchName");
			
			Map<String, Object> map = branchMap.get(branch);
			if(map == null) {
				map = new HashMap<>();
				map.put("title", branch);
				map.put("total", BigDecimal.ZERO);
				map.put("colors", new ArrayList<String>());
				map.put("totalDeclaration", BigDecimal.ZERO);
				map.put("declarationQty", BigDecimal.ZERO);
				map.put("deliveryQty", BigDecimal.ZERO);
				map.put("diffPercent", BigDecimal.ZERO);
				
				map.put("data", new LinkedHashMap<String, BigDecimal>());
				
				branchMap.put(branch, map);
			}

			List<String> colors = (List<String>)map.get("colors");
			String color = (String)record.get("color");
			colors.add(color);
			map.put("colors", colors);
			
			Map<String, BigDecimal> data = (Map<String, BigDecimal>)map.get("data");
			String route = (String)record.get("route");
			data.put(route, BigDecimal.ZERO);
			map.put("data", data);
		}
		
		if(CoreUtils.isNotEmpty(branchMap)) {
			StringBuilder sql = new StringBuilder();
			
			sql.append(" SELECT b.branch_name AS \"branchName\",  ");
			sql.append(" 	COUNT(DISTINCT tbl.declarationnumber) AS \"totalDeclaration\",   ");
			sql.append(" 	SUM(tbl.quantity) AS \"declarationQty\",   ");
			sql.append(" 	SUM(tbl.deliveryqty) AS \"deliveryQty\"   ");
			sql.append(" FROM tblexportdetail tbl  ");
			sql.append(" 	JOIN branch b ON b.cpn_id = tbl.cpn_id AND b.branch_id = tbl.branch_id  ");
			sql.append(" WHERE b.cpn_id = :companyId ");
			sql.append(" 	AND tbl.flagcancel IS NULL ");
			sql.append(" 	AND EXISTS( ");
			sql.append(" 		SELECT 1 ");
			sql.append(" 		FROM relation r "); 
			sql.append(" 			JOIN invoice_item i ON i.cpn_id = r.cpn_id AND i.branch_id = r.branch_id AND i.trf_id = r.trf_id ");
			sql.append(" 		WHERE r.cpn_id = b.cpn_id  ");
			sql.append(" 			AND r.branch_id = b.branch_id ");
			sql.append(" 			AND r.rel_godate >= TRUNC(:processDate) ");
			sql.append(" 			AND r.rel_godate < TRUNC(:processDate + 1) ");
			sql.append(" 			AND i.declarationnumber = tbl.declarationnumber ");
			sql.append(" 	) ");
			sql.append(" GROUP BY b.branch_name "); 
			sql.append(" ORDER BY b.branch_name ");
			
//			sql.append(" SELECT b.branch_name AS \"branchName\",  ");
//			sql.append(" 	COUNT(DISTINCT tbl.declarationnumber) AS \"totalDeclaration\",   ");
//			sql.append(" 	SUM(tbl.quantity) AS \"declarationQty\",   ");
//			sql.append(" 	SUM(tbl.deliveryqty) AS \"deliveryQty\"   ");
//			sql.append(" FROM tblexportdetail tbl  ");
//			sql.append(" 	JOIN branch b ON 1 = 1  ");
//			sql.append(" WHERE b.cpn_id = :companyId ");
//			sql.append(" 	AND tbl.flagcancel IS NULL ");
//			sql.append(" 	AND EXISTS( ");
//			sql.append(" 		SELECT 1 ");
//			sql.append(" 		FROM relation r "); 
//			sql.append(" 			JOIN invoice_item i ON i.cpn_id = r.cpn_id AND i.trf_id = r.trf_id ");
//			sql.append(" 		WHERE r.cpn_id = b.cpn_id  ");
//			sql.append(" 			AND r.rel_godate >= TRUNC(:processDate) ");
//			sql.append(" 			AND r.rel_godate < TRUNC(:processDate + 1) ");
//			sql.append(" 			AND i.declarationnumber = tbl.declarationnumber ");
//			sql.append(" 	) ");
//			sql.append(" GROUP BY b.branch_name "); 
//			sql.append(" ORDER BY b.branch_name ");
			
			params.add("companyId", SecurityUtils.getCompanyId());
			params.add("processDate", processDate);
			
			GridData gridData = coreRepository.searchGridData(sql.toString(), params);
			
			for (Map<String, Object> record : gridData.getRecords()) {
				String  branch = (String) record.get("branchName");
				Map<String, Object> map = branchMap.get(branch);
				if(map != null) {
					BigDecimal totalDeclaration = getData((BigDecimal) record.get("totalDeclaration"));
					map.put("totalDeclaration", totalDeclaration);
					
					BigDecimal declarationQty = getData((BigDecimal) record.get("declarationQty"));
					map.put("declarationQty", declarationQty);
					
					BigDecimal deliveryQty = getData((BigDecimal) record.get("deliveryQty"));
					map.put("deliveryQty", deliveryQty);
					map.put("diffPercent", deliveryQty.multiply(BigDecimal.valueOf(100)).divide(declarationQty, 0, RoundingMode.HALF_UP));
				}
			}
			
			sql.delete(0, sql.length() - 1);
			
			sql.append(" SELECT i.branch_name AS \"branchName\", ");
			sql.append(" 	c.display_name AS \"route\", ");
			sql.append(" 	COUNT(DISTINCT i.declarationnumber) AS \"noOfTrip\" ");
			sql.append(" FROM ( ");
			sql.append(" 	SELECT i.cpn_id, b.branch_id, b.branch_name, i.trf_id, i.declarationnumber ");
			sql.append(" 	FROM branch b ");
			sql.append(" 		JOIN invoice_item i ON i.cpn_id = b.cpn_id AND i.branch_id = b.branch_id ");
			sql.append(" 	WHERE i.cpn_id = :companyId ");
			sql.append(" 		AND EXISTS( ");
			sql.append(" 			SELECT 1 ");
			sql.append(" 			FROM relation r "); 
			sql.append(" 			WHERE r.cpn_id = i.cpn_id ");
			sql.append(" 				AND r.branch_id = i.branch_id "); 
			sql.append(" 				AND r.trf_id = i.trf_id "); 
			sql.append(" 				AND r.rel_godate >= TRUNC(:processDate) ");
			sql.append(" 				AND r.rel_godate < TRUNC(:processDate + 1) ");
			sql.append(" 		) ");
			sql.append(" 		AND EXISTS( ");
			sql.append(" 			SELECT 1 ");
			sql.append(" 			FROM tblexportdetail tbl "); 
			sql.append(" 			WHERE tbl.flagcancel IS NULL  ");
			sql.append(" 				AND tbl.cpn_id = i.cpn_id ");
			sql.append(" 				AND tbl.branch_id = i.branch_id ");
			sql.append(" 				AND tbl.declarationnumber = i.declarationnumber "); 
			sql.append(" 		) ");
			sql.append(" 	GROUP BY i.cpn_id, b.branch_id, b.branch_name, i.trf_id, i.declarationnumber ");
			sql.append(" ) i JOIN transfer t ON t.cpn_id = i.cpn_id  ");
			sql.append(" 	AND t.branch_id = i.branch_id ");
			sql.append(" 	AND t.trf_id = i.trf_id ");
			sql.append(" JOIN route_config c ON c.cpn_id = i.cpn_id "); 
			sql.append(" 	AND c.branch_id = i.branch_id  ");
			sql.append(" 	AND c.trf_from = t.trf_from  ");
			sql.append(" 	AND c.trf_to = t.trf_to ");
			sql.append(" GROUP BY i.branch_name, c.display_name, c.ordinal ");
			sql.append(" ORDER BY i.branch_name, c.ordinal ");
			
//			sql.append(" SELECT i.branch_name AS \"branchName\", ");
//			sql.append(" 	c.display_name AS \"route\", ");
//			sql.append(" 	COUNT(DISTINCT i.declarationnumber) AS \"noOfTrip\" ");
//			sql.append(" FROM ( ");
//			sql.append(" 	SELECT i.cpn_id, b.branch_id, b.branch_name, i.trf_id, i.declarationnumber ");
//			sql.append(" 	FROM branch b ");
//			sql.append(" 		JOIN invoice_item i ON i.cpn_id = b.cpn_id ");
//			sql.append(" 	WHERE i.cpn_id = :companyId ");
//			sql.append(" 		AND EXISTS( ");
//			sql.append(" 			SELECT 1 ");
//			sql.append(" 			FROM relation r "); 
//			sql.append(" 			WHERE r.cpn_id = i.cpn_id "); 
//			sql.append(" 				AND r.trf_id = i.trf_id "); 
//			sql.append(" 				AND r.rel_godate >= TRUNC(:processDate) ");
//			sql.append(" 				AND r.rel_godate < TRUNC(:processDate + 1) ");
//			sql.append(" 		) ");
//			sql.append(" 		AND EXISTS( ");
//			sql.append(" 			SELECT 1 ");
//			sql.append(" 			FROM tblexportdetail tbl "); 
//			sql.append(" 			WHERE tbl.flagcancel IS NULL  ");
//			sql.append(" 				AND tbl.declarationnumber = i.declarationnumber "); 
//			sql.append(" 		) ");
//			sql.append(" 	GROUP BY i.cpn_id, b.branch_id, b.branch_name, i.trf_id, i.declarationnumber ");
//			sql.append(" ) i JOIN transfer t ON t.cpn_id = i.cpn_id  ");
//			sql.append(" 	AND t.trf_id = i.trf_id ");
//			sql.append(" JOIN route_config c ON c.cpn_id = i.cpn_id "); 
//			sql.append(" 	AND c.branch_id = i.branch_id  ");
//			sql.append(" 	AND c.trf_from = t.trf_from  ");
//			sql.append(" 	AND c.trf_to = t.trf_to ");
//			sql.append(" GROUP BY i.branch_name, c.display_name, c.ordinal ");
//			sql.append(" ORDER BY i.branch_name, c.ordinal ");
	
			params.add("companyId", SecurityUtils.getCompanyId());
			params.add("processDate", processDate);
			
			gridData = coreRepository.searchGridData(sql.toString(), params);
			
			for (Map<String, Object> record : gridData.getRecords()) {
				String  branch = (String) record.get("branchName");
				Map<String, Object> map = branchMap.get(branch);
				if(map != null) {
					String route = (String) record.get("route");
					BigDecimal noOfTrip = getData((BigDecimal) record.get("noOfTrip"));
	
					Map<String, BigDecimal> data = (Map<String, BigDecimal>) map.get("data");
					data.put(route, noOfTrip);
	
					map.put("total", ((BigDecimal)map.get("total")).add(noOfTrip));
				}
			}
			
			for (Map.Entry<String, Map<String, Object>> branchEntry : branchMap.entrySet()) {
				Map<String, Object> map = branchEntry.getValue();
	
				Map<String, BigDecimal> data = (Map<String, BigDecimal>)map.get("data");
				map.put("labels", data.keySet());
				
				Map<String, Object> m = new HashMap<>();
				m.put("name", "Route");
				m.put("data", data.values());
				List<Map<String, Object>> series = new ArrayList<Map<String,Object>>();
				series.add(m);
				map.put("series", series);
				map.remove("data");
			}
		}

		log.info("[{}] searchTotalDeclaration result {}", Thread.currentThread().getName(),  branchMap);
		return CompletableFuture.completedFuture(GridData.of(new ArrayList<>(branchMap.values())));
	}
	
	@SuppressWarnings("unchecked")
	@Async("queryTaskExecutor")
	public CompletableFuture<GridData> searchHistoryInformationForDeclaration(final DocumentPerformanceModel model) {
		log.info("[{}] searchHistoryInformationForDeclaration : {}", Thread.currentThread().getName(), model);
		LocalDate processDate = getProcessDate(LocalDate.parse("17/06/2021", DateTimeFormatter.ofPattern("dd/MM/yyyy")));
		
		SqlParams params = SqlParams.create();
		
		GridData labelGridData = getMonthYear(params, model.getPeriod(), processDate);
		
		List<String> labels = new ArrayList<String>();
		List<String[]> textLabels = new ArrayList<>();
		for (Map<String, Object> record : labelGridData.getRecords()) {
			labels.add((String) record.get("monthYear"));
			textLabels.add(((String) record.get("monthYear")).split(" "));
		}
		
		List<String> statuss = new ArrayList<>();
		statuss.add("Declaration Qty");
		statuss.add("Delivery Qty");
		
		GridData branchGridData = this.getTotalBranch(params);
		
		Map<String, Map<String, Object>> branchMap = new LinkedHashMap<>();
		
		for (Map<String, Object> record : branchGridData.getRecords()) {
			String branch = (String) record.get("branchName");
			Map<String, Object> map = new LinkedHashMap<>();
			map.put("title", branch);
			map.put("labels", textLabels);
			map.put("total", BigDecimal.ZERO);
			
			Map<String, Map<String, BigDecimal>> seriesMap = new LinkedHashMap<>();
			for (String status : statuss) {
				Map<String, BigDecimal> periodMap = new LinkedHashMap<>();
				for (String label : labels) {
					periodMap.put(label, getData(BigDecimal.ZERO));
				}
				seriesMap.put(status, periodMap);
			}
			map.put("data", seriesMap);

			branchMap.put(branch, map);
		}
		
		if(CoreUtils.isNotEmpty(branchMap)) {
			StringBuilder sql = new StringBuilder();	
	
			sql.append(" SELECT i.branch_name AS \"branchName\", ");
			sql.append(" 	i.month_year AS \"monthYear\", ");
			sql.append(" 	SUM(i.quantity) AS \"declarationQty\", ");
			sql.append(" 	SUM(i.deliveryqty) AS \"deliveryQty\" ");
			sql.append(" FROM ( ");
			sql.append(" 	SELECT b.branch_name, ");
			sql.append(" 		TO_CHAR(tbl.declarationacceptdate + 1/24, 'MON YYYY') AS month_year, "); 
			sql.append(" 		TO_CHAR(tbl.declarationacceptdate + 1/24, 'YYYYMM') AS month_year_num, ");
			sql.append(" 		tbl.quantity, ");
			sql.append(" 		tbl.deliveryqty  ");
			sql.append(" 	FROM tblexportdetail tbl ");
			sql.append(" 	    JOIN branch b ON b.cpn_id = tbl.cpn_id AND b.branch_id = tbl.branch_id ");
			sql.append(" 	WHERE tbl.cpn_id = :companyId ");
			sql.append(" 		AND tbl.flagcancel IS NULL ");
			sql.append(" 		AND tbl.declarationacceptdate >= ADD_MONTHS(TRUNC(:processDate,'MM'), -(:period - 1)) - 1/24 ");
			sql.append(" 		AND tbl.declarationacceptdate < LAST_DAY(:processDate) - 1/24 ");
			sql.append(" 		AND EXISTS( ");
			sql.append(" 			SELECT 1 ");
			sql.append(" 			FROM invoice_item i ");
			sql.append(" 			WHERE i.cpn_id = tbl.cpn_id ");
			sql.append(" 				AND i.branch_id = tbl.branch_id ");
			sql.append(" 				AND i.declarationnumber = tbl.declarationnumber ");
			sql.append(" 		) ");
			sql.append(" ) i ");
			sql.append(" GROUP BY i.branch_name, i.month_year, i.month_year_num ");
			sql.append(" ORDER BY i.branch_name, i.month_year_num ");
			
//			sql.append(" SELECT i.branch_name AS \"branchName\", ");
//			sql.append(" 	i.month_year AS \"monthYear\", ");
//			sql.append(" 	SUM(i.quantity) AS \"declarationQty\", ");
//			sql.append(" 	SUM(i.deliveryqty) AS \"deliveryQty\" ");
//			sql.append(" FROM ( ");
//			sql.append(" 	SELECT b.branch_name, ");
//			sql.append(" 		TO_CHAR(tbl.declarationacceptdate + 1/24, 'MON YYYY') AS month_year, "); 
//			sql.append(" 		TO_CHAR(tbl.declarationacceptdate + 1/24, 'YYYYMM') AS month_year_num, ");
//			sql.append(" 		tbl.quantity, ");
//			sql.append(" 		tbl.deliveryqty  ");
//			sql.append(" 	FROM tblexportdetail tbl ");
//			sql.append(" 	    JOIN branch b ON 1 = 1 ");
//			sql.append(" 	WHERE tbl.flagcancel IS NULL ");
//			sql.append(" 		AND tbl.declarationacceptdate >= ADD_MONTHS(TRUNC(:processDate,'MM'), -(:period - 1)) - 1/24 ");
//			sql.append(" 		AND tbl.declarationacceptdate < LAST_DAY(:processDate) - 1/24 ");
//			sql.append(" 		AND EXISTS( ");
//			sql.append(" 			SELECT 1 ");
//			sql.append(" 			FROM invoice_item i ");
//			sql.append(" 			WHERE i.cpn_id = :companyId ");
//			sql.append(" 				AND i.cpn_id = b.cpn_id ");
//			sql.append(" 				AND i.declarationnumber = tbl.declarationnumber ");
//			sql.append(" 		) ");
//			sql.append(" ) i ");
//			sql.append(" GROUP BY i.branch_name, i.month_year, i.month_year_num ");
//			sql.append(" ORDER BY i.branch_name, i.month_year_num ");
			
			params.add("companyId", SecurityUtils.getCompanyId());
			params.add("processDate", processDate);
			params.add("period", model.getPeriod());
			
			GridData gridData = coreRepository.searchGridData(sql.toString(), params);
			
			for (Map<String, Object> record : gridData.getRecords()) {
				String branch = (String) record.get("branchName");
				Map<String, Object> map = branchMap.get(branch);
				
				Map<String, Map<String, BigDecimal>> seriesMap = (Map<String, Map<String, BigDecimal>>) map.get("data");
	
				String period = (String) record.get("monthYear");
	
				Map<String, BigDecimal> periodMap = seriesMap.get("Declaration Qty");
				BigDecimal declarationQty = getData((BigDecimal) record.get("declarationQty"));
				periodMap.put(period, declarationQty);
				
				periodMap = seriesMap.get("Delivery Qty");
				BigDecimal deliveryQty = getData((BigDecimal) record.get("deliveryQty"));
				periodMap.put(period, deliveryQty);
	
				map.put("total", ((BigDecimal) map.get("total")).add(declarationQty).add(deliveryQty));
			}
	
			for (Entry<String, Map<String, Object>> branchEntry : branchMap.entrySet()) {
				Map<String, Object> map = (Map<String, Object>)branchEntry.getValue();
				
				Map<String, Map<String, BigDecimal>> statusMap = (Map<String, Map<String, BigDecimal>>)map.get("data");
				
				List<Map<String, Object>> series = new ArrayList<>();
				for (Entry<String, Map<String, BigDecimal>> statusEntry : statusMap.entrySet()) {
					Map<String, Object> serie = new HashMap<String, Object>();
					serie.put("name", statusEntry.getKey());
					serie.put("data", statusEntry.getValue().values());
					series.add(serie);
				}
				
				map.put("series", series);
			}
		}

		log.info("[{}] searchHistoryInformationForDeclaration result : {}", Thread.currentThread().getName(), branchMap);
		return CompletableFuture.completedFuture(GridData.of(new ArrayList<>(branchMap.values())));
	}
	
	private GridData getTotalBranch(SqlParams params) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT b.branch_name AS \"branchName\" ");
		sql.append(" FROM branch b ");
		sql.append(" WHERE b.cpn_id = :companyId ");
		sql.append(" ORDER BY b.branch_name ");
		
		params.add("companyId", SecurityUtils.getCompanyId());

		return coreRepository.searchGridData(sql.toString(), params);
	}
	
	private GridData getTotalBranchRoute(SqlParams params) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT b.branch_name AS \"branchName\", ");
		sql.append(" 	c.display_name AS route,  ");
		sql.append(" 	c.display_color AS color ");
		sql.append(" FROM route_config c ");
		sql.append(" 	JOIN branch b ON b.cpn_id = c.cpn_id "); 
		sql.append(" 		AND b.branch_id = c.branch_id ");
		sql.append(" WHERE c.cpn_id = :companyId ");
		sql.append(" ORDER BY b.branch_name, c.ordinal ");
		
		params.add("companyId", SecurityUtils.getCompanyId());

		return coreRepository.searchGridData(sql.toString(), params);
	}

	private GridData getMonthYear(SqlParams params, Integer period, LocalDate processDate) {
		StringBuilder sql = new StringBuilder();

		sql.append(" SELECT ");
		sql.append(" 	TO_CHAR(ADD_MONTHS(TRUNC(:processDate,'MM'), -(:period - ROWNUM)), 'MON YYYY') AS \"monthYear\" "); 
		sql.append(" FROM DUAL CONNECT BY LEVEL <= :period ");

		params.add("processDate", processDate);
		params.add("period", period);

		return coreRepository.searchGridData(sql.toString(), params);
	}

}
