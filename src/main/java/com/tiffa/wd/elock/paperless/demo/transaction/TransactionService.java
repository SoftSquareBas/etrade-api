package com.tiffa.wd.elock.paperless.demo.transaction;

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

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true, propagation = Propagation.NEVER)
public class TransactionService {

	@Autowired
	private CoreRepository coreRepository;

	public GridData search(final TransactionModel model) {
		log.debug("search", model);

		SqlParams params = SqlParams.createPageParam(model);
		StringBuilder sql = new StringBuilder();

		sql.append(" SELECT ith.ou_code AS \"ouCode\", ");
		sql.append(" 	ith.doc_type AS \"receiveTypeCode\", ");
		sql.append(" 	ith.doc_no AS \"receiveNo\", ");
		sql.append(" 	ith.doc_date AS \"receiveDate\", ");
		sql.append(" 	ith.ware_code AS \"warehouseCode\", ");
		sql.append(" 	iw.ware_name AS \"warehouseName\", ");
		sql.append(" 	ith.staff_code_receive AS \"staffCode\", ");
		sql.append(" 	ge.t_name_concat AS \"staffName\", ");
		sql.append(" 	ith.status AS \"statusCode\" ");
		sql.append(" FROM in_tran_head ith ");
		sql.append(" 	JOIN in_warehouse iw ON iw.ou_code = ith.ou_code AND iw.ware_code = ith.ware_code ");
		sql.append(" 	LEFT JOIN gb_employee ge ON ge.ou_code = ith.ou_code AND ge.emp_id = ith.staff_code_receive ");

		SqlSort sort = SqlSort.create(model, Sort.by("receiveTypeCode", Direction.ASC),
				Sort.by("receiveNo", Direction.ASC));
		return coreRepository.searchPagingGridData(sql.toString(), params, sort);
	}
}
