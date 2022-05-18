package com.tiffa.wd.elock.paperless.core.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public final class CoreUtils {
	
	//public static final TimeZone DEFAULT_TIME_ZONE;
	
//	static {
//		DEFAULT_TIME_ZONE = TimeZone.getTimeZone("UTC");
//	}

	@Deprecated
	public static LocalDate parseLocalDate(String text) {
		return LocalDate.parse(trim(text), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
	}
	
	@Deprecated
	public static LocalDateTime parseLocalDateTime(String text) {
		return LocalDateTime.parse(trim(text), DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
	}
	
	public static LocalDate toLocalDate(Date date) {
		return date.toInstant().atZone(TimeZone.getDefault().toZoneId()).toLocalDate();
	}
	
	public static LocalDateTime toLocalDateTime(Date date) {
		return date.toInstant().atZone(TimeZone.getDefault().toZoneId()).toLocalDateTime();
	}
	
	public static Date toDate(LocalDate localDate) {
		Instant instant = localDate.atStartOfDay(TimeZone.getDefault().toZoneId()).toInstant();
		Date date = Date.from(instant);
		return date;
	}
	
	public static long toEpochMilli(LocalDate localDate) {
		Instant instant = localDate.atStartOfDay(TimeZone.getDefault().toZoneId()).toInstant();
		return instant.toEpochMilli();
	}
	
	public static long toEpochMilli(LocalDateTime localDateTime) {
		Instant instant = localDateTime.atZone(TimeZone.getDefault().toZoneId()).toInstant();
		return instant.toEpochMilli();
	}

	public static LocalDateTime atStartOfDay(LocalDate localDate) {
		return localDate.atStartOfDay();
	}

	public static LocalDateTime atEndOfDay(LocalDate localDate) {
		return localDate.atStartOfDay().with(LocalTime.MAX);
	}
	
	public static LocalDateTime atStartOfDay(LocalDateTime localDate) {
		return localDate.with(LocalTime.MIN);
	}

	public static LocalDateTime atEndOfDay(LocalDateTime localDate) {
		return localDate.with(LocalTime.MAX);
	}
	
	public static Date toDate(LocalDateTime localDateTime) {
		Instant instant = localDateTime.atZone(TimeZone.getDefault().toZoneId()).toInstant();
		return Date.from(instant);
	}
	
	public static Date toDate(String text) {
		return Date.from(Instant.parse(text));
	}

	public static boolean isNotEmpty(Map<?, ?> value) {
		return value != null && !value.isEmpty();
	}
	
	public static boolean isNotEmpty(String value) {
		return StringUtils.hasLength(trim(value));
	}
	
	public static boolean isNotEmpty(Object[] value) {
		return value != null && value.length != 0;
	}

	public static boolean isEmpty(String value) {
		return ObjectUtils.isEmpty(trim(value));
	}

	public static String trim(String text) {
		return StringUtils.trimWhitespace(text);
	}

	public static boolean isNotEmpty(Collection<?> collection) {
		return collection != null && !collection.isEmpty();
	}

	public static boolean isEmpty(Collection<?> collection) {
		return collection == null || collection.isEmpty();
	}
	
	public static boolean isNotNull(Object object) {
		return object != null;
	}
	
	public static boolean isNull(Object object) {
		return object == null;
	}

}
