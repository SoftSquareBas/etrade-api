package com.tiffa.wd.elock.paperless.operation.generateTrip;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tiffa.wd.elock.paperless.core.ComboBoxRequest;
import com.tiffa.wd.elock.paperless.core.Data;
import com.tiffa.wd.elock.paperless.core.GridData;
import com.tiffa.wd.elock.paperless.core.Sort;
import com.tiffa.wd.elock.paperless.core.entity.Invoice;
import com.tiffa.wd.elock.paperless.core.entity.InvoiceItem;
import com.tiffa.wd.elock.paperless.core.entity.PreInvoiceDetail;
import com.tiffa.wd.elock.paperless.core.entity.PreInvoiceHeader;
import com.tiffa.wd.elock.paperless.core.entity.TblExportDetail;
import com.tiffa.wd.elock.paperless.core.entity.TblExportDetailPk;
import com.tiffa.wd.elock.paperless.core.entity.Transfer;
import com.tiffa.wd.elock.paperless.core.entity.TransferItem;
import com.tiffa.wd.elock.paperless.core.entity.TransferPk;
import com.tiffa.wd.elock.paperless.core.repository.CoreRepository;
import com.tiffa.wd.elock.paperless.core.repository.InvoiceItemRepository;
import com.tiffa.wd.elock.paperless.core.repository.InvoiceRepository;
import com.tiffa.wd.elock.paperless.core.repository.PreInvoiceDetailRepository;
import com.tiffa.wd.elock.paperless.core.repository.PreInvoiceHeaderRepository;
import com.tiffa.wd.elock.paperless.core.repository.SqlParams;
import com.tiffa.wd.elock.paperless.core.repository.SqlSort;
import com.tiffa.wd.elock.paperless.core.repository.SqlTypeConversion;
import com.tiffa.wd.elock.paperless.core.repository.TblExportDetailRepository;
import com.tiffa.wd.elock.paperless.core.repository.TransferItemRepository;
import com.tiffa.wd.elock.paperless.core.repository.TransferRepository;
import com.tiffa.wd.elock.paperless.core.service.GeneratorService;
import com.tiffa.wd.elock.paperless.core.util.CoreUtils;
import com.tiffa.wd.elock.paperless.core.util.Response;
import com.tiffa.wd.elock.paperless.core.util.SecurityUtils;
import com.tiffa.wd.elock.paperless.core.util.ValidationUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true, propagation = Propagation.NEVER)
public class GenerateTripService {

	@Autowired
	private CoreRepository coreRepository;

	@Autowired
	private GeneratorService generatorService;

	@Autowired
	private TransferRepository transferRepository;

	@Autowired
	private TransferItemRepository transferItemRepository;

	@Autowired
	private PreInvoiceHeaderRepository preInvoiceHeaderRepository;

	@Autowired
	private PreInvoiceDetailRepository preInvoiceDetailRepository;

	@Autowired
	private InvoiceRepository invoiceRepository;

	@Autowired
	private InvoiceItemRepository invoiceItemRepository;

	@Autowired
	private TblExportDetailRepository tblExportDetailRepository;

	public GridData search(final GenerateTripModel model) {
		SqlParams params = SqlParams.createPageParam(model);
		StringBuilder sql = new StringBuilder();

		sql.append(" SELECT t.cpn_id AS \"companyId\", ");
		sql.append(" 	c.cpn_code AS \"companyCode\", ");
		sql.append(" 	t.branch_id AS \"branchId\", ");
		sql.append(" 	b.branch_code AS \"branchCode\", ");
		sql.append(" 	t.trf_id AS \"tripId\", ");
		sql.append(" 	t.trf_date AS \"tripDate\", ");
		sql.append(" 	t.trf_from AS \"tripFrom\", ");
		sql.append(" 	t.trf_to AS \"tripTo\", ");
		sql.append(" 	t.truck_code AS \"truckCode\", ");
		sql.append(" 	r.tag_code AS \"tagCode\", ");
		sql.append(" 	t.last_user AS \"tripBy\", ");
		sql.append(" 	t.shipment_status AS \"oldShipmentStatus\", ");
		sql.append(" 	(CASE WHEN t.trf_lt = 9 THEN 1 ELSE 0 END) AS \"deleted\" ");
		sql.append(" FROM transfer t ");
		sql.append(" 	JOIN company c ON c.cpn_id = t.cpn_id  ");
		sql.append(" 	JOIN branch b ON b.branch_id = t.branch_id ");
		sql.append(
				" 	LEFT JOIN relation r ON r.cpn_id = t.cpn_id AND r.branch_id = t.branch_id AND r.trf_id = t.trf_id ");
		sql.append(" WHERE 1 = 1 ");

		if (CoreUtils.isNotNull(model.getCompanyId())) {
			sql.append("    AND t.cpn_id = :companyId ");
			params.add("companyId", model.getCompanyId());
		}

		if (CoreUtils.isNotNull(model.getBranchId())) {
			sql.append("    AND t.branch_id = :branchId ");
			params.add("branchId", model.getBranchId());
		}

		if (CoreUtils.isNotNull(model.getTripDate())) {
			sql.append("    AND t.trf_date = :tripDate ");
			params.add("tripDate", model.getTripDate());
		}

		if (CoreUtils.isNotNull(model.getTripFrom())) {
			sql.append("    AND t.trf_from = :tripFrom ");
			params.add("tripFrom", model.getTripFrom());
		}

		if (CoreUtils.isNotNull(model.getTripTo())) {
			sql.append("    AND t.trf_to = :tripTo ");
			params.add("tripTo", model.getTripTo());
		}

		if (CoreUtils.isNotNull(model.getTripId())) {
			sql.append("    AND t.trf_id = :tripId ");
			params.add("tripId", model.getTripId());
		}

		SqlSort sort = SqlSort.create(model, Sort.by("companyCode", Direction.ASC),
				Sort.by("branchCode", Direction.ASC), Sort.by("tripDate", Direction.DESC),
				Sort.by("tripId", Direction.DESC));

		SqlTypeConversion sqlTypeConversion = SqlTypeConversion.create("deleted", Boolean.class);

		return coreRepository.searchPagingGridData(sql.toString(), params, sort, sqlTypeConversion);
	}

	@Transactional
	public Data updateShipmentStatus(final GenerateTripModel model) {
		TransferPk pk = new TransferPk();
		pk.setCompanyId(model.getCompanyId());
		pk.setBranchId(model.getBranchId());
		pk.setTrfId(model.getTripId());

		Transfer transfer = transferRepository.getById(pk);
		transfer.setShipmentStatus(model.getShipmentStatus());
		transferRepository.saveAndFlush(transfer);
		return Data.of();
	}

