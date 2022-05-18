package com.tiffa.wd.elock.paperless.operation.generateTrip;

import java.util.concurrent.Callable;

import com.tiffa.wd.elock.paperless.core.ComboBoxRequest;
import com.tiffa.wd.elock.paperless.core.GridData;
import com.tiffa.wd.elock.paperless.core.util.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/operation/generate-trip")
@PreAuthorize("hasAuthority('GENERATE_TRIP_BY_TRUCKNO_QUERY_TABVISIBLE')")
public class GenerateTripController {

	@Autowired
	private GenerateTripService generateTripService;

	@PostMapping("/search")
	public Callable<Response> search(@RequestBody GenerateTripModel model) {
		return () -> {
			log.info("search model : {}", model);
			GridData gridData = generateTripService.search(model);

			return Response.success(gridData);
		};
	}

	@PostMapping("/updateShipmentStatus")
	public Callable<Response> updateShipmentStatus(@RequestBody GenerateTripModel model) {
		return () -> {
			log.info("updateShipmentStatus model : {}", model);
			return Response.success(generateTripService.updateShipmentStatus(model));
		};
	}

	@PostMapping("/delete")
	public Callable<Response> delete(@RequestBody GenerateTripModel model) {
		return () -> {
			log.info("delete model : {}", model);
			return Response.success(generateTripService.delete(model));
		};
	}

	@PostMapping("/searchDeclaration")
	public Callable<Response> searchDeclaration(@RequestBody ComboBoxRequest model) {
		return () -> {
			log.debug("searchDeclaration : {}", model);
			return Response.success(generateTripService.searchDeclaration(model));
		};
	}

	@PostMapping("/searchDeclarationNoByTrip")
	public Callable<Response> searchDeclarationNoByTrip(@RequestBody GenerateTripModel model) {
		return () -> {
			log.debug("searchDeclarationNoByTrip : {}", model);
			return Response.success(generateTripService.searchDeclarationNoByTrip(model));
		};
	}

	@PostMapping("/searchDeclarationDescByTrip")
	public Callable<Response> searchDeclarationDescByTrip(@RequestBody GenerateTripModel model) {
		return () -> {
			log.debug("searchDeclarationDescByTrip : {}", model);
			return Response.success(generateTripService.searchDeclarationDescByTrip(model));
		};
	}

	@PostMapping("/searchInvoice")
	public Callable<Response> searchInvoice(@RequestBody ComboBoxRequest model) {
		return () -> {
			log.debug("searchInvoice : {}", model);
			return Response.success(generateTripService.searchInvoice(model));
		};
	}

	@PostMapping("/getRemainPackage")
	public Callable<Response> getRemainPackage(@RequestBody GenerateTripModel model) {
		return () -> {
			log.debug("getRemainPackage : {}", model);
			return Response.success(generateTripService.getRemainPackage(model));
		};
	}

	@PostMapping("/loadTripHeader")
	public Callable<Response> loadTripHeader(@RequestBody GenerateTripModel model) {
		return () -> {
			log.debug("loadTripHeader : {}", model);
			return Response.success(generateTripService.loadTripHeader(model), generateTripService.loadTrip(model));
		};
	}

	@PostMapping("/loadDetailByManual")
	public Callable<Response> loadDetailByManual(@RequestBody GenerateTripModel model) {
		return () -> {
			log.debug("loadDetailByManual : {}", model);
			return Response.success(generateTripService.loadDetailByManual(model));
		};
	}

	@PostMapping("/loadDetailByInvoice")
	public Callable<Response> loadDetailByInvoice(@RequestBody GenerateTripModel model) {
		return () -> {
			log.debug("loadDetailByInvoice : {}", model);
			return generateTripService.loadDetailByInvoice(model);
		};
	}

	@PostMapping("/saveTripHeader")
	public Callable<Response> saveTripHeader(@RequestBody GenerateTripModel model) {
		return () -> {
			log.debug("saveTripHeader : {}", model);
			return Response.success(generateTripService.saveTripHeader(model));
		};
	}

	@PostMapping("/addAssignItem")
	public Callable<Response> addAssignItem(@RequestBody GenerateTripModel model) {
		return () -> {
			log.debug("addAssignItem : {}", model);
			return Response.success(generateTripService.addAssignItem(model));
		};
	}

	@PostMapping("/deleteAssignItem")
	public Callable<Response> deleteAssignItem(@RequestBody GenerateTripModel model) {
		return () -> {
			log.debug("deleteAssignItem : {}", model);
			return Response.success(generateTripService.deleteAssignItem(model));
		};
	}

	@PostMapping("/addItemManual")
	public Callable<Response> addItemManual(@RequestBody GenerateTripModel model) {
		return () -> {
			log.debug("addItemManual : {}", model);
			return Response.success(generateTripService.addItemManual(model));
		};
	}

}
