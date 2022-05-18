package com.tiffa.wd.elock.paperless.dashboard.monthlyCostSaving;

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
import com.tiffa.wd.elock.paperless.core.util.SecurityUtils;

@Service
@Transactional(readOnly = true, propagation = Propagation.NEVER)
public class MonthlyCostSavingService {

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
	public CompletableFuture<GridData> getMonthlyCostSavingHistory(final MonthlyCostSavingModel model) {
		LocalDate processDate = getProcessDate(LocalDate.parse("17/06/2021", DateTimeFormatter.ofPattern("dd/MM/yyyy")));
		
		SqlParams params = SqlParams.create();
		
		GridData labelGridData = getMonthYear(params, model.getPeriod(), processDate);
		
		List<String> labels = new ArrayList<>();
		List<String[]> textLabels = new ArrayList<>();
		for (Map<String, Object> record : labelGridData.getRecords()) {
			String label = (String) record.get("monthYear");
			labels.add(label);
			textLabels.add(label.split(" "));
		}

		GridData branchGridData = this.getTotalBranch(params);
		
		List<Map<String, Object>> branchList = new ArrayList<>();
		{		
			Map<String, Object> allSiteMap = new HashMap<>();
			allSiteMap.put("branchName", "All Site");
			branchList.add(allSiteMap);
			
			for (Map<String, Object> record : branchGridData.getRecords()) {
				branchList.add(record);			
			}
		}
		
		Map<String, Map<String, Object>> branchMap = new LinkedHashMap<>();
		
		for (Map<String, Object> record : branchList) {
			String branch = (String)record.get("branchName");
			
			Map<String, Object> map = branchMap.get(branch);
			if(map == null) {
				map = new HashMap<>();
				map.put("title", branch);
				map.put("labels", textLabels);
				map.put("total", getData(BigDecimal.ZERO));
				
				map.put("noOfTripData", new LinkedHashMap<String, BigDecimal>());
				map.put("noOfDeclarationData", new LinkedHashMap<String, BigDecimal>());
				map.put("percentData", new LinkedHashMap<String, BigDecimal>());
				map.put("usdData", new LinkedHashMap<String, BigDecimal>());
				
				map.put("totalAmtCostNoELockData", new LinkedHashMap<String, BigDecimal>());
				map.put("totalAmtCostELockData", new LinkedHashMap<String, BigDecimal>());
				branchMap.put(branch, map);
			}
			
			Map<String, BigDecimal> noOfTripData = (Map<String, BigDecimal>) map.get("noOfTripData");
			Map<String, BigDecimal> noOfDeclarationData = (Map<String, BigDecimal>) map.get("noOfDeclarationData");
			Map<String, BigDecimal> percentData = (Map<String, BigDecimal>) map.get("percentData");
			Map<String, BigDecimal> usdData = (Map<String, BigDecimal>) map.get("usdData");
			
			Map<String, BigDecimal> totalAmtCostNoELockData = (Map<String, BigDecimal>) map.get("totalAmtCostNoELockData");
			Map<String, BigDecimal> totalAmtCostELockData = (Map<String, BigDecimal>) map.get("totalAmtCostELockData");

			for (String label : labels) {
				noOfTripData.put(label, getData(BigDecimal.ZERO));
				noOfDeclarationData.put(label, getData(BigDecimal.ZERO));
				percentData.put(label, getData(BigDecimal.ZERO));
				usdData.put(label, getData(BigDecimal.ZERO));
				
				totalAmtCostNoELockData.put(label, getData(BigDecimal.ZERO));
				totalAmtCostELockData.put(label, getData(BigDecimal.ZERO));
			}
		}
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	b.branch_name AS \"branchName\", ");
		sql.append(" 	INITCAP(TO_CHAR(:processDate,'MON')) AS \"month\",  ");
		sql.append(" 	b.month_year AS \"monthYear\",  ");
		sql.append(" 	b.no_of_trip AS \"noOfTrip\",  ");
		sql.append(" 	b.no_of_declaration AS \"noOfDeclaration\", "); 
		sql.append(" 	b.total_amt_cost_no_elock AS \"totalAmtCostNoELock\", "); 
		sql.append(" 	b.total_amt_cost_elock AS \"totalAmtCostELock\", ");
		sql.append(" 	b.currency AS \"currency\", ");
		sql.append(" 	b.exchange_rate AS \"exchangeRate\" ");
		sql.append(" FROM (  ");
		sql.append(" 	SELECT b.branch_name, "); 
		sql.append(" 		b.month_year, ");
		sql.append(" 		b.no_of_trip,   ");
		sql.append(" 		b.no_of_declaration, "); 
		sql.append(" 		c.currency AS currency, ");
		sql.append(" 		c.exchangerate AS exchange_rate, ");
		sql.append(" 		(CASE WHEN NVL(c.exchangerate,0) = 0 THEN 0 ELSE (b.no_of_trip * c.declarationcost) / c.exchangerate END) AS total_amt_cost_no_elock, "); 
		sql.append(" 		(CASE WHEN NVL(c.exchangerate,0) = 0 THEN 0 ELSE ((b.no_of_declaration * c.declarationcost) + c.webappcost + (b.no_of_trip * c.pertripcost)) / c.exchangerate END) AS total_amt_cost_elock "); 
		sql.append(" 	FROM ( ");
		sql.append(" 		SELECT  ");
		sql.append(" 			b.cpn_id, ");
		sql.append(" 			b.branch_id, ");
		sql.append(" 			b.branch_name, ");
		sql.append(" 			TO_CHAR(r.rel_startdate, 'MON YYYY') AS month_year, ");
		sql.append(" 			COUNT(DISTINCT r.trf_id) AS no_of_trip, ");
		sql.append(" 			COUNT(DISTINCT i.declarationnumber) AS no_of_declaration ");
		sql.append(" 		FROM relation r  ");
		sql.append(" 			JOIN invoice_item i ON i.cpn_id = r.cpn_id AND i.branch_id = r.branch_id AND i.trf_id = r.trf_id "); 
		sql.append(" 			JOIN branch b ON b.cpn_id = r.cpn_id AND b.branch_id = r.branch_id ");
		sql.append(" 		WHERE r.cpn_id = :companyId  ");
		sql.append(" 			AND r.rel_startdate >= ADD_MONTHS(TRUNC(:processDate,'MM'), -(:period - 1)) "); 
		sql.append(" 			AND r.rel_startdate <  TRUNC(ADD_MONTHS(:processDate, 1), 'MM')  ");
		sql.append(" 		GROUP BY b.cpn_id, ");
		sql.append(" 			b.branch_id, ");
		sql.append(" 			b.branch_name, ");
		sql.append(" 			TO_CHAR(r.rel_startdate, 'MON YYYY') ");
		sql.append(" 	) b JOIN branch c ON c.cpn_id = b.cpn_id AND c.branch_id = b.branch_id ");
		sql.append(" ) b  ");
		
//		sql.append(" SELECT ");
//		sql.append(" 	b.branch_name AS \"branchName\", ");
//		sql.append(" 	INITCAP(TO_CHAR(:processDate,'MON')) AS \"month\",  ");
//		sql.append(" 	b.month_year AS \"monthYear\",  ");
//		sql.append(" 	b.no_of_trip AS \"noOfTrip\",  ");
//		sql.append(" 	b.no_of_declaration AS \"noOfDeclaration\", "); 
//		sql.append(" 	b.total_amt_cost_no_elock AS \"totalAmtCostNoELock\", "); 
//		sql.append(" 	b.total_amt_cost_elock AS \"totalAmtCostELock\", ");
//		sql.append(" 	b.currency AS \"currency\", ");
//		sql.append(" 	b.exchange_rate AS \"exchangeRate\" ");
//		sql.append(" FROM (  ");
//		sql.append(" 	SELECT b.branch_name, "); 
//		sql.append(" 		b.month_year, ");
//		sql.append(" 		b.no_of_trip,   ");
//		sql.append(" 		b.no_of_declaration, "); 
//		sql.append(" 		c.currency AS currency, ");
//		sql.append(" 		c.exchangerate AS exchange_rate, ");
//		sql.append(" 		(CASE WHEN NVL(c.exchangerate,0) = 0 THEN 0 ELSE (b.no_of_trip * c.declarationcost) / c.exchangerate END) AS total_amt_cost_no_elock, "); 
//		sql.append(" 		(CASE WHEN NVL(c.exchangerate,0) = 0 THEN 0 ELSE ((b.no_of_declaration * c.declarationcost) + c.webappcost + (b.no_of_trip * c.pertripcost)) / c.exchangerate END) AS total_amt_cost_elock "); 
//		sql.append(" 	FROM ( ");
//		sql.append(" 		SELECT  ");
//		sql.append(" 			b.cpn_id, ");
//		sql.append(" 			b.branch_id, ");
//		sql.append(" 			b.branch_name, ");
//		sql.append(" 			TO_CHAR(r.rel_startdate, 'MON YYYY') AS month_year, ");
//		sql.append(" 			COUNT(DISTINCT r.trf_id) AS no_of_trip, ");
//		sql.append(" 			COUNT(DISTINCT i.declarationnumber) AS no_of_declaration ");
//		sql.append(" 		FROM relation r  ");
//		sql.append(" 			JOIN invoice_item i ON i.cpn_id = r.cpn_id AND i.trf_id = r.trf_id "); 
//		sql.append(" 			JOIN branch b ON b.cpn_id = r.cpn_id ");
//		sql.append(" 		WHERE r.cpn_id = :companyId  ");
//		sql.append(" 			AND r.rel_startdate >= ADD_MONTHS(TRUNC(:processDate,'MM'), -(:period - 1)) "); 
//		sql.append(" 			AND r.rel_startdate <  TRUNC(ADD_MONTHS(:processDate, 1), 'MM')  ");
//		sql.append(" 		GROUP BY b.cpn_id, ");
//		sql.append(" 			b.branch_id, ");
//		sql.append(" 			b.branch_name, ");
//		sql.append(" 			TO_CHAR(r.rel_startdate, 'MON YYYY') ");
//		sql.append(" 	) b JOIN branch c ON c.cpn_id = b.cpn_id AND c.branch_id = b.branch_id ");
//		sql.append(" ) b  ");
		
