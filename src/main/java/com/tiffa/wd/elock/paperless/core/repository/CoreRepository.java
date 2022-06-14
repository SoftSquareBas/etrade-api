package com.tiffa.wd.elock.paperless.core.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.tiffa.wd.elock.paperless.core.Data;
import com.tiffa.wd.elock.paperless.core.GridData;
import com.tiffa.wd.elock.paperless.core.util.CoreUtils;
import com.tiffa.wd.elock.paperless.core.util.TypeConvertionUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class CoreRepository {

	private final NamedParameterJdbcTemplate jdbcTemplate;

	public static final String SQL_PAGING_NAME = "sql_paging__";
	public static final String PAGING_CLAUSE_NAME = "pagin_clause__";
	public static final String ROWNUM_COLUMN = "\"rowNo\"";

	private static final String MAXROW_COLUMN = "max_row_";

	public static final String SQL_NAME = "sql__";
	public static final String CLAUSE_NAME = "clause__";

	@Autowired
	public CoreRepository(DataSource dataSource) {
		this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	public GridData searchPagingGridData(String pagingSql, SqlParams params, SqlSort sort) {
		return searchPagingGridData(pagingSql, null, params, sort, null, null);
	}

	public GridData searchPagingGridData(String pagingSql, SqlParams params, SqlSort sort,
			SqlTypeConversion typeConversion) {
		return searchPagingGridData(pagingSql, null, params, sort, null, typeConversion);
	}

	public GridData searchPagingGridData(String pagingSql, SqlParams params, SqlSort sort,
			Map<String, String> withClauses) {
		return searchPagingGridData(pagingSql, null, params, sort, withClauses, null);
	}

	public GridData searchPagingGridData(String pagingSql, String outputSql, SqlParams params, SqlSort sort) {
		return searchPagingGridData(pagingSql, outputSql, params, sort, null, null);
	}

	public GridData searchPagingGridData(String pagingSql, String outputSql, SqlParams params, SqlSort sort,
			Map<String, String> withClauses) {
		return searchPagingGridData(pagingSql, outputSql, params, sort, withClauses, null);
	}

	public <T> GridData searchPagingGridData(String pagingSql, String outputSql, SqlParams params, SqlSort sort,
			Map<String, String> withClauses, SqlTypeConversion typeConvertion) {
		log.debug("searchPagingGridData params:{}", params);

		StringBuilder statement = new StringBuilder();
		statement.append("WITH ").append(SQL_PAGING_NAME).append(" AS (").append(pagingSql).append("), ");
		statement.append(" temp_count__ AS ( SELECT COUNT(1) AS \"").append(MAXROW_COLUMN).append("\" FROM ")
				.append(SQL_PAGING_NAME).append(" ), ");
		statement.append(PAGING_CLAUSE_NAME).append(" AS ( ");
		statement.append("		SELECT y.* ");
		statement.append("		FROM (");
		statement.append("  	  SELECT x.*, ROW_NUMBER() OVER (").append(sort.generate("x")).append(") AS ")
				.append(ROWNUM_COLUMN);
		statement.append("		  FROM (");
		statement.append("    	    SELECT * FROM ").append(SQL_PAGING_NAME).append(", temp_count__ ");
		statement.append("  	  ) x ");
		statement.append("		) y ");
		statement.append(" WHERE y.").append(ROWNUM_COLUMN).append(" >= :offset_ + 1");
		statement.append(" AND y.").append(ROWNUM_COLUMN).append(" < :offset_ +:limit_ + 1 ");
		statement.append(" ) ");

		if (CoreUtils.isNotEmpty(withClauses)) {
			for (Map.Entry<String, String> clause : withClauses.entrySet()) {
				statement.append(",").append(clause.getKey()).append(" AS (").append(clause.getValue()).append(") ");
			}
		}

		if (CoreUtils.isNotEmpty(outputSql)) {
			statement.append(outputSql);
		} else {
			statement.append(" SELECT * FROM ").append(PAGING_CLAUSE_NAME).append(" ORDER BY ").append(ROWNUM_COLUMN)
					.append(" ASC ");
		}

		return queryPagingGridData(statement.toString(), params, typeConvertion);
	}

	private <T> GridData queryPagingGridData(String sql, SqlParams params, SqlTypeConversion typeConvertions) {
		// params.addIfAbsent("companyId", SecurityUtils.getCompanyId());

		List<Map<String, Object>> result = this.jdbcTemplate.queryForList(sql, params.getParams());

		long total = 0;
		if (result != null && !result.isEmpty()) {
			total = Long.valueOf(String.valueOf(result.get(0).get(MAXROW_COLUMN)));

			if (CoreUtils.isNotNull(typeConvertions)) {
				for (Map<String, Object> map : result) {
					map.remove(MAXROW_COLUMN);
					convertType(map, typeConvertions);
				}
			} else {
				for (Map<String, Object> map : result) {
					map.remove(MAXROW_COLUMN);
				}
			}
		}

		return GridData.of(result, total);
	}

	private <T> void convertType(Map<String, Object> map, SqlTypeConversion typeConvertions) {
		for (Map.Entry<String, Class<?>> convertion : typeConvertions.get().entrySet()) {
			String key = convertion.getKey();
			Class<?> type = convertion.getValue();
			Object value = map.get(key);
			if (CoreUtils.isNotNull(value)) {
				map.put(key, TypeConvertionUtils.convert(value, type));
			}
		}
	}

	public <T> GridData searchGridData(String sql, SqlParams params) {
		return searchGridData(sql, params, (SqlTypeConversion) null);
	}

	public <T> GridData searchGridData(String sql, SqlParams params, SqlSort sort) {
		return searchGridData(sql, params, sort, (SqlTypeConversion) null);
	}

	public <T> GridData searchGridData(String sql, SqlParams params, SqlSort sort, SqlTypeConversion typeConvertions) {
		StringBuilder statement = new StringBuilder();
		statement.append("WITH ").append(SQL_NAME).append(" AS (").append(sql).append("), ");
		statement.append(CLAUSE_NAME).append(" AS ( ");
		statement.append("		SELECT x.*, ROW_NUMBER() OVER (").append(sort.generate("x")).append(") AS ")
				.append(ROWNUM_COLUMN);
		statement.append(" 		FROM ").append(SQL_NAME).append(" x ");
		statement.append(" ) ");
		statement.append(" SELECT * FROM ").append(CLAUSE_NAME).append(" ORDER BY ").append(ROWNUM_COLUMN)
				.append(" ASC ");

		return searchGridData(statement.toString(), params, typeConvertions);
	}

	public <T> GridData searchGridData(String sql, SqlParams params, SqlTypeConversion typeConvertions) {
		log.debug("searchGridData params:{}", params);
		// params.addIfAbsent("companyId", SecurityUtils.getCompanyId());

		List<Map<String, Object>> result = this.jdbcTemplate.queryForList(sql.toString(), params.getParams());

		if (CoreUtils.isNotNull(typeConvertions)) {
			for (Map<String, Object> map : result) {
				convertType(map, typeConvertions);
			}
		}

		return GridData.of(result, result.size());
	}

	public Data getData(String sql, SqlParams params, SqlTypeConversion typeConversions) {
		log.debug("getData params:{}", params);
		// params.addIfAbsent("companyId", SecurityUtils.getCompanyId());

		Map<String, Object> data = this.jdbcTemplate.queryForMap(sql, params.getParams());

		if (CoreUtils.isNotNull(typeConversions)) {
			convertType(data, typeConversions);
		}

		return Data.of(data);
	}

	public Data getData(String sql, SqlParams params) {
		return getData(sql, params, null);
	}

	public Data validate(String sql, SqlParams params) {
		log.debug("validate params:{}", params);

		Map<String, Object> result = this.jdbcTemplate.queryForMap(sql, params.getParams());

		Data data = Data.of();
		for (Map.Entry<String, Object> entry : result.entrySet()) {
			if (entry.getValue() instanceof BigDecimal
					&& ((BigDecimal) entry.getValue()).compareTo(BigDecimal.ZERO) > 0) {
				data.put(entry.getKey(), Boolean.TRUE);
				return data;
			}
		}
		return Data.nil();
	}

	public Integer update(String sql, SqlParams params) {
		log.debug("update params:{}", params);

		return this.jdbcTemplate.update(sql.toString(), params.getParams());
	}

}
