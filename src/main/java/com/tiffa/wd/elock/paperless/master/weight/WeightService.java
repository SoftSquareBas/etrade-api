package com.tiffa.wd.elock.paperless.master.weight;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.tiffa.wd.elock.paperless.core.Data;
import com.tiffa.wd.elock.paperless.core.GridData;
import com.tiffa.wd.elock.paperless.core.Sort;
import com.tiffa.wd.elock.paperless.core.entity.Weight;
import com.tiffa.wd.elock.paperless.core.repository.CoreRepository;
import com.tiffa.wd.elock.paperless.core.repository.SqlParams;
import com.tiffa.wd.elock.paperless.core.repository.SqlSort;
import com.tiffa.wd.elock.paperless.core.repository.WeightRepository;
import com.tiffa.wd.elock.paperless.core.util.CoreUtils;
import com.tiffa.wd.elock.paperless.core.util.ValidationUtils;

@Service
@Transactional(readOnly = true, propagation = Propagation.NEVER)
public class WeightService {

	@Autowired
	private CoreRepository coreRepository;

	@Autowired
	private WeightRepository weightRepository;

	public GridData search(final WeightModel model) {
		SqlParams params = SqlParams.createPageParam(model);
		StringBuilder sql = new StringBuilder();

		sql.append(" SELECT a.*");
		sql.append(" FROM ( ");
		sql.append("   SELECT w.weight_id AS \"weightId\" ");
		sql.append("     , w.cpn_id AS \"companyId\" ");
		sql.append("     , c.cpn_code AS \"companyCode\" ");
		sql.append("     , w.branch_id AS \"branchId\" ");
		sql.append("     , b.branch_code AS \"branchCode\" ");
		sql.append("     , w.weight_code AS \"weightCode\" ");
		sql.append("     , w.weight_name AS \"weightName\" ");
		sql.append("   FROM weight w ");
		sql.append("     JOIN company c ON c.cpn_id = w.cpn_id ");
		sql.append("     JOIN branch b ON b.branch_id = w.branch_id ");
		sql.append("   WHERE 1 = 1 ");
		
		if (CoreUtils.isNotNull(model.getCompanyId())) {
			sql.append("    AND w.cpn_id = :companyId ");
			params.add("companyId", model.getCompanyId());
		}
		
		if (CoreUtils.isNotNull(model.getBranchId())) {
			sql.append("    AND w.branch_id = :branchId ");
			params.add("branchId", model.getBranchId());
		}

		if (CoreUtils.isNotNull(model.getWeightCode())) {
			sql.append("    AND w.weight_code LIKE :weightCode ");
			params.add("weightCode", "%" + model.getWeightCode() + "%");
		}

		if (CoreUtils.isNotNull(model.getWeightName())) {
			sql.append("    AND w.weight_name LIKE :weightName ");
			params.add("weightName", "%" + model.getWeightName() + "%");
		}

		sql.append(" ) a ");
		SqlSort sort = SqlSort.create(model, Sort.by("companyCode", Direction.ASC), Sort.by("branchCode",  Direction.ASC), Sort.by("weightCode", Direction.ASC));

		return coreRepository.searchPagingGridData(sql.toString(), params, sort);
	}

	@Transactional
	public Data add(final WeightModel model) {
		ValidationUtils.checkRequired(model.getCompanyId(), "Company Code");
		ValidationUtils.checkRequired(model.getBranchId(), "Branch Code");
		ValidationUtils.checkRequired(model.getWeightCode(), "Weight Code");
		ValidationUtils.checkRequired(model.getWeightName(), "Weight Name");
		
		model.setValidateModel("weightCode", model.getWeightCode());
		ValidationUtils.checkDuplicate(validate(model), "Weight Code");
		
		
		Weight weight = new Weight();
		weight.setCompanyId(model.getCompanyId());
		weight.setBranchId(model.getBranchId());
		weight.setWeightCode(model.getWeightCode());
		weight.setWeightName(model.getWeightName());

		Integer weightId = weightRepository.saveAndFlush(weight).getWeightId();
		model.setWeightId(weightId);
		return Data.of(model);
	}

	@Transactional
	public Data edit(final WeightModel model) {
		ValidationUtils.checkRequired(model.getWeightName(), "Weight Name");
		
		Weight weight = weightRepository.getById(model.getWeightId());
		weight.setWeightName(model.getWeightName());

		weightRepository.saveAndFlush(weight);
		return Data.of(model);
	}

	@Transactional
	public Data delete(final WeightModel model) {
		weightRepository.deleteById(model.getWeightId());
		return Data.of();
	}

	public Data validate(final WeightModel model) {
		SqlParams params = SqlParams.createValidateParam(model);
		params.add("companyId", model.getCompanyId());
		params.add("branchId", model.getBranchId());
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT CASE WHEN COUNT(1) > 0 THEN 1 ELSE 0 END AS \"duplicate\" ");
		sql.append(" FROM weight w ");
		sql.append(" WHERE w.cpn_id = :companyId ");
		sql.append("    AND w.branch_id = :branchId ");
		sql.append("    AND w.weight_code = :weightCode ");
		
		return coreRepository.validate(sql.toString(), params);	
	}

}
