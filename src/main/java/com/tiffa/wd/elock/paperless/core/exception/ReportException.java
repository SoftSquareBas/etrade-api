package com.tiffa.wd.elock.paperless.core.exception;

import lombok.Getter;

@Getter
public class ReportException extends RuntimeException {

	private static final long serialVersionUID = 5017634860143464076L;
	
	private String title;
	private String type;
	private String message;
	
	private ReportException(String type, String title, String message, Throwable throwable) {
		super(throwable);
		this.type = type;
		this.title = title;
		this.message = message;
	}
	
	private ReportException(String type, String title, String message) {
		super();
		this.type = type;
		this.title = title;
		this.message = message;
	}
	
	public static ReportException error(String title, String message, Throwable throwable) {
		return new ReportException("error", title, message, throwable);
	}
	
	public static ReportException error(String title, String message) {
		return new ReportException("error", title, message);
	}
	
	public static ReportException error(String message, Throwable throwable) {
		return error("Error", message, throwable);
	}
	
	public static ReportException error(String message) {
		return error("Error", message);
	}
	
}
