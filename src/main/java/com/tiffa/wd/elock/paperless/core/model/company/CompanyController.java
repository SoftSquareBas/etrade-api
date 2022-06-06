package com.tiffa.wd.elock.paperless.core.model.company;

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
@RequestMapping("/api/master/company")
@PreAuthorize("hasAuthority('COMPANY_SETUP_TABVISIBLE')")
public class CompanyController {

	@Autowired
	private CompanyService companySetupService;

	@PostMapping("/search")
	public Callable<Response> search(@RequestBody CompanyModel model) {
		return () -> {
			log.info("search model : {}", model);
			GridData gridData = companySetupService.search(model);
			return Response.success(gridData);
		};
	}

	@PostMapping("/load")
	public Callable<Response> load(@RequestBody CompanyModel model) {
		return () -> {
			log.info("load model : {}", model);
			return Response.success(companySetupService.load(model));
		};
	}

	@PostMapping("/loadAccessRight")
	public Callable<Response> loadAccessRight(@RequestBody CompanyModel model) {
		return () -> {
			log.info("loadAccessRight model : {}", model);
			return Response.success(companySetupService.loadAccessRight(model));
		};
	}

	@PostMapping("/saveAccessRight")
	public Callable<Response> saveAccessRight(@RequestBody CompanyModel model) {
		return () -> {
			log.info("saveAccessRight model : {}", model);
			return Response.success(companySetupService.saveAccessRight(model));
		};
	}

	@PostMapping("/validate")
	public Callable<Response> validate(@RequestBody CompanyModel model) {
		return () -> {
			log.info("exists model : {}", model);
			return Response.success(companySetupService.validate(model));
		};
	}

	@PostMapping("/add")
	public Callable<Response> add(@RequestBody CompanyModel model) {
		return () -> {
			log.info("create model : {}", model);
			return Response.success(companySetupService.add(model));
		};
	}

	@PostMapping("/edit")
	public Callable<Response> edit(@RequestBody CompanyModel model) {
		return () -> {
			log.info("edit model : {}", model);
			return Response.success(companySetupService.edit(model));
		};
	}

	@PostMapping("/delete")
	public Callable<Response> delete(@RequestBody CompanyModel model) {
		return () -> {
			log.info("delete model : {}", model);
			companySetupService.delete(model);
			return Response.success(companySetupService.search(model));
		};
	}

}
