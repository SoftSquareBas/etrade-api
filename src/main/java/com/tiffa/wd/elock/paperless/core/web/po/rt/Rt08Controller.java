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
import com.tiffa.wd.elock.paperless.core.model.Rt08Model;
import com.tiffa.wd.elock.paperless.core.service.Rt08Service;
import com.tiffa.wd.elock.paperless.core.util.Response;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/rt08")

public class Rt08Controller {

	@Autowired
	private Rt08Service rt08Service;

	@PostMapping("/save")
	public Callable<Response> save(@RequestBody Rt08Model model) throws Exception {
		return () -> {
			return Response.success(rt08Service.save(model));

		};
	}

	@PostMapping("/search")
	public Callable<Response> search(@RequestBody Rt08Model model) {
		return () -> {
			log.info("search model : {}", model);
			GridData gridData = rt08Service.search(model);
			return Response.success(gridData);
		};
	}

	@PostMapping("/delete")
	public Callable<Response> delete(@RequestBody Rt08Model model) {
		return () -> {
			log.info("delete model : {}", model);
			return Response.success(rt08Service.delete(model));
		};
	}

	@PostMapping("/searchDetail")
	public Callable<Response> searchDetail(@RequestBody Rt08Model model) throws Exception {
		return () -> {
			Data data = rt08Service.searchDetail(model);
			return Response.success(data);

		};

	}

	@PutMapping("/update")
	public Callable<Response> update(@RequestBody Rt08Model model) {
		return () -> {
			log.info("update model : {}", model);
			return Response.success(rt08Service.update(model));
		};
	}

	@PostMapping("/check")
	public Data check(@RequestBody Rt08Model check) throws Exception {

		Data data = rt08Service.check(check);
		return data;
	}

	@PostMapping("/getUms")
	public <DdlModel> GridData getUms() throws Exception {

		return  rt08Service.getUms();
		
	}

}
