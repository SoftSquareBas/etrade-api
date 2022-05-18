package com.tiffa.wd.elock.paperless.master.truck;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.tiffa.wd.elock.paperless.core.Data;
import com.tiffa.wd.elock.paperless.core.GridData;
import com.tiffa.wd.elock.paperless.core.Sort;
import com.tiffa.wd.elock.paperless.core.entity.Truck;
import com.tiffa.wd.elock.paperless.core.repository.CoreRepository;
import com.tiffa.wd.elock.paperless.core.repository.SqlParams;
import com.tiffa.wd.elock.paperless.core.repository.SqlSort;
import com.tiffa.wd.elock.paperless.core.repository.TruckRepository;
import com.tiffa.wd.elock.paperless.core.util.CoreUtils;
import com.tiffa.wd.elock.paperless.core.util.ValidationUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true, propagation = Propagation.NEVER)
public class TruckService {

	@Autowired
	private CoreRepository coreRepository;

	@Autowired
	private TruckRepository truckRepository;

	public GridData search(final TruckModel model) {
		SqlParams params = SqlParams.createPageParam(model);
		StringBuilder sql = new StringBuilder();

		sql.append(" SELECT a.*");
		sql.append(" FROM ( ");

		sql.append("   SELECT t.truck_id AS \"truckId\" ");
		sql.append("     , t.cpn_id AS \"companyId\" ");
		sql.append("     , c.cpn_code AS \"companyCode\" ");
		sql.append("     , t.branch_id AS \"branchId\" ");
		sql.append("     , b.branch_code AS \"branchCode\" ");
		sql.append("     , t.truck_code AS \"truckCode\" ");
		sql.append("     , t.truck_name AS \"truckName\" ");
		sql.append("   FROM truck t ");
		sql.append("     JOIN company c ON c.cpn_id = t.cpn_id ");
		sql.append("     JOIN branch b ON b.branch_id = t.branch_id ");
		sql.append("   WHERE 1 = 1 ");

		if (CoreUtils.isNotNull(model.getCompanyId())) {
			sql.append("    AND t.cpn_id = :companyId ");
			params.add("companyId", model.getCompanyId());
		}
		
		if (CoreUtils.isNotNull(model.getBranchId())) {
			sql.append("    AND t.branch_id = :branchId ");
			params.add("branchId", model.getBranchId());
		}
		
		if (CoreUtils.isNotNull(model.getTruckCode())) {
			sql.append("    AND t.truck_code LIKE :truckCode ");
			params.add("truckCode", "%" + model.getTruckCode() + "%");
		}

		if (CoreUtils.isNotNull(model.getTruckName())) {
			sql.append("    AND t.truck_name LIKE :truckName ");
			params.add("truckName", "%" + model.getTruckName() + "%");
		}

		sql.append(" ) a ");
		SqlSort sort = SqlSort.create(model, Sort.by("companyCode", Direction.ASC), Sort.by("branchCode",  Direction.ASC), Sort.by("truckCode", Direction.ASC));
		return coreRepository.searchPagingGridData(sql.toString(), params, sort);
	}

	@Transactional
	@CacheEvict(cacheNames = { "truckComboBox" }, allEntries = true)
	public Data add(final TruckModel model) {
		ValidationUtils.checkRequired(model.getCompanyId(), "Company Code");
		ValidationUtils.checkRequired(model.getBranchId(), "Branch Code");
		ValidationUtils.checkRequired(model.getTruckCode(), "Truck Code");
		ValidationUtils.checkRequired(model.getTruckName(), "Truck Name");
		
		model.setValidateModel("truckCode", model.getTruckCode());
		ValidationUtils.checkDuplicate(validate(model), "Truck Code");
		
		Truck truck = new Truck();
		truck.setCompanyId(model.getCompanyId());
		truck.setBranchId(model.getBranchId());
		truck.setTruckCode(model.getTruckCode());
		truck.setTruckName(model.getTruckName());
		Integer truckId = truckRepository.saveAndFlush(truck).getTruckId();
		log.debug("Truck ID : {}", truckId);

		model.setTruckId(truckId);
		return Data.of(model);
	}

	@Transactional
	@CacheEvict(cacheNames = { "truckComboBox" }, allEntries = true)
	public Data edit(final TruckModel model) {
		ValidationUtils.checkRequired(model.getTruckName(), "Truck Name");
		
		Truck truck = truckRepository.getById(model.getTruckId());
		truck.setTruckName(model.getTruckName());
		
		truckRepository.saveAndFlush(truck);
		return Data.of(model);
	}

	@Transactional
	public Data delete(final TruckModel model) {
		truckRepository.deleteById(model.getTruckId());
		return Data.of();
	}

	public Data validate(final TruckModel model) {
		SqlParams params = SqlParams.createValidateParam(model);
		params.add("companyId", model.getCompanyId());
		params.add("branchId", model.getBranchId());
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT CASE WHEN COUNT(1) > 0 THEN 1 ELSE 0 END AS \"duplicate\" ");
		sql.append(" FROM truck t ");
		sql.append(" WHERE t.cpn_id = :companyId ");
		sql.append("    AND t.branch_id = :branchId ");
		sql.append("    AND t.drv_code = :truckCode ");
		
		return coreRepository.validate(sql.toString(), params);	
	}

}
