package com.tiffa.wd.elock.paperless.master.weight;

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
@RequestMapping("/api/master/weight")
@PreAuthorize("hasAuthority('WEIGHT_TABVISIBLE')")
public class WeightController {

	@Autowired
	private WeightService weightService;

	@PostMapping("/search")
	public Callable<Response> search(@RequestBody WeightModel model) {
		return () -> {
			log.info("search model : {}", model);
			GridData gridData = weightService.search(model);
			return Response.success(gridData);
		};
	}
	
	@PostMapping("/add")
	@PreAuthorize("hasAuthority('WEIGHT_ADD')")
	public Callable<Response> add(@RequestBody WeightModel model) {
		return () -> {
			log.info("create model : {}", model);
			return Response.success(weightService.add(model));
		};
	}
	
	@PostMapping("/edit")
	@PreAuthorize("hasAuthority('WEIGHT_EDIT')")
	public Callable<Response> edit(@RequestBody WeightModel model) {
		return () -> {
			log.info("edit model : {}", model);
			return Response.success(weightService.edit(model));
		};
	}
	
	@PostMapping("/delete")
	@PreAuthorize("hasAuthority('WEIGHT_DELETE')")
	public Callable<Response> delete(@RequestBody WeightModel model) {
		return () -> {
			log.info("delete model : {}", model);
			weightService.delete(model);
			return Response.success(weightService.search(model));
		};
	}
	
	@PostMapping("/validate")
	@PreAuthorize("hasAnyAuthority('WEIGHT_ADD', 'WEIGHT_EDIT')")
	public Callable<Response> validate(@RequestBody WeightModel model) {
		return () -> {
			log.info("exists model : {}", model);
			return Response.success(weightService.validate(model));
		};
	}

}
