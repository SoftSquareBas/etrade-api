package com.tiffa.wd.elock.paperless.dashboard.overallPerformance;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tiffa.wd.elock.paperless.core.Data;
import com.tiffa.wd.elock.paperless.core.GridData;
import com.tiffa.wd.elock.paperless.core.util.Response;
import com.tiffa.wd.elock.paperless.dashboard.documentPerformance.DocumentPerformanceService;
import com.tiffa.wd.elock.paperless.dashboard.monthlyCostSaving.MonthlyCostSavingModel;
import com.tiffa.wd.elock.paperless.dashboard.monthlyCostSaving.MonthlyCostSavingService;
import com.tiffa.wd.elock.paperless.dashboard.tripPerformance.TripPerformanceService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/dashboard/overall-performance")
@PreAuthorize("hasAuthority('OVERALL_PERFORMANCE_DASHBOARD')")
public class OverallPerformanceController {

	@Autowired
	private TripPerformanceService tripPerformanceService;
	
	@Autowired
	private DocumentPerformanceService documentPerformanceService;
	
	@Autowired
	private MonthlyCostSavingService monthlyCostSavingService;
	
	@PostMapping("/search")	
	public Callable<Response> search() {
		return () -> {
			log.info("search");
			
			CompletableFuture<GridData> totalTripNoOfEachRoute = tripPerformanceService.searchTotalTripNoOfEachRoute();
			CompletableFuture<GridData> onTimeLateTamper = tripPerformanceService.searchOnTimeLateTamper();

			CompletableFuture<GridData> totalDeclaration = documentPerformanceService.searchTotalDeclaration();
			
			MonthlyCostSavingModel model = new MonthlyCostSavingModel();
			model.setPeriod(1);
			CompletableFuture<GridData> monthlyCostSavingHistory = monthlyCostSavingService.getMonthlyCostSavingHistory(model);
			
			CompletableFuture.allOf(totalTripNoOfEachRoute, onTimeLateTamper, totalDeclaration).join();

			Data result = Data.of();
			result.put("serverTime", LocalDateTime.now());
			result.put("totalTripNoOfEachRouteRecords", totalTripNoOfEachRoute.join().getRecords());
			result.put("onTimeLateTamperRecords", onTimeLateTamper.join().getRecords());
			result.put("totalDeclarationRecords", totalDeclaration.join().getRecords());
			
			List<Map<String, Object>> records = monthlyCostSavingHistory.join().getRecords();
			records.remove(0);
			result.put("monthlyCostSavingHistoryRecords", records);
			
			return Response.success(result);
		};
	}
}