		params.add("companyId", SecurityUtils.getCompanyId());
		params.add("processDate", processDate);
		params.add("period", model.getPeriod());
		
		GridData gridData = coreRepository.searchGridData(sql.toString(), params);
		
		for (Map<String, Object> record : gridData.getRecords()) {
			Map<String, Object> allSiteMap = branchMap.get("All Site");

			String  branch = (String) record.get("branchName");
			Map<String, Object> map = branchMap.get(branch);
			if(map != null) {
				String month = (String) record.get("month");
				map.put("month", month);
				allSiteMap.put("month", month);

				String currency = (String) record.get("currency");
				map.put("currency", currency);
				allSiteMap.put("currency", currency);
				
				BigDecimal exchangeRate = (BigDecimal) record.get("exchangeRate");
				map.put("exchangeRate", exchangeRate);
				allSiteMap.put("exchangeRate", exchangeRate);
				
				String monthYear = (String) record.get("monthYear");
				
				BigDecimal noOfTrip = getData((BigDecimal) record.get("noOfTrip"));
				BigDecimal noOfDeclaration = getData((BigDecimal) record.get("noOfDeclaration"));
				
				Map<String, BigDecimal> noOfTripData = (Map<String, BigDecimal>) map.get("noOfTripData");
				Map<String, BigDecimal> noOfDeclarationData = (Map<String, BigDecimal>) map.get("noOfDeclarationData");
				noOfTripData.put(monthYear, noOfTrip);
				noOfDeclarationData.put(monthYear, noOfDeclaration);
				
				Map<String, BigDecimal> noOfTripDataAllSite = (Map<String, BigDecimal>) allSiteMap.get("noOfTripData");
				Map<String, BigDecimal> noOfDeclarationDataAllSite = (Map<String, BigDecimal>) allSiteMap.get("noOfDeclarationData");
				noOfTripDataAllSite.put(monthYear, ((BigDecimal)noOfTripDataAllSite.get(monthYear)).add(noOfTrip));
				noOfDeclarationDataAllSite.put(monthYear, ((BigDecimal)noOfDeclarationDataAllSite.get(monthYear)).add(noOfDeclaration));
				
				map.put("total", ((BigDecimal) map.get("total")).add(noOfTrip).add(noOfDeclaration));
				allSiteMap.put("total", ((BigDecimal) allSiteMap.get("total")).add(noOfTrip).add(noOfDeclaration));
				
				BigDecimal totalAmtCostNoELock = getData((BigDecimal) record.get("totalAmtCostNoELock"));
				BigDecimal totalAmtCostELock = getData((BigDecimal) record.get("totalAmtCostELock"));

				Map<String, BigDecimal> totalAmtCostNoELockData = (Map<String, BigDecimal>) map.get("totalAmtCostNoELockData");
				Map<String, BigDecimal> totalAmtCostELockData = (Map<String, BigDecimal>) map.get("totalAmtCostELockData");
				totalAmtCostNoELockData.put(monthYear, totalAmtCostNoELock);
				totalAmtCostELockData.put(monthYear, totalAmtCostELock);
				
				Map<String, BigDecimal> totalAmtCostNoELockDataAllSite = (Map<String, BigDecimal>) allSiteMap.get("totalAmtCostNoELockData");
				Map<String, BigDecimal> totalAmtCostELockDataAllSite = (Map<String, BigDecimal>) allSiteMap.get("totalAmtCostELockData");
				totalAmtCostNoELockDataAllSite.put(monthYear, totalAmtCostNoELockDataAllSite.get(monthYear).add(totalAmtCostNoELock));
				totalAmtCostELockDataAllSite.put(monthYear, totalAmtCostELockDataAllSite.get(monthYear).add(totalAmtCostELock));
				
				BigDecimal costSavingUsd = totalAmtCostNoELock.subtract(totalAmtCostELock);
				BigDecimal costSavingPercent = totalAmtCostNoELock.subtract(totalAmtCostELock).multiply(new BigDecimal(100)).divide(totalAmtCostNoELock, 0, RoundingMode.HALF_UP);

				Map<String, BigDecimal> percentData = (Map<String, BigDecimal>) map.get("percentData");
				Map<String, BigDecimal> usdData = (Map<String, BigDecimal>) map.get("usdData");
				percentData.put(monthYear, costSavingPercent);
				usdData.put(monthYear, costSavingUsd);
				
				totalAmtCostNoELock = totalAmtCostNoELockDataAllSite.get(monthYear);
				totalAmtCostELock = totalAmtCostELockDataAllSite.get(monthYear);

				costSavingUsd = totalAmtCostNoELock.subtract(totalAmtCostELock);
				costSavingPercent = totalAmtCostNoELock.subtract(totalAmtCostELock).multiply(new BigDecimal(100)).divide(totalAmtCostNoELock, 0, RoundingMode.HALF_UP);
				
				Map<String, BigDecimal> percentDataAllSite = (Map<String, BigDecimal>) allSiteMap.get("percentData");
				Map<String, BigDecimal> usdDataAllSite = (Map<String, BigDecimal>) allSiteMap.get("usdData");
				percentDataAllSite.put(monthYear, costSavingPercent);
				usdDataAllSite.put(monthYear, costSavingUsd);
			}
		}
		
