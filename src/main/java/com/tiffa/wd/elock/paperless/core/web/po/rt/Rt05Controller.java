package com.tiffa.wd.elock.paperless.core.web.po.rt;

import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;
import com.tiffa.wd.elock.paperless.core.util.Response;
import com.tiffa.wd.elock.paperless.core.Data;
// import com.tiffa.wd.elock.paperless.core.Data;
import com.tiffa.wd.elock.paperless.core.GridData;
import com.tiffa.wd.elock.paperless.core.model.Rt05Model;

import com.tiffa.wd.elock.paperless.core.service.Rt05Service;

@Slf4j
@RestController
@RequestMapping("/rt05")

public class Rt05Controller {

	@Autowired
	private Rt05Service rt05service;

	// @PostMapping("/save")
	// public Callable<Response> save(@RequestBody Rt04Model model) throws Exception {
	// 	return () -> {
	// 		// Data data = rt04service.save(model);
	// 		return  Response.success(rt04service.save(model));
			
	// 	};
	// }
	// @PostMapping("/save")
	// public Callable<Response> save(@RequestBody Rt04Model model) throws Exception {
	// 	return () -> {
	// 		// Data data = rt04service.save(model);
	// 		return  Response.success(rt04service.save(model));
			
	// 	};
	// }
	
	@PostMapping("/save")
	public Callable<Response> save(@RequestBody Rt05Model model) throws Exception {
		return () -> {
			// Data data = rt04service.save(model);
			return  Response.success(rt05service.save(model));
			
		};
	}
	
	@PostMapping("/search")
	public Callable<Response> search(@RequestBody Rt05Model model) {
		return () -> {
			log.info("search model : {}", model);
			GridData gridData = rt05service.search(model);
			return Response.success(gridData);
		};
	}
	@PostMapping("/delete")
	public Callable<Response> delete(@RequestBody Rt05Model model) {
		return () -> {
			log.info("delete model : {}", model);
			return Response.success(rt05service.delete(model));
		};
	}

	@PutMapping("/update")
	public Callable<Response> update(@RequestBody Rt05Model model) {
		return () -> {
			log.info("update model : {}", model);
			return Response.success(rt05service.update(model));
		};
	}

	@PostMapping("/searchDetail")
	public Callable<Response> searchDetail(@RequestBody Rt05Model model) throws Exception {
		return () -> {
			Data data = rt05service.searchDetail(model);
			return  Response.success(data);
			
		};
	}

	@PostMapping("/check")
	public Data check(@RequestBody Rt05Model model) throws Exception {

		Data data = rt05service.check(model);
		System.out.println(model);
		// Data data = rt04service.save(model);
		return data;

	}
}
	





        
				