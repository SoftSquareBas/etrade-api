package com.tiffa.wd.elock.paperless.master.uom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.tiffa.wd.elock.paperless.core.Data;
import com.tiffa.wd.elock.paperless.core.GridData;
import com.tiffa.wd.elock.paperless.core.Sort;
import com.tiffa.wd.elock.paperless.core.entity.Uom;
import com.tiffa.wd.elock.paperless.core.repository.CoreRepository;
import com.tiffa.wd.elock.paperless.core.repository.SqlParams;
import com.tiffa.wd.elock.paperless.core.repository.SqlSort;
import com.tiffa.wd.elock.paperless.core.repository.UomRepository;
import com.tiffa.wd.elock.paperless.core.util.CoreUtils;
import com.tiffa.wd.elock.paperless.core.util.ValidationUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true, propagation = Propagation.NEVER)
public class UomService {

	@Autowired
	private CoreRepository coreRepository;
	
	@Autowired
	private UomRepository uomRepository;

	public GridData search(final UomModel model) {
		SqlParams params = SqlParams.createPageParam(model);
		StringBuilder sql = new StringBuilder();

		sql.append(" SELECT a.*");
		sql.append(" FROM ( ");

		sql.append("   SELECT u.uom_id AS \"uomId\" ");
		sql.append("     , u.cpn_id AS \"companyId\" ");
		sql.append("     , c.cpn_code AS \"companyCode\" ");
		sql.append("     , u.branch_id AS \"branchId\" ");
		sql.append("     , b.branch_code AS \"branchCode\" ");
		sql.append("     , u.uom_code AS \"uomCode\" ");
		sql.append("     , u.uom_name AS \"uomName\" ");
		sql.append("     , u.uom_customs_code AS \"uomCustomsCode\" ");
		sql.append("   FROM uom u ");
		sql.append("     JOIN company c ON c.cpn_id = u.cpn_id ");
		sql.append("     JOIN branch b ON b.branch_id = u.branch_id ");
		sql.append("   WHERE 1 = 1 ");
		
		if (CoreUtils.isNotNull(model.getCompanyId())) {
			sql.append("    AND u.cpn_id = :companyId ");
			params.add("companyId", model.getCompanyId());
		}
		
		if (CoreUtils.isNotNull(model.getBranchId())) {
			sql.append("    AND u.branch_id = :branchId ");
			params.add("branchId", model.getBranchId());
		}

		if (CoreUtils.isNotNull(model.getUomCode())) {
			sql.append("    AND u.uom_code LIKE :uomCode ");
			params.add("uomCode", "%" + model.getUomCode() + "%");
		}

		if (CoreUtils.isNotNull(model.getUomName())) {
			sql.append("    AND u.uom_name LIKE :uomName ");
			params.add("uomName", "%" + model.getUomName() + "%");
		}
		
		if (CoreUtils.isNotNull(model.getUomCustomsCode())) {
			sql.append("    AND u.uom_customs_code LIKE :uomCustomsCode ");
			params.add("uomCustomsCode", "%" + model.getUomCustomsCode() + "%");
		}
		
		sql.append(" ) a ");
		SqlSort sort = SqlSort.create(model, Sort.by("companyCode", Direction.ASC), Sort.by("branchCode",  Direction.ASC), Sort.by("uomCode", Direction.ASC));
		return coreRepository.searchPagingGridData(sql.toString(), params, sort);
	}
	
	@Transactional
	@CacheEvict(cacheNames = { "uomComboBox" }, allEntries = true)
	public Data add(final UomModel model) {
		ValidationUtils.checkRequired(model.getCompanyId(), "Company Code");
		ValidationUtils.checkRequired(model.getBranchId(), "Branch Code");
		ValidationUtils.checkRequired(model.getUomCode(), "UOM Code");
		ValidationUtils.checkRequired(model.getUomName(), "UOM Name");
		
		model.setValidateModel("uomCode", model.getUomCode());
		ValidationUtils.checkDuplicate(validate(model), "UOM Code");
		
		Uom uom = new Uom();
		uom.setCompanyId(model.getCompanyId());
		uom.setBranchId(model.getBranchId());
		uom.setUomCode(model.getUomCode());
		uom.setUomName(model.getUomName());
		uom.setUomCustomsCode(model.getUomCustomsCode());
		
		Integer uomId = uomRepository.saveAndFlush(uom).getUomId();
		log.debug("UOM ID : {}", uomId);
		
		model.setUomId(uomId);
		return Data.of(model);
	}
	
	@Transactional
	@CacheEvict(cacheNames = { "uomComboBox" }, allEntries = true)
	public Data edit(final UomModel model) {
		ValidationUtils.checkRequired(model.getUomName(), "UOM Name");
		
		Uom uom = uomRepository.getById(model.getUomId());
		uom.setUomName(model.getUomName());
		uom.setUomCustomsCode(model.getUomCustomsCode());
		
		uomRepository.saveAndFlush(uom);
		return Data.of(model);
	}

	@Transactional
	@CacheEvict(cacheNames = { "uomComboBox" }, allEntries = true)
	public Data delete(final UomModel model) {
		uomRepository.deleteById(model.getUomId());
		return Data.of();
	}
	
	public Data validate(final UomModel model) {
		SqlParams params = SqlParams.createValidateParam(model);
		params.add("companyId", model.getCompanyId());
		params.add("branchId", model.getBranchId());
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT CASE WHEN COUNT(1) > 0 THEN 1 ELSE 0 END AS \"duplicate\" ");
		sql.append(" FROM uom u ");
		sql.append(" WHERE u.cpn_id = :companyId ");
		sql.append("    AND u.branch_id = :branchId ");
		sql.append("    AND u.uom_code = :uomCode ");
		
		return coreRepository.validate(sql.toString(), params);	
	}
	
}
