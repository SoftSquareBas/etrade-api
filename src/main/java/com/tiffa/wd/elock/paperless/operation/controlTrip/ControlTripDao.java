package com.tiffa.wd.elock.paperless.operation.controlTrip;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import com.tiffa.wd.elock.paperless.core.Data;
import com.tiffa.wd.elock.paperless.core.util.CoreUtils;
import com.tiffa.wd.elock.paperless.core.util.SecurityUtils;
import com.tiffa.wd.elock.paperless.core.util.TypeConvertionUtils;

import lombok.extern.slf4j.Slf4j;
import oracle.jdbc.OracleTypes;

@Slf4j
@Deprecated
@Repository
public class ControlTripDao {

	private final NamedParameterJdbcTemplate jdbcTemplate;
	
	@Autowired
	public ControlTripDao(DataSource dataSource) {
		this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	@SuppressWarnings("unchecked")
	public Data validateTag(final ControlTripModel model) {
		log.debug("validateTag {}", model);
		
		SimpleJdbcCall call = new SimpleJdbcCall(this.jdbcTemplate.getJdbcTemplate())
			.withFunctionName("pkg_controltrip_srv.validate_tag")
			.declareParameters(
				new SqlOutParameter("return", OracleTypes.CURSOR, new ColumnMapRowMapper()),
				new SqlParameter("companyId", OracleTypes.INTEGER),
				new SqlParameter("branchId", OracleTypes.INTEGER),
				new SqlParameter("tagCode", OracleTypes.VARCHAR)
			)
			.withoutProcedureColumnMetaDataAccess();
		
		SqlParameterSource sps = new BeanPropertySqlParameterSource(model);
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
	
	public Data confirmTrip(final ControlTripModel model) {
		log.debug("save {}", model);
		
		List<SqlParameterSource> params = new ArrayList<>(model.getTripRecords().size());
		for (TripRecord record : model.getTripRecords()) {
			MapSqlParameterSource sps = new MapSqlParameterSource();
			sps.addValue("companyId", record.getCompanyId());
			sps.addValue("branchId", record.getBranchId());
			sps.addValue("tripId", record.getTripId());
			params.add(sps);
		}

		this.jdbcTemplate.batchUpdate(" INSERT INTO temp_controltrip_trip(company_id, branch_id, trip_id) VALUES(:companyId, :branchId, :tripId) ", params.toArray(new MapSqlParameterSource[params.size()]));
		
		List<Map<String, Object>> list = this.jdbcTemplate.queryForList("SELECT company_id, branch_id, trip_id FROM temp_controltrip_trip", new HashMap<>());
		for (Map<String, Object> map : list) {
			log.info("list -> {}", map);
		}
		
		SimpleJdbcCall call = new SimpleJdbcCall(this.jdbcTemplate.getJdbcTemplate())
			.withProcedureName("pkg_controltrip_srv.confirm_trip")
			.declareParameters(
				new SqlParameter("tagCode", OracleTypes.VARCHAR),
				new SqlParameter("truckCode", OracleTypes.VARCHAR),
				new SqlParameter("driverCode", OracleTypes.VARCHAR),
				new SqlParameter("vehicleType", OracleTypes.VARCHAR),
				new SqlParameter("intervalTime", OracleTypes.INTEGER),
				new SqlParameter("lastTruck", OracleTypes.INTEGER),
				new SqlParameter("routeCode", OracleTypes.VARCHAR),
				new SqlParameter("containerNumber", OracleTypes.VARCHAR),
				new SqlParameter("username", OracleTypes.VARCHAR),
				new SqlParameter("timestamp", OracleTypes.TIMESTAMP)
			)
			.withoutProcedureColumnMetaDataAccess();
		
		MapSqlParameterSource sps = new MapSqlParameterSource();
		sps.addValue("tagCode", model.getTagCode());
		sps.addValue("truckCode", model.getTruckCode());
		sps.addValue("driverCode", model.getDriver());
		sps.addValue("vehicleType", model.getVehicleType());
		sps.addValue("intervalTime", model.getIntervalTime());
		
		sps.addValue("lastTruck", TypeConvertionUtils.convert(model.getLastTruck(), Integer.class));
		sps.addValue("routeCode", model.getRoute());
		sps.addValue("containerNumber", model.getContainerNumber());
		sps.addValue("username", SecurityUtils.getUsername());
		sps.addValue("timestamp", LocalDateTime.now());
		
		Map<String, Object> results = call.execute(sps);
		
		log.info("{}", results);
		return Data.nil();
	}
	
}
