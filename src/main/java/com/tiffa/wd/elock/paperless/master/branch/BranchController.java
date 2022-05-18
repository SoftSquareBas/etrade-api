package com.tiffa.wd.elock.paperless.master.branch;

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
@RequestMapping("/api/master/branch")
@PreAuthorize("hasAuthority('BRANCH_SETUP_TABVISIBLE')")
public class BranchController {

	@Autowired
	private BranchService branchService;

	@PostMapping("/search")
	public Callable<Response> search(@RequestBody BranchModel model) {
		return () -> {
			log.info("search model : {}", model);
			GridData gridData = branchService.search(model);
			return Response.success(gridData);
		};
	}
	
	@PostMapping("/load")
	public Callable<Response> load(@RequestBody BranchModel model) {
		return () -> {
			log.info("load model : {}", model);
			return Response.success(branchService.load(model));
		};
	}

	@PostMapping("/loadAccessRight")
	public Callable<Response> loadAccessRight(@RequestBody BranchModel model) {
		return () -> {
			log.info("loadAccessRight model : {}", model);
			return Response.success(branchService.loadAccessRight(model));
		};
	}
	
	@PostMapping("/saveAccessRight")
	public Callable<Response> saveAccessRight(@RequestBody BranchModel model) {
		return () -> {
			log.info("saveAccessRight model : {}", model);
			return Response.success(branchService.saveAccessRight(model));
		};
	}
	
	@PostMapping("/validate")
	public Callable<Response> validate(@RequestBody BranchModel model) {
		return () -> {
			log.info("exists model : {}", model);
			return Response.success(branchService.validate(model));
		};
	}

	@PostMapping("/add")
	public Callable<Response> add(@RequestBody BranchModel model) {
		return () -> {
			log.info("create model : {}", model);
			return Response.success(branchService.add(model));
		};
	}

	@PostMapping("/edit")
	public Callable<Response> edit(@RequestBody BranchModel model) {
		return () -> {
			log.info("edit model : {}", model);
			return Response.success(branchService.edit(model));
		};
	}

	@PostMapping("/delete")
	public Callable<Response> delete(@RequestBody BranchModel model) {
		return () -> {
			log.info("delete model : {}", model);
			branchService.delete(model);
			return Response.success(branchService.search(model));
		};
	}

}
