package com.tiffa.wd.elock.paperless.master.branch;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.tiffa.wd.elock.paperless.core.Data;
import com.tiffa.wd.elock.paperless.core.GridData;
import com.tiffa.wd.elock.paperless.core.Sort;
import com.tiffa.wd.elock.paperless.core.entity.Branch;
import com.tiffa.wd.elock.paperless.core.entity.User;
import com.tiffa.wd.elock.paperless.core.entity.UserPrivilege;
import com.tiffa.wd.elock.paperless.core.entity.UserPrivilegePk;
import com.tiffa.wd.elock.paperless.core.repository.BranchRepository;
import com.tiffa.wd.elock.paperless.core.repository.CoreRepository;
import com.tiffa.wd.elock.paperless.core.repository.SqlParams;
import com.tiffa.wd.elock.paperless.core.repository.SqlSort;
import com.tiffa.wd.elock.paperless.core.repository.UserPrivilegeRepository;
import com.tiffa.wd.elock.paperless.core.repository.UserRepository;
import com.tiffa.wd.elock.paperless.core.util.CoreUtils;
import com.tiffa.wd.elock.paperless.core.util.SecurityUtils;
import com.tiffa.wd.elock.paperless.core.util.ValidationUtils;

@Service
@Transactional(readOnly = true, propagation = Propagation.NEVER)
public class BranchService {

	@Autowired
	private CoreRepository coreRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserPrivilegeRepository userPrivilegeRepository;

	// @Autowired
	// private TagRepository tagRepository;

	@Autowired
	private BranchRepository branchRepository;

	public GridData search(final BranchModel model) {
		SqlParams params = SqlParams.createPageParam(model);
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT a.* ");
		sql.append(" FROM ( ");

		sql.append("	SELECT b.branch_id AS \"branchId\", ");
		sql.append("		b.branch_code AS \"branchCode\", ");
		sql.append("		b.branch_name AS \"branchName\", ");
		sql.append("		b.branch_status AS \"branchStatus\", ");
		sql.append("		c.cpn_code AS \"companyCode\", ");
		sql.append("		c.cpn_id AS \"companyId\" ");
		sql.append("	FROM branch b ");
		sql.append("		JOIN company c ON c.cpn_id = b.cpn_id ");
		sql.append("	WHERE 1 = 1 ");

		if (CoreUtils.isNotNull(model.getCompanyId())) {
			sql.append("    AND c.cpn_id = :companyId ");
			params.add("companyId", model.getCompanyId());
		}

		if (CoreUtils.isNotEmpty(model.getBranchCode())) {
			sql.append("    AND LOWER(b.branch_code) LIKE LOWER(:branchCode) ");
			params.add("branchCode", "%" + model.getBranchCode() + "%");
		}

		if (CoreUtils.isNotEmpty(model.getBranchName())) {
			sql.append("    AND LOWER(b.branch_name) LIKE LOWER(:branchName) ");
			params.add("branchName", "%" + model.getBranchName() + "%");
		}

		if (CoreUtils.isNotNull(model.getBranchStatus())) {
			sql.append("    AND b.branch_status = :branchStatus ");
			params.add("branchStatus", model.getBranchStatus());
		}
		sql.append(" ) a ");

		// sql.append(SqlUtils.generateSqlOrderBy(model, "a", Sort.by("companyCode",
		// Direction.ASC), Sort.by("branchCode", Direction.ASC)));
		SqlSort sort = SqlSort.create(model, Sort.by("companyCode", Direction.ASC),
				Sort.by("branchCode", Direction.ASC));
		return coreRepository.searchPagingGridData(sql.toString(), params, sort);
	}

	public GridData loadAccessRight(final BranchModel model) {
		SqlParams params = SqlParams.create();
		StringBuilder sql = new StringBuilder();

		sql.append(" WITH user_tab AS ( ");
		sql.append(" 	SELECT DISTINCT up.privilege_id ");
		sql.append(" 	FROM se_user_privilege up ");
		sql.append(" 	WHERE up.user_id = :branchUserId ");
		sql.append(" ), company_privilege AS ( ");
		sql.append(" 	SELECT DISTINCT up.privilege_id ");
		sql.append(" 	FROM se_user_privilege up ");
		sql.append(" 		JOIN company c ON c.user_id = up.user_id ");
		sql.append(" 	WHERE c.cpn_id = :companyId AND up.user_id = :userId ");
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

		params.add("companyId", model.getCompanyId());
		params.add("branchUserId", model.getBranchUserId());
		params.add("userId", SecurityUtils.getUserId());

		return coreRepository.searchGridData(sql.toString(), params);
	}

