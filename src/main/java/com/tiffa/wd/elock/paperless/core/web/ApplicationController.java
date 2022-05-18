package com.tiffa.wd.elock.paperless.core.web;

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
@RequestMapping("/version")
public class ApplicationController {
	
	@Value("${app.mvn.groupId}")
	private String groupId;
	
	@Value("${app.mvn.artifactId}")
	private String artifactId;
	
	@Value("${app.mvn.version}")
	private String version;
	
	@GetMapping
	public Callable<Response> version() {
		return () -> {
			log.info("version");
			return Response.success(Data.of("artifactId", artifactId).put("groupId", groupId).put("version", version));
		};
	}
}
