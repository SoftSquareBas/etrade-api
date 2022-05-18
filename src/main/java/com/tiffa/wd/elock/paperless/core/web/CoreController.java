package com.tiffa.wd.elock.paperless.core.web;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.tiffa.wd.elock.paperless.core.exception.BusinessLogicException;
import com.tiffa.wd.elock.paperless.core.exception.ReportException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class CoreController extends ResponseEntityExceptionHandler {

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		StringTrimmerEditor stringtrimmer = new StringTrimmerEditor(true);
		binder.registerCustomEditor(String.class, stringtrimmer);
	}

	@ExceptionHandler({ BusinessLogicException.class })
	protected ResponseEntity<Object> handleOther(BusinessLogicException ex, WebRequest request) {
		log.error("BusinessLogicException...");
		HttpHeaders header = new HttpHeaders();
		
		Map<String, Object> body = new HashMap<>();
		body.put("success", false);
		body.put("type", ex.getType());
		body.put("title", ex.getTitle());
		body.put("message", ex.getMessage());
		return handleExceptionInternal(ex, body, header, HttpStatus.OK, request);
	}
	
	@ExceptionHandler({ AccessDeniedException.class })
	protected ResponseEntity<Object> handleOther(AccessDeniedException ex, WebRequest request) {
		log.error("AccessDeniedException...");
		HttpHeaders header = new HttpHeaders();
		
		Map<String, Object> body = new HashMap<>();
		body.put("success", false);
		body.put("type", "error");
		body.put("title", "Error");
		body.put("message", "Access is denied");
		
		StringWriter sw = new StringWriter();
		ex.printStackTrace(new PrintWriter(sw));
		body.put("exception", sw.toString());
		return handleExceptionInternal(ex, body, header, HttpStatus.OK, request);
	}
	
	@ExceptionHandler({ ReportException.class })
	protected ResponseEntity<Object> handleOther(ReportException ex, WebRequest request) {
		log.error("ReportException...");
		HttpHeaders header = new HttpHeaders();
		
		Map<String, Object> body = new HashMap<>();
		body.put("success", false);
		body.put("type", ex.getType());
		body.put("title", ex.getTitle());
		body.put("message", ex.getMessage());
		
		StringWriter sw = new StringWriter();
		ex.printStackTrace(new PrintWriter(sw));
		body.put("exception", sw.toString());
		return handleExceptionInternal(ex, null, header, HttpStatus.INTERNAL_SERVER_ERROR, request);
	}

	@ExceptionHandler({ Exception.class })
	protected ResponseEntity<Object> handleOther(Exception ex, WebRequest request) {
		log.error("Exception...");
		HttpHeaders header = new HttpHeaders();
		
		Map<String, Object> body = new HashMap<>();
		body.put("success", false);
		body.put("type", "error");
		body.put("title", "Error");
		body.put("message", "Please contact admin!");

		StringWriter sw = new StringWriter();
		ex.printStackTrace(new PrintWriter(sw));
		body.put("exception", sw.toString());

		return handleExceptionInternal(ex, body, header, HttpStatus.OK, request);
	}
	
	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		log.error(ex.getMessage(), ex);
		return super.handleExceptionInternal(ex, body, headers, status, request);
	}
}
