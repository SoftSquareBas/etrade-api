package com.tiffa.wd.elock.paperless.core.web.po.rt;

import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tiffa.wd.elock.paperless.core.Data;
import com.tiffa.wd.elock.paperless.core.GridData;
import com.tiffa.wd.elock.paperless.core.model.Rt13Model;
import com.tiffa.wd.elock.paperless.core.service.Rt13Service;
import com.tiffa.wd.elock.paperless.core.util.Response;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/rt13")

public class Rt13Controller {

	@Autowired
	private Rt13Service rt13Service;

	@PostMapping("/save")
	public Callable<Response> save(@RequestBody Rt13Model model) throws Exception {
		return () -> {
			return Response.success(rt13Service.save(model));

		};
	}

	@PostMapping("/search")
	public Callable<Response> search(@RequestBody Rt13Model model) {
		return () -> {
			log.info("search model : {}", model);
			GridData gridData = rt13Service.search(model);
			return Response.success(gridData);
		};
	}

	@PostMapping("/delete")
	public Callable<Response> delete(@RequestBody Rt13Model model) {
		return () -> {
			log.info("delete model : {}", model);
			return Response.success(rt13Service.delete(model));
		};
	}

	@PostMapping("/searchDetail")
	public Callable<Response> searchDetail(@RequestBody Rt13Model model) throws Exception {
		return () -> {
			Data data = rt13Service.searchDetail(model);
			return Response.success(data);

		};

	}

	@PutMapping("/update")
	public Callable<Response> update(@RequestBody Rt13Model model) {
		return () -> {
			log.info("update model : {}", model);
			return Response.success(rt13Service.update(model));
		};
	}

	@PostMapping("/check")
	public Data check(@RequestBody Rt13Model check) throws Exception {

		Data data = rt13Service.check(check);
		return data;
	}



}
