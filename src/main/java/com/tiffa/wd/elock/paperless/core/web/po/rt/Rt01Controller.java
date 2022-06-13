package com.tiffa.wd.elock.paperless.core.web.po.rt;

import java.util.concurrent.Callable;

import com.tiffa.wd.elock.paperless.core.Data;
import com.tiffa.wd.elock.paperless.core.GridData;
import com.tiffa.wd.elock.paperless.core.model.Rt01Model;
import com.tiffa.wd.elock.paperless.core.service.Rt01Service;
import com.tiffa.wd.elock.paperless.core.util.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/rt01")

public class Rt01Controller {

	@Autowired
	private Rt01Service rt01Service;

	@PostMapping("/save")
	public Callable<Response> save(@RequestBody Rt01Model model) throws Exception {
		return () -> {
			return Response.success(rt01Service.save(model));

		};
	}

	@PostMapping("/search")
	public Callable<Response> search(@RequestBody Rt01Model model) {
		return () -> {
			log.info("search model : {}", model);
			GridData gridData = rt01Service.search(model);
			return Response.success(gridData);
		};
	}

	@PostMapping("/delete")
	public Callable<Response> delete(@RequestBody Rt01Model model) {
		return () -> {
			log.info("delete model : {}", model);
			return Response.success(rt01Service.delete(model));
		};
	}

	@PostMapping("/searchDetail")
	public Callable<Response> searchDetail(@RequestBody Rt01Model model) throws Exception {
		return () -> {
			Data data = rt01Service.searchDetail(model);
			return Response.success(data);

		};

	}

	@PutMapping("/update")
	public Callable<Response> update(@RequestBody Rt01Model model) {
		return () -> {
			log.info("update model : {}", model);
			return Response.success(rt01Service.update(model));
		};
	}

	@PostMapping("/check")
	public Data check(@RequestBody Rt01Model check) throws Exception {

		Data data = rt01Service.check(check);
		return data;
	}

	@PostMapping("/getarcode")
	public <DdlModel> GridData getarCode() throws Exception {

		return  rt01Service.getarCode();
		
	}

}
