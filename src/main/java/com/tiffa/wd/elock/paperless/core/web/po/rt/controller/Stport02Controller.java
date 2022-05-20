package com.tiffa.wd.elock.paperless.core.web.po.rt.controller;

import java.util.concurrent.Callable;

import com.tiffa.wd.elock.paperless.core.web.po.rt.model.Stport02Model;
import com.tiffa.wd.elock.paperless.core.web.po.rt.service.Stport02Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;
import com.tiffa.wd.elock.paperless.core.util.Response;
import com.tiffa.wd.elock.paperless.core.GridData;

@Slf4j
@RestController
@RequestMapping("/stport02")

public class Stport02Controller {

	@Autowired
	private Stport02Service stport02service;

	@PostMapping("/save")
	public String save(@RequestBody Stport02Model model) throws Exception {
		return stport02service.save(model);
	}

	@PostMapping("/search")
	public Callable<Response> search(@RequestBody Stport02Model model) {
		return () -> {
			log.info("search model : {}", model);
			GridData gridData = stport02service.search(model);
			return Response.success(gridData);
		};
	}

	@PostMapping("/delete")
	public Callable<Response> delete(@RequestBody Stport02Model model) {
		return () -> {
			log.info("delete model : {}", model);
			return Response.success(stport02service.delete(model));
		};
	}

	@PostMapping("/update")
	public Callable<Response> update(@RequestBody Stport02Model model) {
		return () -> {
			log.info("update model : {}", model);
			return Response.success(stport02service.update(model));
		};
	}

}