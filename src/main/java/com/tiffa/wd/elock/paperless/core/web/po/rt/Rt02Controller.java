package com.tiffa.wd.elock.paperless.core.web.po.rt;

import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;
import com.tiffa.wd.elock.paperless.core.util.Response;
// import com.tiffa.wd.elock.paperless.core.Data;
import com.tiffa.wd.elock.paperless.core.GridData;
import com.tiffa.wd.elock.paperless.core.model.Rt02Model;
import com.tiffa.wd.elock.paperless.core.service.Rt02Service;

@Slf4j
@RestController
@RequestMapping("/rt02")

public class Rt02Controller {

	@Autowired
	private Rt02Service rt02service;

	// @PostMapping("/save")
	// public Callable<Response> save(@RequestBody Stport02Model model) throws Exception {
	// 	return () -> {
	// 		// Data data = stport02service.save(model);
	// 		return  Response.success(stport02service.save(model));
			
	// 	};
	// }
	@PostMapping("/save")
	public Callable<Response> save(@RequestBody Rt02Model model) throws Exception {
		return () -> {
			// Data data = stport02service.save(model);
			return  Response.success(rt02service.save(model));
			
		};
	}
	@PostMapping("/search")
	public Callable<Response> search(@RequestBody Rt02Model model) {
		return () -> {
			log.info("search model : {}", model);
			GridData gridData = rt02service.search(model);
			return Response.success(gridData);
		};
	}


	// @PostMapping("/searchDetail")
	// public Callable<Response> searchDetail(@RequestBody Rt02Model model) throws Exception {
	// 	return () -> {
	// 		Data data = rt02service.searchDetail(model);
	// 		return  Response.success(data);
			
	// 	};
	// }
}
	// @PostMapping("/searchDetail")
	// public Callable<Response> searchDetail(@RequestBody Stport02Model model) {
	// return () -> {
	// log.info("searchDetail model : {}", model);
	// return Response.success(stport02service.searchDetail(model));
	// };
	// }

	// @PostMapping("/delete")
	// public Callable<Response> delete(@RequestBody Stport02Model model) {
	// 	return () -> {
	// 		log.info("delete model : {}", model);
	// 		return Response.success(rt02service.delete(model));
	// 	};
	// }

	// @PutMapping("/update")
	// public Callable<Response> update(@RequestBody Stport02Model model) {
	// 	return () -> {
	// 		log.info("update model : {}", model);
	// 		return Response.success(rt02service.update(model));
	// 	};
	// }





        
				