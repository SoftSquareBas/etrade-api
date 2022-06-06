package com.tiffa.wd.elock.paperless.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.tiffa.wd.elock.paperless.core.ComboBox;
import com.tiffa.wd.elock.paperless.core.ComboBoxRequest;
import com.tiffa.wd.elock.paperless.core.DoctypeComboBox;
import com.tiffa.wd.elock.paperless.core.EmployeeComboBox;
import com.tiffa.wd.elock.paperless.core.GridData;
import com.tiffa.wd.elock.paperless.core.repository.CoreRepository;
import com.tiffa.wd.elock.paperless.core.repository.SqlParams;
import com.tiffa.wd.elock.paperless.core.util.CoreUtils;

@Service
@Transactional(readOnly = true, propagation = Propagation.NEVER)
public class ComboBoxService {

	@Autowired
	private CoreRepository coreRepository;

	@Cacheable(value = "warehouseComboBox", key = "#model")
	public GridData searchWarehouse(ComboBox model) {
		SqlParams params = SqlParams.createComboBox(model);

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT wh.ware_code AS \"value\", ");
		sql.append(" COALESCE(wh.ware_code, '') || ' : ' || COALESCE(wh.ware_name , '' ) AS \"text\" ");
		sql.append(" FROM in_warehouse wh ");
		sql.append(" WHERE 1 = 1 ");

		if (CoreUtils.isNotEmpty(model.getQuery())) {
			sql.append(" AND (wh.ware_code LIKE :query OR wh.ware_name LIKE :query)");
		}

		return coreRepository.searchGridData(sql.toString(), params);
	}

	@Cacheable(value = "doctypeComboBox", key = "#model")
	public GridData searchDoctypee(DoctypeComboBox model) {
		SqlParams params = SqlParams.createComboBox(model);

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT idt.main_doc  AS \"mainDoc\", ");
		sql.append(" idt.doc_type AS \"value\", ");
		sql.append(" idt.doc_type  || ' : ' || idt.doc_desc AS \"text\" ");
		sql.append(" FROM in_doc_type idt ");
		sql.append(" WHERE 1 = 1 ");

		// sql.append(" AND idt.doc_type = :doctype ");
		// params.add("doctype", model.getDoctype());

		// if (CoreUtils.isNotEmpty(model.getQuery())) {
		// sql.append(" AND idt.doc_type LIKE :query");
		// }

		sql.append(" ORDER BY idt.doc_desc ASC ");
		return coreRepository.searchGridData(sql.toString(), params);
	}

	@Cacheable(value = "documentComboBox", key = "#model")
	public GridData searchDocument(DoctypeComboBox model) {
		SqlParams params = SqlParams.createComboBox(model);

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ith.doc_no  AS \"value\", ");
		sql.append(" ith.doc_type || ' : ' || ith.doc_no AS \"text\" ");
		sql.append(" FROM in_tran_head ith ");
		sql.append(" WHERE 1 = 1 ");

		// sql.append(" AND ith.doc_type = :doctype ");
		// params.add("doctype", model.getDoctype());

		// if (CoreUtils.isNotEmpty(model.getQuery())) {
		// sql.append(" AND ith.doc_type LIKE :query");
		// }

		sql.append(" ORDER BY ith.doc_type, ith.doc_no ASC ");
		return coreRepository.searchGridData(sql.toString(), params);
	}

	// @Cacheable(value = "employeeComboBox", key = "#model")
	// public GridData searchEmployee(EmployeeComboBox model) {
	// SqlParams params = SqlParams.createComboBox(model);

	// StringBuilder sql = new StringBuilder();
	// sql.append(" SELECT emp_id AS \"value\", ");
	// sql.append(
	// " tit.title_name_tha || COALESCE(t_first_name, '') || ' ' ||
	// COALESCE(t_last_name, '') AS \"text\" ");
	// sql.append(" FROM gb_employee emp ");
	// sql.append(" WHERE 1 = 1 ");

	// // sql.append(" AND ith.doc_type = :doctype ");
	// // params.add("doctype", model.getDoctype());

	// // if (CoreUtils.isNotEmpty(model.getQuery())) {
	// // sql.append(" AND ith.doc_type LIKE :query");
	// // }

	// sql.append(" LEFT JOIN db_title tit ON emp.pre_name_id = tit.title_code ");
	// return coreRepository.searchGridData(sql.toString(), params);
	// }

}
