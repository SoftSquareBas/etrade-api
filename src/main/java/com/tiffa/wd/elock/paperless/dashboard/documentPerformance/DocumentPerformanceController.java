package com.tiffa.wd.elock.paperless.dashboard.documentPerformance;

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
@RequestMapping("/api/dashboard/document-performance")
@PreAuthorize("hasAuthority('DOC_PERFORMANCE_DASHBOARD')")
public class DocumentPerformanceController {

	@Autowired
	private DocumentPerformanceService documentPerformanceService;
	
	@PostMapping("/search")
	public Callable<Response> search(@RequestBody DocumentPerformanceModel model) {
		return () -> {
			log.info("search model : {}", model);
			
			CompletableFuture<GridData> totalDeclaration = documentPerformanceService.searchTotalDeclaration();
			CompletableFuture<GridData> historyInformationForDeclaration = documentPerformanceService.searchHistoryInformationForDeclaration(model);
			
			CompletableFuture.allOf(totalDeclaration, historyInformationForDeclaration).join();
			
			Data result = Data.of();
			result.put("serverTime", LocalDateTime.now());
			result.put("totalDeclarationRecords", totalDeclaration.join().getRecords());
			result.put("historyInformationForDeclarationRecords", historyInformationForDeclaration.join().getRecords());
	
			return Response.success(result);
		};
	}
}
