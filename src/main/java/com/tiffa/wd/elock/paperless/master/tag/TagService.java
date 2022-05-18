package com.tiffa.wd.elock.paperless.master.tag;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.tiffa.wd.elock.paperless.core.Data;
import com.tiffa.wd.elock.paperless.core.GridData;
import com.tiffa.wd.elock.paperless.core.Sort;
import com.tiffa.wd.elock.paperless.core.entity.Tag;
import com.tiffa.wd.elock.paperless.core.repository.CoreRepository;
import com.tiffa.wd.elock.paperless.core.repository.SqlParams;
import com.tiffa.wd.elock.paperless.core.repository.SqlSort;
import com.tiffa.wd.elock.paperless.core.repository.TagRepository;
import com.tiffa.wd.elock.paperless.core.util.CoreUtils;
import com.tiffa.wd.elock.paperless.core.util.ValidationUtils;

@Service
@Transactional(readOnly = true, propagation = Propagation.NEVER)
public class TagService {

	@Autowired
	private CoreRepository coreRepository;

	@Autowired
	private TagRepository tagRepository;

	public GridData search(final TagModel model) {
		SqlParams params = SqlParams.createPageParam(model);
		StringBuilder sql = new StringBuilder();

		sql.append(" SELECT a.*");
		sql.append(" FROM ( ");

		sql.append("   SELECT t.tag_id AS \"tagId\" ");
		sql.append("     , t.cpn_id AS \"companyId\" ");
		sql.append("     , c.cpn_code AS \"companyCode\" ");
		sql.append("     , t.branch_id AS \"branchId\" ");
		sql.append("     , b.branch_code AS \"branchCode\" ");
		sql.append("     , t.tag_code AS \"tagCode\" ");
		sql.append("     , t.tag_name AS \"tagName\" ");
		sql.append("     , t.tag_status AS \"tagStatus\" ");
		sql.append("   FROM tag t ");
		sql.append("	 JOIN company c ON c.cpn_id = t.cpn_id ");
		sql.append("	 JOIN branch b ON b.branch_id = t.branch_id ");
		sql.append("   WHERE 1 = 1 ");

		if (CoreUtils.isNotNull(model.getCompanyId())) {
			sql.append("    AND t.cpn_id = :companyId ");
			params.add("companyId", model.getCompanyId());
		}
		
		if (CoreUtils.isNotNull(model.getBranchId())) {
			sql.append("    AND t.branch_id = :branchId ");
			params.add("branchId", model.getBranchId());
		}
		
		if (CoreUtils.isNotNull(model.getTagCode())) {
			sql.append("    AND t.tag_code LIKE :tagCode ");
			params.add("tagCode", "%" + model.getTagCode() + "%");
		}

		if (CoreUtils.isNotNull(model.getTagName())) {
			sql.append("    AND t.tag_name LIKE :tagName ");
			params.add("tagName", "%" + model.getTagName() + "%");
		}

		if (CoreUtils.isNotNull(model.getTagStatus())) {
			sql.append("    AND t.tag_status = :tagStatus ");
			params.add("tagStatus", model.getTagStatus());
		}

		sql.append(" ) a ");
		SqlSort sort = SqlSort.create(model, Sort.by("companyCode", Direction.ASC), Sort.by("branchCode", Direction.ASC), Sort.by("tagCode", Direction.ASC));

		return coreRepository.searchPagingGridData(sql.toString(), params, sort);
	}

	@Transactional
	public Data add(final TagModel model) {
		ValidationUtils.checkRequired(model.getCompanyId(), "Company Code");
		ValidationUtils.checkRequired(model.getBranchId(), "Branch Code");
		ValidationUtils.checkRequired(model.getTagCode(), "Tag Code");
		ValidationUtils.checkRequired(model.getTagName(), "Tag Name");
		ValidationUtils.checkRequired(model.getTagStatus(), "Tag Status");
		
		model.setValidateModel("tagCode", model.getTagCode());
		ValidationUtils.checkDuplicate(validate(model), "Tag Code");

		Tag tag = new Tag();
		tag.setCompanyId(model.getCompanyId());
		tag.setBranchId(model.getBranchId());
		tag.setTagCode(model.getTagCode());
		tag.setTagName(model.getTagName());
		tag.setTagStatus(model.getTagStatus());
		tag.setLastUpdate(LocalDateTime.now());

		Integer tagId = tagRepository.saveAndFlush(tag).getTagId();
		model.setTagId(tagId);
		return Data.of(model);
	}

	@Transactional
	public Data edit(final TagModel model) {
		ValidationUtils.checkRequired(model.getTagName(), "Tag Name");
		ValidationUtils.checkRequired(model.getTagStatus(), "Tag Status");
		
		Tag tag = tagRepository.getById(model.getTagId());
		tag.setTagName(model.getTagName());
		tag.setTagStatus(model.getTagStatus());

		tagRepository.saveAndFlush(tag);
		return Data.of(model);
	}

	@Transactional
	public Data delete(final TagModel model) {
		tagRepository.deleteById(model.getTagId());
		return Data.of();
	}

	public Data validate(final TagModel model) {
		SqlParams params = SqlParams.createValidateParam(model);
		params.add("companyId", model.getCompanyId());
		params.add("branchId", model.getBranchId());

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT CASE WHEN COUNT(1) > 0 THEN 1 ELSE 0 END AS \"duplicate\" ");
		sql.append(" FROM tag t ");
		sql.append(" WHERE t.cpn_id = :companyId ");
		sql.append("    AND t.tag_code = :tagCode ");
		
		return coreRepository.validate(sql.toString(), params);	
	}

}
