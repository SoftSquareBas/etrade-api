package com.tiffa.wd.elock.paperless.core.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.TimeZone;

public final class TypeConvertionUtils {

	@SuppressWarnings("unchecked")
	public static <T> T convert(Object value, Class<T> type) {
		Object result = value;
		if (String.class.equals(type)) {
			result = value.toString();
		} else if (Boolean.class.equals(type)) {
			result = decode(value.toString().toLowerCase(), "1", Boolean.TRUE, "true", Boolean.TRUE, "t", Boolean.TRUE,
					"Y", Boolean.TRUE,
					Boolean.FALSE);
		} else if (Integer.class.equals(type)) {
			if (value instanceof Boolean) {
				result = decode((Boolean) value, Boolean.TRUE, 1, 0);
			} else {
				result = Integer.valueOf(value.toString());
			}
		} else if (Long.class.equals(type)) {
			if (value instanceof Boolean) {
				result = decode((Boolean) value, Boolean.TRUE, 1L, 0L);
			} else {
				result = Long.valueOf(value.toString());
			}
		} else if (Float.class.equals(type)) {
			result = Float.valueOf(value.toString());
		} else if (Double.class.equals(type)) {
			result = Double.valueOf(value.toString());
		} else if (BigDecimal.class.equals(type)) {
			result = new BigDecimal(value.toString());
		} else if (BigInteger.class.equals(type)) {
			result = new BigInteger(value.toString());
		} else if (Date.class.equals(type)) {
			if (value instanceof LocalDate) {
				result = CoreUtils.toDate((LocalDate) value);
			} else if (value instanceof LocalDateTime) {
				result = CoreUtils.toDate((LocalDateTime) value);
			} else if (value instanceof java.sql.Date) {
				result = new Date(((java.sql.Date) value).getTime());
			} else {
				result = CoreUtils.toDate(value.toString());
			}
		} else if (java.sql.Date.class.equals(type)) {
			if (value instanceof LocalDate) {
				result = java.sql.Date.valueOf((LocalDate) value);
			} else if (value instanceof LocalDateTime) {
				result = java.sql.Date.valueOf(((LocalDateTime) value).toLocalDate());
			} else if (value instanceof Date) {
				result = new java.sql.Date(((Date) value).getTime());
			} else {
				result = new java.sql.Date(CoreUtils.toDate(value.toString()).getTime());
			}
		} else if (LocalDate.class.equals(type)) {
			if (value instanceof Date) {
				result = CoreUtils.toLocalDate((Date) value);
			} else if (value instanceof java.sql.Date) {
				result = ((java.sql.Date) value).toLocalDate();
			} else if (value instanceof LocalDateTime) {
				result = ((LocalDateTime) value).toLocalDate();
			} else {
				result = Instant.parse(value.toString()).atZone(TimeZone.getDefault().toZoneId()).toLocalDate();
			}
		} else if (LocalDateTime.class.equals(type)) {
			if (value instanceof Date) {
				result = CoreUtils.toLocalDateTime((Date) value);
			} else if (value instanceof java.sql.Date) {
				result = ((java.sql.Timestamp) value).toLocalDateTime();
			} else if (value instanceof LocalDate) {
				result = ((LocalDate) value).atStartOfDay();
			} else {
				result = Instant.parse(value.toString()).atZone(TimeZone.getDefault().toZoneId()).toLocalDateTime();
			}
		}
		return (T) result;
	}

	public static Object decode(Object value, Object param1, Object return1) {
		return decode(value, param1, return1, null, null, null, null, null, null, null);
	}

	public static Object decode(Object value, Object param1, Object return1, Object elseReturn) {
		return decode(value, param1, return1, null, null, null, null, null, null, elseReturn);
	}

	public static Object decode(Object value, Object param1, Object return1, Object param2, Object return2,
			Object elseReturn) {
		return decode(value, param1, return1, param2, return2, null, null, null, null, elseReturn);
	}

	public static Object decode(Object value, Object param1, Object return1, Object param2, Object return2,
			Object param3, Object return3, Object elseReturn) {
		return decode(value, param1, return1, param2, return2, param3, return3, null, null, elseReturn);
	}

	public static Object decode(Object value,
			Object param1, Object return1,
			Object param2, Object return2,
			Object param3, Object return3,
			Object param4, Object return4,
			Object elseReturn) {
		if (value == param1 || value != null && value.equals(param1)) {
			return return1;
		} else if (value == param2 || value != null && value.equals(param2)) {
			return return2;
		} else if (value == param3 || value != null && value.equals(param3)) {
			return return3;
		} else if (value == param4 || value != null && value.equals(param4)) {
			return return4;
		}
		return elseReturn;
	}
}
