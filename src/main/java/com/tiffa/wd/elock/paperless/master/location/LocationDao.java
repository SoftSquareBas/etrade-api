package com.tiffa.wd.elock.paperless.master.location;

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
import com.tiffa.wd.elock.paperless.core.entity.Location;
import com.tiffa.wd.elock.paperless.core.util.CoreUtils;

import lombok.extern.slf4j.Slf4j;
import oracle.jdbc.OracleTypes;

@Slf4j
@Deprecated
@Repository
public class LocationDao {

	private final NamedParameterJdbcTemplate jdbcTemplate;
	
	@Autowired
	public LocationDao(DataSource dataSource) {
		this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	public Integer save(Location location) {
		log.debug("save {}", location);
		
		SimpleJdbcCall call = new SimpleJdbcCall(this.jdbcTemplate.getJdbcTemplate())
			.withFunctionName("pkg_master_srv.save_location")
			.declareParameters(
				new SqlOutParameter("return", OracleTypes.INTEGER),
				new SqlParameter("companyId", OracleTypes.INTEGER),
				new SqlParameter("branchId", OracleTypes.INTEGER),
				new SqlParameter("locationCode", OracleTypes.VARCHAR),
				new SqlParameter("locationName", OracleTypes.VARCHAR),
				new SqlParameter("customerOfficeCode", OracleTypes.VARCHAR),
				new SqlParameter("customerOfficeName", OracleTypes.VARCHAR),
				new SqlParameter("stationCompanyAreaCode", OracleTypes.VARCHAR),
				new SqlParameter("address", OracleTypes.VARCHAR),
				new SqlParameter("companyBranch", OracleTypes.VARCHAR),
				new SqlParameter("telephone", OracleTypes.VARCHAR),
				new SqlParameter("fax", OracleTypes.VARCHAR),
				new SqlParameter("reader1", OracleTypes.VARCHAR),
				new SqlParameter("reader2", OracleTypes.VARCHAR),
				new SqlParameter("reader3", OracleTypes.VARCHAR),
				new SqlParameter("reader4", OracleTypes.VARCHAR),
				new SqlParameter("reader5", OracleTypes.VARCHAR)
			)
			.withoutProcedureColumnMetaDataAccess();
		
		SqlParameterSource sps = new BeanPropertySqlParameterSource(location);
		return call.executeFunction(Integer.class, sps);
	}
	
	public void update(Location location) {
		log.debug("update {}", location);
		
		SimpleJdbcCall call = new SimpleJdbcCall(this.jdbcTemplate.getJdbcTemplate())
			.withFunctionName("pkg_master_srv.update_location")
			.declareParameters(
				new SqlOutParameter("return", OracleTypes.INTEGER),
				new SqlParameter("companyId", OracleTypes.INTEGER),
				new SqlParameter("branchId", OracleTypes.INTEGER),
				new SqlParameter("locationId", OracleTypes.INTEGER),
				new SqlParameter("locationName", OracleTypes.VARCHAR),
				new SqlParameter("customerOfficeCode", OracleTypes.VARCHAR),
				new SqlParameter("customerOfficeName", OracleTypes.VARCHAR),
				new SqlParameter("stationCompanyAreaCode", OracleTypes.VARCHAR),
				new SqlParameter("address", OracleTypes.VARCHAR),
				new SqlParameter("companyBranch", OracleTypes.VARCHAR),
				new SqlParameter("telephone", OracleTypes.VARCHAR),
				new SqlParameter("fax", OracleTypes.VARCHAR),
				new SqlParameter("reader1", OracleTypes.VARCHAR),
				new SqlParameter("reader2", OracleTypes.VARCHAR),
				new SqlParameter("reader3", OracleTypes.VARCHAR),
				new SqlParameter("reader4", OracleTypes.VARCHAR),
				new SqlParameter("reader5", OracleTypes.VARCHAR)
			)
			.withoutProcedureColumnMetaDataAccess();

		SqlParameterSource sps = new BeanPropertySqlParameterSource(location);
		call.executeFunction(Integer.class, sps);
	}
	
	public void delete(Location location) {
		log.debug("delete {}", location);
		
		SimpleJdbcCall call = new SimpleJdbcCall(this.jdbcTemplate.getJdbcTemplate())
			.withFunctionName("pkg_master_srv.delete_location")
			.declareParameters(
				new SqlOutParameter("return", OracleTypes.INTEGER),
				new SqlParameter("companyId", OracleTypes.INTEGER),
				new SqlParameter("branchId", OracleTypes.INTEGER),
				new SqlParameter("locationId", OracleTypes.INTEGER)
			)
			.withoutProcedureColumnMetaDataAccess();

		SqlParameterSource sps = new BeanPropertySqlParameterSource(location);
		call.executeFunction(Integer.class, sps);
	}
	
	@SuppressWarnings("unchecked")
	public Data validate(Location location) {
		log.debug("validate {}", location);
		
		SimpleJdbcCall call = new SimpleJdbcCall(this.jdbcTemplate.getJdbcTemplate())
			.withFunctionName("pkg_master_srv.validate_location")
			.declareParameters(
				new SqlOutParameter("return", OracleTypes.CURSOR, new ColumnMapRowMapper()),
				new SqlParameter("companyId", OracleTypes.INTEGER),
				new SqlParameter("branchId", OracleTypes.INTEGER),
				new SqlParameter("locationCode", OracleTypes.VARCHAR)
			)
			.withoutProcedureColumnMetaDataAccess();
		
		SqlParameterSource sps = new BeanPropertySqlParameterSource(location);
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