		for (Entry<String, Map<String, Object>> branchEntry : branchMap.entrySet()) {
			Map<String, Object> map = (Map<String, Object>)branchEntry.getValue();
			
			Map<String, BigDecimal> noOfTripData = (Map<String, BigDecimal>) map.get("noOfTripData");
			Map<String, BigDecimal> noOfDeclarationData = (Map<String, BigDecimal>) map.get("noOfDeclarationData");
			Map<String, BigDecimal> percentData = (Map<String, BigDecimal>) map.get("percentData");
			Map<String, BigDecimal> usdData = (Map<String, BigDecimal>) map.get("usdData");

			List<Map<String, Object>> list = new ArrayList<>(1);
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("name", "No of Trip");
			data.put("data", noOfTripData.values());
			list.add(data);
			map.put("noOfTripSeries", list);
			map.remove("noOfTripData");
			
			list = new ArrayList<>(1);
			data = new HashMap<String, Object>();
			data.put("name", "No of Declaration");
			data.put("data", noOfDeclarationData.values());
			list.add(data);
			map.put("noOfDeclarationSeries", list);
			map.remove("noOfDeclarationData");
			
			list = new ArrayList<>(1);
			data = new HashMap<String, Object>();
			data.put("name", "Cost (%)");
			data.put("data", percentData.values());
			list.add(data);
			map.put("percentSeries", list);
			map.remove("percentData");
			
			list = new ArrayList<>(1);
			data = new HashMap<String, Object>();
			data.put("name", "Cost (USD)");
			data.put("data", usdData.values());
			list.add(data);
			map.put("usdSeries", list);
			map.remove("usdData");
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
