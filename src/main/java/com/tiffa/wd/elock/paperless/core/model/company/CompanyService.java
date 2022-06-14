package com.tiffa.wd.elock.paperless.core.model.company;

import java.time.LocalDate;

import com.tiffa.wd.elock.paperless.core.Data;
import com.tiffa.wd.elock.paperless.core.GridData;
import com.tiffa.wd.elock.paperless.core.Sort;
import com.tiffa.wd.elock.paperless.core.entity.Company;
import com.tiffa.wd.elock.paperless.core.entity.User;
import com.tiffa.wd.elock.paperless.core.entity.UserPrivilege;
import com.tiffa.wd.elock.paperless.core.entity.UserPrivilegePk;
import com.tiffa.wd.elock.paperless.core.repository.CompanyRepository;
import com.tiffa.wd.elock.paperless.core.repository.CoreRepository;
import com.tiffa.wd.elock.paperless.core.repository.SqlParams;
import com.tiffa.wd.elock.paperless.core.repository.SqlSort;
import com.tiffa.wd.elock.paperless.core.repository.SqlTypeConversion;
import com.tiffa.wd.elock.paperless.core.repository.UserPrivilegeRepository;
import com.tiffa.wd.elock.paperless.core.repository.UserRepository;
import com.tiffa.wd.elock.paperless.core.util.CoreUtils;
import com.tiffa.wd.elock.paperless.core.util.ValidationUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true, propagation = Propagation.NEVER)
public class CompanyService {

	@Autowired
	private CoreRepository coreRepository;

	@Autowired
	private CompanyRepository companyRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserPrivilegeRepository userPrivilegeRepository;

	// @Autowired
	// private TagRepository tagRepository;

	public GridData search(final CompanyModel model) {
		SqlParams params = SqlParams.createPageParam(model);
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT a.* ");
		sql.append(" FROM ( ");
		sql.append("	SELECT ");
		sql.append("   	 	 c.cpn_id AS \"companyId\" ");
		sql.append("   		, c.cpn_code AS \"companyCode\" ");
		sql.append("   		, c.cpn_status AS \"companyStatus\" ");
		sql.append("   		, c.cpn_name AS \"companyName\" ");
		sql.append("	FROM company c ");
		sql.append("  	WHERE 1 = 1 ");

		if (CoreUtils.isNotEmpty(model.getCompanyCode())) {
			sql.append("    AND LOWER(c.cpn_code) LIKE LOWER(:companyCode) ");
			params.add("companyCode", "%" + model.getCompanyCode() + "%");
		}

		if (CoreUtils.isNotEmpty(model.getCompanyName())) {
			sql.append("    AND LOWER(c.cpn_name) LIKE LOWER(:companyName) ");
			params.add("companyName", "%" + model.getCompanyName() + "%");
		}

		if (CoreUtils.isNotNull(model.getCompanyStatus())) {
			sql.append("    AND c.cpn_status = :companyStatus ");
			params.add("companyStatus", model.getCompanyStatus());
		}
		sql.append(" ) a ");
		// sql.append(SqlUtils.generateSqlOrderBy(model, "a", Sort.by("companyCode",
		// Direction.ASC)));

		SqlSort sort = SqlSort.create(model, Sort.by("companyCode", Direction.ASC));
		return coreRepository.searchPagingGridData(sql.toString(), params, sort);
	}

	public GridData loadAccessRight(final CompanyModel model) {
		SqlParams params = SqlParams.create();
		StringBuilder sql = new StringBuilder();

		sql.append(" WITH user_tab AS ( ");
		sql.append(" 	SELECT DISTINCT up.privilege_id ");
		sql.append(" 	FROM se_user_privilege up ");
		sql.append(" 	WHERE up.user_id = :companyUserId ");
		sql.append(" ) ");
		sql.append(" SELECT p.privilege_id AS \"key\", ");
		sql.append(" 	p.privilege AS \"title\", ");
		sql.append(" 	'privilege' AS \"type\", ");
		sql.append(" 	(CASE WHEN up.privilege_id IS NOT NULL THEN 'right' ELSE 'left' END) AS \"direction\", ");
		sql.append(" 	(CASE WHEN up.privilege_id IS NOT NULL THEN 'right' ELSE 'left' END) AS \"source\" ");
		sql.append(" FROM se_privilege p ");
		sql.append(" 	LEFT JOIN user_tab up ON up.privilege_id = p.privilege_id ");
		sql.append(" ORDER BY p.privilege ");
		params.add("companyUserId", model.getCompanyUserId());

		return coreRepository.searchGridData(sql.toString(), params);
	}

