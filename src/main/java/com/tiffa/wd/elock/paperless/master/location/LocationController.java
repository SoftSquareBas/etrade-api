package com.tiffa.wd.elock.paperless.master.location;

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
@RequestMapping("/api/master/location")
@PreAuthorize("hasAuthority('LOCATION_TABVISIBLE')")
public class LocationController {

	@Autowired
	private LocationService locationService;

	@PostMapping("/search")
	public Callable<Response> search(@RequestBody LocationModel model) {
		return () -> {
			log.info("search model : {}", model);
			GridData gridData = locationService.search(model);
			return Response.success(gridData);
		};
	}

	@PostMapping("/add")
	@PreAuthorize("hasAuthority('LOCATION_ADD')")
	public Callable<Response> add(@RequestBody LocationModel model) {
		return () -> {
			log.info("create model : {}", model);
			return Response.success(locationService.add(model));
		};
	}
	
	@PostMapping("/edit")
	@PreAuthorize("hasAuthority('LOCATION_EDIT')")
	public Callable<Response> edit(@RequestBody LocationModel model) {
		return () -> {
			log.info("edit model : {}", model);
			return Response.success(locationService.edit(model));
		};
	}
	
	@PostMapping("/delete")
	@PreAuthorize("hasAuthority('LOCATION_DELETE')")
	public Callable<Response> delete(@RequestBody LocationModel model) {
		return () -> {
			log.info("delete model : {}", model);
			return Response.success(locationService.delete(model));
		};
	}
	
	@PostMapping("/validate")
	@PreAuthorize("hasAnyAuthority('LOCATION_ADD', 'LOCATION_EDIT')")
	public Callable<Response> validate(@RequestBody LocationModel model) {
		return () -> {
			log.info("exists model : {}", model);
			return Response.success(locationService.validate(model));
		};
	}

}