	@Transactional
	public Data saveAccessRight(final BranchModel model) {
		for (BranchModel.TransferItem item : model.getItems()) {
			if ("right".equals(item.getSource()) && "left".equals(item.getDirection())) {
				UserPrivilegePk pk = new UserPrivilegePk();
				pk.setUserId(model.getBranchUserId());
				pk.setPrivilegeId(item.getKey());
				UserPrivilege up = new UserPrivilege();
				up.setPk(pk);

				userPrivilegeRepository.delete(up);
			} else if ("left".equals(item.getSource()) && "right".equals(item.getDirection())) {
				UserPrivilegePk pk = new UserPrivilegePk();
				pk.setUserId(model.getBranchUserId());
				pk.setPrivilegeId(item.getKey());
				UserPrivilege up = new UserPrivilege();
				up.setPk(pk);

				userPrivilegeRepository.save(up);
			}
		}
		return Data.of();
	}

	public Data load(final BranchModel model) {
		SqlParams params = SqlParams.create();

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT b.branch_id AS \"branchId\", ");
		sql.append(" 	b.branch_code AS \"branchCode\", ");
		sql.append(" 	b.branch_name AS \"branchName\", ");
		sql.append(" 	b.branch_status AS \"branchStatus\", ");
		sql.append(" 	u.user_id AS \"branchUserId\", ");
		sql.append(" 	u.user_login AS \"branchUsername\", ");
		sql.append(" 	u.user_pwd AS \"branchPassword\", ");
		sql.append(" 	u.user_expiredate AS \"branchExpireDate\", ");
		sql.append(" 	b.declarationcost AS \"declarationCost\", ");
		sql.append(" 	b.webappcost AS \"webAppCost\", ");
		sql.append(" 	b.pertripcost AS \"perTripCost\", ");
		sql.append(" 	b.currency AS \"currency\", ");
		sql.append(" 	b.exchangerate AS \"exchangeRate\", ");
		sql.append(" 	b.cpn_id AS \"companyId\" ");
		sql.append(" FROM branch b ");
		sql.append(
				" 	LEFT JOIN user_paperless u ON u.user_id = b.user_id AND u.cpn_id = b.cpn_id AND u.branch_id = b.branch_id ");
		sql.append(" WHERE b.cpn_id = :companyId ");
		sql.append("    AND b.branch_id = :branchId ");

