package com.tiffa.wd.elock.paperless;

import java.io.IOException;
import java.text.DateFormat;
import java.time.LocalDate;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.DateTimeSerializerBase;
import com.tiffa.wd.elock.paperless.core.util.CoreUtils;

public class LocalDateSerializer extends DateTimeSerializerBase<LocalDate> {

	private static final long serialVersionUID = 2904932375743245558L;

	public LocalDateSerializer() {
        this(null, null);
    }
	
	protected LocalDateSerializer(Boolean useTimestamp, DateFormat customFormat) {
		super(LocalDate.class, useTimestamp, customFormat);
	}

	@Override
	public LocalDateSerializer withFormat(Boolean timestamp, DateFormat customFormat) {
		return new LocalDateSerializer(timestamp, customFormat);
	}

	@Override
	protected long _timestamp(LocalDate value) {
		return (value == null) ? 0L : CoreUtils.toEpochMilli(value);
	}

	@Override
	public void serialize(LocalDate value, JsonGenerator g, SerializerProvider provider) throws IOException {
		if (_asTimestamp(provider)) {
            g.writeNumber(_timestamp(value));
            return;
        }
        _serializeAsString(CoreUtils.toDate(value), g, provider);
	}

}
