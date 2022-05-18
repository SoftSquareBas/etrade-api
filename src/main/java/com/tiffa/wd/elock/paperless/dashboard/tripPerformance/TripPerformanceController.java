package com.tiffa.wd.elock.paperless.dashboard.tripPerformance;

import java.time.LocalDateTime;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tiffa.wd.elock.paperless.core.Data;
import com.tiffa.wd.elock.paperless.core.GridData;
import com.tiffa.wd.elock.paperless.core.util.Response;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/dashboard/trip-performance")
@PreAuthorize("hasAuthority('TRIP_PERFORMANCE_DASHBOARD')")
public class TripPerformanceController {

	@Autowired
	private TripPerformanceService tripPerformanceService;
	
	@PostMapping("/search")
	public Callable<Response> search(@RequestBody TripPerformanceModel model) {
		return () -> {
			log.info("search model : {}", model);
	
			CompletableFuture<GridData> totalTripNoOfEachRoute = tripPerformanceService.searchTotalTripNoOfEachRoute();
			CompletableFuture<GridData> onTimeLateTamper = tripPerformanceService.searchOnTimeLateTamper();
			CompletableFuture<GridData> historyInformationForNoOfTrip = tripPerformanceService.searchHistoryInformationForNoOfTrip(model);
			CompletableFuture<GridData> historyInformationForTripStatus = tripPerformanceService.searchHistoryInformationForTripStatus(model);
	
			CompletableFuture.allOf(totalTripNoOfEachRoute, onTimeLateTamper, historyInformationForNoOfTrip, historyInformationForTripStatus).join();
			
			Data result = Data.of();
			result.put("serverTime", LocalDateTime.now());
			result.put("totalTripNoOfEachRouteRecords", totalTripNoOfEachRoute.join().getRecords());
			result.put("onTimeLateTamperRecords", onTimeLateTamper.join().getRecords());
			result.put("historyInformationForNoOfTripRecords", historyInformationForNoOfTrip.join().getRecords());
			result.put("historyInformationForTripStatusRecords", historyInformationForTripStatus.join().getRecords());
			
			return Response.success(result);
		};
	}
}
