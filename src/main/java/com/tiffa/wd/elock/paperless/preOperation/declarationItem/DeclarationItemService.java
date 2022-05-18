package com.tiffa.wd.elock.paperless.preOperation.declarationItem;

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
public class DeclarationItemService {

	@Autowired
	private CoreRepository coreRepository;

	public GridData search(final DeclarationItemModel model) {
		SqlParams params = SqlParams.createPageParam(model);
		StringBuilder sql = new StringBuilder();
		
		sql.append(" SELECT a.*");
		sql.append(" FROM ( ");
		
		sql.append(" SELECT  ");
		sql.append(" 	a.cpn_id AS \"companyId\", ");
		sql.append(" 	a.cpn_code AS \"companyCode\", ");
		sql.append(" 	a.branch_id AS \"branchId\", ");
		sql.append(" 	a.branch_code AS \"branchCode\", ");
		sql.append(" 	a.declarationnumber AS \"declarationNo\",  ");
		sql.append(" 	SUBSTR(MIN(TO_CHAR(a.declarationdate, 'YYYYMMDD')||a.referenceno), 9) AS \"referenceNo\",  ");
		sql.append(" 	SUBSTR(MIN(TO_CHAR(a.declarationdate, 'YYYYMMDD')||LPAD(a.referenceno, 35, '0')||TO_CHAR(a.itemno,'FM0000000000')||a.filename), 46) AS \"filename\", ");
		sql.append(" 	TO_DATE(SUBSTR(MIN(TO_CHAR(a.declarationdate, 'YYYYMMDD')||LPAD(a.referenceno, 35, '0')||TO_CHAR(a.itemno,'FM0000000000')||TO_CHAR(a.declarationacceptdate, 'YYYYMMDD')), 54), 'YYYYMMDD') AS \"declarationDate\", ");
		sql.append(" 	SUBSTR(MIN(TO_CHAR(a.declarationdate, 'YYYYMMDD')||LPAD(a.referenceno, 35, '0')||TO_CHAR(a.itemno,'FM0000000000')||a.invoiceno), 54) AS \"masterInvoiceNo\", ");
		sql.append(" 	TO_DATE(SUBSTR(MIN(TO_CHAR(a.declarationdate, 'YYYYMMDD')||LPAD(a.referenceno, 35, '0')||TO_CHAR(a.itemno,'FM0000000000')||TO_CHAR(a.invoicedate, 'YYYYMMDD')), 54), 'YYYYMMDD') AS \"masterInvoiceDate\", ");
		sql.append(" 	COUNT(a.itemno) AS \"itemNo\", ");
		sql.append(" 	SUM(NVL(a.invoicequantity,0)) AS \"declarationQty\", ");
		sql.append(" 	SUM(NVL(a.deliveryqty,0)) AS \"deliveryQty\", ");
		sql.append(" 	SUM(NVL(a.remainqty,0)) AS \"remainQty\", ");
		sql.append(" 	SUM(NVL(a.shquantity,0)) AS \"shortQty\", ");
		sql.append(" 	SUM(NVL(a.netweight,0)) AS \"netWeight\", ");
		sql.append(" 	SUM(NVL(a.netweight,0) - NVL(a.remainnetweight,0)) AS \"deliveryNetWeight\", ");
		sql.append(" 	SUM(NVL(a.invoiceamountbaht,0)) AS \"valueBaht\", ");
		sql.append(" 	SUM(NVL(a.invoiceamountbaht,0) - NVL(a.remainvalue,0)) AS \"deliveryValueBaht\", ");
		sql.append(" 	SUM((NVL(a.invoiceamountbaht,0) - NVL(a.remainvalue,0)) / NVL(a.exchangerate,0)) AS \"deliveryValueUsd\", ");
		sql.append(" 	SUM(NVL(a.packageamount,0)) AS \"noOfPackage\", ");
		sql.append(" 	SUM(NVL(a.remaintotalpackageamount,0)) AS \"deliveryNoOfPackage\", ");
		sql.append(" 	SUM(NVL(a.packageamount,0) - NVL(a.remaintotalpackageamount,0)) AS \"remainNoOfPackage\" ");
		sql.append(" FROM ( ");
		sql.append("  SELECT t.cpn_id, ");
		sql.append(" 	c.cpn_code, ");
		sql.append(" 	t.branch_id, ");
		sql.append(" 	b.branch_code, ");
		sql.append(" 	t.declarationnumber, ");
		sql.append(" 	t.declarationdate, ");
		sql.append(" 	t.filename, ");
		sql.append(" 	t.declarationacceptdate, ");
		sql.append(" 	t.referenceno, ");
		sql.append(" 	t.invoiceno, ");
		sql.append(" 	t.invoicedate, ");
		sql.append(" 	t.invoicequantity, ");
		sql.append(" 	t.itemno, ");
		sql.append(" 	t.productcode, ");
		sql.append(" 	t.engdescofgoods, ");
		sql.append(" 	t.thaidescofgoods, ");
		sql.append(" 	t.tariffcode, ");
		sql.append(" 	t.quantityunit, ");
		sql.append(" 	t.quantity, ");
		sql.append(" 	t.deliveryqty, ");
		sql.append(" 	t.remainqty, ");
		sql.append(" 	t.shquantity, ");
		sql.append(" 	t.netweight, ");
		sql.append(" 	t.remainnetweight, ");
		sql.append(" 	t.invoiceamountbaht, ");
		sql.append(" 	t.remainvalue, ");
		sql.append(" 	t.exchangerate, ");
		sql.append(" 	t.packageunit, ");
		sql.append(" 	t.packageamount, ");
		sql.append(" 	t.remaintotalpackageamount ");
		sql.append("   FROM tblexportdetail t ");
		sql.append("   	JOIN company c ON c.cpn_id = t.cpn_id ");
		sql.append(" 	JOIN branch b ON b.branch_id = t.branch_id ");
		sql.append("   WHERE t.flagcancel IS NULL ");
		sql.append("     AND t.declarationnumber IS NOT NULL ");
		
		if (CoreUtils.isNotNull(model.getCompanyId())) {
			sql.append("    AND t.cpn_id = :companyId ");
			params.add("companyId", model.getCompanyId());
		}

		if (CoreUtils.isNotNull(model.getBranchId())) {
			sql.append("    AND t.branch_id = :branchId ");
			params.add("branchId", model.getBranchId());
		}
		
		if (CoreUtils.isNotEmpty(model.getDeclarationNo())) {
			sql.append("    AND t.declarationnumber = :declarationNo ");
			params.add("declarationNo", model.getDeclarationNo());
		}
		
		if (CoreUtils.isNotNull(model.getDateFrom())) {
			sql.append("    AND t.declarationacceptdate >= :fromDate ");
			params.add("fromDate", CoreUtils.atStartOfDay(model.getDateFrom()));
		}
		
		if (CoreUtils.isNotNull(model.getDateTo())) {
			sql.append("    AND t.declarationacceptdate <= :toDate ");
			params.add("toDate", CoreUtils.atEndOfDay(model.getDateTo()));
		}
		
		if(CoreUtils.isNotNull(model.getDisplayOnlyRemainItem()) && model.getDisplayOnlyRemainItem()) {
			sql.append("    AND t.remainqty > 0 ");
		}

		sql.append(" ) a ");
		sql.append(" GROUP BY a.cpn_id, ");
		sql.append(" 	a.cpn_code, ");
		sql.append(" 	a.branch_id, ");
		sql.append(" 	a.branch_code, ");
		sql.append(" 	a.declarationnumber ");

		sql.append(" ) a ");
		SqlSort sort = SqlSort.create(model, Sort.by("companyCode", Direction.ASC), Sort.by("branchCode",  Direction.ASC), Sort.by("declarationNo", Direction.DESC));

		return coreRepository.searchPagingGridData(sql.toString(), params, sort);
	}
	
