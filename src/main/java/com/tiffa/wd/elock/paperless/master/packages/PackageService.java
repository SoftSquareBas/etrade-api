package com.tiffa.wd.elock.paperless.master.packages;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.tiffa.wd.elock.paperless.core.Data;
import com.tiffa.wd.elock.paperless.core.GridData;
import com.tiffa.wd.elock.paperless.core.Sort;
import com.tiffa.wd.elock.paperless.core.entity.Package;
import com.tiffa.wd.elock.paperless.core.repository.CoreRepository;
import com.tiffa.wd.elock.paperless.core.repository.PackageRepository;
import com.tiffa.wd.elock.paperless.core.repository.SqlParams;
import com.tiffa.wd.elock.paperless.core.repository.SqlSort;
import com.tiffa.wd.elock.paperless.core.util.CoreUtils;
import com.tiffa.wd.elock.paperless.core.util.ValidationUtils;

@Service
@Transactional(readOnly = true, propagation = Propagation.NEVER)
public class PackageService {

	@Autowired
	private CoreRepository coreRepository;
	
	@Autowired
	private PackageRepository packageRepository;

	public GridData search(final PackageModel model) {
		SqlParams params = SqlParams.createPageParam(model);
		StringBuilder sql = new StringBuilder();

		sql.append(" SELECT a.*");
		sql.append(" FROM ( ");

		sql.append("   SELECT p.package_id AS \"packageId\" ");
		sql.append("     , p.cpn_id AS \"companyId\" ");
		sql.append("     , c.cpn_code AS \"companyCode\" ");
		sql.append("     , p.branch_id AS \"branchId\" ");
		sql.append("     , b.branch_code AS \"branchCode\" ");
		sql.append("     , p.pkg_code AS \"packageCode\" ");
		sql.append("     , p.pkg_name AS \"packageName\" ");
		sql.append("   FROM package p ");
		sql.append("     JOIN company c ON c.cpn_id = p.cpn_id ");
		sql.append("     JOIN branch b ON b.branch_id = p.branch_id ");
		sql.append("   WHERE 1 = 1 ");
		
		if (CoreUtils.isNotNull(model.getCompanyId())) {
			sql.append("    AND p.cpn_id = :companyId ");
			params.add("companyId", model.getCompanyId());
		}
		
		if (CoreUtils.isNotNull(model.getBranchId())) {
			sql.append("    AND p.branch_id = :branchId ");
			params.add("branchId", model.getBranchId());
		}
		
		if (CoreUtils.isNotNull(model.getPackageCode())) {
			sql.append("    AND p.pkg_code LIKE :packageCode ");
			params.add("packageCode", "%" + model.getPackageCode() + "%");
		}

		if (CoreUtils.isNotNull(model.getPackageName())) {
			sql.append("    AND p.pkg_name LIKE :packageName ");
			params.add("packageName", "%" + model.getPackageName() + "%");
		}
		
		sql.append(" ) a ");
		SqlSort sort = SqlSort.create(model, Sort.by("companyCode", Direction.ASC), Sort.by("branchCode",  Direction.ASC), Sort.by("packageCode", Direction.ASC));
		return coreRepository.searchPagingGridData(sql.toString(), params, sort);
	}
	
	@Transactional
	public Data add(final PackageModel model) {
		ValidationUtils.checkRequired(model.getCompanyId(), "Company Code");
		ValidationUtils.checkRequired(model.getBranchId(), "Branch Code");
		ValidationUtils.checkRequired(model.getPackageCode(), "Package Code");
		ValidationUtils.checkRequired(model.getPackageName(), "Package Name");
		
		model.setValidateModel("packageCode", model.getPackageCode());
		ValidationUtils.checkDuplicate(validate(model), "Package Code");
		
		Package packages = new Package();
		packages.setCompanyId(model.getCompanyId());
		packages.setBranchId(model.getBranchId());
		packages.setPackageCode(model.getPackageCode());
		packages.setPackageName(model.getPackageName());
		
		Integer packageId = packageRepository.saveAndFlush(packages).getPackageId();
		model.setPackageId(packageId);
		return Data.of(model);
	}
	
	@Transactional
	public Data edit(final PackageModel model) {
		ValidationUtils.checkRequired(model.getPackageName(), "Package Name");

		Package packages = packageRepository.getById(model.getPackageId());
		packages.setPackageName(model.getPackageName());

		packageRepository.saveAndFlush(packages);
		return Data.of(model);
	}

	@Transactional
	public Data delete(final PackageModel model) {
		Package packages = new Package();
		packages.setCompanyId(model.getCompanyId());
		packages.setBranchId(model.getBranchId());
		packages.setPackageId(model.getPackageId());
		
		packageRepository.delete(packages);
		return Data.of();
	}
	
	public Data validate(final PackageModel model) {
		SqlParams params = SqlParams.createValidateParam(model);
		params.add("companyId", model.getCompanyId());
		params.add("branchId", model.getBranchId());
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT CASE WHEN COUNT(1) > 0 THEN 1 ELSE 0 END AS \"duplicate\" ");
		sql.append(" FROM package p ");
		sql.append(" WHERE p.cpn_id = :companyId ");
		sql.append("    AND p.branch_id = :branchId ");
		sql.append("    AND p.pkg_code = :packageCode ");
		
		return coreRepository.validate(sql.toString(), params);	
	}
	
}
