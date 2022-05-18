package com.tiffa.wd.elock.paperless.master.product;

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
import com.tiffa.wd.elock.paperless.core.entity.Product;
import com.tiffa.wd.elock.paperless.core.util.CoreUtils;

import lombok.extern.slf4j.Slf4j;
import oracle.jdbc.OracleTypes;

@Slf4j
@Deprecated
@Repository
public class ProductDao {
	private final NamedParameterJdbcTemplate jdbcTemplate;
	
	@Autowired
	public ProductDao(DataSource dataSource) {
		this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	public Integer save(Product product) {
		log.debug("save {}", product);
		
		SimpleJdbcCall call = new SimpleJdbcCall(this.jdbcTemplate.getJdbcTemplate())
			.withFunctionName("pkg_master_srv.save_product")
			.declareParameters(
				new SqlOutParameter("return", OracleTypes.INTEGER),
				new SqlParameter("companyId", OracleTypes.INTEGER),
				new SqlParameter("branchId", OracleTypes.INTEGER),
				new SqlParameter("productCode", OracleTypes.VARCHAR),
				new SqlParameter("productDescriptionEn", OracleTypes.VARCHAR),
				new SqlParameter("productDescriptionTh", OracleTypes.VARCHAR),
				new SqlParameter("productDescriptionTh", OracleTypes.VARCHAR),
				new SqlParameter("uomCode", OracleTypes.VARCHAR)
			)
			.withoutProcedureColumnMetaDataAccess();
		
		SqlParameterSource sps = new BeanPropertySqlParameterSource(product);
		return call.executeFunction(Integer.class, sps);
	}
	
	public void update(Product product) {
		log.debug("update {}", product);
		
		SimpleJdbcCall call = new SimpleJdbcCall(this.jdbcTemplate.getJdbcTemplate())
			.withFunctionName("pkg_master_srv.update_product")
			.declareParameters(
				new SqlOutParameter("return", OracleTypes.INTEGER),
				new SqlParameter("companyId", OracleTypes.INTEGER),
				new SqlParameter("branchId", OracleTypes.INTEGER),
				new SqlParameter("productId", OracleTypes.INTEGER),
				new SqlParameter("productDescriptionEn", OracleTypes.VARCHAR),
				new SqlParameter("productDescriptionTh", OracleTypes.VARCHAR),
				new SqlParameter("uomCode", OracleTypes.VARCHAR)
			)
			.withoutProcedureColumnMetaDataAccess();

		SqlParameterSource sps = new BeanPropertySqlParameterSource(product);
		call.executeFunction(Integer.class, sps);
	}
	
	public void delete(Product product) {
		log.debug("delete {}", product);
		
		SimpleJdbcCall call = new SimpleJdbcCall(this.jdbcTemplate.getJdbcTemplate())
			.withFunctionName("pkg_master_srv.delete_product")
			.declareParameters(
				new SqlOutParameter("return", OracleTypes.INTEGER),
				new SqlParameter("companyId", OracleTypes.INTEGER),
				new SqlParameter("branchId", OracleTypes.INTEGER),
				new SqlParameter("productId", OracleTypes.INTEGER)
			)
			.withoutProcedureColumnMetaDataAccess();

		SqlParameterSource sps = new BeanPropertySqlParameterSource(product);
		call.executeFunction(Integer.class, sps);
	}
	
	@SuppressWarnings("unchecked")
	public Data validate(Product product) {
		log.debug("validate {}", product);
		
		SimpleJdbcCall call = new SimpleJdbcCall(this.jdbcTemplate.getJdbcTemplate())
			.withFunctionName("pkg_master_srv.validate_product")
			.declareParameters(
				new SqlOutParameter("return", OracleTypes.CURSOR, new ColumnMapRowMapper()),
				new SqlParameter("companyId", OracleTypes.INTEGER),
				new SqlParameter("branchId", OracleTypes.INTEGER),
				new SqlParameter("productCode", OracleTypes.VARCHAR)
			)
			.withoutProcedureColumnMetaDataAccess();
		
		SqlParameterSource sps = new BeanPropertySqlParameterSource(product);
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
