package com.tiffa.wd.elock.paperless.core.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.tiffa.wd.elock.paperless.core.ComboBoxRequest;
import com.tiffa.wd.elock.paperless.core.PageRequest;
import com.tiffa.wd.elock.paperless.core.ValidateRequest;
import com.tiffa.wd.elock.paperless.core.util.CoreUtils;

import lombok.ToString;

@ToString()
public class SqlParams {

	Map<String, Object> params;

	private SqlParams() {
		params = new HashMap<>();
	}

	public static SqlParams create() {
		return new SqlParams();
	}

	public static SqlParams create(String name, Object value) {
		return new SqlParams().add(name, value);
	}

	public static SqlParams createPageParam(PageRequest model) {
		SqlParams params = new SqlParams();
		if (model.getPageNumber() != null & model.getPageSize() != null) {
			params.params.put("start_", model.getPageNumber() - 1);
			params.params.put("limit_", model.getPageSize());
			params.params.put("offset_", (model.getPageNumber() - 1) * model.getPageSize());
		}
		return params;
	}

	public static SqlParams createComboBoxParam(ComboBoxRequest model) {
		SqlParams params = new SqlParams();
		params.params.put("query", "%" + model.getQuery() + "%");
		params.params.put("from", model.getFrom());
		params.params.put("to", model.getTo());
		return params;
	}

	public static SqlParams createValidateParam(ValidateRequest model) {
		SqlParams params = new SqlParams();
		params.params.put(model.getField(), model.getValue());
		return params;
	}

	public SqlParams add(String name, Date value) {
		this.params.put(name, value);
		return this;
	}

	public SqlParams add(String name, LocalDate value) {
		this.params.put(name, CoreUtils.toDate(value));
		return this;
	}

	public SqlParams add(String name, LocalDateTime value) {
		this.params.put(name, CoreUtils.toDate(value));
		return this;
	}

	public SqlParams add(String name, Object value) {
		this.params.put(name, value);
		return this;
	}

	public SqlParams addIfAbsent(String name, Object value) {
		this.params.putIfAbsent(name, value);
		return this;
	}

	public Object get(String name) {
		return this.params.get(name);
	}

	Map<String, Object> getParams() {
		return this.params;
	}
}
