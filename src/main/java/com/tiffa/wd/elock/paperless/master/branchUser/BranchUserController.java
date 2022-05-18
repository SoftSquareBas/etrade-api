package com.tiffa.wd.elock.paperless.master.branchUser;

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
@RequestMapping("/api/master/branch-user")
@PreAuthorize("hasAuthority('BRANCH_USER_TABVISIBLE')")
public class BranchUserController {

	@Autowired
	private BranchUserService branchUserService;

	@PostMapping("/search")
	public Callable<Response> search(@RequestBody BranchUserModel model) {
		return () -> {
			log.info("search model : {}", model);
			GridData gridData = branchUserService.search(model);
			return Response.success(gridData);
		};
	}
	
	@PostMapping("/load")
	public Callable<Response> load(@RequestBody BranchUserModel model) {
		return () -> {
			return _load(model);
		};
	}
	
	private Response _load(BranchUserModel model) {
		log.info("load model : {}", model);
		Data data = branchUserService.load(model);
		GridData gridData = branchUserService.loadAccessRight(model);
		return Response.success(gridData, data);
	}

	@PostMapping("/validate")
	public Callable<Response> validate(@RequestBody BranchUserModel model) {
		return () -> {
			log.info("exists model : {}", model);
			return Response.success(branchUserService.validate(model));
		};
	}

	@PostMapping("/add")
	public Callable<Response> add(@RequestBody BranchUserModel model) {
		return () -> {
			log.info("create model : {}", model);
			branchUserService.add(model);
			return _load(model);
		};
	}

	@PostMapping("/edit")
	public Callable<Response> edit(@RequestBody BranchUserModel model) {
		return () -> {
			log.info("edit model : {}", model);
			branchUserService.edit(model);
			return _load(model);
		};
	}

	@PostMapping("/delete")
	public Callable<Response> delete(@RequestBody BranchUserModel model) {
		return () -> {
			log.info("delete model : {}", model);
			branchUserService.delete(model);
			return Response.success(branchUserService.search(model));
		};
	}
}
