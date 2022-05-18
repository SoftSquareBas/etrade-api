package com.tiffa.wd.elock.paperless.master.branchTag;

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
@RequestMapping("/api/master/branch-tag")
@PreAuthorize("hasAuthority('BRANCH_TAG_TABVISIBLE')")
public class BranchTagController {

	private BranchTagService tagService;

	@Autowired
	public BranchTagController(BranchTagService tagService) {
		this.tagService = tagService;
	}

	@PostMapping("/search")
	public Callable<Response> search(@RequestBody BranchTagModel model) {
		return () -> {
			log.info("search model : {}", model);
			GridData gridData = tagService.search(model);
			return Response.success(gridData);
		};
	}
	
}
