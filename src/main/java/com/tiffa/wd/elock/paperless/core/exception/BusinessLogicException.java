package com.tiffa.wd.elock.paperless.core.exception;

import lombok.Getter;

@Getter
public class BusinessLogicException extends RuntimeException {

	private static final long serialVersionUID = 5017634860143464076L;
	
	private String title;
	private String type;
	private String message;
	
	private BusinessLogicException(String type, String title, String message) {
		this.type = type;
		this.title = title;
		this.message = message;
	}
	
	public static BusinessLogicException error(String title, String message) {
		return new BusinessLogicException("error", title, message);
	}
	
	public static BusinessLogicException error(String message) {
		return error("Error", message);
	}
	
	public static BusinessLogicException warning(String title, String message) {
		return new BusinessLogicException("warning", title, message);
	}

	public static BusinessLogicException warning(String message) {
		return warning("Warning", message);
	}
	
	@Deprecated
	public static BusinessLogicException required() {
		return BusinessLogicException.required("");
	}
	
	public static BusinessLogicException required(String field) {
		return error("Error", String.format("Mandatory field(s) is required! (%s)", field));
	}

	public static BusinessLogicException duplicated(String field) {
		return error("Error", String.format("%s is duplicated!", field));
	}

}
