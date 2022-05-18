package com.tiffa.wd.elock.paperless.master.branchRole;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.tiffa.wd.elock.paperless.core.Data;
import com.tiffa.wd.elock.paperless.core.GridData;
import com.tiffa.wd.elock.paperless.core.Sort;
import com.tiffa.wd.elock.paperless.core.entity.Role;
import com.tiffa.wd.elock.paperless.core.entity.RolePrivilege;
import com.tiffa.wd.elock.paperless.core.entity.RolePrivilegePk;
import com.tiffa.wd.elock.paperless.core.repository.CoreRepository;
import com.tiffa.wd.elock.paperless.core.repository.RolePrivilegeRepository;
import com.tiffa.wd.elock.paperless.core.repository.RoleRepository;
import com.tiffa.wd.elock.paperless.core.repository.SqlParams;
import com.tiffa.wd.elock.paperless.core.repository.SqlSort;
import com.tiffa.wd.elock.paperless.core.util.CoreUtils;
import com.tiffa.wd.elock.paperless.core.util.SecurityUtils;
import com.tiffa.wd.elock.paperless.core.util.ValidationUtils;

@Service
@Transactional(readOnly = true, propagation = Propagation.NEVER)
public class BranchRoleService {

	@Autowired
	private CoreRepository coreRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private RolePrivilegeRepository rolePrivilegeRepository;

	public GridData search(final BranchRoleModel model) {
		SqlParams params = SqlParams.createPageParam(model);
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT a.*");
		sql.append(" FROM ( ");
		sql.append(" 	SELECT sr.role_id AS \"roleId\" ");
		sql.append(" 		, c.cpn_code AS \"companyCode\" ");
		sql.append(" 		, b.branch_code AS \"branchCode\" ");
		sql.append(" 		, sr.rolecode AS \"roleCode\" ");
		sql.append(" 		, sr.rolename AS \"roleName\" ");
		sql.append(" 		, sr.cpn_id AS \"companyId\" ");
		sql.append(" 		, sr.branch_id AS \"branchId\" ");
		sql.append(" 	FROM se_role sr  ");
		sql.append(" 		JOIN company c ON c.cpn_id = sr.cpn_id   ");
		sql.append(" 		JOIN branch b ON b.branch_id = sr.branch_id   ");
		sql.append(" 	WHERE 1 = 1 ");

		if (CoreUtils.isNotNull(model.getCompanyId())) {
			sql.append("    AND sr.cpn_id = :companyId ");
			params.add("companyId", model.getCompanyId());
		}

		if (CoreUtils.isNotNull(model.getBranchId())) {
			sql.append("    AND sr.branch_id = :branchId ");
			params.add("branchId", model.getBranchId());
		}

		if (CoreUtils.isNotEmpty(model.getRoleCode())) {
			sql.append(" 	AND LOWER(sr.rolecode) LIKE LOWER(:roleCode) ");
			params.add("roleCode", "%" + model.getRoleCode() + "%");
		}

		if (CoreUtils.isNotEmpty(model.getRoleName())) {
			sql.append(" 	AND LOWER(sr.rolename) LIKE LOWER(:roleName) ");
			params.add("roleName", "%" + model.getRoleName() + "%");
		}

		sql.append(" ) a ");
		// sql.append(SqlUtils.generateSqlOrderBy(model, "a", Sort.by("companyCode",
		// Direction.ASC), Sort.by("roleCode", Direction.ASC)));

		SqlSort sort = SqlSort.create(model, Sort.by("companyCode", Direction.ASC), Sort.by("roleCode", Direction.ASC));
		return coreRepository.searchPagingGridData(sql.toString(), params, sort);
	}

