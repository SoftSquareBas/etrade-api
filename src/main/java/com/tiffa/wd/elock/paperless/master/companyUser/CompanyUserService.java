package com.tiffa.wd.elock.paperless.master.companyUser;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.tiffa.wd.elock.paperless.core.Data;
import com.tiffa.wd.elock.paperless.core.GridData;
import com.tiffa.wd.elock.paperless.core.Sort;
import com.tiffa.wd.elock.paperless.core.entity.User;
import com.tiffa.wd.elock.paperless.core.entity.UserPrivilege;
import com.tiffa.wd.elock.paperless.core.entity.UserPrivilegePk;
import com.tiffa.wd.elock.paperless.core.entity.UserRole;
import com.tiffa.wd.elock.paperless.core.entity.UserRolePk;
import com.tiffa.wd.elock.paperless.core.repository.CoreRepository;
import com.tiffa.wd.elock.paperless.core.repository.SqlParams;
import com.tiffa.wd.elock.paperless.core.repository.SqlSort;
import com.tiffa.wd.elock.paperless.core.repository.SqlTypeConversion;
import com.tiffa.wd.elock.paperless.core.repository.UserPrivilegeRepository;
import com.tiffa.wd.elock.paperless.core.repository.UserRepository;
import com.tiffa.wd.elock.paperless.core.repository.UserRoleRepository;
import com.tiffa.wd.elock.paperless.core.util.CoreUtils;
import com.tiffa.wd.elock.paperless.core.util.SecurityUtils;
import com.tiffa.wd.elock.paperless.core.util.ValidationUtils;

@Service
@Transactional(readOnly = true, propagation = Propagation.NEVER)
public class CompanyUserService {

	@Autowired
	private CoreRepository coreRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserPrivilegeRepository userPrivilegeRepository;

	@Autowired
	private UserRoleRepository userRoleRepository;

	public GridData search(final CompanyUserModel model) {
		SqlParams params = SqlParams.createPageParam(model);
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT a.*");
		sql.append(" FROM ( ");
		sql.append(" 	SELECT u.user_id AS \"userId\" ");
		sql.append(" 		, c.cpn_code AS \"companyCode\" ");
		sql.append(" 		, u.user_login AS \"username\" ");
		sql.append(" 		, u.user_name AS \"userFullName\" ");
		sql.append(" 		, u.user_startdate AS \"effectiveDate\" ");
		sql.append(" 		, u.user_expiredate AS \"expireDate\" ");
		sql.append(" 		, (CASE WHEN u.user_id = c.user_id THEN 1 ELSE 0 END) AS \"isSecureUser\" ");
		sql.append("		, c.cpn_id AS \"companyId\" ");
		sql.append(" 	FROM user_paperless u  ");
		sql.append(" 		JOIN company c ON c.cpn_id = u.cpn_id ");
		sql.append(" 	WHERE u.branch_id IS NULL ");

		if (CoreUtils.isNotNull(model.getCompanyId())) {
			sql.append("    AND c.cpn_id = :companyId ");
			params.add("companyId", model.getCompanyId());
		}

		if (CoreUtils.isNotEmpty(model.getUsername())) {
			sql.append(" 	AND LOWER(u.user_login) LIKE LOWER(:username) ");
			params.add("username", "%" + model.getUsername() + "%");
		}

		if (CoreUtils.isNotEmpty(model.getUserFullName())) {
			sql.append(" 	AND LOWER(u.user_name) LIKE LOWER(:userFullName) ");
			params.add("userFullName", "%" + model.getUserFullName() + "%");
		}

		if (CoreUtils.isNotNull(model.getShowOnlyExpireUsers()) && model.getShowOnlyExpireUsers()) {
			sql.append(" 	AND u.user_expiredate < :todayDate ");
			params.add("todayDate", LocalDate.now());
		}

		sql.append(" ) a ");
		// sql.append(SqlUtils.generateSqlOrderBy(model, "a", Sort.by("companyCode",
		// Direction.ASC), Sort.by("username", Direction.ASC)));

		SqlSort sort = SqlSort.create(model, Sort.by("companyCode", Direction.ASC), Sort.by("username", Direction.ASC));

		SqlTypeConversion conversion = SqlTypeConversion.create();
		conversion.add("isSecureUser", Boolean.class);
		GridData gridData = coreRepository.searchPagingGridData(sql.toString(), params, sort, conversion);

		return gridData;
	}

