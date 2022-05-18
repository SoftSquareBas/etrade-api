package com.tiffa.wd.elock.paperless.master.uom;

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
@RequestMapping("/api/master/uom")
@PreAuthorize("hasAuthority('UOM_TABVISIBLE')")
public class UomController {

	@Autowired
	private UomService uomService;

	@PostMapping("/search")
	public Callable<Response> search(@RequestBody UomModel model) {
		return () -> {
			log.info("search model : {}", model);
			GridData gridData = uomService.search(model);
			return Response.success(gridData);
		};
	}
	
	@PostMapping("/add")
	@PreAuthorize("hasAuthority('UOM_ADD')")
	public Callable<Response> add(@RequestBody UomModel model) {
		return () -> {
			log.info("create model : {}", model);
			return Response.success(uomService.add(model));
		};
	}
	
	@PostMapping("/edit")
	@PreAuthorize("hasAuthority('UOM_EDIT')")
	public Callable<Response> edit(@RequestBody UomModel model) {
		return () -> {
			log.info("edit model : {}", model);
			return Response.success(uomService.edit(model));
		};
	}
	
	@PostMapping("/delete")
	@PreAuthorize("hasAuthority('UOM_DELETE')")
	public Callable<Response> delete(@RequestBody UomModel model) {
		return () -> {
			log.info("delete model : {}", model);
			uomService.delete(model);
			return Response.success(uomService.search(model));
		};
	}
	
	@PostMapping("/validate")
	@PreAuthorize("hasAnyAuthority('UOM_ADD', 'UOM_EDIT')")
	public Callable<Response> validate(@RequestBody UomModel model) {
		return () -> {
			log.info("exists model : {}", model);
			return Response.success(uomService.validate(model));
		};
	}

}
