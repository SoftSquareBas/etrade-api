package com.tiffa.wd.elock.paperless.dashboard.tripPerformance;

import java.math.BigDecimal;
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
import com.tiffa.wd.elock.paperless.core.util.SecurityUtils;

@Service
@Transactional(readOnly = true, propagation = Propagation.NEVER)
public class TripPerformanceService {

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
	public CompletableFuture<GridData> searchTotalTripNoOfEachRoute() {
		LocalDate processDate = getProcessDate(LocalDate.parse("17/06/2021", DateTimeFormatter.ofPattern("dd/MM/yyyy")));
		
		SqlParams params = SqlParams.create();
		
		GridData branchGridData = this.getTotalBranchRoute(params);
		
		Map<String, Map<String, Object>> branchMap = new LinkedHashMap<>();
		for (Map<String, Object> record : branchGridData.getRecords()) {
			String branch = (String)record.get("branchName");
			
			Map<String, Object> map = branchMap.get(branch);
			if(map == null) {
				map = new HashMap<>();
				map.put("title", branch);
				map.put("total", BigDecimal.ZERO);
				map.put("colors", new ArrayList<String>());
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
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT b.branch_name AS \"branchName\" ");
		sql.append(" 	, c.display_name AS \"route\" ");
		sql.append(" 	, COUNT(1) AS \"noOfTrip\" ");
		sql.append(" FROM relation r ");
		sql.append(" 	JOIN transfer t ON t.cpn_id = r.cpn_id AND t.branch_id = r.branch_id AND t.trf_id = r.trf_id ");
		sql.append(" 	JOIN branch b ON b.cpn_id = r.cpn_id AND b.branch_id = r.branch_id ");
		sql.append("	JOIN route_config c ON c.cpn_id = b.cpn_id AND c.branch_id = b.branch_id ");
		sql.append("		AND c.trf_from = t.trf_from AND c.trf_to = t.trf_to ");
		sql.append(" WHERE r.cpn_id = :companyId ");
		sql.append(" 	AND r.rel_startdate >= TRUNC(:processDate) ");
		sql.append(" 	AND r.rel_startdate < TRUNC(:processDate + 1) ");
		sql.append(" GROUP BY b.branch_name, c.display_name, c.ordinal ");
		sql.append(" ORDER BY b.branch_name, c.ordinal ");
		
//		sql.append(" SELECT b.branch_name AS \"branchName\" ");
//		sql.append(" 	, c.display_name AS \"route\" ");
//		sql.append(" 	, COUNT(1) AS \"noOfTrip\" ");
//		sql.append(" FROM relation r ");
//		sql.append(" 	JOIN transfer t ON t.cpn_id = r.cpn_id AND t.trf_id = r.trf_id ");
//		sql.append(" 	JOIN branch b ON b.cpn_id = r.cpn_id ");
//		sql.append("	JOIN route_config c ON c.cpn_id = b.cpn_id AND c.branch_id = b.branch_id ");
//		sql.append("		AND c.trf_from = t.trf_from AND c.trf_to = t.trf_to ");
//		sql.append(" WHERE r.cpn_id = :companyId ");
//		sql.append(" 	AND r.rel_startdate >= TRUNC(:processDate) ");
//		sql.append(" 	AND r.rel_startdate < TRUNC(:processDate + 1) ");
//		sql.append(" GROUP BY b.branch_name, c.display_name, c.ordinal ");
//		sql.append(" ORDER BY b.branch_name, c.ordinal ");
		
		params.add("companyId", SecurityUtils.getCompanyId());
		params.add("processDate", processDate);
		
		GridData gridData = coreRepository.searchGridData(sql.toString(), params);
		
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
			map.put("series", data.values());
			map.remove("data");
		}

		return CompletableFuture.completedFuture(GridData.of(new ArrayList<>(branchMap.values())));
	}
	