	public Data load(final CompanyUserModel model) {
		if (CoreUtils.isNotNull(model.getUserId())) {
			SqlParams params = SqlParams.create();

			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT u.user_id AS \"userId\" ");
			sql.append(" 	, u.user_login AS \"username\" ");
			sql.append(" 	, u.user_name AS \"userFullName\" ");
			sql.append(" 	, u.user_pwd AS \"password\" ");
			sql.append(" 	, u.user_startdate AS \"effectiveDate\" ");
			sql.append(" 	, u.user_expiredate AS \"expireDate\" ");
			sql.append(" 	, u.cpn_id AS \"companyId\" ");
			sql.append(" FROM user_paperless u ");
			sql.append(" WHERE u.cpn_id = :companyId ");
			sql.append(" 	AND u.user_id = :userId ");

			params.add("companyId", model.getCompanyId());
			params.add("userId", model.getUserId());
			return coreRepository.getData(sql.toString(), params);
		}
		return Data.of();
	}

	public GridData loadAccessRight(final CompanyUserModel model) {
		SqlParams params = SqlParams.create();
		StringBuilder sql = new StringBuilder();

		sql.append(" WITH assign_privilege AS ( ");
		sql.append(" 	SELECT DISTINCT up.privilege_id ");
		sql.append(" 	FROM se_user_privilege up ");
		sql.append(" 	WHERE up.user_id = :userId ");
		sql.append(" ), company_privilege AS ( ");
		sql.append(" 	SELECT DISTINCT up.privilege_id ");
		sql.append(" 	FROM se_user_privilege up ");
		sql.append(" 	WHERE up.user_id = :companyUserId ");
		sql.append(" ), assign_role AS ( ");
		sql.append(" 	SELECT DISTINCT ur.role_id ");
		sql.append(" 	FROM se_user_role ur ");
		sql.append(" 	WHERE ur.user_id = :userId ");
		sql.append(" ), company_role AS ( ");
		sql.append(" 	SELECT DISTINCT r.role_id ");
		sql.append(" 	FROM se_role r ");
		sql.append(" ) ");
		sql.append(" SELECT a.\"key\", a.\"title\", a.\"type\", a.\"direction\", a.\"source\" ");
		sql.append(" FROM ( ");
		sql.append(" 	SELECT r.role_id AS \"key\", ");
		sql.append(" 		'ROLE : ' || r.rolecode AS \"title\", ");
		sql.append(" 		'role' AS \"type\", ");
		sql.append(" 		(CASE WHEN ar.role_id IS NOT NULL THEN 'right' ELSE 'left' END) AS \"direction\", ");
		sql.append(" 		(CASE WHEN ar.role_id IS NOT NULL THEN 'right' ELSE 'left' END) AS \"source\", ");
		sql.append(" 		1 AS \"ordinal\" ");
		sql.append(" 	FROM company_role rp ");
		sql.append(" 		JOIN se_role r ON r.role_id = rp.role_id ");
		sql.append(" 		LEFT JOIN assign_role ar ON ar.role_id = rp.role_id ");
		sql.append(" 	UNION ALL ");
		sql.append(" 	SELECT p.privilege_id AS \"key\", ");
		sql.append(" 		p.privilege AS \"title\", ");
		sql.append(" 		'privilege' AS \"type\", ");
		sql.append(" 		(CASE WHEN ap.privilege_id IS NOT NULL THEN 'right' ELSE 'left' END) AS \"direction\", ");
		sql.append(" 		(CASE WHEN ap.privilege_id IS NOT NULL THEN 'right' ELSE 'left' END) AS \"source\", ");
		sql.append(" 		2 AS \"ordinal\" ");
		sql.append(" 	FROM company_privilege up ");
		sql.append(" 		JOIN se_privilege p ON p.privilege_id = up.privilege_id ");
		sql.append(" 		LEFT JOIN assign_privilege ap ON ap.privilege_id = up.privilege_id ");
		sql.append(" ) a ");
		sql.append(" ORDER BY a.\"ordinal\", a.\"title\" ");

		params.add("userId", model.getUserId());
		params.add("companyUserId", model.getUserId());
		params.add("companyId", model.getCompanyId());

		return coreRepository.searchGridData(sql.toString(), params);
	}

	public Data validate(final CompanyUserModel model) {
		SqlParams params = SqlParams.createValidateParam(model);
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT CASE WHEN COUNT(1) > 0 THEN 1 ELSE 0 END AS \"duplicate\" ");
		sql.append(" FROM user_paperless u ");
		sql.append(" WHERE 1 = 1 ");
		sql.append(" AND u.cpn_id = :companyId ");
		sql.append(" AND LOWER(u.user_login) = LOWER(:username) ");

		params.add("companyId", SecurityUtils.getCompanyId());
		if (CoreUtils.isNotNull(model.getUserId())) {
			sql.append(" AND u.user_id <> :userId ");
			params.add("userId", model.getUserId());
		}
		return coreRepository.validate(sql.toString(), params);
	}

	private void saveAccesRight(final CompanyUserModel model) {
		for (CompanyUserModel.TransferItem item : model.getItems()) {
			if ("right".equals(item.getSource()) && "left".equals(item.getDirection())) {
				if ("privilege".equals(item.getType())) {
					userPrivilegeRepository.deleteAllByUserIdAndPrivilegeId(model.getUserId(), item.getKey());
				} else if ("role".equals(item.getType())) {
					userRoleRepository.deleteAllByUserIdAndRoldId(model.getUserId(), item.getKey());
				}
			} else if ("left".equals(item.getSource()) && "right".equals(item.getDirection())) {
				if ("privilege".equals(item.getType())) {
					UserPrivilegePk pk = new UserPrivilegePk();
					pk.setUserId(model.getUserId());
					pk.setPrivilegeId(item.getKey());
					UserPrivilege up = new UserPrivilege();
					up.setPk(pk);

					userPrivilegeRepository.save(up);
				} else if ("role".equals(item.getType())) {
					UserRolePk pk = new UserRolePk();
					pk.setUserId(model.getUserId());
					pk.setRoleId(item.getKey());

					UserRole ur = new UserRole();
					ur.setPk(pk);
					userRoleRepository.save(ur);
				}
			}
		}
	}

	@Transactional
	public Data add(final CompanyUserModel model) {
		ValidationUtils.checkRequired(model.getCompanyId(), "Company Code");
		ValidationUtils.checkRequired(model.getUsername(), "Username");
		ValidationUtils.checkRequired(model.getPassword(), "Password");
		ValidationUtils.checkRequired(model.getUserFullName(), "User Full Name");
		ValidationUtils.checkRequired(model.getEffectiveDate(), "Effective Date");
		ValidationUtils.checkRequired(model.getExpireDate(), "Expire Date");

		model.setValidateModel("username", model.getUsername());
		ValidationUtils.checkDuplicate(validate(model), "Username");

		User user = new User();
		user.setCompanyId(model.getCompanyId());
		user.setUserLogin(model.getUsername());
		user.setUserPwd(model.getPassword());
		user.setUserName(model.getUserFullName());
		user.setUserStartDate(model.getEffectiveDate());
		user.setUserExpireDate(model.getExpireDate());

		model.setUserId(userRepository.save(user).getUserId());

		saveAccesRight(model);
		return Data.of(model);
	}

	@Transactional
	public Data edit(final CompanyUserModel model) {
		ValidationUtils.checkRequired(model.getCompanyId(), "Company Code");
		ValidationUtils.checkRequired(model.getUsername(), "Username");
		ValidationUtils.checkRequired(model.getPassword(), "Password");
		ValidationUtils.checkRequired(model.getUserFullName(), "User Full Name");
		ValidationUtils.checkRequired(model.getEffectiveDate(), "Effective Date");
		ValidationUtils.checkRequired(model.getExpireDate(), "Expire Date");

		model.setValidateModel("username", model.getUsername());
		ValidationUtils.checkDuplicate(validate(model), "Username");

		User user = userRepository.findById(model.getUserId()).get();
		user.setCompanyId(model.getCompanyId());
		user.setUserLogin(model.getUsername());
		user.setUserPwd(model.getPassword());
		user.setUserName(model.getUserFullName());
		user.setUserStartDate(model.getEffectiveDate());
		user.setUserExpireDate(model.getExpireDate());

		saveAccesRight(model);
		return Data.of(model);
	}

	@Transactional
	public Data delete(final CompanyUserModel model) {
		userRoleRepository.deleteAllRolesByUserId(model.getUserId());
		userPrivilegeRepository.deleteAllPrivilesgeByUserId(model.getUserId());
		userRepository.deleteById(model.getUserId());
		return Data.of();
	}

}