		params.add("companyId", model.getCompanyId());
		params.add("branchId", model.getBranchId());
		return coreRepository.getData(sql.toString(), params);
	}

	public Data validate(final BranchModel model) {
		if ("branchCode".equals(model.getField())) {
			SqlParams params = SqlParams.createValidateParam(model);
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT CASE WHEN COUNT(1) > 0 THEN 1 ELSE 0 END AS \"duplicate\" ");
			sql.append(" FROM branch b ");
			sql.append(" WHERE 1 = 1 ");
			sql.append(" 	AND b.cpn_id = :companyId ");
			sql.append(" 	AND LOWER(b.branch_code) = LOWER(:branchCode) ");
			params.add("companyId", SecurityUtils.getCompanyId());
			return coreRepository.validate(sql.toString(), params);
		} else if ("branchUsername".equals(model.getField())) {
			SqlParams params = SqlParams.createValidateParam(model);
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT CASE WHEN COUNT(1) > 0 THEN 1 ELSE 0 END AS \"duplicate\" ");
			sql.append(" FROM user_paperless u ");
			sql.append(" WHERE u.cpn_id = :companyId ");
			sql.append(" AND LOWER(u.user_login) = LOWER(:branchUsername) ");
			sql.append(" AND u.user_id <> :branchUserId ");
			params.add("companyId", SecurityUtils.getCompanyId());
			params.add("branchId", model.getBranchId());
			params.add("branchUserId", model.getBranchUserId());
			return coreRepository.validate(sql.toString(), params);
		}
		return null;
	}

	@Transactional
	@CacheEvict(cacheNames = { "branchComboBox" }, allEntries = true)
	public Data add(final BranchModel model) {

		if (CoreUtils.isNull(model.getBranchId())) {
			ValidationUtils.checkRequired(model.getCompanyId(), "Company Code");
			ValidationUtils.checkRequired(model.getBranchCode(), "Branch Code");
			ValidationUtils.checkRequired(model.getBranchStatus(), "Status");
			ValidationUtils.checkRequired(model.getBranchName(), "Branch Name");
			ValidationUtils.checkRequired(model.getBranchUsername(), "Username");
			ValidationUtils.checkRequired(model.getBranchPassword(), "Password");
			ValidationUtils.checkRequired(model.getBranchExpireDate(), "Expire Date");

			model.setValidateModel("branchCode", model.getBranchCode());
			ValidationUtils.checkDuplicate(validate(model), "Branch Code");

			Branch branch = new Branch();
			branch.setCompanyId(model.getCompanyId());
			branch.setBranchCode(model.getBranchCode());
			branch.setBranchName(model.getBranchName());
			branch.setBranchStatus(model.getBranchStatus());
			branch.setCreatedDate(LocalDateTime.now());
			branch.setCreatedUser(SecurityUtils.getUsername());
			branch.setUpdatedDate(LocalDateTime.now());
			branch.setUpdatedUser(SecurityUtils.getUsername());

			branch.setDeclarationCost(model.getDeclarationCost());
			branch.setWebAppCost(model.getWebAppCost());
			branch.setPerTripCost(model.getPerTripCost());
			branch.setCurrency(model.getCurrency());
			branch.setExchangeRate(model.getExchangeRate());

			branch = branchRepository.save(branch);

			model.setBranchId(branch.getBranchId());

			model.setValidateModel("branchUsername", model.getBranchUsername());
			ValidationUtils.checkDuplicate(validate(model), "Username");

			User user = new User();
			user.setCompanyId(model.getCompanyId());
			user.setBranchId(model.getBranchId());
			user.setUserLogin(model.getBranchUsername());
			user.setUserPwd(model.getBranchPassword());
			user.setUserName(model.getBranchUsername());
			user.setIsOfficer(0);
			user.setUserStartDate(LocalDate.now());
			user.setUserExpireDate(model.getBranchExpireDate());

			user = userRepository.save(user);
			model.setBranchUserId(user.getUserId());
			branch.setUserId(user.getUserId());

			branchRepository.save(branch);
		}

		return Data.of(model);
	}

	@Transactional
	@CacheEvict(cacheNames = { "branchComboBox" }, allEntries = true)
	public Data edit(final BranchModel model) {
		if (CoreUtils.isNotNull(model.getBranchId())) {
			ValidationUtils.checkRequired(model.getCompanyId(), "Company Code");
			ValidationUtils.checkRequired(model.getBranchStatus(), "Status");
			ValidationUtils.checkRequired(model.getBranchName(), "Branch Name");
			ValidationUtils.checkRequired(model.getBranchUsername(), "Username");
			ValidationUtils.checkRequired(model.getBranchPassword(), "Password");
			ValidationUtils.checkRequired(model.getBranchExpireDate(), "Expire Date");

			User user = null;
			if (CoreUtils.isNotNull(model.getBranchUserId())) {
				user = userRepository.findById(model.getBranchUserId()).get();
				user.setUserLogin(model.getBranchUsername());
				user.setUserPwd(model.getBranchPassword());
				user.setUserName(model.getBranchUsername());
				user.setUserExpireDate(model.getBranchExpireDate());
				userRepository.save(user);
			} else {
				user = new User();
				user.setCompanyId(model.getCompanyId());
				user.setBranchId(model.getBranchId());
				user.setUserLogin(model.getBranchUsername());
				user.setUserPwd(model.getBranchPassword());
				user.setUserName(model.getBranchUsername());
				user.setIsOfficer(0);
				user.setUserStartDate(LocalDate.now());
				user.setUserExpireDate(model.getBranchExpireDate());

				user = userRepository.save(user);
				model.setBranchUserId(user.getUserId());
			}

			Branch branch = branchRepository.findById(model.getBranchId()).get();
			branch.setCompanyId(model.getCompanyId());
			branch.setBranchName(model.getBranchName());
			branch.setBranchStatus(model.getBranchStatus());
			branch.setUpdatedDate(LocalDateTime.now());
			branch.setUpdatedUser(SecurityUtils.getUsername());
			branch.setUserId(model.getBranchUserId());

			branch.setDeclarationCost(model.getDeclarationCost());
			branch.setWebAppCost(model.getWebAppCost());
			branch.setPerTripCost(model.getPerTripCost());
			branch.setCurrency(model.getCurrency());
			branch.setExchangeRate(model.getExchangeRate());

			branchRepository.save(branch);
		}

		return Data.of(model);
	}

	@Transactional
	@CacheEvict(cacheNames = { "branchComboBox" }, allEntries = true)
	public Data delete(final BranchModel model) {
		Branch branch = branchRepository.findById(model.getBranchId()).get();

		// tagRepository.clearAllTagsByCompanyIdAndBranchId(branch.getCpnId(),
		// branch.getBranchId());
		userPrivilegeRepository.deleteAllPrivilesgeByUserId(branch.getUserId());
		branchRepository.deleteById(branch.getBranchId());
		userRepository.deleteById(branch.getUserId());

		return Data.of();
	}
}
