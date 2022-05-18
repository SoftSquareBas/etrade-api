package com.tiffa.wd.elock.paperless.master.packages;

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
import com.tiffa.wd.elock.paperless.core.entity.Package;
import com.tiffa.wd.elock.paperless.core.util.CoreUtils;

import lombok.extern.slf4j.Slf4j;
import oracle.jdbc.OracleTypes;

@Slf4j
@Deprecated
@Repository
public class PackageDao {

	private final NamedParameterJdbcTemplate jdbcTemplate;
	
	@Autowired
	public PackageDao(DataSource dataSource) {
		this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	public Integer save(Package packages) {
		log.debug("save {}", packages);
		
		SimpleJdbcCall call = new SimpleJdbcCall(this.jdbcTemplate.getJdbcTemplate())
			.withFunctionName("pkg_master_srv.save_package")
			.declareParameters(
				new SqlOutParameter("return", OracleTypes.INTEGER),
				new SqlParameter("companyId", OracleTypes.INTEGER),
				new SqlParameter("branchId", OracleTypes.INTEGER),
				new SqlParameter("packageCode", OracleTypes.VARCHAR),
				new SqlParameter("packageName", OracleTypes.VARCHAR)
			)
			.withoutProcedureColumnMetaDataAccess();
		
		SqlParameterSource sps = new BeanPropertySqlParameterSource(packages);
		return call.executeFunction(Integer.class, sps);
	}
	
	public void update(Package packages) {
		log.debug("update {}", packages);
		
		SimpleJdbcCall call = new SimpleJdbcCall(this.jdbcTemplate.getJdbcTemplate())
			.withFunctionName("pkg_master_srv.update_package")
			.declareParameters(
				new SqlOutParameter("return", OracleTypes.INTEGER),
				new SqlParameter("companyId", OracleTypes.INTEGER),
				new SqlParameter("branchId", OracleTypes.INTEGER),
				new SqlParameter("packageId", OracleTypes.INTEGER),
				new SqlParameter("packageName", OracleTypes.VARCHAR)
			)
			.withoutProcedureColumnMetaDataAccess();

		SqlParameterSource sps = new BeanPropertySqlParameterSource(packages);
		call.executeFunction(Integer.class, sps);
	}
	
	public void delete(Package packages) {
		log.debug("delete {}", packages);
		
		SimpleJdbcCall call = new SimpleJdbcCall(this.jdbcTemplate.getJdbcTemplate())
			.withFunctionName("pkg_master_srv.delete_package")
			.declareParameters(
				new SqlOutParameter("return", OracleTypes.INTEGER),
				new SqlParameter("companyId", OracleTypes.INTEGER),
				new SqlParameter("branchId", OracleTypes.INTEGER),
				new SqlParameter("packageId", OracleTypes.INTEGER)
			)
			.withoutProcedureColumnMetaDataAccess();

		SqlParameterSource sps = new BeanPropertySqlParameterSource(packages);
		call.executeFunction(Integer.class, sps);
	}
	
	@SuppressWarnings("unchecked")
	public Data validate(Package packages) {
		log.debug("validate {}", packages);
		
		SimpleJdbcCall call = new SimpleJdbcCall(this.jdbcTemplate.getJdbcTemplate())
			.withFunctionName("pkg_master_srv.validate_package")
			.declareParameters(
				new SqlOutParameter("return", OracleTypes.CURSOR, new ColumnMapRowMapper()),
				new SqlParameter("companyId", OracleTypes.INTEGER),
				new SqlParameter("branchId", OracleTypes.INTEGER),
				new SqlParameter("packageCode", OracleTypes.VARCHAR)
			)
			.withoutProcedureColumnMetaDataAccess();
		
		SqlParameterSource sps = new BeanPropertySqlParameterSource(packages);
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