	@Transactional
	public Data saveAccessRight(final CompanyModel model) {

		for (CompanyModel.TransferItem item : model.getItems()) {
			if ("right".equals(item.getSource()) && "left".equals(item.getDirection())) {
				UserPrivilegePk pk = new UserPrivilegePk();
				pk.setUserId(model.getCompanyUserId());
				pk.setPrivilegeId(item.getKey());
				UserPrivilege up = new UserPrivilege();
				up.setPk(pk);

				userPrivilegeRepository.delete(up);
			} else if ("left".equals(item.getSource()) && "right".equals(item.getDirection())) {
				UserPrivilegePk pk = new UserPrivilegePk();
				pk.setUserId(model.getCompanyUserId());
				pk.setPrivilegeId(item.getKey());
				UserPrivilege up = new UserPrivilege();
				up.setPk(pk);

				userPrivilegeRepository.save(up);
			}
		}
		return Data.of();
	}

	public Data load(final CompanyModel model) {
		SqlParams params = SqlParams.create();

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT c.cpn_id AS \"companyId\", ");
		sql.append(" 	c.cpn_code AS \"companyCode\", ");
		sql.append(" 	c.cpn_name AS \"companyName\", ");
		sql.append(" 	c.cpn_fullname AS \"companyFullName\", ");
		sql.append(" 	c.cpn_address AS \"companyAddress\", ");
		sql.append(" 	c.cpn_telephone AS \"companyTelephone\", ");
		sql.append(" 	c.cpn_fax AS \"companyFax\", ");
		sql.append(" 	c.cpn_status AS \"companyStatus\", ");
		sql.append(" 	c.cpn_taxid AS \"companyTaxId\", ");
		sql.append(" 	c.cpn_establishno AS \"companyEstablishNo\", ");
		sql.append(" 	c.prefix_code AS \"tripInfoPrefixCode\", ");
		sql.append(" 	c.no_digit AS \"tripInfoNumberOfDigit\", ");

		sql.append(
				" 	CASE WHEN c.ops_year = 1 AND c.ops_year_format = 'YYYY' THEN 1 ELSE 0 END AS \"tripInfoYearYYYY\", ");
		sql.append(
				" 	CASE WHEN c.ops_year = 1 AND c.ops_year_format = 'YY' THEN 1 ELSE 0 END AS \"tripInfoYearYY\", ");

		sql.append(" 	c.ops_month AS \"tripInfoMonthMM\", ");
		sql.append(" 	c.ops_day AS \"tripInfoDayDD\", ");
		sql.append(" 	c.current_running AS \"tripInfoCurrentRunning\", ");
		sql.append(" 	c.col_begin_journey AS \"beginJourneyShow\", ");
		sql.append(" 	c.col_end_journey AS \"endJourneyShow\", ");
		sql.append(" 	c.col_disarm AS \"disarmShow\", ");
		sql.append(" 	c.auto_begin_journey AS \"beginJourneyAutofill\", ");
		sql.append(" 	c.auto_end_journey AS \"endJourneyAutofill\", ");
		sql.append(" 	c.auto_disarm AS \"disarmAutofill\", ");
		sql.append(" 	u.user_id AS \"companyUserId\", ");
		sql.append(" 	u.user_login AS \"companyUsername\", ");
		sql.append(" 	u.user_pwd AS \"companyPassword\", ");
		sql.append(" 	u.user_expiredate AS \"companyExpireDate\" ");
		sql.append(" FROM company c ");
		sql.append(" 	LEFT JOIN user_paperless u ON u.user_id = c.user_id AND u.cpn_id = c.cpn_id ");
		sql.append(" WHERE c.cpn_id = :companyId ");
		params.add("companyId", model.getCompanyId());