	@SuppressWarnings("unchecked")
	@Async("queryTaskExecutor")
	public CompletableFuture<GridData> searchOnTimeLateTamper() {
		LocalDate processDate = getProcessDate(LocalDate.parse("17/06/2021", DateTimeFormatter.ofPattern("dd/MM/yyyy")));
		SqlParams params = SqlParams.create();
		
		GridData branchGridData = this.getTotalBranch(params);
		
		Map<String, Map<String, Object>> branchMap = new LinkedHashMap<>();
		for (Map<String, Object> record : branchGridData.getRecords()) {
			String branch = (String)record.get("branchName");
			
			Map<String, Object> map = new HashMap<>();
			map.put("title", branch);
			map.put("total", BigDecimal.ZERO);
			
			Map<String, BigDecimal> data = new LinkedHashMap<>();
			data.put("On Time", getData(BigDecimal.ZERO));
			data.put("Late", getData(BigDecimal.ZERO));
			data.put("Tamper", getData(BigDecimal.ZERO));
			map.put("data", data);
			
			map.put("labels", data.keySet());
			branchMap.put(branch, map);
		}
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT r.branch_name AS \"branchName\", r.status AS \"status\", COUNT(1) AS \"noOfStatus\" ");
		sql.append(" FROM ( ");
		sql.append(" 	SELECT b.branch_name ");
		sql.append(" 		, (CASE ");
		sql.append(" 			WHEN r.rel_enddate IS NOT NULL AND r.late_flag = 0 AND r.tampered_flag = 0 THEN 'On Time' ");
		sql.append(" 			WHEN r.late_flag = 1 THEN 'Late' ");
		sql.append(" 			WHEN r.tampered_flag = 1 THEN 'Tamper' ");
		sql.append(" 			ELSE NULL ");
		sql.append(" 		END) AS status ");
		sql.append(" 	FROM relation r ");
		sql.append(" 		JOIN transfer t ON t.cpn_id = r.cpn_id AND t.branch_id = r.branch_id AND t.trf_id = r.trf_id ");
		sql.append(" 		JOIN branch b ON b.cpn_id = r.cpn_id AND b.branch_id = r.branch_id ");
		sql.append(" 	WHERE r.cpn_id = :companyId ");
		sql.append(" 		AND r.rel_startdate >= TRUNC(:processDate) ");
		sql.append(" 		AND r.rel_startdate < TRUNC(:processDate + 1) ");
		sql.append(" ) r ");
		sql.append(" WHERE r.status IS NOT NULL ");
		sql.append(" GROUP BY r.branch_name, r.status ");
		sql.append(" ORDER BY r.branch_name, (CASE WHEN r.status = 'On Time' THEN 1 WHEN r.status = 'Late' THEN 2 ELSE 3 END) ");
		
//		sql.append(" SELECT r.branch_name AS \"branchName\", r.status AS \"status\", COUNT(1) AS \"noOfStatus\" ");
//		sql.append(" FROM ( ");
//		sql.append(" 	SELECT b.branch_name ");
//		sql.append(" 		, (CASE ");
//		sql.append(" 			WHEN r.rel_enddate IS NOT NULL AND r.late_flag = 0 AND r.tampered_flag = 0 THEN 'On Time' ");
//		sql.append(" 			WHEN r.late_flag = 1 THEN 'Late' ");
//		sql.append(" 			WHEN r.tampered_flag = 1 THEN 'Tamper' ");
//		sql.append(" 			ELSE NULL ");
//		sql.append(" 		END) AS status ");
//		sql.append(" 	FROM relation r ");
//		sql.append(" 		JOIN transfer t ON t.cpn_id = r.cpn_id AND t.trf_id = r.trf_id ");
//		sql.append(" 		JOIN branch b ON b.cpn_id = r.cpn_id ");
//		sql.append(" 	WHERE r.cpn_id = :companyId ");
//		sql.append(" 		AND r.rel_startdate >= TRUNC(:processDate) ");
//		sql.append(" 		AND r.rel_startdate < TRUNC(:processDate + 1) ");
//		sql.append(" ) r ");
//		sql.append(" WHERE r.status IS NOT NULL ");
//		sql.append(" GROUP BY r.branch_name, r.status ");
//		sql.append(" ORDER BY r.branch_name, (CASE WHEN r.status = 'On Time' THEN 1 WHEN r.status = 'Late' THEN 2 ELSE 3 END) ");
		
		params.add("companyId", SecurityUtils.getCompanyId());
		params.add("processDate", processDate);
		
		GridData gridData = coreRepository.searchGridData(sql.toString(), params);
		
		for (Map<String, Object> record : gridData.getRecords()) {
			String  branch = (String) record.get("branchName");
			Map<String, Object> map = branchMap.get(branch);
			
			if(map != null) {
				BigDecimal noOfStatus = getData((BigDecimal) record.get("noOfStatus"));
				map.put("total", ((BigDecimal)map.get("total")).add(noOfStatus));

				Map<String, BigDecimal> data = (Map<String, BigDecimal>) map.get("data");
				
				String status = (String) record.get("status");
				data.put(status, noOfStatus);
			}
		}

		for (Entry<String, Map<String, Object>> branchEntry : branchMap.entrySet()) {
			Map<String, Object> map = (Map<String, Object>)branchEntry.getValue();
			
			Map<String, BigDecimal> data = (Map<String, BigDecimal>)map.get("data");
			map.put("series", data.values());
			map.remove("data");
		}

