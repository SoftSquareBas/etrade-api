package com.tiffa.wd.elock.paperless.demo.master;

import java.util.concurrent.Callable;

import com.tiffa.wd.elock.paperless.core.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tiffa.wd.elock.paperless.core.GridData;
import com.tiffa.wd.elock.paperless.core.util.Response;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/demo/master")
public class MasterController {

	@Autowired
	private MasterService masterService;

	@PostMapping("/add")
	public Callable<Response> add(@RequestBody MasterModel model) throws Exception {
		return () -> {
			return Response.success(masterService.add(model));

		};
	}

	@PostMapping("/search")
	public Callable<Response> search(@RequestBody MasterModel model) {
		return () -> {
			log.info("search model : {}", model);
			GridData gridData = masterService.search(model);
			return Response.success(gridData);
		};
	}

	@PostMapping("/delete")
	public Callable<Response> delete(@RequestBody MasterModel model) {
		return () -> {
			log.info("delete model : {}", model);
			return Response.success(masterService.delete(model));
		};
	}

	@PostMapping("/edit")
	public Callable<Response> edit(@RequestBody MasterModel model) {
		return () -> {
			log.info("edit model : {}", model);
			return Response.success(masterService.edit(model));
		};
	}

	@PostMapping("/searchDetail")
	public Callable<Response> searchDetail(@RequestBody MasterModel model) throws Exception {
		return () -> {
			Data data = masterService.searchDetail(model);
			return Response.success(data);

		};
	}

}
