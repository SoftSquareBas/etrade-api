package com.tiffa.wd.elock.paperless.inspection;

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
@RequestMapping("/api/inspection")
@PreAuthorize("hasAuthority('INSPECT_TABVISIBLE')")
public class InspectionController {

	@Autowired
	private InspectionService inspectionService;

	@PostMapping("/search")
	public Callable<Response> search(@RequestBody InspectionModel model) {
		return () -> {
			log.info("search model : {}", model);
	
			CompletableFuture<GridData> gridData = inspectionService.searchGrid(model);
			CompletableFuture<Data> data = inspectionService.searchSummaryBar(model);
	
			CompletableFuture.allOf(gridData, data).join();
	
			return Response.success(gridData.join(), data.join());
		};
	}

	@PostMapping("/searchItemDetail")
	public Callable<Response> searchItemDetail(@RequestBody SearchItemDetailModel model) {
		return () -> {
			log.info("searchItemDetail model : {}", model);
			CompletableFuture<Data> data = inspectionService.searchItemDetail(model);
			CompletableFuture<String> description = inspectionService.getToDescription(model);
			
			CompletableFuture.allOf(data, description).join();
			
			Data result = data.join();
			result.put("description", description.join());
			
			return Response.success(result);
		};
	}

	@PostMapping("/requestInspect")
	public Callable<Response> requestInspect(@RequestBody InspectionRequestModel model) {
		return () -> {
			log.info("requestInspect model : {}", model);
			return Response.success(inspectionService.requestInspect(model));
		};
	}

	@PostMapping("/saveInspect")
	public Callable<Response> saveInspect(@RequestBody InspectionSaveModel model) {
		return () -> {
			log.info("saveInspect model : {}", model);
			return Response.success(inspectionService.saveInspect(model));
		};
	}

}