		SqlTypeConversion conversion = SqlTypeConversion.create();
		conversion.add("tripInfoYearYYYY", Boolean.class);
		conversion.add("tripInfoYearYY", Boolean.class);
		conversion.add("tripInfoMonthMM", Boolean.class);
		conversion.add("tripInfoDayDD", Boolean.class);
		conversion.add("beginJourneyShow", Boolean.class);
		conversion.add("endJourneyShow", Boolean.class);
		conversion.add("disarmShow", Boolean.class);
		conversion.add("beginJourneyAutofill", Boolean.class);
		conversion.add("endJourneyAutofill", Boolean.class);
		conversion.add("disarmAutofill", Boolean.class);

		Data data = coreRepository.getData(sql.toString(), params, conversion);

		// data.put("tripInfoYearYYYY", Boolean.FALSE );
		// data.put("tripInfoYearYY", Boolean.FALSE );
		//
		// if("1".equals(data.get("tripInfoYear"))) {
		// if("YYYY".equals(data.get("tripInfoYearFormat"))) {
		// data.put("tripInfoYearYYYY", Boolean.TRUE );
		// } else if("YY".equals(data.get("tripInfoYearFormat"))) {
		// data.put("tripInfoYearYY", Boolean.TRUE );
		// }
		// }
		// data.remove("tripInfoYear");
		// data.remove("tripInfoYearFormat");
		//
		// data.put("tripInfoMonthMM", SqlUtils.decode(data.get("tripInfoMonthMM"), "1",
		// Boolean.TRUE, Boolean.FALSE));
		// data.put("tripInfoDayDD", SqlUtils.decode(data.get("tripInfoDayDD"), "1",
		// Boolean.TRUE, Boolean.FALSE));
		//
		// data.put("beginJourneyShow", SqlUtils.decode(data.get("beginJourneyShow"),
		// "1", Boolean.TRUE, Boolean.FALSE));
		// data.put("endJourneyShow", SqlUtils.decode(data.get("endJourneyShow"), "1",
		// Boolean.TRUE, Boolean.FALSE));
		// data.put("disarmShow", SqlUtils.decode(data.get("disarmShow"), "1",
		// Boolean.TRUE, Boolean.FALSE));
		// data.put("beginJourneyAutofill",
		// SqlUtils.decode(data.get("beginJourneyAutofill"), "1", Boolean.TRUE,
		// Boolean.FALSE));
		// data.put("endJourneyAutofill",
		// SqlUtils.decode(data.get("endJourneyAutofill"), "1", Boolean.TRUE,
		// Boolean.FALSE));
		// data.put("disarmAutofill", SqlUtils.decode(data.get("disarmAutofill"), "1",
		// Boolean.TRUE, Boolean.FALSE));