	@Transactional
	public Data delete(final GenerateTripModel model) {
		TransferPk pk = new TransferPk();
		pk.setCompanyId(model.getCompanyId());
		pk.setBranchId(model.getBranchId());
		pk.setTrfId(model.getTripId());

		Transfer transfer = transferRepository.getById(pk);
		transfer.setTrfLt(9);
		transferRepository.saveAndFlush(transfer);
		return Data.of();
	}

	public GridData searchDeclaration(final ComboBoxRequest model) {
		SqlParams params = SqlParams.createComboBoxParam(model);

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT t.declarationnumber AS \"value\", ");
		sql.append("   t.declarationnumber AS \"text\" ");
		sql.append(" FROM tblexportdetail t ");
		sql.append(" WHERE t.cpn_id = :companyId ");
		params.add("companyId", model.getCompanyId());

		sql.append("   AND t.branch_id = :branchId ");
		params.add("branchId", model.getBranchId());

		sql.append("   AND t.declarationnumber IS NOT NULL ");
		sql.append("   AND t.flagcancel IS NULL ");

		if (CoreUtils.isNotEmpty(model.getQuery())) {
			sql.append(" AND t.declarationnumber LIKE :query ");
		}

		sql.append(" GROUP BY t.declarationnumber ");
		sql.append(" HAVING (SUM(NVL(t.remainqty, 0)) > 0)");
		sql.append(" ORDER BY t.declarationnumber ASC ");

