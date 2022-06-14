package com.tiffa.wd.elock.paperless.demo.master;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.tiffa.wd.elock.paperless.core.Data;
import com.tiffa.wd.elock.paperless.core.GridData;
import com.tiffa.wd.elock.paperless.core.Sort;
import com.tiffa.wd.elock.paperless.core.entity.DbPoType;
import com.tiffa.wd.elock.paperless.core.repository.CoreRepository;
import com.tiffa.wd.elock.paperless.core.repository.PoTypeRepository;
import com.tiffa.wd.elock.paperless.core.repository.SqlParams;
import com.tiffa.wd.elock.paperless.core.repository.SqlSort;
import com.tiffa.wd.elock.paperless.core.util.CoreUtils;

@Service
@Transactional(readOnly = true, propagation = Propagation.NEVER)
public class MasterService {

	@Autowired
	private PoTypeRepository poTypeRepository;

	@Autowired
	private CoreRepository coreRepository;

	public Data add(final MasterModel model) {
		// ValidationUtils.checkRequired(model.getPoTypeCode(), "Potype Code");

		DbPoType potype = new DbPoType();
		potype.setPoTypeCode(model.getPoTypeCode());
		potype.setPoTypeDesc(model.getPoTypeDesc());
		potype.setActive(model.getActive());
		String.valueOf(poTypeRepository.saveAndFlush(potype).getPoTypeCode());

		// return Data.of(model);
		return searchDetail(model);
		// return null;
	}

	public GridData search(MasterModel model) {
		SqlParams params = SqlParams.createPageParam(model);
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT  pt.po_type_code AS\"poTypeCode\" ");
		sql.append(" , pt.po_type_desc AS \"poTypeDesc\" ");
		sql.append(" , pt.active AS \"active\" ");
		sql.append(" FROM po_type pt ");
		sql.append(" WHERE 1=1 ");

		if (CoreUtils.isNotNull(model.getSearch())) {
			sql.append(
					"AND (pt.po_type_code LIKE  :search  OR pt.po_type_desc LIKE  :search  ) ");
			params.add("search", "%" + model.getSearch() + "%");
		}

		SqlSort sort = SqlSort.create(model, Sort.by("poTypeCode", Direction.ASC),
				Sort.by("poTypeCode", Direction.ASC), Sort.by("poTypeCode", Direction.ASC));
		return coreRepository.searchPagingGridData(sql.toString(), params, sort);
	}

	@Transactional
	public Data delete(final MasterModel model) {

		poTypeRepository.deleteById(model.getPoTypeCode());
		return Data.of();
	}

	@Transactional
	public Data searchDetail(final MasterModel model) {

		DbPoType potype = poTypeRepository.findById(model.getPoTypeCode()).get();
		return Data.of(potype);

	}

	@Transactional
	public Data edit(final MasterModel model) {

		DbPoType potype = poTypeRepository.findById(model.getPoTypeCode()).get();

		potype.setPoTypeCode(model.getPoTypeCode());
		potype.setPoTypeDesc(model.getPoTypeDesc());
		potype.setActive(model.getActive());

		poTypeRepository.saveAndFlush(potype);
		return searchDetail(model);
	}
}