		return CompletableFuture.completedFuture(GridData.of(new ArrayList<>(branchMap.values())));
	}
	
	@SuppressWarnings("unchecked")
	@Async("queryTaskExecutor")
	public CompletableFuture<GridData> searchHistoryInformationForNoOfTrip(final TripPerformanceModel model) {
		LocalDate processDate = getProcessDate(LocalDate.parse("17/06/2021", DateTimeFormatter.ofPattern("dd/MM/yyyy")));
		
		SqlParams params = SqlParams.create();
		
		GridData labelGridData = getMonthYear(params, model.getNoOfTripPeriod(), processDate);
		
		List<String> labels = new ArrayList<>();
		List<String[]> textLabels = new ArrayList<>();
		for (Map<String, Object> record : labelGridData.getRecords()) {
			String label = (String) record.get("monthYear");
			labels.add(label);
			textLabels.add(label.split(" "));
		}

		GridData branchGridData = this.getTotalBranchRoute(params);
		
		Map<String, Map<String, Object>> branchMap = new LinkedHashMap<>();
		
		for (Map<String, Object> record : branchGridData.getRecords()) {
			String branch = (String)record.get("branchName");
			
			Map<String, Object> map = branchMap.get(branch);
			if(map == null) {
				map = new HashMap<>();
				map.put("title", branch);
				map.put("labels", textLabels);
				map.put("total", BigDecimal.ZERO);
				map.put("colors", new ArrayList<String>());
				map.put("data", new LinkedHashMap<String, Map<String, BigDecimal>>());
				branchMap.put(branch, map);
			}

			List<String> colors = (List<String>)map.get("colors");
			String color = (String)record.get("color");
			colors.add(color);
			map.put("colors", colors);
			
			Map<String, Map<String, BigDecimal>> data = (Map<String, Map<String, BigDecimal>>) map.get("data");
			String route = (String)record.get("route");
			
			Map<String, BigDecimal> series = new LinkedHashMap<>();
			for (String label : labels) {
				series.put(label, getData(BigDecimal.ZERO));
			}

			data.put(route, series);
			map.put("data", data);
		}

		StringBuilder sql = new StringBuilder();
		
		sql.append(" SELECT r.branch_name \"branchName\", r.route AS \"route\", r.month_year As \"monthYear\", COUNT(1) AS \"noOfTrip\" ");
		sql.append(" FROM ( ");
		sql.append(" 	SELECT b.branch_id, b.branch_name, "); 
		sql.append(" 		TO_CHAR(r.rel_startdate, 'MON YYYY') AS month_year, "); 
		sql.append(" 		TO_CHAR(r.rel_startdate, 'YYYYMM') AS month_year_num,  ");
		sql.append(" 		c.display_name AS route, ");
		sql.append(" 		c.ordinal AS ordinal ");
		sql.append(" 	FROM relation r ");
		sql.append(" 		JOIN transfer t ON t.cpn_id = r.cpn_id AND t.branch_id = r.branch_id AND r.trf_id = t.trf_id ");
		sql.append(" 		JOIN branch b ON b.cpn_id = r.cpn_id AND b.branch_id = r.branch_id ");
		sql.append(" 		JOIN route_config c ON c.cpn_id = b.cpn_id AND c.branch_id = b.branch_id ");
		sql.append("			AND c.trf_from = t.trf_from AND c.trf_to = t.trf_to ");
		sql.append(" 	WHERE r.cpn_id = :companyId ");
		sql.append(" 		AND r.rel_startdate >= ADD_MONTHS(TRUNC(:processDate,'MM'), -(:period - 1)) ");
		sql.append(" 		AND r.rel_startdate < TRUNC(ADD_MONTHS(:processDate, 1), 'MM') ");
		sql.append(" ) r ");
		sql.append(" GROUP BY r.branch_name, r.route, r.ordinal, r.month_year, r.month_year_num ");
		sql.append(" ORDER BY r.branch_name, r.ordinal, r.month_year_num ");
		
//		sql.append(" SELECT r.branch_name \"branchName\", r.route AS \"route\", r.month_year As \"monthYear\", COUNT(1) AS \"noOfTrip\" ");
//		sql.append(" FROM ( ");
//		sql.append(" 	SELECT b.branch_id, b.branch_name, "); 
//		sql.append(" 		TO_CHAR(r.rel_startdate, 'MON YYYY') AS month_year, "); 
//		sql.append(" 		TO_CHAR(r.rel_startdate, 'YYYYMM') AS month_year_num,  ");
//		sql.append(" 		c.display_name AS route, ");
//		sql.append(" 		c.ordinal AS ordinal ");
//		sql.append(" 	FROM relation r ");
//		sql.append(" 		JOIN transfer t ON t.cpn_id = r.cpn_id AND r.trf_id = t.trf_id ");
//		sql.append(" 		JOIN branch b ON b.cpn_id = r.cpn_id ");
//		sql.append(" 		JOIN route_config c ON c.cpn_id = b.cpn_id AND c.branch_id = b.branch_id ");
//		sql.append("			AND c.trf_from = t.trf_from AND c.trf_to = t.trf_to ");
//		sql.append(" 	WHERE r.cpn_id = :companyId ");
//		sql.append(" 		AND r.rel_startdate >= ADD_MONTHS(TRUNC(:processDate,'MM'), -(:period - 1)) ");
//		sql.append(" 		AND r.rel_startdate < TRUNC(ADD_MONTHS(:processDate, 1), 'MM') ");
//		sql.append(" ) r ");
//		sql.append(" GROUP BY r.branch_name, r.route, r.ordinal, r.month_year, r.month_year_num ");
//		sql.append(" ORDER BY r.branch_name, r.ordinal, r.month_year_num ");
		
		params.add("companyId", SecurityUtils.getCompanyId());
		params.add("processDate", processDate);
		params.add("period", Integer.valueOf(model.getNoOfTripPeriod()));
		
		GridData gridData = coreRepository.searchGridData(sql.toString(), params);
		
		for (Map<String, Object> record : gridData.getRecords()) {
			String branch = (String) record.get("branchName");
			Map<String, Object> map = branchMap.get(branch);
			Map<String, Map<String, BigDecimal>> routeMap = (Map<String, Map<String, BigDecimal>>)map.get("data");
			
			String route = (String)record.get("route");
			String period = (String) record.get("monthYear");

			Map<String, BigDecimal> periodMap = routeMap.get(route);
			BigDecimal noOfTrip = getData((BigDecimal) record.get("noOfTrip"));
			periodMap.put(period, noOfTrip);

			map.put("total", ((BigDecimal)map.get("total")).add(noOfTrip));
		}

		for (Entry<String, Map<String, Object>> branchEntry : branchMap.entrySet()) {
			Map<String, Object> map = (Map<String, Object>)branchEntry.getValue();
			
			Map<String, Map<String, BigDecimal>> routeMap = (Map<String, Map<String, BigDecimal>>)map.get("data");
			
			List<Map<String, Object>> series = new ArrayList<>();
			for (Entry<String, Map<String, BigDecimal>> routeEntry : routeMap.entrySet()) {
				Map<String, Object> serie = new HashMap<String, Object>();
				serie.put("name", routeEntry.getKey());
				serie.put("data", routeEntry.getValue().values());
				series.add(serie);
			}
			
			map.put("series", series);
			map.remove("data");
		}

		return CompletableFuture.completedFuture(GridData.of(new ArrayList<>(branchMap.values())));
	}

	@SuppressWarnings("unchecked")
	@Async("queryTaskExecutor")
	public CompletableFuture<GridData> searchHistoryInformationForTripStatus(final TripPerformanceModel model) {
		LocalDate processDate = getProcessDate(LocalDate.parse("17/06/2021", DateTimeFormatter.ofPattern("dd/MM/yyyy")));
		SqlParams params = SqlParams.create();
		
		GridData labelGridData = getMonthYear(params, model.getTripStatusPeriod(), processDate);
		
		List<String> labels = new ArrayList<String>();
		List<String[]> textLabels = new ArrayList<>();
		for (Map<String, Object> record : labelGridData.getRecords()) {
			labels.add((String) record.get("monthYear"));
			textLabels.add(((String) record.get("monthYear")).split(" "));
		}
		
		List<String> statuss = new ArrayList<>();
		statuss.add("On Time");
		statuss.add("Late");
		statuss.add("Tamper");
		
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
		
		StringBuilder sql = new StringBuilder();
		
		sql.append(" SELECT r.branch_name AS \"branchName\", r.status AS \"status\", r.month_year AS \"monthYear\", COUNT(1) AS \"noOfStatus\" ");
		sql.append(" FROM ( ");
		sql.append(" 	SELECT b.branch_name ");
		sql.append(" 		, TO_CHAR(r.rel_startdate, 'MON YYYY') AS month_year "); 
		sql.append(" 		, TO_CHAR(r.rel_startdate, 'YYYYMM') AS month_year_num  ");
		sql.append(" 		, (CASE ");
		sql.append(" 		 	WHEN r.rel_enddate IS NOT NULL AND r.late_flag = 0 AND r.tampered_flag = 0 THEN 'On Time' ");
		sql.append(" 		 	WHEN r.late_flag = 1 THEN 'Late' ");
		sql.append(" 		 	WHEN r.tampered_flag = 1 THEN 'Tamper' ");
		sql.append(" 		 	ELSE NULL ");
		sql.append(" 		 END) AS status ");
		sql.append(" 	FROM relation r ");
		sql.append(" 		JOIN transfer t ON t.cpn_id = r.cpn_id AND t.branch_id = r.branch_id AND t.trf_id = r.trf_id ");
		sql.append(" 		JOIN branch b ON b.cpn_id = r.cpn_id AND b.branch_id = r.branch_id ");
		sql.append(" 	WHERE r.cpn_id = :companyId ");
		sql.append(" 		AND r.rel_startdate >= ADD_MONTHS(TRUNC(:processDate,'MM'), -(:period - 1)) ");
		sql.append(" 		AND r.rel_startdate < TRUNC(ADD_MONTHS(:processDate, 1), 'MM') ");
		sql.append(" ) r ");
		sql.append(" WHERE r.status IS NOT NULL ");
		sql.append(" GROUP BY r.branch_name, r.status, r.month_year_num, r.month_year ");
		sql.append(" ORDER BY r.branch_name, (CASE WHEN r.status = 'On Time' THEN 1 WHEN r.status = 'Late' THEN 2 ELSE 3 END), r.month_year_num ");
		
		
//		sql.append(" SELECT r.branch_name AS \"branchName\", r.status AS \"status\", r.month_year AS \"monthYear\", COUNT(1) AS \"noOfStatus\" ");
//		sql.append(" FROM ( ");
//		sql.append(" 	SELECT b.branch_name ");
//		sql.append(" 		, TO_CHAR(r.rel_startdate, 'MON YYYY') AS month_year "); 
//		sql.append(" 		, TO_CHAR(r.rel_startdate, 'YYYYMM') AS month_year_num  ");
//		sql.append(" 		, (CASE ");
//		sql.append(" 		 	WHEN r.rel_enddate IS NOT NULL AND r.late_flag = 0 AND r.tampered_flag = 0 THEN 'On Time' ");
//		sql.append(" 		 	WHEN r.late_flag = 1 THEN 'Late' ");
//		sql.append(" 		 	WHEN r.tampered_flag = 1 THEN 'Tamper' ");
//		sql.append(" 		 	ELSE NULL ");
//		sql.append(" 		 END) AS status ");
//		sql.append(" 	FROM relation r ");
//		sql.append(" 		JOIN transfer t ON t.cpn_id = r.cpn_id AND t.trf_id = r.trf_id ");
//		sql.append(" 		JOIN branch b ON b.cpn_id = r.cpn_id ");
//		sql.append(" 	WHERE r.cpn_id = :companyId ");
//		sql.append(" 		AND r.rel_startdate >= ADD_MONTHS(TRUNC(:processDate,'MM'), -(:period - 1)) ");
//		sql.append(" 		AND r.rel_startdate < TRUNC(ADD_MONTHS(:processDate, 1), 'MM') ");
//		sql.append(" ) r ");
//		sql.append(" WHERE r.status IS NOT NULL ");
//		sql.append(" GROUP BY r.branch_name, r.status, r.month_year_num, r.month_year ");
//		sql.append(" ORDER BY r.branch_name, (CASE WHEN r.status = 'On Time' THEN 1 WHEN r.status = 'Late' THEN 2 ELSE 3 END), r.month_year_num ");

		params.add("companyId", SecurityUtils.getCompanyId());
		params.add("processDate", processDate);
		params.add("period", Integer.valueOf(model.getTripStatusPeriod()));
		
		GridData gridData = coreRepository.searchGridData(sql.toString(), params);
		
		for (Map<String, Object> record : gridData.getRecords()) {
			String branch = (String) record.get("branchName");
			Map<String, Object> map = branchMap.get(branch);
			
			Map<String, Map<String, BigDecimal>> seriesMap = (Map<String, Map<String, BigDecimal>>) map.get("data");

			String status = (String)record.get("status");
			Map<String, BigDecimal> periodMap = seriesMap.get(status);
			
			String period = (String) record.get("monthYear");
			BigDecimal noOfStatus = getData((BigDecimal) record.get("noOfStatus"));
			periodMap.put(period, noOfStatus);
			
			map.put("total", ((BigDecimal) map.get("total")).add(noOfStatus));
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
