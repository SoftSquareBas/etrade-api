package com.tiffa.wd.elock.paperless;

import java.io.IOException;
import java.sql.Date;
import java.text.DateFormat;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.DateTimeSerializerBase;

public class SqlDateSerializer extends DateTimeSerializerBase<Date> {

	private static final long serialVersionUID = -5128034642380007759L;

	protected SqlDateSerializer(Boolean useTimestamp, DateFormat customFormat) {
		super(java.sql.Date.class, useTimestamp, customFormat);
	}

	@Override
	public DateTimeSerializerBase<Date> withFormat(Boolean timestamp, DateFormat customFormat) {
		return new SqlDateSerializer(timestamp, customFormat);
	}

	@Override
	protected long _timestamp(Date value) {
		return (value == null) ? 0L : value.getTime();
	}

	@Override
	public void serialize(Date value, JsonGenerator g, SerializerProvider provider) throws IOException {
        if (_asTimestamp(provider)) {
            g.writeNumber(_timestamp(value));
            return;
        }
        // Alas, can't just call `_serializeAsString()`....
        if (_customFormat == null) {
            // 11-Oct-2016, tatu: For backwards-compatibility purposes, we shall just use
            //    the awful standard JDK serialization via `sqlDate.toString()`... this
            //    is problematic in multiple ways (including using arbitrary timezone...)
            g.writeString(value.toString());
            return;
        }
        _serializeAsString(value, g, provider);
    }

}