	public Data load(final BranchRoleModel model) {
		if (CoreUtils.isNotNull(model.getRoleId())) {
			SqlParams params = SqlParams.create();

			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT r.role_id AS \"roleId\", ");
			sql.append(" 	r.rolecode AS \"roleCode\", ");
			sql.append(" 	r.rolename AS \"roleName\", ");
			sql.append(" 	r.cpn_id AS \"companyId\", ");
			sql.append(" 	r.branch_id AS \"branchId\" ");
			sql.append(" FROM se_role r ");
			sql.append(" WHERE r.cpn_id = :companyId ");
			sql.append(" 	AND r.role_id = :roleId ");

			params.add("companyId", model.getCompanyId());
			params.add("roleId", model.getRoleId());
			return coreRepository.getData(sql.toString(), params);
		}
		return Data.of();
	}

	public GridData loadAccessRight(final BranchRoleModel model) {
		SqlParams params = SqlParams.create();
		StringBuilder sql = new StringBuilder();

		sql.append(" WITH user_tab AS ( ");
		sql.append(" 	SELECT DISTINCT rp.privilege_id ");
		sql.append(" 	FROM se_role_privilege rp ");
		sql.append(" 	WHERE rp.role_id = :roleId ");
		sql.append(" ), company_privilege AS ( ");
		sql.append(" 	SELECT DISTINCT up.privilege_id ");
		sql.append(" 	FROM se_user_privilege up ");
		sql.append(" 	WHERE up.user_id = :userId ");
		sql.append(" ) ");
		sql.append(" SELECT p.privilege_id AS \"key\", ");
		sql.append(" 	p.privilege AS \"title\", ");
		sql.append(" 	'privilege' AS \"type\", ");
		sql.append(" 	(CASE WHEN ut.privilege_id IS NOT NULL THEN 'right' ELSE 'left' END) AS \"direction\", ");
		sql.append(" 	(CASE WHEN ut.privilege_id IS NOT NULL THEN 'right' ELSE 'left' END) AS \"source\" ");
		sql.append(" FROM company_privilege cp ");
		sql.append(" 	JOIN se_privilege p ON p.privilege_id = cp.privilege_id ");
		sql.append(" 	LEFT JOIN user_tab ut ON ut.privilege_id = p.privilege_id ");
		sql.append(" ORDER BY p.privilege ");

		params.add("roleId", model.getRoleId());
		params.add("userId", SecurityUtils.getUserId());
		params.add("companyId", model.getCompanyId());

		return coreRepository.searchGridData(sql.toString(), params);
	}

	public Data validate(final BranchRoleModel model) {
		SqlParams params = SqlParams.createValidateParam(model);
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT CASE WHEN COUNT(1) > 0 THEN 1 ELSE 0 END AS \"duplicate\" ");
		sql.append(" FROM se_role sr ");
		sql.append(" WHERE 1 = 1 ");
		sql.append(" AND sr.cpn_id = :companyId ");
		sql.append(" AND LOWER(sr.rolecode) = LOWER(:roleCode) ");

		params.add("companyId", model.getCompanyId());

		if (CoreUtils.isNotNull(model.getRoleId())) {
			sql.append(" AND sr.role_id <> :roleId ");
			params.add("roleId", model.getRoleId());
		}
		return coreRepository.validate(sql.toString(), params);
	}

	private void saveAccesRight(final BranchRoleModel model) {
		for (BranchRoleModel.TransferItem item : model.getItems()) {
			if ("right".equals(item.getSource()) && "left".equals(item.getDirection())) {
				rolePrivilegeRepository.deleteByRoleIdAndPrivilegeId(model.getRoleId(), item.getKey());
			} else if ("left".equals(item.getSource()) && "right".equals(item.getDirection())) {
				RolePrivilegePk pk = new RolePrivilegePk();
				pk.setRoleId(model.getRoleId());
				pk.setPrivilegeId(item.getKey());
				RolePrivilege rp = new RolePrivilege();
				rp.setPk(pk);
				rolePrivilegeRepository.save(rp);
			}
		}
	}

	@Transactional
	public Data add(final BranchRoleModel model) {

		ValidationUtils.checkRequired(model.getCompanyId(), "Company Code");
		ValidationUtils.checkRequired(model.getBranchId(), "Branch Code");
		ValidationUtils.checkRequired(model.getRoleCode(), "Role Code");
		ValidationUtils.checkRequired(model.getRoleCode(), "Role Name");

		model.setValidateModel("roleCode", model.getRoleCode());
		ValidationUtils.checkDuplicate(validate(model), "Role Code");

		Role role = new Role();
		role.setRoleCode(model.getRoleCode());
		role.setRoleName(model.getRoleName());
		role.setCompanyId(model.getCompanyId());
		role.setBranchId(model.getBranchId());
		role.setInactive(LocalDate.now());
		model.setRoleId(roleRepository.save(role).getRoleId());

		saveAccesRight(model);
		return Data.of(model);
	}

	@Transactional
	public Data edit(final BranchRoleModel model) {
		ValidationUtils.checkRequired(model.getCompanyId(), "Company Code");
		ValidationUtils.checkRequired(model.getBranchId(), "Branch Code");
		ValidationUtils.checkRequired(model.getRoleCode(), "Role Code");
		ValidationUtils.checkRequired(model.getRoleCode(), "Role Name");

		model.setValidateModel("roleCode", model.getRoleCode());
		ValidationUtils.checkDuplicate(validate(model), "Role Code");

		Role role = roleRepository.findById(model.getRoleId()).get();
		role.setRoleCode(model.getRoleCode());
		role.setRoleName(model.getRoleName());
		role.setCompanyId(model.getCompanyId());
		role.setBranchId(model.getBranchId());
		roleRepository.save(role);

		saveAccesRight(model);
		return Data.of(model);
	}

	@Transactional
	public Data delete(final BranchRoleModel model) {
		rolePrivilegeRepository.deleteAllPrivilegesByRoleId(model.getRoleId());
		roleRepository.deleteById(model.getRoleId());
		return Data.of();
	}

}