		return coreRepository.searchGridData(sql.toString(), params);
	}

	public GridData searchDeclarationNoByTrip(final GenerateTripModel model) {
		SqlParams params = SqlParams.create();

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ii.declarationnumber AS \"value\", ");
		sql.append(" 	ii.declarationnumber AS \"text\" ");
		sql.append(" FROM invoice_item ii ");
		sql.append(" 	JOIN tblexportdetail ted ON ted.cpn_id = ii.cpn_id ");
		sql.append(" 		AND ted.branch_id = ii.branch_id ");
		sql.append(" 		AND ted.declarationnumber = ii.declarationnumber ");
		sql.append(" WHERE ii.cpn_id = :companyId ");
		params.add("companyId", model.getCompanyId());

		sql.append(" 	AND ii.branch_id = :branchId ");
		params.add("branchId", model.getBranchId());

		sql.append(" 	AND ii.trf_id = :tripId ");
		params.add("tripId", model.getTripId());

		sql.append(" 	AND ted.packageamount > 0 ");
		sql.append(" 	AND ted.packageamount > NVL(ted.remaintotalpackageamount, 0) ");

		if (CoreUtils.isNotEmpty(model.getQuery())) {
			sql.append(" ii.declarationnumbers LIKE :query ");
		}

		sql.append(" GROUP BY ii.declarationnumber ");
		sql.append(" ORDER BY ii.declarationnumber ASC ");

		return coreRepository.searchGridData(sql.toString(), params);
	}

	public GridData searchDeclarationDescByTrip(final GenerateTripModel model) {
		SqlParams params = SqlParams.create();

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ted.tblexportdetail_id AS \"value\", ");
		sql.append(" 	ted.engdescofgoods AS \"text\" ");
		sql.append(" FROM invoice_item ii ");
		sql.append(" 	JOIN tblexportdetail ted ON ted.cpn_id = ii.cpn_id ");
		sql.append(" 		AND ted.branch_id = ii.branch_id ");
		sql.append(" 		AND ted.declarationnumber = ii.declarationnumber ");
		sql.append(" WHERE ii.cpn_id = :companyId ");
		params.add("companyId", model.getCompanyId());

		sql.append(" 	AND ii.branch_id = :branchId ");
		params.add("branchId", model.getBranchId());

		sql.append(" 	AND ii.trf_id = :tripId ");
		params.add("tripId", model.getTripId());

		sql.append(" 	AND ii.declarationnumber = :declarationNo ");
		params.add("declarationNo", model.getDeclarationNo());

		sql.append(" 	AND ted.packageamount > 0 ");
		sql.append(" 	AND ted.packageamount > NVL(ted.remaintotalpackageamount, 0) ");

		if (CoreUtils.isNotEmpty(model.getQuery())) {
			sql.append(" ted.engdescofgoods LIKE :query ");
			params.add("query", "%" + model.getQuery() + "%");
		}

		sql.append(" GROUP BY ted.tblexportdetail_id, ted.engdescofgoods ");
		sql.append(" ORDER BY ted.tblexportdetail_id ASC ");

		return coreRepository.searchGridData(sql.toString(), params);
	}

	public GridData searchInvoice(final ComboBoxRequest model) {
		SqlParams params = SqlParams.createComboBoxParam(model);

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT pih.invoiceno AS \"value\", ");
		sql.append("    pih.invoiceno AS \"text\" ");
		sql.append(" FROM pre_invoice_hdr pih ");
		sql.append(
				" 	JOIN pre_invoice_dtl pid ON pid.cpn_id = pih.cpn_id AND pid.branch_id = pih.branch_id AND pid.invhdr_id = pih.id ");
		sql.append(" WHERE pih.cpn_id = :companyId ");
		params.add("companyId", model.getCompanyId());

		sql.append(" 	AND pih.branch_id = :branchId ");
		params.add("branchId", model.getBranchId());

		sql.append(" 	AND pih.invoicestatus <> 'R' ");

		if (CoreUtils.isNotEmpty(model.getQuery())) {
			sql.append(" AND pih.invoiceno LIKE :query ");
		}

		sql.append(" GROUP BY pih.invoiceno ");
		sql.append(" HAVING (SUM(pid.remain_invqty) > 0) ");
		sql.append(" ORDER BY pih.invoiceno DESC ");

		return coreRepository.searchGridData(sql.toString(), params);
	}

	public Data loadTrip(final GenerateTripModel model) {
		SqlParams params = SqlParams.createPageParam(model);
		StringBuilder sql = new StringBuilder();

		sql.append(" SELECT t.cpn_id AS \"companyId\", ");
		sql.append(" 	t.branch_id AS \"branchId\", ");
		sql.append(" 	t.trf_id AS \"tripId\", ");
		sql.append(" 	t.track_id AS \"trackId\", ");
		sql.append(" 	t.trf_from AS \"tripFrom\", ");
		sql.append(" 	t.trf_to AS \"tripTo\", ");
		sql.append(" 	t.shipment_status AS \"shipmentStatus\", ");
		sql.append(" 	t.trf_status AS \"tripStatus\", ");
		sql.append(" 	t.remark AS \"remark\" ");
		sql.append(" FROM transfer t ");
		sql.append(" WHERE t.cpn_id = :companyId ");
		params.add("companyId", model.getCompanyId());

		sql.append(" 	AND t.branch_id = :branchId ");
		params.add("branchId", model.getBranchId());

		sql.append(" 	AND t.trf_id = :tripId ");
		params.add("tripId", model.getTripId());

		return coreRepository.getData(sql.toString(), params);
	}

	public Data getRemainPackage(final GenerateTripModel model) {
		SqlParams params = SqlParams.create();
		StringBuilder sql = new StringBuilder();

		sql.append(" SELECT NVL(packageamount,0) - NVL(remaintotalpackageamount,0) AS \"remainPackage\" ");
		sql.append(" FROM tblexportdetail ted ");
		sql.append(" WHERE ted.cpn_id = :companyId ");
		params.add("companyId", model.getCompanyId());

		sql.append(" 	AND ted.branch_id = :branchId ");
		params.add("branchId", model.getBranchId());

		sql.append(" 	AND ted.tblexportdetail_id = :tblExportDetailId ");
		params.add("tblExportDetailId", model.getTblExportDetailId());

		sql.append(" 	AND ted.declarationnumber = :declarationNo ");
		params.add("declarationNo", model.getDeclarationNo());

		return coreRepository.getData(sql.toString(), params);
	}

	public GridData loadTripHeader(final GenerateTripModel model) {
		SqlParams params = SqlParams.createPageParam(model);
		StringBuilder sql = new StringBuilder();

		sql.append(" SELECT i.cpn_id AS \"companyId\", ");
		sql.append(" 	i.branch_id AS \"branchId\", ");
		sql.append(" 	i.trf_id  AS \"tripId\", ");
		sql.append(" 	i.invitem_id  AS \"invoiceItemId\", ");
		sql.append(" 	ROW_NUMBER() OVER (ORDER BY i.inv_no, i.product_code, i.invitem_dec) AS \"itemNo\", ");
		sql.append(" 	i.inv_no AS \"invoiceNo\", ");
		sql.append(" 	i.product_code AS \"partNo\", ");
		sql.append(" 	i.invitem_dec AS \"itemDescription\", ");
		sql.append(" 	i.invitem_uom AS \"unit\", ");
		sql.append(" 	i.invitem_qty AS \"qty\", ");
		sql.append(" 	i.invitem_value AS \"value\", ");
		sql.append(" 	i.invitem_netweight AS \"netWeight\", ");
		sql.append(" 	i.invitem_grossweight AS \"grossWeight\", ");
		sql.append(" 	i.invitem_grossweight_unit AS \"grossWeightUnit\", ");
		sql.append(" 	i.invitem_noofpack AS \"noOfPackage\", ");
		sql.append(" 	i.invitem_noofpack_unit AS \"noOfPackageUnit\", ");
		sql.append(" 	i.invitem_noofpack_packinglist AS \"noOfPackingForPackingList\", ");
		sql.append(" 	i.last_user AS \"createdBy\", ");
		sql.append(" 	i.last_update AS \"createdDate\", ");
		sql.append(" 	i.tblexportdetail_id_assign_pack AS \"tblExportDetailId\", ");
		sql.append(" 	i.declaration_filename AS \"filename\" ");
		sql.append(" FROM invoice_item i ");
		sql.append(" WHERE  i.cpn_id = :companyId ");
		params.add("companyId", model.getCompanyId());

		sql.append(" 	AND i.branch_id = :branchId ");
		params.add("branchId", model.getBranchId());

		sql.append(" 	AND i.trf_id = :tripId ");
		params.add("tripId", model.getTripId());

		SqlSort sort = SqlSort.create(model, Sort.by("itemNo"));

		return coreRepository.searchGridData(sql.toString(), params, sort);
	}

	public GridData loadDetailByManual(final GenerateTripModel model) {
		SqlParams params = SqlParams.createPageParam(model);
		StringBuilder sql = new StringBuilder();

		sql.append(" SELECT ");
		sql.append(" 	t.filename AS \"filename\", ");
		sql.append(" 	t.tblexportdetail_id AS \"tblExportDetailId\", ");
		sql.append(" 	t.declarationnumber AS \"declarationNo\", ");
		sql.append(" 	t.referenceno AS \"referenceNo\", ");
		sql.append(" 	t.declarationdate AS \"declarationDate\", ");
		sql.append(" 	t.invoiceno AS \"invoiceNo\", ");
		sql.append(" 	t.invoicedate AS \"invoiceDate\", ");
		sql.append(" 	t.itemno AS \"itemNo\", ");
		sql.append(" 	t.productcode AS \"productCode\", ");
		sql.append(" 	t.engdescofgoods AS \"itemDescriptionEn\", ");
		sql.append(" 	t.thaidescofgoods AS \"itemDescriptionTh\", ");
		sql.append(" 	t.tariffcode AS \"tariffCode\", ");
		sql.append(" 	t.invoicequantityunit AS \"unit\", ");
		sql.append(" 	NVL(t.invoicequantity,0) AS \"declarationQty\", ");
		sql.append(" 	NVL(t.remainqty,0) AS \"remainQty\", ");
		sql.append(" 	NVL(t.netweight,0) AS \"netWeight\", ");
		sql.append(" 	NVL(t.remainnetweight,0) AS \"remainNetWeight\", ");
		sql.append(" 	NVL(t.invoiceamountbaht,0) AS \"value\", ");
		sql.append(" 	NVL(t.remainvalue,0) AS \"remainValue\", ");
		sql.append(" 	t.packageunit AS \"packageUnit\", ");
		sql.append(" 	NVL(t.packageamount,0) AS \"packageAmount\", ");
		sql.append(" 	NVL(t.packageamount,0) - NVL(t.remaintotalpackageamount,0) AS \"remainPackageAmount\", ");
		sql.append(" 	t.netweightunit AS \"netWeightUnit\" ");
		sql.append(" FROM tblexportdetail t  ");
		sql.append(" 	JOIN tblexportcontrol tc ON tc.cpn_id = t.cpn_id ");
		sql.append(" 		AND tc.branch_id = t.branch_id  ");
		sql.append(" 		AND tc.filename = t.filename  ");
		sql.append(" 		AND tc.referenceno = t.referenceno ");
		sql.append(" WHERE t.flagcancel IS NULL  ");
		sql.append(" 	AND t.cpn_id = :companyId ");
		params.add("companyId", model.getCompanyId());

		sql.append(" 	AND t.branch_id = :branchId ");
		params.add("branchId", model.getBranchId());

		sql.append(" 	AND t.declarationnumber = :declarationNo ");
		params.add("declarationNo", model.getDeclarationNo());

		SqlSort sort = SqlSort.create(model, Sort.by("declarationDate"), Sort.by("referenceNo"), Sort.by("itemNo"));

		return coreRepository.searchGridData(sql.toString(), params, sort);
	}

	@Transactional
	public Data saveTripHeader(final GenerateTripModel model) {
		ValidationUtils.checkRequired(model.getCompanyId(), "Company Code");
		ValidationUtils.checkRequired(model.getBranchId(), "Branch Code");

		createOrUpdateTransfer(model);

		return Data.of(model);
	}

	private Transfer createOrUpdateTransfer(final GenerateTripModel model) {
		Integer companyId = model.getCompanyId();
		Integer branchId = model.getBranchId();
		String tripId = model.getTripId();

		Transfer transfer = null;

		if (CoreUtils.isEmpty(tripId)) {
			tripId = generatorService.generateTripId(model.getCompanyId(), model.getBranchId());
			model.setTripId(tripId);

			String trackId = generatorService.generateTrackId();
			model.setTrackId(trackId);

			TransferPk transferPk = new TransferPk();
			transferPk.setTrfId(tripId);
			transferPk.setCompanyId(companyId);
			transferPk.setBranchId(branchId);

			transfer = new Transfer();
			transfer.setPk(transferPk);

			transfer.setCompanyId(companyId);
			transfer.setBranchId(branchId);
			transfer.setTrfId(tripId);
			transfer.setTrfDate(LocalDate.now());
		} else {
			TransferPk pk = new TransferPk();
			pk.setTrfId(tripId);
			pk.setCompanyId(companyId);
			pk.setBranchId(branchId);

			transfer = transferRepository.getById(pk);
		}

		transfer.setTrfFrom(model.getTripFrom());
		transfer.setTrfTo(model.getTripTo());
		transfer.setTrfStatus("Opened");
		transfer.setShipmentStatus(model.getShipmentStatus());
		transfer.setRemark(model.getRemark());
		transfer.setLastUser(SecurityUtils.getUsername());

		transfer = transferRepository.saveAndFlush(transfer);

		return transfer;
	}

	private Invoice createOrUpdateInvoice(final Transfer transfer) {

		Invoice invoice = invoiceRepository.findInvoice(transfer.getCompanyId(), transfer.getBranchId(),
				transfer.getTrfId());

		if (CoreUtils.isNull(invoice)) {
			TransferItem transferItem = new TransferItem();
			transferItem.setCompanyId(transfer.getCompanyId());
			transferItem.setBranchId(transfer.getBranchId());
			transferItem.setTrfId(transfer.getTrfId());

			transferItem = transferItemRepository.saveAndFlush(transferItem);

			invoice = new Invoice();
			invoice.setCompanyId(transfer.getCompanyId());
			invoice.setBranchId(transfer.getBranchId());
			invoice.setTrfId(transfer.getTrfId());
			invoice.setTrfItemId(transferItem.getTrfItemId());
			invoice.setInvDate(transfer.getTrfDate());
			invoice.setInvGrossWeight(BigDecimal.ZERO);
			invoice.setInvPackage(0);

			invoice = invoiceRepository.saveAndFlush(invoice);
		}

		return invoice;
	}

	@Transactional
	public Data addAssignItem(final GenerateTripModel model) {
		ValidationUtils.checkRequired(model.getCompanyId(), "Company Code");
		ValidationUtils.checkRequired(model.getBranchId(), "Branch Code");

		Integer entryOption = model.getEntryOption();

		if (entryOption == 1) {
			return addAssignItemInvoice(model);
		} else if (entryOption == 2) {
		} else if (entryOption == 3) {
			return addAssignItemManual(model);
		}

		return Data.nil();
	}

	private Data addAssignItemInvoice(final GenerateTripModel model) {
		Integer companyId = model.getCompanyId();
		Integer branchId = model.getBranchId();
		String username = SecurityUtils.getUsername();
		LocalDateTime now = LocalDateTime.now();

		Transfer transfer = createOrUpdateTransfer(model);
		String tripId = transfer.getTrfId();

		Invoice invoice = createOrUpdateInvoice(transfer);
		Integer invoiceId = invoice.getInvId();
		Integer trfItemId = invoice.getTrfItemId();

		for (DeclarationRecord record : model.getRecords()) {

			PreInvoiceHeader preInvoiceHeader = preInvoiceHeaderRepository.getById(record.getInvoiceHdrId());
			PreInvoiceDetail preInvoiceDetail = preInvoiceDetailRepository.getById(record.getInvoiceDtlId());

			InvoiceItem invoiceItem = new InvoiceItem();
			invoiceItem.setCompanyId(companyId);
			invoiceItem.setBranchId(branchId);
			invoiceItem.setInvId(invoiceId);
			invoiceItem.setTrfId(tripId);
			invoiceItem.setTrfItemId(trfItemId);

			invoiceItem.setInvHdrId(preInvoiceHeader.getId());
			invoiceItem.setId(preInvoiceDetail.getId());
			invoiceItem.setInvNo(preInvoiceHeader.getInvoiceNo());
			invoiceItem.setItemNo(preInvoiceDetail.getItemNo());

			invoiceItem.setItemType(record.getItemType());

			invoiceItem.setInvItemQty(record.getAssignQty().intValue());

			invoiceItem.setInvItemGrossWeight(model.getGrossWeight());
			invoiceItem.setInvItemGrossWeightUnit(model.getGrossWeightUnit());

			invoiceItem.setLastUser(username);
			invoiceItem.setLastUpdate(now);

			if ("D".equals(record.getItemType())) {
				TblExportDetailPk tblExportDetailPk = new TblExportDetailPk();
				tblExportDetailPk.setCompanyId(companyId);
				tblExportDetailPk.setBranchId(branchId);
				tblExportDetailPk.setFilename(record.getFilename());
				tblExportDetailPk.setTblExportDetailId(record.getTblExportDetailId());
				TblExportDetail tblExportDetail = tblExportDetailRepository.getById(tblExportDetailPk);

				invoiceItem.setInvNoDec(preInvoiceHeader.getInvoiceNo());
				invoiceItem.setInvItemNumber(tblExportDetail.getTblExportDetailId());
				invoiceItem.setProductCode(tblExportDetail.getProductCode());
				invoiceItem.setInvItemDec(tblExportDetail.getEngDescOfGoods());

				invoiceItem.setInvItemUom(tblExportDetail.getInvoiceQuantityUnit());

				BigDecimal netWeight = tblExportDetail.getNetWeight()
						.divide(tblExportDetail.getInvoiceQuantity(), 3, RoundingMode.HALF_UP)
						.multiply(record.getAssignQty());
				invoiceItem.setInvItemNetWeight(netWeight);
				invoiceItem.setInvItemNetWeightUnit(tblExportDetail.getNetWeightUnit());

				BigDecimal value = tblExportDetail.getInvoiceAmountBaht()
						.divide(tblExportDetail.getInvoiceQuantity(), 2, RoundingMode.HALF_UP)
						.multiply(record.getAssignQty());
				invoiceItem.setInvItemValue(value);

				invoiceItem.setInvItemNoofPack(null);
				invoiceItem.setInvItemNoofPackUnit(null);
				invoiceItem.setInvItemNoofPackPackingList(null);

				invoiceItem.setTblExportDetailId(tblExportDetail.getTblExportDetailId());
				invoiceItem.setDeclarationNumber(tblExportDetail.getDeclarationNumber());
				invoiceItem.setDeclarationFileName(tblExportDetail.getFilename());
				invoiceItem.setRefDec(null);

				invoiceItemRepository.saveAndFlush(invoiceItem);

				tblExportDetail.setRemainQty(tblExportDetail.getRemainQty().subtract(record.getAssignQty()));
				tblExportDetail.setDeliveryQty(tblExportDetail.getDeliveryQty().add(record.getAssignQty()));
				tblExportDetail.setRemainValue(tblExportDetail.getRemainValue().subtract(value));
				tblExportDetail.setRemainNetWeight(tblExportDetail.getRemainNetWeight().subtract(netWeight));
				tblExportDetail.setRemainTotalPackageAmount(
						tblExportDetail.getRemainTotalPackageAmount().add(record.getAssignPackageAmount()));
				tblExportDetailRepository.saveAndFlush(tblExportDetail);

			} else if ("S".equals(record.getItemType())) {
				TblExportDetailPk tblExportDetailPk = new TblExportDetailPk();
				tblExportDetailPk.setCompanyId(companyId);
				tblExportDetailPk.setBranchId(branchId);
				tblExportDetailPk.setFilename(record.getFilename());
				tblExportDetailPk.setTblExportDetailId(record.getTblExportDetailId());
				TblExportDetail tblExportDetail = tblExportDetailRepository.getById(tblExportDetailPk);

				invoiceItem.setInvNoDec(preInvoiceHeader.getInvoiceNo());
				invoiceItem.setInvItemNumber(null);
				invoiceItem.setProductCode(tblExportDetail.getProductCode());
				invoiceItem.setInvItemDec(tblExportDetail.getEngDescOfGoods());

				invoiceItem.setInvItemUom(tblExportDetail.getInvoiceQuantityUnit());

				BigDecimal netWeight = tblExportDetail.getNetWeight()
						.divide(tblExportDetail.getInvoiceQuantity(), 3, RoundingMode.HALF_UP)
						.multiply(record.getAssignQty());
				invoiceItem.setInvItemNetWeight(netWeight);
				invoiceItem.setInvItemNetWeightUnit(tblExportDetail.getNetWeightUnit());

				BigDecimal value = tblExportDetail.getInvoiceAmountBaht()
						.divide(tblExportDetail.getInvoiceQuantity(), 2, RoundingMode.HALF_UP)
						.multiply(record.getAssignQty());
				invoiceItem.setInvItemValue(value);

				invoiceItem.setInvItemNoofPack(null); // TODO:
				invoiceItem.setInvItemNoofPackUnit(tblExportDetail.getPackageUnit());
				invoiceItem.setInvItemNoofPackPackingList(null); // TODO:

				invoiceItem.setDeclarationNumber(null);
				invoiceItem.setDeclarationFileName(null);
				invoiceItem.setRefDec(tblExportDetail.getDeclarationNumber());

				invoiceItemRepository.saveAndFlush(invoiceItem);
			} else if ("M".equals(record.getItemType())) { // TODO:
				invoiceItem.setInvNoDec(null);
				invoiceItem.setInvItemNumber(null);
				invoiceItem.setProductCode(preInvoiceDetail.getPartNumber());

				invoiceItem.setInvItemDec(preInvoiceDetail.getPartDescription());

				// invoiceItem.setInvItemUom(tblExportDetail.getInvoiceQuantityUnit());

				// invoiceItemRepository.saveAndFlush(invoiceItem);
			}

			BigDecimal remainInvoiceQty = preInvoiceDetail.getRemainInvoiceQty();
			preInvoiceDetail.setRemainInvoiceQty(remainInvoiceQty.subtract(record.getAssignQty()));
			preInvoiceDetailRepository.saveAndFlush(preInvoiceDetail);
		}

		invoiceRepository.updateAllSummary(companyId, branchId, tripId);

		transferRepository.updateAllSummary(companyId, branchId, tripId);

		return Data.nil();
	}

	private Data addAssignItemManual(final GenerateTripModel model) {
		Integer companyId = model.getCompanyId();
		Integer branchId = model.getBranchId();
		String username = SecurityUtils.getUsername();
		LocalDateTime now = LocalDateTime.now();

		Transfer transfer = createOrUpdateTransfer(model);
		String tripId = transfer.getTrfId();

		Invoice invoice = createOrUpdateInvoice(transfer);
		Integer invoiceId = invoice.getInvId();
		Integer trfItemId = invoice.getTrfItemId();

		for (DeclarationRecord record : model.getRecords()) {

			TblExportDetailPk tblExportDetailPk = new TblExportDetailPk();
			tblExportDetailPk.setCompanyId(companyId);
			tblExportDetailPk.setBranchId(branchId);
			tblExportDetailPk.setFilename(record.getFilename());
			tblExportDetailPk.setTblExportDetailId(record.getTblExportDetailId());

			TblExportDetail tblExportDetail = tblExportDetailRepository.getById(tblExportDetailPk);

			InvoiceItem invoiceItem = new InvoiceItem();
			invoiceItem.setCompanyId(companyId);
			invoiceItem.setBranchId(branchId);
			invoiceItem.setInvId(invoiceId);
			invoiceItem.setTrfId(tripId);
			invoiceItem.setTrfItemId(trfItemId);

			invoiceItem.setItemType("D");

			invoiceItem.setInvItemQty(record.getAssignQty().intValue());
			invoiceItem.setInvItemUom(tblExportDetail.getInvoiceQuantityUnit());

			invoiceItem.setProductCode(tblExportDetail.getProductCode());

			BigDecimal value = tblExportDetail.getInvoiceAmountBaht()
					.divide(tblExportDetail.getInvoiceQuantity(), 2, RoundingMode.HALF_UP)
					.multiply(record.getAssignQty());
			invoiceItem.setInvItemValue(value);
			invoiceItem.setInvItemDec(tblExportDetail.getEngDescOfGoods());
			invoiceItem.setInvNoDec(tblExportDetail.getInvoiceNo());
			invoiceItem.setInvNo(model.getInvoiceNo());
			invoiceItem.setInvItemNo(String.valueOf(tblExportDetail.getItemNo()));
			invoiceItem.setDeclarationNumber(tblExportDetail.getDeclarationNumber());

			BigDecimal netWeight = tblExportDetail.getNetWeight()
					.divide(tblExportDetail.getInvoiceQuantity(), 3, RoundingMode.HALF_UP)
					.multiply(record.getAssignQty());
			invoiceItem.setInvItemNetWeight(netWeight);
			invoiceItem.setInvItemNetWeightUnit(tblExportDetail.getNetWeightUnit());
			invoiceItem.setInvItemGrossWeight(model.getGrossWeight());
			invoiceItem.setInvItemGrossWeightUnit(model.getGrossWeightUnit());

			invoiceItem.setTblExportDetailId(tblExportDetail.getTblExportDetailId());
			invoiceItem.setTblExportDetailIdAssignPack(tblExportDetail.getTblExportDetailId());

			invoiceItem.setInvItemNoofPackPackingList(model.getNoOfPackagePackingList());
			invoiceItem.setDeclarationFileName(tblExportDetail.getFilename());

			BigDecimal assignPackageAmount = BigDecimal.ZERO;
			if (record.getAssignPackageAmount() != null) {
				assignPackageAmount = record.getAssignPackageAmount();
			}

			invoiceItem.setInvItemNoofPack(assignPackageAmount);
			invoiceItem.setInvItemNoofPackUnit(tblExportDetail.getPackageUnit());

			invoiceItem.setLastUser(username);
			invoiceItem.setLastUpdate(now);

			invoiceItemRepository.saveAndFlush(invoiceItem);

			tblExportDetail.setRemainQty(tblExportDetail.getRemainQty().subtract(record.getAssignQty()));
			tblExportDetail.setDeliveryQty(tblExportDetail.getDeliveryQty().add(record.getAssignQty()));
			tblExportDetail.setRemainValue(tblExportDetail.getRemainValue().subtract(value));
			tblExportDetail.setRemainNetWeight(tblExportDetail.getRemainNetWeight().subtract(netWeight));
			tblExportDetail.setRemainTotalPackageAmount(
					tblExportDetail.getRemainTotalPackageAmount().add(assignPackageAmount));

			tblExportDetailRepository.saveAndFlush(tblExportDetail);
		}

		invoiceRepository.updateAllSummary(companyId, branchId, tripId);

		transferRepository.updateAllSummary(companyId, branchId, tripId);

		return Data.of(model);
	}

	@Transactional
	public Data deleteAssignItem(final GenerateTripModel model) {
		InvoiceItem invoiceItem = invoiceItemRepository.getById(model.getInvoiceItemId());

		// EntryOption: Invoice, AllowExtra
		if (CoreUtils.isNotNull(invoiceItem.getId())) {
			PreInvoiceDetail preInvoiceDetail = preInvoiceDetailRepository.getById(invoiceItem.getId());
			preInvoiceDetail.setRemainInvoiceQty(
					preInvoiceDetail.getRemainInvoiceQty().add(BigDecimal.valueOf(invoiceItem.getInvItemQty())));
			preInvoiceDetailRepository.saveAndFlush(preInvoiceDetail);
		}

		// EntryOption: Invoice, AllowExtra, Manual
		if ("D".equals(invoiceItem.getItemType())) {
			TblExportDetailPk pk = new TblExportDetailPk();
			pk.setCompanyId(model.getCompanyId());
			pk.setBranchId(model.getBranchId());
			pk.setFilename(model.getFilename());
			pk.setTblExportDetailId(model.getTblExportDetailId());

			TblExportDetail tblExportDetail = tblExportDetailRepository.getById(pk);

			tblExportDetail
					.setRemainQty(tblExportDetail.getRemainQty().add(BigDecimal.valueOf(invoiceItem.getInvItemQty())));
			tblExportDetail.setDeliveryQty(
					tblExportDetail.getDeliveryQty().add(BigDecimal.valueOf(invoiceItem.getInvItemQty())));
			tblExportDetail.setRemainValue(tblExportDetail.getRemainValue().add(invoiceItem.getInvItemValue()));
			tblExportDetail
					.setRemainNetWeight(tblExportDetail.getRemainNetWeight().add(invoiceItem.getInvItemNetWeight()));
			tblExportDetail.setRemainTotalPackageAmount(
					tblExportDetail.getRemainTotalPackageAmount().subtract(invoiceItem.getInvItemNoofPack()));
			tblExportDetailRepository.saveAndFlush(tblExportDetail);
		}

		invoiceItemRepository.delete(invoiceItem);

		invoiceRepository.updateAllSummary(model.getCompanyId(), model.getBranchId(), model.getTripId());

		transferRepository.updateAllSummary(model.getCompanyId(), model.getBranchId(), model.getTripId());

		return Data.of();
	}

	@Transactional
	public Data addItemManual(final GenerateTripModel model) {
		Integer companyId = model.getCompanyId();
		Integer branchId = model.getBranchId();
		String username = SecurityUtils.getUsername();
		LocalDateTime now = LocalDateTime.now();

		Transfer transfer = createOrUpdateTransfer(model);
		String tripId = transfer.getTrfId();

		Invoice invoice = createOrUpdateInvoice(transfer);
		Integer invoiceId = invoice.getInvId();
		Integer trfItemId = invoice.getTrfItemId();

		for (InvoiceItemRecord record : model.getInvoiceItems()) {
			InvoiceItem invoiceItem = new InvoiceItem();
			invoiceItem.setCompanyId(companyId);
			invoiceItem.setBranchId(branchId);
			invoiceItem.setInvId(invoiceId);
			invoiceItem.setTrfId(tripId);
			invoiceItem.setTrfItemId(trfItemId);

			invoiceItem.setItemType("M");
			invoiceItem.setProductCode(record.getProductCode());
			invoiceItem.setInvItemDec(record.getDescription());
			invoiceItem.setInvItemQty(record.getQty().intValue());
			invoiceItem.setInvItemUom(record.getQtyUnit());
			invoiceItem.setInvItemNetWeight(record.getNetWeight());
			invoiceItem.setInvItemNetWeightUnit(record.getNetWeightUnit());
			invoiceItem.setInvItemValue(record.getValue());

			invoiceItem.setInvNo(model.getInvoiceNo());
			invoiceItem.setInvItemGrossWeight(model.getGrossWeight());
			invoiceItem.setInvItemGrossWeightUnit(model.getGrossWeightUnit());
			invoiceItem.setInvItemNoofPack(model.getNoOfPackage());
			invoiceItem.setInvItemNoofPackUnit(model.getNoOfPackageUnit());
			invoiceItem.setInvItemNoofPackPackingList(model.getNoOfPackagePackingList());
			invoiceItem.setRefDec(model.getDeclarationNo());
			invoiceItem.setLastUser(username);
			invoiceItem.setLastUpdate(now);

			invoiceItemRepository.saveAndFlush(invoiceItem);
		}

		invoiceRepository.updateAllSummary(companyId, branchId, tripId);

		transferRepository.updateAllSummary(companyId, branchId, tripId);

		return Data.of();
	}

	public Response loadDetailByInvoice(final GenerateTripModel model) {
		Integer entryOption = model.getEntryOption();

		Integer successRecord = 0;
		Integer notSuccessRecord = 0;

		Integer companyId = model.getCompanyId();
		Integer branchId = model.getBranchId();
		String invoiceNo = model.getInvoiceNo();
		String declarationNo = model.getDeclarationNo();

		List<Map<String, Object>> records = new ArrayList<>();

		if (CoreUtils.isEmpty(invoiceNo) || CoreUtils.isEmpty(declarationNo)) {
			return Response.success(GridData.of(records));
		}

		List<Map<String, Object>> preInvoiceList = searchPreInvoiceByInvoiceNo(companyId, branchId, invoiceNo);
		Map<String, List<Map<String, Object>>> declarationMap = searchDeclarationByDeclarationNoAndInvoiceNo(companyId,
				branchId, declarationNo, invoiceNo);

		for (Map<String, Object> preInvoice : preInvoiceList) {
			String productCode = (String) preInvoice.get("partNumber");
			BigDecimal invoiceQty = (BigDecimal) preInvoice.get("remainInvQty");
			if (invoiceQty != null && invoiceQty.compareTo(BigDecimal.ZERO) > 0) {
				List<Map<String, Object>> declarationList = declarationMap.get(productCode);
				if (CoreUtils.isNotEmpty(declarationList)) {
					int declarationListSize = declarationList.size();
					Map<String, Object> firstDeclaration = null;
					for (int i = 0; i < declarationListSize; i++) {
						Map<String, Object> declaration = declarationList.get(i);
						if (i == 0) {
							firstDeclaration = declaration;
						}
						BigDecimal declarationQty = (BigDecimal) declaration.get("remainQty");

						if (declarationQty != null && declarationQty.compareTo(BigDecimal.ZERO) > 0) {
							BigDecimal assignQty = invoiceQty.compareTo(declarationQty) <= 0 ? invoiceQty
									: declarationQty;
							// insert item_type D

							invoiceQty = invoiceQty.subtract(assignQty);
							preInvoice.put("remainInvQty", invoiceQty);

							declarationQty = declarationQty.subtract(assignQty);
							declaration.put("remainQty", declarationQty);

							successRecord++;

							records.add(generateInvoice(preInvoice, declaration, "D", assignQty, declarationNo));
						}
						if (invoiceQty.compareTo(BigDecimal.ZERO) <= 0) {
							break;
						}
					}
					if (invoiceQty.compareTo(BigDecimal.ZERO) > 0) {
						// insert item_type S
						BigDecimal assignQty = BigDecimal.ZERO;

						notSuccessRecord++;

						if (entryOption == 2) {
							assignQty = invoiceQty;
						}
						records.add(generateInvoice(preInvoice, firstDeclaration, "S", assignQty, declarationNo));
					}
				} else {
					// insert item_type M
					BigDecimal assignQty = BigDecimal.ZERO;

					notSuccessRecord++;

					if (entryOption == 2) {
						assignQty = invoiceQty;
					}
					records.add(generateInvoice(preInvoice, null, "M", assignQty, declarationNo));
				}
			}
		}

		Map<String, Object> data = new HashMap<>();
		data.put("successRecord", successRecord);
		data.put("notSuccessRecord", notSuccessRecord);

		return Response.success(GridData.of(records), Data.of(data));
	}

	private Map<String, Object> generateInvoice(Map<String, Object> preInvoice, Map<String, Object> declaration,
			String itemType, BigDecimal assignQty, String declarationNo) {
		Map<String, Object> gt = new HashMap<>();
		gt.put("companyId", preInvoice.get("companyId"));
		gt.put("branchId", preInvoice.get("branchId"));
		gt.put("itemType", itemType);
		gt.put("invoiceHdrId", preInvoice.get("invhdrId"));
		gt.put("invoiceDtlId", preInvoice.get("invdtlId"));
		gt.put("invoiceNo", preInvoice.get("invoiceNo"));
		gt.put("invoiceDate", preInvoice.get("invoiceDate"));
		gt.put("partNumber", preInvoice.get("partNumber"));
		gt.put("partDescription", preInvoice.get("partDescription"));

		if (assignQty.compareTo(BigDecimal.ZERO) > 0) {
			gt.put("invoiceQty", assignQty);
			gt.put("remainInvoiceQty", assignQty);
		} else {
			gt.put("invoiceQty", preInvoice.get("remain_invqty"));
			gt.put("remainInvoiceQty", preInvoice.get("remain_invqty"));
		}

		gt.put("remainInvoiceQty", BigDecimal.valueOf(10));

		gt.put("assignQty", assignQty);
		gt.put("invoiceItemNo", preInvoice.get("itemNo"));
		gt.put("truckCode", preInvoice.get("truck_code"));

		gt.put("declarationNo", declarationNo);

		if (declaration != null) {
			gt.put("declarationItemNo", declaration.get("itemNo"));
			gt.put("filename", declaration.get("declarationFilename"));
			gt.put("declarationNo", declaration.get("declarationNo"));
			gt.put("declarationDate", declaration.get("declarationDate"));
			gt.put("declarationInvoiceNo", declaration.get("declarationInvoiceNo"));
			gt.put("declarationInvoiceDate", declaration.get("declarationInvoiceDate"));
			gt.put("tblExportDetailId", declaration.get("tblexportdetailId"));

			gt.put("productCode", declaration.get("productCode"));
			gt.put("productEngDesc", declaration.get("engDescOfGoods"));
			gt.put("productThaiDesc", declaration.get("thaiDescOfGoods"));
			gt.put("tariffCode", declaration.get("tariffCode"));

			gt.put("declarationQtyUnit", declaration.get("invoiceQtyUnit"));
			gt.put("declarationQty", declaration.get("invoiceQty"));
			gt.put("remainDeclarationQty", declaration.get("remainQty2"));

			gt.put("netWeight", declaration.get("netWeight"));
			gt.put("netWeightUnit", declaration.get("netWeightUnit"));
			gt.put("remainNetWeight", declaration.get("remainNetWeight"));
			gt.put("valueAmt", declaration.get("valueBaht"));
			gt.put("remainValueAmt", declaration.get("remainValueBaht"));
			gt.put("noOfPackageUnit", declaration.get("noOfPackageUnit"));
			gt.put("noOfPackage", declaration.get("noOfPackage"));
		}

		return gt;
	}

	private List<Map<String, Object>> searchPreInvoiceByInvoiceNo(Integer companyId, Integer branchId,
			String invoiceNo) {
		SqlParams params = SqlParams.create();

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	inh.cpn_id AS \"companyId\", ");
		sql.append(" 	inh.branch_id AS \"branchId\", ");
		sql.append(" 	inh.id AS \"invhdrId\", ");
		sql.append(" 	inh.invoiceno AS \"invoiceNo\", ");
		sql.append(" 	inh.invoicedate AS \"invoiceDate\", ");
		sql.append(" 	ind.id AS \"invdtlId\", ");
		sql.append(" 	ind.itemno AS \"itemNo\", ");
		sql.append(" 	ind.partnumber AS \"partNumber\", ");
		sql.append(" 	ind.partdescription AS \"partDescription\", ");
		sql.append(" 	ind.invoicequantity AS \"invoiceQuantity\", ");
		sql.append(" 	ind.remain_invqty AS \"remainInvQty\", ");
		sql.append(" 	inh.truck_code AS \"truckCode\" ");
		sql.append(" FROM pre_invoice_dtl ind ");
		sql.append(" 	JOIN pre_invoice_hdr inh ON inh.cpn_id = ind.cpn_id ");
		sql.append(" 		AND inh.branch_id = ind.branch_id ");
		sql.append(" 		AND inh.id = ind.invhdr_id ");
		sql.append(" WHERE inh.cpn_id = :companyId ");
		params.add("companyId", companyId);

		sql.append(" 	AND inh.branch_id = :branchId ");
		params.add("branchId", branchId);

		sql.append(" 	AND inh.invoiceno = :invoiceNo ");
		params.add("invoiceNo", invoiceNo);

		sql.append(" ORDER BY ind.partnumber, ind.itemno ");

		SqlTypeConversion conversion = SqlTypeConversion.create();
		conversion.add("companyId", Integer.class);
		conversion.add("branchId", Integer.class);
		conversion.add("invhdrId", Integer.class);
		conversion.add("invoiceNo", String.class);
		conversion.add("invoiceDate", LocalDate.class);
		conversion.add("invdtlId", Integer.class);
		conversion.add("itemNo", String.class);
		conversion.add("partNumber", String.class);
		conversion.add("partDescription", String.class);
		conversion.add("invoiceQuantity", BigDecimal.class);
		conversion.add("remainInvQty", BigDecimal.class);
		conversion.add("truckCode", String.class);

		GridData gridData = coreRepository.searchGridData(sql.toString(), params, conversion);
		return gridData.getRecords();
	}

	private Map<String, List<Map<String, Object>>> searchDeclarationByDeclarationNoAndInvoiceNo(Integer companyId,
			Integer branchId, String declarationNo, String invoiceNo) {
		SqlParams params = SqlParams.create();

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT ");
		sql.append(" 	tbd.cpn_id AS \"companyId\", ");
		sql.append(" 	tbd.branch_id AS \"branchId\", ");
		sql.append(" 	tbd.tblexportdetail_id AS \"tblexportdetailId\", ");
		sql.append(" 	tbd.filename AS \"declarationFilename\", ");
		sql.append(" 	tbd.declarationnumber AS \"declarationNo\", ");
		sql.append(" 	tbd.declarationdate AS \"declarationDate\", ");
		sql.append(" 	tbd.itemno          AS \"itemNo\", ");
		sql.append(" 	tbd.productcode     AS \"productCode\", ");
		sql.append(" 	tbd.thaidescofgoods AS \"thaiDescOfGoods\", ");
		sql.append(" 	tbd.engdescofgoods AS \"engDescOfGoods\", ");
		sql.append(" 	tbd.invoicequantity AS \"invoiceQty\", ");
		sql.append(" 	tbd.invoicequantityunit AS \"invoiceQtyUnit\", ");
		sql.append(" 	tbd.remainqty       AS \"remainQty\", ");
		sql.append(" 	tbd.remainqty       AS \"remainQty2\", ");
		sql.append(" 	tbd.invoiceno       AS \"declarationInvoiceNo\", ");
		sql.append(" 	tbd.invoicedate AS \"declarationInvoiceDate\", ");
		sql.append(" 	tbd.tariffcode      AS \"tariffCode\", ");
		sql.append(" 	tbd.netweight       AS \"netWeight\", ");
		sql.append(" 	tbd.netweightunit       AS \"netWeightUnit\", ");
		sql.append(" 	tbd.remainnetweight AS \"remainNetWeight\", ");
		sql.append(" 	tbd.invoiceamountbaht AS \"valueBaht\", ");
		sql.append(" 	tbd.remainvalue     AS \"remainValueBaht\", ");
		sql.append(" 	tbd.packageunit     AS \"noOfPackageUnit\", ");
		sql.append(" 	tbd.packageamount   AS \"noOfPackage\" ");
		sql.append(" FROM tblexportdetail tbd ");
		sql.append(" WHERE tbd.declarationnumber = :declarationNo ");
		sql.append(" 	AND tbd.cpn_id = :companyId ");
		sql.append(" 	AND tbd.branch_id = :branchId ");
		sql.append(" 	AND EXISTS( ");
		sql.append(" 		SELECT 1 ");
		sql.append(
				" 		FROM pre_invoice_dtl ind JOIN pre_invoice_hdr inh ON inh.id = ind.invhdr_id AND inh.cpn_id = ind.cpn_id AND inh.branch_id = ind.branch_id ");
		sql.append(" 		WHERE ind.partnumber = tbd.productcode ");
		sql.append(" 			AND inh.invoiceno = :invoiceNo ");
		sql.append(" 			AND inh.cpn_id = tbd.cpn_id ");
		sql.append(" 			AND inh.branch_id = tbd.branch_id ");
		sql.append(" 	) ");
		sql.append(" ORDER BY tbd.productcode, tbd.itemno ");

		params.add("declarationNo", declarationNo);
		params.add("invoiceNo", invoiceNo);
		params.add("companyId", companyId);
		params.add("branchId", branchId);

		SqlTypeConversion conversion = SqlTypeConversion.create();
		conversion.add("companyId", Integer.class);
		conversion.add("branchId", Integer.class);
		conversion.add("tblexportdetailId", Integer.class);
		conversion.add("declarationFilename", String.class);
		conversion.add("declarationNo", String.class);
		conversion.add("declarationDate", LocalDate.class);
		conversion.add("itemNo", String.class);
		conversion.add("productCode", String.class);
		conversion.add("thaiDescOfGoods", String.class);
		conversion.add("engDescOfGoods", String.class);
		conversion.add("invoiceQty", BigDecimal.class);
		conversion.add("invoiceQtyUnit", String.class);

		conversion.add("remainQty", BigDecimal.class);
		conversion.add("declarationInvoiceNo", String.class);
		conversion.add("declarationInvoiceDate", LocalDate.class);
		conversion.add("tariffCode", String.class);
		conversion.add("netWeight", BigDecimal.class);
		conversion.add("netWeightUnit", String.class);
		conversion.add("remainNetWeight", BigDecimal.class);
		conversion.add("valueBaht", BigDecimal.class);
		conversion.add("remainValueBaht", BigDecimal.class);
		conversion.add("noOfPackageUnit", String.class);
		conversion.add("noOfPackage", BigDecimal.class);

		GridData gridData = coreRepository.searchGridData(sql.toString(), params, conversion);

		Map<String, List<Map<String, Object>>> resultMap = new HashMap<String, List<Map<String, Object>>>();
		for (Map<String, Object> map : gridData.getRecords()) {
			String productCode = (String) map.get("productCode");
			if (resultMap.containsKey(productCode)) {
				resultMap.get(productCode).add(map);
			} else {
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				list.add(map);
				resultMap.put(productCode, list);
			}
		}

		return resultMap;
	}
}
