package com.tiffa.wd.elock.paperless.core.web.db;

import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tiffa.wd.elock.paperless.core.Data;
import com.tiffa.wd.elock.paperless.core.util.Response;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/test")
public class TestController {
	
	
	@GetMapping("/service")
	public String version() {
		return "hello";
	}
}
