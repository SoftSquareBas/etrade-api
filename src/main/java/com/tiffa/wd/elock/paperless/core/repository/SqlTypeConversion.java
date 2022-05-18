package com.tiffa.wd.elock.paperless.core.repository;

import java.util.HashMap;
import java.util.Map;

public final class SqlTypeConversion {

	private Map<String, Class<?>> typeConversions;

	private SqlTypeConversion() {
		typeConversions = new HashMap<>();
	}
	
	public static SqlTypeConversion create() {
		return new SqlTypeConversion();
	}
	
	public static <T> SqlTypeConversion create(String name, Class<T> value) {
		return create().add(name, value);
	}
	
	public <T> SqlTypeConversion add(String name, Class<T> value) {
		this.typeConversions.put(name, value);
		return this;
	}

	<T> Map<String, Class<?>> get() {
		return typeConversions;
	}
}