	public GridData getDetail(final DeclarationItemModel model) {
		SqlParams params = SqlParams.createPageParam(model);
		StringBuilder sql = new StringBuilder();
		
		sql.append(" SELECT t.invoiceno AS \"masterInvoiceNo\", ");
		sql.append(" 	t.invoicedate AS \"masterInvoiceDate\", ");
		sql.append(" 	TO_NUMBER(t.itemno) AS \"itemNo\", ");
		sql.append(" 	t.productcode AS \"partNo\", ");
		sql.append(" 	t.engdescofgoods AS \"itemDescEn\", ");
		sql.append(" 	t.thaidescofgoods AS \"itemDescTh\", ");
		sql.append(" 	t.tariffcode AS \"tariffCode\", ");
		sql.append(" 	t.quantityunit AS \"unit\", ");
		sql.append(" 	NVL(t.quantity,0) AS \"declarationQty\", ");
		sql.append(" 	NVL(t.deliveryqty,0) AS \"deliveryQty\", ");
		sql.append(" 	NVL(t.remainqty,0) AS \"remainQty\", ");
		sql.append(" 	NVL(t.shquantity,0) AS \"shortQty\", ");
		sql.append(" 	NVL(t.netweight,0) AS \"netWeight\", ");
		sql.append(" 	NVL(t.netweight,0) - NVL(t.remainnetweight,0) AS \"deliveryNetWeight\", ");
		sql.append(" 	NVL(t.invoiceamountbaht,0) AS \"valueBaht\", ");
		sql.append(" 	NVL(t.invoiceamountbaht,0) - NVL(t.remainvalue,0) AS \"deliveryValueBaht\", ");
		sql.append(" 	(NVL(t.invoiceamountbaht,0) - NVL(t.remainvalue,0)) / NVL(t.exchangerate,0) AS \"deliveryValueUsd\", ");
		sql.append(" 	t.packageunit AS \"noOfPackageUnit\", ");
		sql.append(" 	NVL(t.packageamount,0) AS \"noOfPackage\", ");
		sql.append(" 	NVL(t.remaintotalpackageamount,0) AS \"deliveryNoOfPackage\", ");
		sql.append(" 	NVL(t.packageamount,0) - NVL(t.remaintotalpackageamount,0) AS \"remainNoOfPackage\" ");
		sql.append(" FROM tblexportdetail t ");
		sql.append(" WHERE t.cpn_id = :companyId ");
		sql.append(" 	AND t.branch_id = :branchId ");
		sql.append(" 	AND t.declarationnumber = :declarationNo ");
		
		params.add("companyId", model.getCompanyId());
		params.add("branchId", model.getBranchId());
		params.add("declarationNo", model.getDeclarationNo());
		
		SqlSort sort = SqlSort.create(model, Sort.by("masterInvoiceNo", Direction.DESC), Sort.by("itemNo", Direction.DESC));

		return coreRepository.searchGridData(sql.toString(), params, sort);
	}
}
