package com.tiffa.wd.elock.paperless.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.tiffa.wd.elock.paperless.core.ComboBoxRequest;
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
	public GridData searchWarehouse(ComboBoxRequest model) {
		SqlParams params = SqlParams.createComboBoxParam(model);

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

	@Cacheable(value = "receiveDoctypeComboBox", key = "#model")
	public GridData searchReceiveDoctype(ComboBoxRequest model) {
		SqlParams params = SqlParams.createComboBoxParam(model);

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" idt.doc_type AS \"value\", ");
		sql.append(" idt.doc_type  || ' : ' || idt.doc_desc AS \"text\" ");
		sql.append(" FROM in_doc_type idt ");
		sql.append(" WHERE idt.main_doc = 'FGI' ");

		if (CoreUtils.isNotEmpty(model.getQuery())) {
			sql.append(" AND (idt.doc_type LIKE :query OR idt.doc_desc LIKE :query)");
		}

		sql.append(" ORDER BY idt.doc_type ASC ");
		return coreRepository.searchGridData(sql.toString(), params);
	}

	@Cacheable(value = "documentComboBox", key = "#model")
	public GridData searchDocument(ComboBoxRequest model) {
		SqlParams params = SqlParams.createComboBoxParam(model);

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ith.doc_no  AS \"value\", ");
		sql.append(" ith.doc_type || ' : ' || ith.doc_no AS \"text\" ");
		sql.append(" FROM in_tran_head ith ");
		sql.append(" WHERE 1 = 1 ");

		if (CoreUtils.isNotEmpty(model.getQuery())) {
			sql.append(" AND (ith.doc_no LIKE :query OR ith.doc_type LIKE :query)");
		}

		sql.append(" ORDER BY ith.doc_type, ith.doc_no ASC ");
		return coreRepository.searchGridData(sql.toString(), params);
	}

	@Cacheable(value = "employeeComboBox", key = "#model")
	public GridData searchEmployee(ComboBoxRequest model) {
		SqlParams params = SqlParams.createComboBoxParam(model);

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT DISTINCT emp.emp_id AS \"value\", ");
		sql.append(
				" emp.emp_id || ' : ' || COALESCE(emp.t_name_concat, '') AS \"text\" ");
		sql.append(" FROM gb_employee emp ");
		sql.append(" WHERE 1 = 1 ");

		// sql.append(" AND emp.t_first_name= :consigneeFrom ");
		// params.add("consigneeFrom", model.getConsigneeFrom());

		if (CoreUtils.isNotEmpty(model.getQuery())) {
			sql.append(
					" AND (emp.emp_id LIKE :query OR emp.t_name_concat LIKE :query)");
		}

		return coreRepository.searchGridData(sql.toString(), params);
	}
}
