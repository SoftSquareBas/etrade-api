package com.tiffa.wd.elock.paperless.preOperation.invoiceList;

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
public class InvoiceListService {

	@Autowired
	private CoreRepository coreRepository;

	public GridData search(final InvoiceListModel model) {
		SqlParams params = SqlParams.createPageParam(model);
		StringBuilder sql = new StringBuilder();
		
		sql.append(" SELECT pih.cpn_id AS \"companyId\", ");
		sql.append(" 	c.cpn_code AS \"companyCode\", ");
		sql.append(" 	pih.branch_id AS \"branchId\", ");
		sql.append(" 	b.branch_code AS \"branchCode\", ");
		sql.append(" 	pih.invoiceno AS \"invoiceNo\", ");
		sql.append(" 	pih.invoicedate AS \"invoiceDate\", ");
		sql.append(" 	pih.declarationnumber AS \"declarationNo\", ");
		sql.append(" 	pih.truck_code AS \"truckNo\", ");
		sql.append(" 	pih.invoicestatus AS \"invoiceStatus\" ");
		sql.append(" FROM pre_invoice_hdr pih ");
		sql.append(" 	JOIN company c ON c.cpn_id = pih.cpn_id ");
		sql.append(" 	JOIN branch b ON b.branch_id = pih.branch_id ");
		sql.append(" WHERE 1 = 1 ");
		
		if (CoreUtils.isNotNull(model.getCompanyId())) {
			sql.append("    AND pih.cpn_id = :companyId ");
			params.add("companyId", model.getCompanyId());
		}

		if (CoreUtils.isNotNull(model.getBranchId())) {
			sql.append("    AND pih.branch_id = :branchId ");
			params.add("branchId", model.getBranchId());
		}
		
		if (CoreUtils.isNotNull(model.getDateFrom())) {
			sql.append("    AND pih.invoicedate >= :fromDate ");
			params.add("fromDate", CoreUtils.atStartOfDay(model.getDateFrom()));
		}
		
		if (CoreUtils.isNotNull(model.getDateTo())) {
			sql.append("    AND pih.invoicedate <= :toDate ");
			params.add("toDate", CoreUtils.atEndOfDay(model.getDateTo()));
		}
		
		SqlSort sort = SqlSort.create(model, Sort.by("companyCode", Direction.ASC), Sort.by("branchCode",  Direction.ASC), Sort.by("invoiceNo", Direction.DESC));

		return coreRepository.searchPagingGridData(sql.toString(), params, sort);
	}
}
