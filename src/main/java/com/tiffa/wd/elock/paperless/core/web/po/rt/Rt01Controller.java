package com.tiffa.wd.elock.paperless.core.web.po.rt;

import java.util.concurrent.Callable;

import com.tiffa.wd.elock.paperless.core.GridData;
import com.tiffa.wd.elock.paperless.core.model.Rt01Model;
import com.tiffa.wd.elock.paperless.core.service.Rt01Service;
import com.tiffa.wd.elock.paperless.core.util.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
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
	

	@PostMapping("/search")
	public Callable<Response> search(@RequestBody Rt01Model  model) {
		return () -> {
			log.info("search model : {}", model);
			GridData gridData = rt01Service.search(model);
			return Response.success(gridData);
		};
	}

}
