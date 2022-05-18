package com.tiffa.wd.elock.paperless.master.companyRole;

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
@RequestMapping("/api/master/company-role")
@PreAuthorize("hasAuthority('COMPANY_ROLE_TABVISIBLE')")
public class CompanyRoleController {

	@Autowired
	private CompanyRoleService companyRoleService;

	@PostMapping("/search")
	public Callable<Response> search(@RequestBody CompanyRoleModel model) {
		return () -> {
			log.info("search model : {}", model);
			GridData gridData = companyRoleService.search(model);
			return Response.success(gridData);
		};
	}
	
	@PostMapping("/load")
	public Callable<Response> load(@RequestBody CompanyRoleModel model) {
		return () -> {
			return _load(model);
		};
	}
	
	private Response _load(CompanyRoleModel model) {
		log.info("load model : {}", model);
		Data data = companyRoleService.load(model);
		GridData gridData = companyRoleService.loadAccessRight(model);
		return Response.success(gridData, data);
	}

	@PostMapping("/validate")
	public Callable<Response> validate(@RequestBody CompanyRoleModel model) {
		return () -> {
			log.info("exists model : {}", model);
			return Response.success(companyRoleService.validate(model));
		};
	}

	@PostMapping("/add")
	public Callable<Response> add(@RequestBody CompanyRoleModel model) {
		return () -> {
			log.info("create model : {}", model);
			companyRoleService.add(model);
			return _load(model);
		};
	}

	@PostMapping("/edit")
	public Callable<Response> edit(@RequestBody CompanyRoleModel model) {
		return () -> {
			log.info("edit model : {}", model);
			companyRoleService.edit(model);
			return _load(model);
		};
	}

	@PostMapping("/delete")
	public Callable<Response> delete(@RequestBody CompanyRoleModel model) {
		return () -> {
			log.info("delete model : {}", model);
			companyRoleService.delete(model);
			return Response.success(companyRoleService.search(model));
		};
	}
}
