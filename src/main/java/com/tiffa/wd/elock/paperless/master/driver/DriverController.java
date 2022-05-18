package com.tiffa.wd.elock.paperless.master.driver;

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
@RequestMapping("/api/master/driver")
@PreAuthorize("hasAuthority('DRIVER_TABVISIBLE')")
public class DriverController {

	@Autowired
	private DriverService driverService;

	@PostMapping("/search")
	public Callable<Response> search(@RequestBody DriverModel model) {
		return () -> {
			log.info("search model : {}", model);
			GridData gridData = driverService.search(model);
			return Response.success(gridData);
		};
	}

	@PostMapping("/add")
	@PreAuthorize("hasAuthority('DRIVER_ADD')")
	public Callable<Response> add(@RequestBody DriverModel model) {
		return () -> {
			log.info("create model : {}", model);
			return Response.success(driverService.add(model));
		};
	}
	
	@PostMapping("/edit")
	@PreAuthorize("hasAuthority('DRIVER_EDIT')")
	public Callable<Response> edit(@RequestBody DriverModel model) {
		return () -> {
			log.info("edit model : {}", model);
			return Response.success(driverService.edit(model));
		};
	}
	
	@PostMapping("/delete")
	@PreAuthorize("hasAuthority('DRIVER_DELETE')")
	public Callable<Response> delete(@RequestBody DriverModel model) {
		return () -> {
			log.info("delete model : {}", model);
			return Response.success(driverService.delete(model));
		};
	}
	
	@PostMapping("/validate")
	@PreAuthorize("hasAnyAuthority('DRIVER_ADD', 'DRIVER_EDIT')")
	public Callable<Response> validate(@RequestBody DriverModel model) {
		return () -> {
			log.info("exists model : {}", model);
			return Response.success(driverService.validate(model));
		};
	}

}
