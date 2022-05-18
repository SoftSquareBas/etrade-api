package com.tiffa.wd.elock.paperless;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.tiffa.wd.elock.paperless.core.util.CoreUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ObjectMapperCustomModule extends SimpleModule {

	private static final long serialVersionUID = 2585301134604057846L;

	public ObjectMapperCustomModule() {
		log.debug("ObjectMapperCustomModule");
		
		addDeserializer(String.class, new StdScalarDeserializer<String>(String.class) {
			private static final long serialVersionUID = -3682669850924589396L;

			@Override
			public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
				return CoreUtils.trim(p.getValueAsString());
			}
		});

		addDeserializer(LocalDate.class, new StdScalarDeserializer<LocalDate>(LocalDate.class) {
			private static final long serialVersionUID = 1933341266195348729L;

			@Override
			public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
				try {
					String value = CoreUtils.trim(p.getValueAsString());
					LocalDate localDate = CoreUtils.isNotEmpty(value) ? Instant.parse(value).atZone(TimeZone.getDefault().toZoneId()).toLocalDate() : null;
					return localDate;
				} catch (DateTimeParseException e) {
					log.error(e.getMessage(), e);
					return null;
				}
			}
		});

		addDeserializer(LocalDateTime.class, new StdScalarDeserializer<LocalDateTime>(LocalDateTime.class) {
			private static final long serialVersionUID = -4476815913777202745L;

			@Override
			public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
				try {
					String value = CoreUtils.trim(p.getValueAsString());
					LocalDateTime localDateTime = CoreUtils.isNotEmpty(value) ? Instant.parse(value).atZone(TimeZone.getDefault().toZoneId()).toLocalDateTime() : null;
					return localDateTime;
				} catch (DateTimeParseException e) {
					log.error(e.getMessage(), e);
					return null;
				}
			}
		});
		
		addDeserializer(Date.class, new StdScalarDeserializer<Date>(Date.class) {
			private static final long serialVersionUID = -4476815913777202745L;

			@Override
			public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
				try {
					String value = CoreUtils.trim(p.getValueAsString());
					return CoreUtils.isNotEmpty(value) ? Date.from(Instant.parse(value)) : null;
				} catch (DateTimeParseException e) {
					log.error(e.getMessage(), e);
					return null;
				}
			}
		});
		
		addSerializer(Date.class, new DateSerializer(Boolean.FALSE, getDateFormat()));
		addSerializer(java.sql.Date.class, new SqlDateSerializer(Boolean.FALSE, getDateFormat()));
		addSerializer(java.sql.Timestamp.class, new SqlTimestampSerializer(Boolean.FALSE, getDateFormat()));
		addSerializer(LocalDate.class, new LocalDateSerializer(Boolean.FALSE, getDateFormat()));
		addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(Boolean.FALSE, getDateFormat()));
	}

	private DateFormat getDateFormat() {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
		return df;
	}

}
