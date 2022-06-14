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
import com.tiffa.wd.elock.paperless.core.model.Rt04Model;
import com.tiffa.wd.elock.paperless.core.service.Rt04Service;
import com.tiffa.wd.elock.paperless.core.util.Response;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/rt04")

public class Rt04Controller {

	@Autowired
	private Rt04Service rt04Service;

	@PostMapping("/save")
	public Callable<Response> save(@RequestBody Rt04Model model) throws Exception {
		return () -> {
			return Response.success(rt04Service.save(model));

		};
	}

	@PostMapping("/search")
	public Callable<Response> search(@RequestBody Rt04Model model) {
		return () -> {
			log.info("search model : {}", model);
			GridData gridData = rt04Service.search(model);
			return Response.success(gridData);
		};
	}


	@PostMapping("/delete")
	public Callable<Response> delete(@RequestBody Rt04Model model) {
		return () -> {
			log.info("delete model : {}", model);
			System.out.println(model);
			return Response.success(rt04Service.delete(model));
		};
	}

	@PostMapping("/searchDetail")
	public Callable<Response> searchDetail(@RequestBody Rt04Model model) throws Exception {
		return () -> {
			Data data = rt04Service.searchDetail(model);
			return Response.success(data);

		};

	}

	@PutMapping("/update")
	public Callable<Response> update(@RequestBody Rt04Model model) {
		return () -> {
			log.info("update model : {}", model);
			return Response.success(rt04Service.update(model));
		};
	}


	@PostMapping("/check")
	public Data check(@RequestBody Rt04Model check) throws Exception {

		Data data = rt04Service.check(check);
		return data;
	}

	@PostMapping("/getGroupCode")
	public <DdlModel> GridData getGroupCode() throws Exception {

		return  rt04Service.getGroupCode();
		
	}


}
