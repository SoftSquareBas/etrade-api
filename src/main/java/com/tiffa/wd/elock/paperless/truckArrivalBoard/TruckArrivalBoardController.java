package com.tiffa.wd.elock.paperless.truckArrivalBoard;

import java.util.Date;
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
@RequestMapping("/api/truck-arrival-board")
@PreAuthorize("hasAuthority('TRUCK_ARRIVAL_BOARD')")
public class TruckArrivalBoardController {

	@Autowired
	private TruckArrvialBoardService truckArrivalBoard;

	@PostMapping("/search")
	public Callable<Response> search(@RequestBody TruckArrivalBoardModel model) {
		return () -> {
			log.info("search model : {}", model);
			
			CompletableFuture<GridData> gridData = truckArrivalBoard.searchGrid(model);
			CompletableFuture<Data> summaryData = truckArrivalBoard.searchSummaryBar(model);
			
			CompletableFuture.allOf(gridData, summaryData).join();
			
			Data data = summaryData.join();
			data.put("serverTime", new Date());
			
			return Response.success(gridData.join(), data);
		};
	}
	
	@PostMapping("/more-trip-info")
	public Callable<Response> searchItemDetail(@RequestBody TruckArrivalBoardItemDetailModel model) {
		return () -> {
			log.info("searchItemDetail model : {}", model);
			
			CompletableFuture<Data> data = truckArrivalBoard.searchItemDetail(model);
			CompletableFuture<String> description = truckArrivalBoard.getToDescription(model);
			
			CompletableFuture.allOf(data, description).join();
			
			Data result = data.join();
			result.put("description", description.join());
			
			return Response.success(result);
		};
	}
}
