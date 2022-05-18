package com.tiffa.wd.elock.paperless.dashboard.monthlyCostSaving;

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
@RequestMapping("/api/dashboard/monthly-cost-saving")
@PreAuthorize("hasAuthority('MONTHLY_COST_SAVING_DASHBOARD')")
public class MonthlyCostSavingController {

	@Autowired
	private MonthlyCostSavingService monthlyCostSavingService;
	
	@PostMapping("/search")
	public Callable<Response> search(@RequestBody MonthlyCostSavingModel model) {
		return () -> {
			log.info("search model : {}", model);
			CompletableFuture<GridData> monthlyCostSavingHistory = monthlyCostSavingService.getMonthlyCostSavingHistory(model);
	
			Data result = Data.of();
			result.put("serverTime", LocalDateTime.now());
			result.put("monthlyCostSavingHistoryRecords", monthlyCostSavingHistory.join().getRecords());
			
			log.info("{}", result);
			return Response.success(result);
		};
	}
}
