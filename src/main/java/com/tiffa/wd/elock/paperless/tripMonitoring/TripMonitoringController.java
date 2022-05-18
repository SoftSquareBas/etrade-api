package com.tiffa.wd.elock.paperless.tripMonitoring;

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
@RequestMapping("/api/trip-monitoring")
@PreAuthorize("hasAuthority('TAGMONITOR_TABVISIBLE')")
public class TripMonitoringController {
	
	@Autowired
	private TripMonitoringService tripMonitorService;

	@PostMapping("/search")
	public Callable<Response> search(@RequestBody TripMonitoringModel model) {
		return () -> {
			log.info("search model : {}", model);
			CompletableFuture<GridData> gridData = tripMonitorService.searchGrid(model);
			CompletableFuture<Data> data = tripMonitorService.searchSummaryBar(model);
			CompletableFuture.allOf(gridData, data).join();

			return Response.success(gridData.join(), data.join());
		};
	}
	
}
