package com.tiffa.wd.elock.paperless.master.truck;

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
@RequestMapping("/api/master/truck")
@PreAuthorize("hasAuthority('TRUCK_TABVISIBLE')")
public class TruckController {

	@Autowired
	private TruckService truckService;

	@PostMapping("/search")
	public Callable<Response> search(@RequestBody TruckModel model) {
		return () -> {
			log.info("search model : {}", model);
			GridData gridData = truckService.search(model);
			return Response.success(gridData);
		};
	}
	
	@PostMapping("/add")
	@PreAuthorize("hasAuthority('TRUCK_ADD')")
	public Callable<Response> add(@RequestBody TruckModel model) {
		return () -> {
			log.info("create model : {}", model);
			return Response.success(truckService.add(model));
		};
	}
	
	@PostMapping("/edit")
	@PreAuthorize("hasAuthority('TRUCK_EDIT')")
	public Callable<Response> edit(@RequestBody TruckModel model) {
		return () -> {
			log.info("edit model : {}", model);
			return Response.success(truckService.edit(model));
		};
	}
	
	@PostMapping("/delete")
	@PreAuthorize("hasAuthority('TRUCK_DELETE')")
	public Callable<Response> delete(@RequestBody TruckModel model) {
		return () -> {
			log.info("delete model : {}", model);
			truckService.delete(model);
			return Response.success(truckService.search(model));
		};
	}
	
	@PostMapping("/validate")
	@PreAuthorize("hasAnyAuthority('TRUCK_ADD', 'TRUCK_EDIT')")
	public Callable<Response> validate(@RequestBody TruckModel model) {
		return () -> {
			log.info("exists model : {}", model);
			return Response.success(truckService.validate(model));
		};
	}

}
