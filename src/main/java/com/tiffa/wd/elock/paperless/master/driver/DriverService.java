package com.tiffa.wd.elock.paperless.master.driver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.tiffa.wd.elock.paperless.core.Data;
import com.tiffa.wd.elock.paperless.core.GridData;
import com.tiffa.wd.elock.paperless.core.Sort;
import com.tiffa.wd.elock.paperless.core.entity.Driver;
import com.tiffa.wd.elock.paperless.core.repository.CoreRepository;
import com.tiffa.wd.elock.paperless.core.repository.DriverRepository;
import com.tiffa.wd.elock.paperless.core.repository.SqlParams;
import com.tiffa.wd.elock.paperless.core.repository.SqlSort;
import com.tiffa.wd.elock.paperless.core.util.CoreUtils;
import com.tiffa.wd.elock.paperless.core.util.ValidationUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true, propagation = Propagation.NEVER)
public class DriverService {

	@Autowired
	private CoreRepository coreRepository;
	
	@Autowired
	private DriverRepository driverRepository;

	public GridData search(final DriverModel model) {
		SqlParams params = SqlParams.createPageParam(model);
		StringBuilder sql = new StringBuilder();

		sql.append("   SELECT d.driver_id AS \"driverId\" ");
		sql.append("     , d.cpn_id AS \"companyId\" ");
		sql.append("     , c.cpn_code AS \"companyCode\" ");
		sql.append("     , d.branch_id AS \"branchId\" ");
		sql.append("     , b.branch_code AS \"branchCode\" ");
		sql.append("     , d.drv_code AS \"driverCode\" ");
		sql.append("     , d.drv_name AS \"driverName\" ");
		sql.append("   FROM driver d ");
		sql.append("     JOIN company c ON c.cpn_id = d.cpn_id ");
		sql.append("     JOIN branch b ON b.branch_id = d.branch_id ");
		sql.append("   WHERE 1 = 1 ");

		if (CoreUtils.isNotNull(model.getCompanyId())) {
			sql.append("    AND d.cpn_id = :companyId ");
			params.add("companyId", model.getCompanyId());
		}
		
		if (CoreUtils.isNotNull(model.getBranchId())) {
			sql.append("    AND d.branch_id = :branchId ");
			params.add("branchId", model.getBranchId());
		}
		
		if (CoreUtils.isNotNull(model.getDriverCode())) {
			sql.append("    AND d.drv_code LIKE :driverCode ");
			params.add("driverCode", "%" + model.getDriverCode() + "%");
		}

		if (CoreUtils.isNotNull(model.getDriverName())) {
			sql.append("    AND d.drv_name LIKE :driverName ");
			params.add("driverName", "%" + model.getDriverName() + "%");
		}
		
		SqlSort sort = SqlSort.create(model, Sort.by("companyCode", Direction.ASC), Sort.by("branchCode",  Direction.ASC), Sort.by("driverCode", Direction.ASC));
		return coreRepository.searchPagingGridData(sql.toString(), params, sort);
	}
	
	@Transactional
	@CacheEvict(cacheNames = { "driverComboBox" }, allEntries = true)
	public Data add(final DriverModel model) {
		ValidationUtils.checkRequired(model.getCompanyId(), "Company Code");
		ValidationUtils.checkRequired(model.getBranchId(), "Branch Code");
		ValidationUtils.checkRequired(model.getDriverCode(), "Driver Code");
		ValidationUtils.checkRequired(model.getDriverName(), "Driver Name");
		
		model.setValidateModel("driverCode", model.getDriverCode());
		ValidationUtils.checkDuplicate(validate(model), "Driver Code");
		
		Driver driver = new Driver();
		driver.setCompanyId(model.getCompanyId());
		driver.setBranchId(model.getBranchId());
		driver.setDriverCode(model.getDriverCode());
		driver.setDriverName(model.getDriverName());

		Integer driverId = driverRepository.saveAndFlush(driver).getDriverId();
		log.debug("Driver ID : {}", driverId);

		model.setDriverId(driverId);
		return Data.of(model);
	}
	
	@Transactional
	@CacheEvict(cacheNames = { "driverComboBox" }, allEntries = true)
	public Data edit(final DriverModel model) {
		ValidationUtils.checkRequired(model.getDriverName(), "Driver Name");
		
		Driver driver = driverRepository.getById(model.getDriverId());
		driver.setDriverName(model.getDriverName());
		
		driverRepository.saveAndFlush(driver);
		return Data.of(model);
	}

	@Transactional
	public Data delete(final DriverModel model) {
		driverRepository.deleteById(model.getDriverId());
		return Data.of();
	}
	
	public Data validate(final DriverModel model) {
		SqlParams params = SqlParams.createValidateParam(model);
		params.add("companyId", model.getCompanyId());
		params.add("branchId", model.getBranchId());
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT CASE WHEN COUNT(1) > 0 THEN 1 ELSE 0 END AS \"duplicate\" ");
		sql.append(" FROM driver d ");
		sql.append(" WHERE d.cpn_id = :companyId ");
		sql.append("    AND d.branch_id = :branchId ");
		sql.append("    AND d.drv_code = :driverCode ");
		
		return coreRepository.validate(sql.toString(), params);	
	}
	
}
