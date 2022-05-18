package com.tiffa.wd.elock.paperless.master.branchRole;

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
@RequestMapping("/api/master/branch-role")
@PreAuthorize("hasAuthority('BRANCH_ROLE_TABVISIBLE')")
public class BranchRoleController {

	@Autowired
	private BranchRoleService branchRoleService;

	@PostMapping("/search")
	public Callable<Response> search(@RequestBody BranchRoleModel model) {
		return () -> {
			log.info("search model : {}", model);
			GridData gridData = branchRoleService.search(model);
			return Response.success(gridData);
		};
	}
	
	@PostMapping("/load")
	public Callable<Response> load(@RequestBody BranchRoleModel model) {
		return () -> {
			return _load(model);
		};
	}
	
	private Response _load(BranchRoleModel model) {
		log.info("load model : {}", model);
		Data data = branchRoleService.load(model);
		GridData gridData = branchRoleService.loadAccessRight(model);
		return Response.success(gridData, data);
	}

	@PostMapping("/validate")
	public Callable<Response> validate(@RequestBody BranchRoleModel model) {
		return () -> {
			log.info("exists model : {}", model);
			return Response.success(branchRoleService.validate(model));
		};
	}

	@PostMapping("/add")
	public Callable<Response> add(@RequestBody BranchRoleModel model) {
		return () -> {
			log.info("create model : {}", model);
			branchRoleService.add(model);
			return _load(model);
		};
	}

	@PostMapping("/edit")
	public Callable<Response> edit(@RequestBody BranchRoleModel model) {
		return () -> {
			log.info("edit model : {}", model);
			branchRoleService.edit(model);
			return _load(model);
		};
	}

	@PostMapping("/delete")
	public Callable<Response> delete(@RequestBody BranchRoleModel model) {
		return () -> {
			log.info("delete model : {}", model);
			branchRoleService.delete(model);
			return Response.success(branchRoleService.search(model));
		};
	}
}
