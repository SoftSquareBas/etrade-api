package com.tiffa.wd.elock.paperless.master.branchTag;

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
import com.tiffa.wd.elock.paperless.core.util.SecurityUtils;

@Service
@Transactional(readOnly = true, propagation = Propagation.NEVER)
public class BranchTagService {

	@Autowired
	private CoreRepository coreRepository;

	public GridData search(final BranchTagModel model) {
		SqlParams params = SqlParams.createPageParam(model);
		StringBuilder sql = new StringBuilder();

		sql.append(" SELECT a.*");
		sql.append(" FROM ( ");

		sql.append("   SELECT t.tag_id AS \"tagId\" ");
		sql.append("     , t.tag_code AS \"tagCode\" ");
		sql.append("     , t.tag_name AS \"tagName\" ");
		sql.append("     , c.cpn_code AS \"companyCode\" ");
		sql.append("     , b.branch_code AS \"branchCode\" ");
		sql.append("   FROM tag t ");
		sql.append("		JOIN company c ON c.cpn_id = t.cpn_id ");
		sql.append("		JOIN branch b ON b.branch_id = t.branch_id ");
		sql.append("   WHERE t.cpn_id = :companyId ");
		sql.append("   		AND t.branch_id = :branchId ");

		params.add("companyId", SecurityUtils.getCompanyId());
		params.add("branchId", SecurityUtils.getBranchId());

		if (CoreUtils.isNotNull(model.getTagCode())) {
			sql.append("    AND t.tag_code LIKE :tagCode ");
			params.add("tagCode", "%" + model.getTagCode() + "%");
		}

		if (CoreUtils.isNotNull(model.getTagName())) {
			sql.append("    AND t.tag_name LIKE :tagName ");
			params.add("tagName", "%" + model.getTagName() + "%");
		}

		sql.append(" ) a ");
		//sql.append(SqlUtils.generateSqlOrderBy(model, "a", Sort.by("tagCode", Direction.ASC)));

		SqlSort sort = SqlSort.create(model, Sort.by("tagCode", Direction.ASC));
		return coreRepository.searchPagingGridData(sql.toString(), params, sort);
	}
}
