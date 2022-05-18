package com.tiffa.wd.elock.paperless.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.tiffa.wd.elock.paperless.core.ComboBoxRequest;
import com.tiffa.wd.elock.paperless.core.GridData;
import com.tiffa.wd.elock.paperless.core.repository.CoreRepository;
import com.tiffa.wd.elock.paperless.core.repository.SqlParams;
import com.tiffa.wd.elock.paperless.core.util.CoreUtils;

@Service
@Transactional(readOnly = true, propagation = Propagation.NEVER)
public class ComboBoxService {

	@Autowired
	private CoreRepository coreRepository;
	
	@Cacheable(value = "stationComboBox", key = "#model")
	public GridData searchStation(ComboBoxRequest model) {
		SqlParams params = SqlParams.createComboBoxParam(model);
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT stn_code AS \"value\", "); 
		sql.append("    stn_code AS \"text\" ");
		sql.append(" FROM station ");
		sql.append(" WHERE 1 = 1 ");
		
		if (CoreUtils.isNotNull(model.getCompanyId())) {
			sql.append(" AND cpn_id = :companyId ");
			params.add("companyId", model.getCompanyId());
		}
		
		if (CoreUtils.isNotNull(model.getBranchId())) {
			sql.append(" AND branch_id = :branchId ");
			params.add("branchId", model.getBranchId());
		}
		
		if(CoreUtils.isNotEmpty(model.getQuery())) {
			sql.append(" AND stn_code LIKE :query");
		}
		
		if(CoreUtils.isNotEmpty(model.getFrom())) {
			sql.append(" AND stn_code >= :from");
		}
		
		if(CoreUtils.isNotEmpty(model.getTo())) {
			sql.append(" AND stn_code <= :to");
		}

		sql.append(" GROUP BY stn_code ");
		sql.append(" ORDER BY stn_code ASC ");

