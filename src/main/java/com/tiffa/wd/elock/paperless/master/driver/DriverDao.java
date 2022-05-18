package com.tiffa.wd.elock.paperless.master.driver;

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
import com.tiffa.wd.elock.paperless.core.entity.Driver;
import com.tiffa.wd.elock.paperless.core.util.CoreUtils;

import lombok.extern.slf4j.Slf4j;
import oracle.jdbc.OracleTypes;

@Slf4j
@Repository
@Deprecated
public class DriverDao {

	private final NamedParameterJdbcTemplate jdbcTemplate;
	
	@Autowired
	public DriverDao(DataSource dataSource) {
		this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	public Integer save(Driver driver) {
		log.debug("save {}", driver);
		
		SimpleJdbcCall call = new SimpleJdbcCall(this.jdbcTemplate.getJdbcTemplate())
			.withFunctionName("pkg_master_srv.save_driver")
			.declareParameters(
				new SqlOutParameter("return", OracleTypes.INTEGER),
				new SqlParameter("companyId", OracleTypes.INTEGER),
				new SqlParameter("branchId", OracleTypes.INTEGER),
				new SqlParameter("driverCode", OracleTypes.VARCHAR),
				new SqlParameter("driverName", OracleTypes.VARCHAR)
			)
			.withoutProcedureColumnMetaDataAccess();
		
		SqlParameterSource sps = new BeanPropertySqlParameterSource(driver);
		return call.executeFunction(Integer.class, sps);
	}
	
	public void update(Driver driver) {
		log.debug("update {}", driver);
		
		SimpleJdbcCall call = new SimpleJdbcCall(this.jdbcTemplate.getJdbcTemplate())
			.withFunctionName("pkg_master_srv.update_driver")
			.declareParameters(
				new SqlOutParameter("return", OracleTypes.INTEGER),
				new SqlParameter("companyId", OracleTypes.INTEGER),
				new SqlParameter("branchId", OracleTypes.INTEGER),
				new SqlParameter("driverId", OracleTypes.INTEGER),
				new SqlParameter("driverName", OracleTypes.VARCHAR)
			)
			.withoutProcedureColumnMetaDataAccess();

		SqlParameterSource sps = new BeanPropertySqlParameterSource(driver);
		call.executeFunction(Integer.class, sps);
	}
	
	public void delete(Driver driver) {
		log.debug("delete {}", driver);
		
		SimpleJdbcCall call = new SimpleJdbcCall(this.jdbcTemplate.getJdbcTemplate())
			.withFunctionName("pkg_master_srv.delete_driver")
			.declareParameters(
				new SqlOutParameter("return", OracleTypes.INTEGER),
				new SqlParameter("companyId", OracleTypes.INTEGER),
				new SqlParameter("branchId", OracleTypes.INTEGER),
				new SqlParameter("driverId", OracleTypes.INTEGER)
			)
			.withoutProcedureColumnMetaDataAccess();

		SqlParameterSource sps = new BeanPropertySqlParameterSource(driver);
		call.executeFunction(Integer.class, sps);
	}
	
	@SuppressWarnings("unchecked")
	public Data validate(Driver driver) {
		log.debug("validate {}", driver);
		
		SimpleJdbcCall call = new SimpleJdbcCall(this.jdbcTemplate.getJdbcTemplate())
			.withFunctionName("pkg_master_srv.validate_driver")
			.declareParameters(
				new SqlOutParameter("return", OracleTypes.CURSOR, new ColumnMapRowMapper()),
				new SqlParameter("companyId", OracleTypes.INTEGER),
				new SqlParameter("branchId", OracleTypes.INTEGER),
				new SqlParameter("driverCode", OracleTypes.VARCHAR)
			)
			.withoutProcedureColumnMetaDataAccess();
		
		SqlParameterSource sps = new BeanPropertySqlParameterSource(driver);
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
