package com.tiffa.wd.elock.paperless.master.location;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.tiffa.wd.elock.paperless.core.Data;
import com.tiffa.wd.elock.paperless.core.GridData;
import com.tiffa.wd.elock.paperless.core.Sort;
import com.tiffa.wd.elock.paperless.core.entity.Location;
import com.tiffa.wd.elock.paperless.core.repository.CoreRepository;
import com.tiffa.wd.elock.paperless.core.repository.LocationRepository;
import com.tiffa.wd.elock.paperless.core.repository.SqlParams;
import com.tiffa.wd.elock.paperless.core.repository.SqlSort;
import com.tiffa.wd.elock.paperless.core.util.CoreUtils;
import com.tiffa.wd.elock.paperless.core.util.ValidationUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true, propagation = Propagation.NEVER)
public class LocationService {

	@Autowired
	private CoreRepository coreRepository;
	
	@Autowired
	private LocationRepository locationRepository;

	public GridData search(final LocationModel model) {
		SqlParams params = SqlParams.createPageParam(model);
		StringBuilder sql = new StringBuilder();

		sql.append(" SELECT a.*");
		sql.append(" FROM ( ");

		sql.append(" SELECT s.station_id AS \"locationId\" ");
		sql.append("    , s.cpn_id AS \"companyId\" ");
		sql.append("    , c.cpn_code AS \"companyCode\" ");
		sql.append("    , s.branch_id AS \"branchId\" ");
		sql.append("    , b.branch_code AS \"branchCode\" ");
		
		sql.append(" 	, s.stn_code AS \"locationCode\" ");
		sql.append(" 	, s.stn_name AS \"locationName\" ");
		sql.append(" 	, s.cust_office_code AS \"customerOfficeCode\" ");
		sql.append(" 	, s.cust_office_name AS \"customerOfficeName\" ");
		sql.append(" 	, s.station_cpn_area_code AS \"stationCompanyAreaCode\" ");
		sql.append(" 	, s.address AS \"address\" ");
		sql.append(" 	, s.telephone AS \"telephone\" ");
		sql.append(" 	, s.fax AS \"fax\" ");
		sql.append(" 	, s.reader1 AS \"reader1\" ");
		sql.append(" 	, s.reader2 AS \"reader2\" ");
		sql.append(" 	, s.reader3 AS \"reader3\" ");
		sql.append(" 	, s.reader4 AS \"reader4\" ");
		sql.append(" 	, s.reader5 AS \"reader5\" ");
		sql.append(" 	, s.cpn_branch AS \"companyBranch\" ");
		sql.append(" FROM station s ");
		sql.append(" 	JOIN company c ON c.cpn_id = s.cpn_id ");
		sql.append(" 	JOIN branch b ON b.branch_id = s.branch_id ");
		sql.append(" WHERE 1 = 1 ");

		if (CoreUtils.isNotNull(model.getCompanyId())) {
			sql.append("    AND s.cpn_id = :companyId ");
			params.add("companyId", model.getCompanyId());
		}
		
		if (CoreUtils.isNotNull(model.getBranchId())) {
			sql.append("    AND s.branch_id = :branchId ");
			params.add("branchId", model.getBranchId());
		}
		
		if (CoreUtils.isNotNull(model.getLocationCode())) {
			sql.append("    AND s.stn_code LIKE :locationCode ");
			params.add("locationCode", "%" + model.getLocationCode() + "%");
		}

		if (CoreUtils.isNotNull(model.getLocationName())) {
			sql.append("    AND s.stn_name LIKE :locationName ");
			params.add("locationName", "%" + model.getLocationName() + "%");
		}
		
		sql.append(" ) a ");
		SqlSort sort = SqlSort.create(model, Sort.by("companyCode", Direction.ASC), Sort.by("branchCode",  Direction.ASC), Sort.by("locationCode", Direction.ASC));
		return coreRepository.searchPagingGridData(sql.toString(), params, sort);
	}
	
	@Transactional
	public Data add(final LocationModel model) {
		ValidationUtils.checkRequired(model.getCompanyId(), "Company Code");
		ValidationUtils.checkRequired(model.getBranchId(), "Branch Code");
		ValidationUtils.checkRequired(model.getLocationCode(), "Location Code");
		ValidationUtils.checkRequired(model.getLocationName(), "Location Name");
		
		model.setValidateModel("locationCode", model.getLocationCode());
		ValidationUtils.checkDuplicate(validate(model), "Location Code");
		
		Location location = new Location();
		location.setCompanyId(model.getCompanyId());
		location.setBranchId(model.getBranchId());
		location.setLocationCode(model.getLocationCode());
		location.setLocationName(model.getLocationName());
		location.setCustomerOfficeCode(model.getCustomerOfficeCode());
		location.setCustomerOfficeName(model.getCustomerOfficeName());
		location.setStationCompanyAreaCode(model.getStationCompanyAreaCode());
		location.setAddress(model.getAddress());
		location.setCompanyBranch(model.getCompanyBranch());
		location.setTelephone(model.getTelephone());
		location.setFax(model.getFax());
		location.setReader1(model.getReader1());
		location.setReader2(model.getReader2());
		location.setReader3(model.getReader3());
		location.setReader4(model.getReader4());
		location.setReader5(model.getReader5());

		Integer locationId = locationRepository.saveAndFlush(location).getLocationId();
		log.debug("Location ID : {}", locationId);

		model.setLocationId(locationId);
		return Data.of(model);
	}
	
	@Transactional
	public Data edit(final LocationModel model) {
		ValidationUtils.checkRequired(model.getLocationName(), "Location Name");
		
		Location location = locationRepository.getById(model.getLocationId());
		location.setLocationName(model.getLocationName());
		location.setCustomerOfficeCode(model.getCustomerOfficeCode());
		location.setCustomerOfficeName(model.getCustomerOfficeName());
		location.setStationCompanyAreaCode(model.getStationCompanyAreaCode());
		location.setAddress(model.getAddress());
		location.setCompanyBranch(model.getCompanyBranch());
		location.setTelephone(model.getTelephone());
		location.setFax(model.getFax());
		location.setReader1(model.getReader1());
		location.setReader2(model.getReader2());
		location.setReader3(model.getReader3());
		location.setReader4(model.getReader4());
		location.setReader5(model.getReader5());
		
		locationRepository.saveAndFlush(location);
		return Data.of(model);
	}

	@Transactional
	public Data delete(final LocationModel model) {
		locationRepository.deleteById(model.getLocationId());
		return Data.of();
	}
	
	public Data validate(final LocationModel model) {
		SqlParams params = SqlParams.createValidateParam(model);
		params.add("companyId", model.getCompanyId());
		params.add("branchId", model.getBranchId());
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT CASE WHEN COUNT(1) > 0 THEN 1 ELSE 0 END AS \"duplicate\" ");
		sql.append(" FROM station s ");
		sql.append(" WHERE s.cpn_id = :companyId ");
		sql.append("    AND s.branch_id = :branchId ");
		sql.append("    AND s.stn_code = :locationCode ");
		
		return coreRepository.validate(sql.toString(), params);	
	}
	
}