		return coreRepository.searchGridData(sql.toString(), params);
	}
	
	@Cacheable(value = "uomComboBox", key = "#model")
	public GridData searchUom(ComboBoxRequest model) {
		SqlParams params = SqlParams.createComboBoxParam(model);
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT uom_code AS \"value\", "); 
		sql.append("    uom_code AS \"text\" ");
		sql.append(" FROM uom ");
		sql.append(" WHERE 1 = 1 ");
		
		if (CoreUtils.isNotNull(model.getCompanyId())) {
			sql.append("    AND cpn_id = :companyId ");
			params.add("companyId", model.getCompanyId());
		}
		
		if (CoreUtils.isNotNull(model.getBranchId())) {
			sql.append("    AND branch_id = :branchId ");
			params.add("branchId", model.getBranchId());
		}
		
		if(CoreUtils.isNotEmpty(model.getQuery())) {
			sql.append(" AND uom_code LIKE :query");
		}
		
		if(CoreUtils.isNotEmpty(model.getFrom())) {
			sql.append(" AND uom_code >= :from");
		}
		
		if(CoreUtils.isNotEmpty(model.getTo())) {
			sql.append(" AND uom_code <= :to");
		}
		
		sql.append(" GROUP BY uom_code ");
		sql.append(" ORDER BY uom_code ASC ");

		return coreRepository.searchGridData(sql.toString(), params);
	}
	
	@Cacheable(value = "companyComboBox", key = "#model")
	public GridData searchCompany(ComboBoxRequest model) {
		SqlParams params = SqlParams.createComboBoxParam(model);
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT c.cpn_id AS \"value\", ");  
		sql.append("  c.cpn_code AS \"text\" "); 
		sql.append(" FROM company c "); 
		sql.append(" WHERE 1 = 1 ");
		
		if(CoreUtils.isNotNull(model.getCompanyId())) {
			sql.append(" AND c.cpn_id = :companyId ");
			params.add("companyId", model.getCompanyId());
		}

		if(CoreUtils.isNotEmpty(model.getQuery())) {
			sql.append(" AND c.cpn_code LIKE :query");
		}
		
		sql.append(" ORDER BY c.cpn_code ASC ");
		
		return coreRepository.searchGridData(sql.toString(), params);
	}
	
	@Cacheable(value = "branchComboBox", key = "#model")
	public GridData searchBranch(ComboBoxRequest model) {
		SqlParams params = SqlParams.createComboBoxParam(model);
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT b.branch_id AS \"value\", ");  
		sql.append("  b.branch_code AS \"text\" "); 
		sql.append(" FROM branch b ");
		sql.append(" WHERE b.branch_status = 0 ");
		sql.append("   AND b.cpn_id = :companyId ");
		params.add("companyId", model.getCompanyId());

		if(CoreUtils.isNotEmpty(model.getQuery())) {
			sql.append(" AND b.branch_code LIKE :query");
		}
		sql.append(" ORDER BY b.branch_code ASC "); 
		
		return coreRepository.searchGridData(sql.toString(), params);
	}
	
	@Cacheable(value = "truckComboBox", key = "#model")
	public GridData searchTruck(ComboBoxRequest model) {
		SqlParams params = SqlParams.createComboBoxParam(model);
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT t.truck_code AS \"value\", ");  
		sql.append("  t.truck_code AS \"text\" "); 
		sql.append(" FROM truck t ");
		sql.append(" WHERE 1 = 1 ");
		sql.append(" AND t.cpn_id = :companyId ");
		params.add("companyId", model.getCompanyId());

		sql.append(" AND t.branch_id = :branchId ");
		params.add("branchId", model.getBranchId());
	
		if(CoreUtils.isNotEmpty(model.getQuery())) {
			sql.append(" AND t.truck_code LIKE :query");
		}
		
		sql.append(" GROUP BY t.truck_code ");
		sql.append(" ORDER BY t.truck_code ASC "); 
		
		return coreRepository.searchGridData(sql.toString(), params);
	}
	
	@Cacheable(value = "driverComboBox", key = "#model")
	public GridData searchDriver(ComboBoxRequest model) {
		SqlParams params = SqlParams.createComboBoxParam(model);
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT d.drv_code AS \"value\", ");  
		sql.append("  d.drv_code AS \"text\" "); 
		sql.append(" FROM driver d ");
		sql.append(" WHERE 1 = 1 ");
		sql.append(" AND d.cpn_id = :companyId ");
		params.add("companyId", model.getCompanyId());

		sql.append(" AND d.branch_id = :branchId ");
		params.add("branchId", model.getBranchId());
		
		if(CoreUtils.isNotEmpty(model.getQuery())) {
			sql.append(" AND d.drv_code LIKE :query");
		}
		
		sql.append(" GROUP BY d.drv_code ");
		sql.append(" ORDER BY d.drv_code ASC "); 
		
		return coreRepository.searchGridData(sql.toString(), params);
	}
	
	@Cacheable(value = "weightComboBox", key = "#model")
	public GridData searchWeight(ComboBoxRequest model) {
		SqlParams params = SqlParams.createComboBoxParam(model);
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT w.weight_code AS \"value\", ");  
		sql.append("  w.weight_code AS \"text\" "); 
		sql.append(" FROM weight w ");
		sql.append(" WHERE 1 = 1 ");
		sql.append(" AND w.cpn_id = :companyId ");
		params.add("companyId", model.getCompanyId());

		sql.append(" AND w.branch_id = :branchId ");
		params.add("branchId", model.getBranchId());
		
		if(CoreUtils.isNotEmpty(model.getQuery())) {
			sql.append(" AND w.weight_code LIKE :query");
		}
		
		sql.append(" GROUP BY w.weight_code ");
		sql.append(" ORDER BY w.weight_code ASC "); 
		
		return coreRepository.searchGridData(sql.toString(), params);
	}
	
	@Cacheable(value = "packageComboBox", key = "#model")
	public GridData searchPackage(ComboBoxRequest model) {
		SqlParams params = SqlParams.createComboBoxParam(model);
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT p.pkg_code AS \"value\", ");  
		sql.append("  p.pkg_code AS \"text\" "); 
		sql.append(" FROM \"PACKAGE\" p ");
		sql.append(" WHERE 1 = 1 ");
		sql.append(" AND p.cpn_id = :companyId ");
		params.add("companyId", model.getCompanyId());

		sql.append(" AND p.branch_id = :branchId ");
		params.add("branchId", model.getBranchId());
		
		if(CoreUtils.isNotEmpty(model.getQuery())) {
			sql.append(" AND p.pkg_code LIKE :query");
		}
		
		sql.append(" GROUP BY p.pkg_code ");
		sql.append(" ORDER BY p.pkg_code ASC "); 
		
		return coreRepository.searchGridData(sql.toString(), params);
	}
	
	@Cacheable(value = "routeComboBox", key = "#model")
	public GridData searchRoute(ComboBoxRequest model) {
		SqlParams params = SqlParams.createComboBoxParam(model);
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT d.drv_code AS \"value\", ");  
		sql.append("  d.drv_code AS \"text\" "); 
		sql.append(" FROM driver d ");
		sql.append(" WHERE 1 = 1 ");
		sql.append(" AND d.cpn_id = :companyId ");
		params.add("companyId", model.getCompanyId());

		sql.append(" AND d.branch_id = :branchId ");
		params.add("branchId", model.getBranchId());
		
		if(CoreUtils.isNotEmpty(model.getQuery())) {
			sql.append(" AND d.drv_code LIKE :query");
		}
		
		sql.append(" GROUP BY d.drv_code ");
		sql.append(" ORDER BY d.drv_code ASC "); 
		
		return coreRepository.searchGridData(sql.toString(), params);
	}
}
