package com.tiffa.wd.elock.paperless.operation.controlTrip;

import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tiffa.wd.elock.paperless.core.GridData;
import com.tiffa.wd.elock.paperless.core.util.Response;
import com.tiffa.wd.elock.paperless.master.devicePreparation.DevicePreparationModel;
import com.tiffa.wd.elock.paperless.master.devicePreparation.DevicePreparationService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/operation/control-trip")
@PreAuthorize("hasAuthority('RELATION3_TABVISIBLE')")
public class ControlTripController {
	
	@Autowired
	private ControlTripService controlTripService;
	
	@Autowired
	private DevicePreparationService devicePreparationService;
	
	
	@PostMapping("/search")
	public Callable<Response> search(@RequestBody ControlTripModel model) {
		return () -> {
			log.info("search model : {}", model);
			GridData gridData = controlTripService.search(model);

			return Response.success(gridData);
		};
	}
	
	@PostMapping("/searchTagSignal")
	public Callable<Response> searchTagSignal(@RequestBody ControlTripModel model) {
		return () -> {
			log.info("search model : {}", model);
			
			DevicePreparationModel m = new DevicePreparationModel();
			m.setCompanyId(model.getCompanyId());
			m.setBranchId(model.getBranchId());
			m.setTagCode(model.getTagCode());
			m.setMaxRow(model.getMaxRow());
			
			GridData gridData = devicePreparationService.search(m);

			return Response.success(gridData);
		};
	}
	
	@PostMapping("/validate")
	public Callable<Response> validate(@RequestBody ControlTripModel model) {
		return () -> {
			log.info("validate model : {}", model);
			return Response.success(controlTripService.validate(model));
		};
	}
	
	@PostMapping("/confirmTrip")
	public Callable<Response> confirmTrip(@RequestBody ControlTripModel model) {
		return () -> {
			log.info("confirmTrip model : {}", model);
			return Response.success(controlTripService.confirmTrip(model));
		};
	}
	
}
