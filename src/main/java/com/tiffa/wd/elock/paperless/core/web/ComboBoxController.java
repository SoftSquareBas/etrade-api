package com.tiffa.wd.elock.paperless.core.web;

import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tiffa.wd.elock.paperless.core.ComboBox;
import com.tiffa.wd.elock.paperless.core.ComboBoxRequest;
import com.tiffa.wd.elock.paperless.core.DoctypeComboBox;
import com.tiffa.wd.elock.paperless.core.EmployeeComboBox;
import com.tiffa.wd.elock.paperless.core.service.ComboBoxService;
import com.tiffa.wd.elock.paperless.core.util.Response;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/combobox")
public class ComboBoxController {

	@Autowired
	private ComboBoxService comboBoxService;

	@PostMapping("/warehouse")
	public Callable<Response> searchWarehouse(@RequestBody ComboBox model) {
		return () -> {
			log.debug("searchWarehouse : {}", model);
			return Response.success(comboBoxService.searchWarehouse(model));
		};
	}

	@PostMapping("/doctype")
	public Callable<Response> searchDoctypee(@RequestBody DoctypeComboBox model) {
		return () -> {
			log.debug("searchWarehouse : {}", model);
			return Response.success(comboBoxService.searchDoctypee(model));
		};
	}

	@PostMapping("/document")
	public Callable<Response> searchDocument(@RequestBody DoctypeComboBox model) {
		return () -> {
			log.debug("searchDocument : {}", model);
			return Response.success(comboBoxService.searchDocument(model));
		};
	}

	@PostMapping("/employee")
	public Callable<Response> searchEmployee(@RequestBody EmployeeComboBox model) {
		return () -> {
			log.debug("searchEmployee : {}", model);
			return Response.success(comboBoxService.searchEmployee(model));
		};
	}
}
