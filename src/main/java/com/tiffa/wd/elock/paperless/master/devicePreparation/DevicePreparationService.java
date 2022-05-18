package com.tiffa.wd.elock.paperless.master.devicePreparation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.tiffa.wd.elock.paperless.core.GridData;
import com.tiffa.wd.elock.paperless.core.Sort;
import com.tiffa.wd.elock.paperless.core.repository.CoreRepository;
import com.tiffa.wd.elock.paperless.core.repository.SqlParams;
import com.tiffa.wd.elock.paperless.core.repository.SqlSort;
import com.tiffa.wd.elock.paperless.core.util.CoreUtils;

@Service
@Transactional(readOnly = true, propagation = Propagation.NEVER)
public class DevicePreparationService {

	@Autowired
	private CoreRepository coreRepository;
	
	public GridData search(final DevicePreparationModel model) {
		SqlParams params = SqlParams.createPageParam(model);
		StringBuilder sql = new StringBuilder();

		sql.append(" SELECT a.*");
		sql.append(" FROM ( ");

		sql.append("   SELECT e.timestamp * 1000 AS \"timestamp\" ");
		sql.append("     , e.latitude AS \"latitude\" ");
		sql.append("     , e.longitude AS \"longitude\" ");
		sql.append("     , e.batterylevel AS \"batteryLevel\" ");
		sql.append("     , e.address AS \"location\" ");
		sql.append("	 , ROW_NUMBER() OVER (ORDER BY e.timestamp DESC) AS \"rowNum\" ");
		sql.append("   FROM eventdata e ");
		sql.append("     JOIN tag t ON t.cpn_id = e.cpn_id AND t.branch_id = e.branch_id AND t.tag_code = e.deviceid ");
		sql.append("   WHERE t.tag_status = 'Yes' ");
		sql.append("     AND e.deviceid = :tagCode ");
		params.add("tagCode", model.getTagCode());
		
		if (CoreUtils.isNotNull(model.getCompanyId())) {
			sql.append("    AND e.accountid = (SELECT LOWER(c.cpn_code) FROM company c WHERE c.cpn_id = :companyId) ");
			sql.append("    AND e.cpn_id = :companyId ");
			params.add("companyId", model.getCompanyId());
		}
		
		if (CoreUtils.isNotNull(model.getBranchId())) {
			sql.append("    AND e.branch_id = :branchId ");
			params.add("branchId", model.getBranchId());
		}

		// SELECT (SYSDATE - TO_DATE('1970-01-01', 'YYYY-MM-DD')) *24*60*60 FROM DUAL; Date to Number
		if(CoreUtils.isNotNull(model.getDateFrom())) {
			sql.append("    AND e.timestamp >= :dateFrom ");
			params.add("dateFrom", CoreUtils.toDate(CoreUtils.atStartOfDay(model.getDateFrom())).getTime() / 1000);
		}

		if(CoreUtils.isNotNull(model.getDateTo())) {
			sql.append("    AND e.timestamp <= :dateTo ");
			params.add("dateTo", CoreUtils.toDate(CoreUtils.atEndOfDay(model.getDateTo())).getTime() / 1000);
		}

		sql.append(" ) a ");
		if(CoreUtils.isNotNull(model.getMaxRow())) {
			sql.append(" WHERE a.\"rowNum\" <= :maxRow ");
			params.add("maxRow", model.getMaxRow());
		}
		
		SqlSort sort = SqlSort.create(model, Sort.by("timestamp", Direction.DESC));
		return coreRepository.searchGridData(sql.toString(), params, sort);
	}
	
	public GridData checkAllDevice(final DevicePreparationModel model) {
		SqlParams params = SqlParams.createPageParam(model);
		StringBuilder sql = new StringBuilder();
		
		sql.append(" SELECT e.deviceid AS \"tagCode\" ");
		sql.append(" 	, MAX(e.timestamp) * 1000 AS \"timestamp\" ");
		sql.append(" 	, TO_NUMBER(SUBSTR(MAX(TO_CHAR(e.timestamp, 'FM0000000000000000000')||e.latitude), 20)) AS \"lat\" ");
		sql.append(" 	, TO_NUMBER(SUBSTR(MAX(TO_CHAR(e.timestamp, 'FM0000000000000000000')||e.longitude), 20)) AS \"lng\" ");
		sql.append(" 	, TO_NUMBER(SUBSTR(MAX(TO_CHAR(e.timestamp, 'FM0000000000000000000')||e.batterylevel), 20)) AS \"batteryLevel\" ");
		sql.append(" 	, SUBSTR(MAX(TO_CHAR(e.timestamp, 'FM0000000000000000000')||e.address), 20) AS \"location\" ");
		sql.append(" FROM eventdata e ");
		sql.append("   JOIN tag t ON t.cpn_id = e.cpn_id AND t.branch_id = e.branch_id AND t.tag_code = e.deviceid ");
		sql.append(" WHERE t.tag_status = 'Yes' ");
		
		if (CoreUtils.isNotNull(model.getCompanyId())) {
			sql.append("    AND e.accountid = (SELECT LOWER(c.cpn_code) FROM company c WHERE c.cpn_id = :companyId) ");
			sql.append("    AND e.cpn_id = :companyId ");
			params.add("companyId", model.getCompanyId());
		}
		
		if (CoreUtils.isNotNull(model.getBranchId())) {
			sql.append("    AND e.branch_id = :branchId ");
			params.add("branchId", model.getBranchId());
		}

		sql.append("    AND e.timestamp >= :dateFrom ");
		params.add("dateFrom", CoreUtils.toDate(CoreUtils.atStartOfDay(model.getDateFrom())).getTime() / 1000);

		sql.append("    AND e.timestamp <= :dateTo ");
		params.add("dateTo", CoreUtils.toDate(CoreUtils.atEndOfDay(model.getDateTo())).getTime() / 1000);
		
		sql.append(" GROUP BY e.deviceid ");

		SqlSort sort = SqlSort.create(model, Sort.by("timestamp", Direction.DESC));
		return coreRepository.searchGridData(sql.toString(), params, sort);
	}
}
