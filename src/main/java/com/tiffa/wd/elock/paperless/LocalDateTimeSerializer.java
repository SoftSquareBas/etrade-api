package com.tiffa.wd.elock.paperless;

import java.io.IOException;
import java.text.DateFormat;
import java.time.LocalDateTime;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.DateTimeSerializerBase;
import com.tiffa.wd.elock.paperless.core.util.CoreUtils;

public class LocalDateTimeSerializer extends DateTimeSerializerBase<LocalDateTime> {

	private static final long serialVersionUID = 2904932375743245558L;

	public LocalDateTimeSerializer() {
        this(null, null);
    }
	
	protected LocalDateTimeSerializer(Boolean useTimestamp, DateFormat customFormat) {
		super(LocalDateTime.class, useTimestamp, customFormat);
	}

	@Override
	public LocalDateTimeSerializer withFormat(Boolean timestamp, DateFormat customFormat) {
		return new LocalDateTimeSerializer(timestamp, customFormat);
	}

	@Override
	protected long _timestamp(LocalDateTime value) {
		return (value == null) ? 0L : CoreUtils.toEpochMilli(value);
	}

	@Override
	public void serialize(LocalDateTime value, JsonGenerator g, SerializerProvider provider) throws IOException {
		if (_asTimestamp(provider)) {
            g.writeNumber(_timestamp(value));
            return;
        }
        _serializeAsString(CoreUtils.toDate(value), g, provider);
	}

}
