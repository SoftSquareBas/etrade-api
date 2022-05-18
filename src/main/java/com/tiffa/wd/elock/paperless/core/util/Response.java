package com.tiffa.wd.elock.paperless.core.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

import com.tiffa.wd.elock.paperless.core.Data;
import com.tiffa.wd.elock.paperless.core.GridData;

public class Response extends ResponseEntity<Map<String, Object>> {

	public Response(HttpStatus status) {
		super(status);
	}

	public Response(Map<String, Object> body, HttpStatus status) {
		super(body, status);
	}

	public Response(Map<String, Object> body, MultiValueMap<String, String> headers, HttpStatus status) {
		super(body, headers, status);
	}

	public static Response success(GridData gridData) {
		return Response.success(gridData, Data.of());
	}
	
	public static Response success(GridData gridData, Data data) {
		Map<String, Object> content = new HashMap<>();
		content.put("records", gridData.getRecords());
		content.put("success", true);
		content.put("total", gridData.getTotal());
		content.put("data", data.getData());
		return new Response(content, HttpStatus.OK);
	}

	public static Response success(Data data) {
		Map<String, Object> content = new HashMap<>();
		content.put("data", data.getData());
		content.put("success", true);
		return new Response(content, HttpStatus.OK);
	}

	public static Response success() {
		Map<String, Object> content = new HashMap<>();
		content.put("success", true);
		return new Response(content, HttpStatus.OK);
	}

	public static Response fail() {
		Map<String, Object> content = new HashMap<>();
		content.put("success", false);
		return new Response(content, HttpStatus.OK);
	}

}
