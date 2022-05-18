package com.tiffa.wd.elock.paperless.master.tag;

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
@RequestMapping("/api/master/tag")
@PreAuthorize("hasAuthority('TAG_TABVISIBLE')")
public class TagController {

	@Autowired
	private TagService tagService;

	@PostMapping("/search")
	public Callable<Response> search(@RequestBody TagModel model) {
		return () -> {
			log.info("search model : {}", model);
			GridData gridData = tagService.search(model);
			return Response.success(gridData);
		};
	}
	
	@PostMapping("/add")
	@PreAuthorize("hasAuthority('TAG_ADD')")
	public Callable<Response> add(@RequestBody TagModel model) {
		return () -> {
			log.info("create model : {}", model);
			return Response.success(tagService.add(model));
		};
	}
	
	@PostMapping("/edit")
	@PreAuthorize("hasAuthority('TAG_EDIT')")
	public Callable<Response> edit(@RequestBody TagModel model) {
		return () -> {
			log.info("edit model : {}", model);
			return Response.success(tagService.edit(model));
		};
	}
	
	@PostMapping("/delete")
	@PreAuthorize("hasAuthority('TAG_DELETE')")
	public Callable<Response> delete(@RequestBody TagModel model) {
		return () -> {
			log.info("delete model : {}", model);
			tagService.delete(model);
			return Response.success(tagService.search(model));
		};
	}
	
	@PostMapping("/validate")
	@PreAuthorize("hasAnyAuthority('TAG_ADD', 'TAG_EDIT')")
	public Callable<Response> validate(@RequestBody TagModel model) {
		return () -> {
			log.info("exists model : {}", model);
			return Response.success(tagService.validate(model));
		};
	}

}
