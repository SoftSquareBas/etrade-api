package com.tiffa.wd.elock.paperless.master.devicePreparation;

import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tiffa.wd.elock.paperless.core.GridData;
import com.tiffa.wd.elock.paperless.core.util.Response;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/master/device-preparation")
@PreAuthorize("hasAuthority('DEVICEPREPARATION_TABVISIBLE')")
public class DevicePreparationController {

	@Autowired
	private DevicePreparationService devicePreparationModel;
	
	@PostMapping("/search")
	public Callable<Response> search(@RequestBody DevicePreparationModel model) {
		return () -> {
			log.info("search model : {}", model);
			GridData gridData = devicePreparationModel.search(model);
			return Response.success(gridData);
		};
	}
	
	@PostMapping("/checkAllDevice")
	public Callable<Response> checkAllDevice(@RequestBody DevicePreparationModel model) {
		return () -> {
			log.info("checkAllDevice model : {}", model);
			GridData gridData = devicePreparationModel.checkAllDevice(model);
			return Response.success(gridData);
		};
	}
}