		return data;
	}

	public Data validate(final CompanyModel model) {
		if ("companyCode".equals(model.getField())) {
			SqlParams params = SqlParams.createValidateParam(model);
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT CASE WHEN COUNT(1) > 0 THEN 1 ELSE 0 END AS \"duplicate\" ");
			sql.append(" FROM company c ");
			sql.append(" WHERE 1 = 1 ");
			sql.append(" AND LOWER(c.cpn_code) = LOWER(:companyCode) ");
			return coreRepository.validate(sql.toString(), params);
		} else if ("companyUsername".equals(model.getField())) {
			SqlParams params = SqlParams.createValidateParam(model);
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT CASE WHEN COUNT(1) > 0 THEN 1 ELSE 0 END AS \"duplicate\" ");
			sql.append(" FROM user_paperless u ");
			sql.append(" WHERE u.cpn_id = :companyId ");
			sql.append(" AND LOWER(u.user_login) = LOWER(:companyUsername) ");
			sql.append(" AND u.user_id <> :companyUserId ");
			params.add("companyId", model.getCompanyId());
			params.add("companyUserId", model.getCompanyUserId());
			return coreRepository.validate(sql.toString(), params);
		}

		return Data.of("duplicate", false);

	}

	@Transactional
	@CacheEvict(cacheNames = { "companyComboBox" }, allEntries = true)
	public Data add(final CompanyModel model) {

		if (CoreUtils.isNull(model.getCompanyId())) {
			ValidationUtils.checkRequired(model.getCompanyCode(), "Company Code");
			ValidationUtils.checkRequired(model.getCompanyStatus(), "Status");
			ValidationUtils.checkRequired(model.getCompanyName(), "Company Name");
			ValidationUtils.checkRequired(model.getCompanyFullName(), "Company Name (Full)");
			ValidationUtils.checkRequired(model.getCompanyAddress(), "Company Address");
			ValidationUtils.checkRequired(model.getCompanyTaxId(), "Company Tax ID");
			ValidationUtils.checkRequired(model.getCompanyTelephone(), "Tel");
			ValidationUtils.checkRequired(model.getCompanyFax(), "Fax");
			ValidationUtils.checkRequired(model.getCompanyEstablishNo(), "Establish No.");
			ValidationUtils.checkRequired(model.getTripInfoNumberOfDigit(), "Number of Digit");
			ValidationUtils.checkRequired(model.getTripInfoCurrentRunning(), "Current Running");
			ValidationUtils.checkRequired(model.getCompanyUsername(), "Username");
			ValidationUtils.checkRequired(model.getCompanyPassword(), "Password");
			ValidationUtils.checkRequired(model.getCompanyExpireDate(), "Expire Date");

			model.setValidateModel("companyCode", model.getCompanyCode());
			ValidationUtils.checkDuplicate(validate(model), "Company Code");

			Company company = new Company();
			company.setCompanyCode(model.getCompanyCode());
			company.setCpnStatus(model.getCompanyStatus());
			company.setCompanyName(model.getCompanyName());
			company.setCompanyFullName(model.getCompanyFullName());
			company.setCpnAddress(model.getCompanyAddress());
			company.setCpnTaxId(model.getCompanyTaxId());
			company.setCpnTelephone(model.getCompanyTelephone());
			company.setCpnFax(model.getCompanyFax());
			company.setCpnEstablishNo(model.getCompanyEstablishNo());

			String year = "0";
			String yearFormat = null;
			if (CoreUtils.isNotNull(model.getTripInfoYearYYYY()) && model.getTripInfoYearYYYY()) {
				year = "1";
				yearFormat = "YYYY";
			} else if (CoreUtils.isNotNull(model.getTripInfoYearYY()) && model.getTripInfoYearYY()) {
				year = "1";
				yearFormat = "YY";
			}

			company.setOpsYear(year);
			company.setOpsYearFormat(yearFormat);

			company.setOpsMonth(
					(CoreUtils.isNotNull(model.getTripInfoMonthMM()) && model.getTripInfoMonthMM()) ? "1" : "0");
			company.setOpsDay((CoreUtils.isNotNull(model.getTripInfoDayDD()) && model.getTripInfoDayDD()) ? "1" : "0");

			company.setNoDigit(model.getTripInfoNumberOfDigit());

			String prefixCode = model.getCompanyCode();

			if (CoreUtils.isNotNull(model.getTripInfoYearYYYY()) && model.getTripInfoYearYYYY()) {
				prefixCode += "YYYY";
			} else if (CoreUtils.isNotNull(model.getTripInfoYearYY()) && model.getTripInfoYearYY()) {
				prefixCode += "YY";
			}

			if (CoreUtils.isNotNull(model.getTripInfoMonthMM()) && model.getTripInfoMonthMM()) {
				prefixCode += "MM";
			}

			if (CoreUtils.isNotNull(model.getTripInfoDayDD()) && model.getTripInfoDayDD()) {
				prefixCode += "DD";
			}

			company.setPrefixCode(prefixCode);
			company.setCurrentRunning(model.getTripInfoCurrentRunning());

			company.setColBeginJourney(
					(CoreUtils.isNotNull(model.getBeginJourneyShow()) && model.getBeginJourneyShow()) ? "1" : "0");
			company.setAutoBeginJourney(
					(CoreUtils.isNotNull(model.getBeginJourneyAutofill()) && model.getBeginJourneyAutofill()) ? "1"
							: "0");
			company.setColEndJourney(
					(CoreUtils.isNotNull(model.getEndJourneyShow()) && model.getEndJourneyShow()) ? "1" : "0");
			company.setAutoEndJourney(
					(CoreUtils.isNotNull(model.getEndJourneyAutofill()) && model.getEndJourneyAutofill()) ? "1" : "0");
			company.setColDisarm((CoreUtils.isNotNull(model.getDisarmShow()) && model.getDisarmShow()) ? "1" : "0");
			company.setAutoDisarm(
					(CoreUtils.isNotNull(model.getDisarmAutofill()) && model.getDisarmAutofill()) ? "1" : "0");

			company = companyRepository.save(company);

			model.setCompanyId(company.getCompanyId());
			model.setValidateModel("companyUsername", model.getCompanyUsername());
			ValidationUtils.checkDuplicate(validate(model), "Username");

			User user = new User();
			user.setCompanyId(company.getCompanyId());
			user.setUserLogin(model.getCompanyUsername());
			user.setUserPwd(model.getCompanyPassword());
			user.setUserName(model.getCompanyUsername());
			user.setIsOfficer(0);
			user.setUserStartDate(LocalDate.now());
			user.setUserExpireDate(model.getCompanyExpireDate());

			user = userRepository.save(user);
			model.setCompanyUserId(user.getUserId());

			company.setUserId(user.getUserId());
			companyRepository.save(company);
		}

		return Data.of(model);
	}

	@Transactional
	@CacheEvict(cacheNames = { "companyComboBox" }, allEntries = true)
	public Data edit(final CompanyModel model) {
		if (CoreUtils.isNotNull(model.getCompanyId())) {
			ValidationUtils.checkRequired(model.getCompanyStatus(), "Status");
			ValidationUtils.checkRequired(model.getCompanyName(), "Company Name");
			ValidationUtils.checkRequired(model.getCompanyFullName(), "Company Name (Full)");
			ValidationUtils.checkRequired(model.getCompanyAddress(), "Company Address");
			ValidationUtils.checkRequired(model.getCompanyTaxId(), "Company Tax ID");
			ValidationUtils.checkRequired(model.getCompanyTelephone(), "Tel");
			ValidationUtils.checkRequired(model.getCompanyFax(), "Fax");
			ValidationUtils.checkRequired(model.getCompanyEstablishNo(), "Establish No.");
			ValidationUtils.checkRequired(model.getTripInfoNumberOfDigit(), "Number of Digit");
			ValidationUtils.checkRequired(model.getTripInfoCurrentRunning(), "Current Running");
			ValidationUtils.checkRequired(model.getCompanyUsername(), "Username");
			ValidationUtils.checkRequired(model.getCompanyPassword(), "Password");
			ValidationUtils.checkRequired(model.getCompanyExpireDate(), "Expire Date");

			User user = null;
			if (CoreUtils.isNotNull(model.getCompanyUserId())) {
				user = userRepository.findById(model.getCompanyUserId()).get();
				user.setUserLogin(model.getCompanyUsername());
				user.setUserPwd(model.getCompanyPassword());
				user.setUserName(model.getCompanyUsername());
				user.setUserExpireDate(model.getCompanyExpireDate());
				userRepository.save(user);
			} else {
				user = new User();
				user.setCompanyId(model.getCompanyId());
				user.setUserLogin(model.getCompanyUsername());
				user.setUserPwd(model.getCompanyPassword());
				user.setUserName(model.getCompanyUsername());
				user.setIsOfficer(0);
				user.setUserStartDate(LocalDate.now());
				user.setUserExpireDate(model.getCompanyExpireDate());

				user = userRepository.save(user);
				model.setCompanyUserId(user.getUserId());
			}

			Company company = companyRepository.findById(model.getCompanyId()).get();

			company.setCpnStatus(model.getCompanyStatus());
			company.setCompanyName(model.getCompanyName());
			company.setCompanyFullName(model.getCompanyFullName());
			company.setCpnAddress(model.getCompanyAddress());
			company.setCpnTaxId(model.getCompanyTaxId());
			company.setCpnTelephone(model.getCompanyTelephone());
			company.setCpnFax(model.getCompanyFax());
			company.setCpnEstablishNo(model.getCompanyEstablishNo());
			company.setUserId(user.getUserId());

			String year = "0";
			String yearFormat = null;
			if (CoreUtils.isNotNull(model.getTripInfoYearYYYY()) && model.getTripInfoYearYYYY()) {
				year = "1";
				yearFormat = "YYYY";
			} else if (CoreUtils.isNotNull(model.getTripInfoYearYY()) && model.getTripInfoYearYY()) {
				year = "1";
				yearFormat = "YY";
			}

			company.setOpsYear(year);
			company.setOpsYearFormat(yearFormat);

			company.setOpsMonth(
					(CoreUtils.isNotNull(model.getTripInfoMonthMM()) && model.getTripInfoMonthMM()) ? "1" : "0");
			company.setOpsDay((CoreUtils.isNotNull(model.getTripInfoDayDD()) && model.getTripInfoDayDD()) ? "1" : "0");

			company.setNoDigit(model.getTripInfoNumberOfDigit());

			String prefixCode = model.getCompanyCode();

			if (CoreUtils.isNotNull(model.getTripInfoYearYYYY()) && model.getTripInfoYearYYYY()) {
				prefixCode += "YYYY";
			} else if (CoreUtils.isNotNull(model.getTripInfoYearYY()) && model.getTripInfoYearYY()) {
				prefixCode += "YY";
			}

			if (CoreUtils.isNotNull(model.getTripInfoMonthMM()) && model.getTripInfoMonthMM()) {
				prefixCode += "MM";
			}

			if (CoreUtils.isNotNull(model.getTripInfoDayDD()) && model.getTripInfoDayDD()) {
				prefixCode += "DD";
			}

			company.setPrefixCode(prefixCode);
			company.setCurrentRunning(model.getTripInfoCurrentRunning());

			company.setColBeginJourney(
					(CoreUtils.isNotNull(model.getBeginJourneyShow()) && model.getBeginJourneyShow()) ? "1" : "0");
			company.setAutoBeginJourney(
					(CoreUtils.isNotNull(model.getBeginJourneyAutofill()) && model.getBeginJourneyAutofill()) ? "1"
							: "0");
			company.setColEndJourney(
					(CoreUtils.isNotNull(model.getEndJourneyShow()) && model.getEndJourneyShow()) ? "1" : "0");
			company.setAutoEndJourney(
					(CoreUtils.isNotNull(model.getEndJourneyAutofill()) && model.getEndJourneyAutofill()) ? "1" : "0");
			company.setColDisarm((CoreUtils.isNotNull(model.getDisarmShow()) && model.getDisarmShow()) ? "1" : "0");
			company.setAutoDisarm(
					(CoreUtils.isNotNull(model.getDisarmAutofill()) && model.getDisarmAutofill()) ? "1" : "0");

			companyRepository.save(company);
		}

		return Data.of(model);
	}

	@Transactional
	@CacheEvict(cacheNames = { "companyComboBox" }, allEntries = true)
	public Data delete(final CompanyModel model) {
		Company company = companyRepository.findById(model.getCompanyId()).get();

		// tagRepository.clearAllTagsByCompanyId(SecurityUtils.getTiffaCompanyId(),
		// company.getCpnId());
		userPrivilegeRepository.deleteAllPrivilesgeByUserId(company.getUserId());
		companyRepository.deleteById(company.getCompanyId());
		userRepository.deleteById(company.getUserId());

		return Data.of();
	}

}
