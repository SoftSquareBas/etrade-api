package com.tiffa.wd.elock.paperless.master.packages;

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
@RequestMapping("/api/master/package")
@PreAuthorize("hasAuthority('PACKAGE_TABVISIBLE')")
public class PackageController {

	@Autowired
	private PackageService packageService;

	@PostMapping("/search")
	public Callable<Response> search(@RequestBody PackageModel model) {
		return () -> {
			log.info("search model : {}", model);
			GridData gridData = packageService.search(model);
			return Response.success(gridData);
		};
	}

	@PostMapping("/add")
	@PreAuthorize("hasAuthority('PACKAGE_ADD')")
	public Callable<Response> add(@RequestBody PackageModel model) {
		return () -> {
			log.info("create model : {}", model);
			return Response.success(packageService.add(model));
		};
	}

	@PostMapping("/edit")
	@PreAuthorize("hasAuthority('PACKAGE_EDIT')")
	public Callable<Response> edit(@RequestBody PackageModel model) {
		return () -> {
			log.info("edit model : {}", model);
			return Response.success(packageService.edit(model));
		};
	}

	@PostMapping("/delete")
	@PreAuthorize("hasAuthority('PACKAGE_DELETE')")
	public Callable<Response> delete(@RequestBody PackageModel model) {
		return () -> {
			log.info("delete model : {}", model);
			packageService.delete(model);
			return Response.success(packageService.search(model));
		};
	}

	@PostMapping("/validate")
	@PreAuthorize("hasAnyAuthority('PACKAGE_ADD', 'PACKAGE_EDIT')")
	public Callable<Response> validate(@RequestBody PackageModel model) {
		return () -> {
			log.info("exists model : {}", model);
			return Response.success(packageService.validate(model));
		};
	}

}
