package com.tiffa.wd.elock.paperless.core.web;

import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tiffa.wd.elock.paperless.core.ComboBoxRequest;
import com.tiffa.wd.elock.paperless.core.service.ComboBoxService;
import com.tiffa.wd.elock.paperless.core.util.Response;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/combobox")
public class ComboBoxController {

	@Autowired
	private ComboBoxService comboBoxService;
	
	@PostMapping("/station")
	public Callable<Response> searchStation(@RequestBody ComboBoxRequest model) {
		return () -> {
			log.debug("searchStation : {}", model);
			return Response.success(comboBoxService.searchStation(model));
		};
	}
	
	@PostMapping("/uom")
	public Callable<Response> searchUom(@RequestBody ComboBoxRequest model) {
		return () -> {
			log.debug("searchUom : {}", model);
			return Response.success(comboBoxService.searchUom(model));
		};
	}
	
	@PostMapping("/company")
	public Callable<Response> searchCompany(@RequestBody ComboBoxRequest model) {
		return () -> {
			log.debug("searchCompany : {}", model);
			return Response.success(comboBoxService.searchCompany(model));
		};
	}
	
	@PostMapping("/branch")
	public Callable<Response> searchBranch(@RequestBody ComboBoxRequest model) {
		return () -> {
			log.debug("searchBranch : {}", model);
			return Response.success(comboBoxService.searchBranch(model));
		};
	}
	
	@PostMapping("/truck")
	public Callable<Response> searchTruck(@RequestBody ComboBoxRequest model) {
		return () -> {
			log.debug("searchTruck : {}", model);
			return Response.success(comboBoxService.searchTruck(model));
		};
	}
	
	@PostMapping("/driver")
	public Callable<Response> searchDriver(@RequestBody ComboBoxRequest model) {
		return () -> {
			log.debug("searchDriver : {}", model);
			return Response.success(comboBoxService.searchDriver(model));
		};
	}
	
	@PostMapping("/weight")
	public Callable<Response> searchWeight(@RequestBody ComboBoxRequest model) {
		return () -> {
			log.debug("searchWeight : {}", model);
			return Response.success(comboBoxService.searchWeight(model));
		};
	}
	
	@PostMapping("/package")
	public Callable<Response> searchPackage(@RequestBody ComboBoxRequest model) {
		return () -> {
			log.debug("searchPackage : {}", model);
			return Response.success(comboBoxService.searchPackage(model));
		};
	}
	
	@PostMapping("/route")
	public Callable<Response> searchRoute(@RequestBody ComboBoxRequest model) {
		return () -> {
			log.debug("searchRoute : {}", model);
			return Response.success(comboBoxService.searchRoute(model));
		};
	}
}
