package com.tiffa.wd.elock.paperless.master.companyUser;

import java.util.concurrent.Callable;

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
@RequestMapping("/api/master/company-user")
@PreAuthorize("hasAuthority('COMPANY_USER_TABVISIBLE')")
public class CompanyUserController {

	@Autowired
	private CompanyUserService companyUserService;

	@PostMapping("/search")
	public Callable<Response> search(@RequestBody CompanyUserModel model) {
		return () -> {
			log.info("search model : {}", model);
			GridData gridData = companyUserService.search(model);
			return Response.success(gridData);
		};
	}
	
	@PostMapping("/load")
	public Callable<Response> load(@RequestBody CompanyUserModel model) {
		return () -> {
			return _load(model);
		};
	}
	
	private Response _load(CompanyUserModel model) {
		log.info("load model : {}", model);
		Data data = companyUserService.load(model);
		GridData gridData = companyUserService.loadAccessRight(model);
		return Response.success(gridData, data);
	}

	@PostMapping("/validate")
	public Callable<Response> validate(@RequestBody CompanyUserModel model) {
		return () -> {
			log.info("exists model : {}", model);
			return Response.success(companyUserService.validate(model));
		};
	}

	@PostMapping("/add")
	public Callable<Response> add(@RequestBody CompanyUserModel model) {
		return () -> {
			log.info("create model : {}", model);
			companyUserService.add(model);
			return _load(model);
		};
	}

	@PostMapping("/edit")
	public Callable<Response> edit(@RequestBody CompanyUserModel model) {
		return () -> {
			log.info("edit model : {}", model);
			companyUserService.edit(model);
			return _load(model);
		};
	}

	@PostMapping("/delete")
	public Callable<Response> delete(@RequestBody CompanyUserModel model) {
		return () -> {
			log.info("delete model : {}", model);
			companyUserService.delete(model);
			return Response.success(companyUserService.search(model));
		};
	}
}
