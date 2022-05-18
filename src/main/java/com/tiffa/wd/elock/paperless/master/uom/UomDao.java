package com.tiffa.wd.elock.paperless.master.uom;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import com.tiffa.wd.elock.paperless.core.Data;
import com.tiffa.wd.elock.paperless.core.entity.Uom;
import com.tiffa.wd.elock.paperless.core.util.CoreUtils;

import lombok.extern.slf4j.Slf4j;
import oracle.jdbc.OracleTypes;

@Slf4j
@Deprecated
@Repository
public class UomDao {

	private final NamedParameterJdbcTemplate jdbcTemplate;
	
	@Autowired
	public UomDao(DataSource dataSource) {
		this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	public Integer save(Uom uom) {
		log.debug("save {}", uom);
		
		SimpleJdbcCall call = new SimpleJdbcCall(this.jdbcTemplate.getJdbcTemplate())
			.withFunctionName("pkg_master_srv.save_uom")
			.declareParameters(
				new SqlOutParameter("return", OracleTypes.INTEGER),
				new SqlParameter("companyId", OracleTypes.INTEGER),
				new SqlParameter("branchId", OracleTypes.INTEGER),
				new SqlParameter("uomCode", OracleTypes.VARCHAR),
				new SqlParameter("uomName", OracleTypes.VARCHAR),
				new SqlParameter("uomCustomsCode", OracleTypes.VARCHAR)
			)
			.withoutProcedureColumnMetaDataAccess();
		
		SqlParameterSource sps = new BeanPropertySqlParameterSource(uom);
		return call.executeFunction(Integer.class, sps);
	}
	
	public void update(Uom uom) {
		log.debug("update {}", uom);
		
		SimpleJdbcCall call = new SimpleJdbcCall(this.jdbcTemplate.getJdbcTemplate())
			.withFunctionName("pkg_master_srv.update_uom")
			.declareParameters(
				new SqlOutParameter("return", OracleTypes.INTEGER),
				new SqlParameter("companyId", OracleTypes.INTEGER),
				new SqlParameter("branchId", OracleTypes.INTEGER),
				new SqlParameter("uomId", OracleTypes.INTEGER),
				new SqlParameter("uomName", OracleTypes.VARCHAR),
				new SqlParameter("uomCustomsCode", OracleTypes.VARCHAR)
			)
			.withoutProcedureColumnMetaDataAccess();

		SqlParameterSource sps = new BeanPropertySqlParameterSource(uom);
		call.executeFunction(Integer.class, sps);
	}
	
	public void delete(Uom uom) {
		log.debug("delete {}", uom);
		
		SimpleJdbcCall call = new SimpleJdbcCall(this.jdbcTemplate.getJdbcTemplate())
			.withFunctionName("pkg_master_srv.delete_uom")
			.declareParameters(
				new SqlOutParameter("return", OracleTypes.INTEGER),
				new SqlParameter("companyId", OracleTypes.INTEGER),
				new SqlParameter("branchId", OracleTypes.INTEGER),
				new SqlParameter("uomId", OracleTypes.INTEGER)
			)
			.withoutProcedureColumnMetaDataAccess();

		SqlParameterSource sps = new BeanPropertySqlParameterSource(uom);
		call.executeFunction(Integer.class, sps);
	}
	
	@SuppressWarnings("unchecked")
	public Data validate(Uom uom) {
		log.debug("validate {}", uom);
		
		SimpleJdbcCall call = new SimpleJdbcCall(this.jdbcTemplate.getJdbcTemplate())
			.withFunctionName("pkg_master_srv.validate_uom")
			.declareParameters(
				new SqlOutParameter("return", OracleTypes.CURSOR, new ColumnMapRowMapper()),
				new SqlParameter("companyId", OracleTypes.INTEGER),
				new SqlParameter("branchId", OracleTypes.INTEGER),
				new SqlParameter("uomCode", OracleTypes.VARCHAR)
			)
			.withoutProcedureColumnMetaDataAccess();
		
		SqlParameterSource sps = new BeanPropertySqlParameterSource(uom);
		List<Map<String, Object>> results = call.executeFunction(List.class, sps);
		
		if(CoreUtils.isNotEmpty(results)) {
			Map<String, Object> result = results.get(0);

			Data data = Data.of();
			for (Map.Entry<String, Object> entry : result.entrySet()) {
				if(entry.getValue() instanceof BigDecimal && ((BigDecimal)entry.getValue()).compareTo(BigDecimal.ZERO) > 0) {
					data.put(entry.getKey(), Boolean.TRUE);
					return data;
				}
			}
		}
		return Data.nil();
	}
	
}
