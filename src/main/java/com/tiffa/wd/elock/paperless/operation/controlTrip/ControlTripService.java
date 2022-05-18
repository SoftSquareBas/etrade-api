package com.tiffa.wd.elock.paperless.operation.controlTrip;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.tiffa.wd.elock.paperless.core.Data;
import com.tiffa.wd.elock.paperless.core.GridData;
import com.tiffa.wd.elock.paperless.core.Sort;
import com.tiffa.wd.elock.paperless.core.entity.Relation;
import com.tiffa.wd.elock.paperless.core.entity.RelationPk;
import com.tiffa.wd.elock.paperless.core.entity.TagStatus;
import com.tiffa.wd.elock.paperless.core.entity.Transfer;
import com.tiffa.wd.elock.paperless.core.entity.TransferPk;
import com.tiffa.wd.elock.paperless.core.repository.CoreRepository;
import com.tiffa.wd.elock.paperless.core.repository.RelationRepository;
import com.tiffa.wd.elock.paperless.core.repository.SqlParams;
import com.tiffa.wd.elock.paperless.core.repository.SqlSort;
import com.tiffa.wd.elock.paperless.core.repository.TagStatusRepository;
import com.tiffa.wd.elock.paperless.core.repository.TransferRepository;
import com.tiffa.wd.elock.paperless.core.util.CoreUtils;
import com.tiffa.wd.elock.paperless.core.util.SecurityUtils;
import com.tiffa.wd.elock.paperless.core.util.TypeConvertionUtils;

@Service
@Transactional(readOnly = true, propagation = Propagation.NEVER)
public class ControlTripService {

	@Autowired
	private CoreRepository coreRepository;
	
	@Autowired
	private TransferRepository transferRepository;
	
	@Autowired
	private RelationRepository relationRepository;
	
	@Autowired
	private TagStatusRepository tagStatusRepository;
	
	public GridData search(final ControlTripModel model) {
		SqlParams params = SqlParams.createPageParam(model);
		StringBuilder sql = new StringBuilder();
		
		sql.append(" SELECT t.cpn_id AS \"companyId\", ");
		sql.append(" 	c.cpn_code AS \"companyCode\", ");
		sql.append(" 	t.branch_id AS \"branchId\", ");
		sql.append(" 	b.branch_code AS \"branchCode\", ");
		sql.append(" 	t.trf_date AS \"tripDate\", ");
		sql.append(" 	t.trf_id AS \"tripId\", ");
		sql.append(" 	t.trf_from AS \"tripFrom\", ");
		sql.append(" 	t.trf_to AS \"tripTo\", ");
		sql.append(" 	l.time AS \"time\" ");
		sql.append(" FROM transfer t ");
		sql.append(" 	JOIN company c ON c.cpn_id = t.cpn_id "); 
		sql.append(" 	JOIN branch b ON b.branch_id = t.branch_id ");
		sql.append(" 	JOIN station s ON s.stn_code = t.trf_from AND s.cpn_id = t.cpn_id AND s.branch_id = t.branch_id ");
		sql.append(" 	JOIN station s2 ON s2.stn_code = t.trf_to AND s2.cpn_id = t.cpn_id AND s2.branch_id = t.branch_id ");
		sql.append(" 	JOIN location_mapping_time l ON l.trf_from = s.cust_office_code AND l.trf_to = s2.cust_office_code AND l.cpn_id = t.cpn_id AND l.branch_id = t.branch_id ");
		sql.append(" WHERE t.trf_lt = 0 ");
		sql.append(" 	AND t.trf_from IS NOT NULL ");
		sql.append(" 	AND t.trf_to IS NOT NULL ");
		sql.append(" 	AND t.trf_from <> '%'  ");
		sql.append(" 	AND t.trf_to <> '%' "); 
		
		sql.append("    AND t.cpn_id = :companyId ");
		params.add("companyId", model.getCompanyId());

		sql.append("    AND t.branch_id = :branchId ");
		params.add("branchId", model.getBranchId());
		
		SqlSort sort = SqlSort.create(model, Sort.by("tripId", Direction.DESC));

		return coreRepository.searchPagingGridData(sql.toString(), params, sort);
	}
	
	public Data validate(final ControlTripModel model) {
		SqlParams params = SqlParams.createValidateParam(model);
		params.add("companyId", model.getCompanyId());
		
		if("tagCode".equals(model.getField())) {
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT CASE WHEN COUNT(1) > 0 THEN 0 ELSE 1 END AS \"notexists\" ");
			sql.append(" FROM tag t ");
			sql.append(" WHERE t.cpn_id = :companyId ");
			sql.append("    AND t.tag_code = :tagCode ");
			sql.append("    AND t.tag_status = 'Yes' ");

			return coreRepository.validate(sql.toString(), params);	
		}
		return Data.nil();
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Data confirmTrip(final ControlTripModel model) {
		if(CoreUtils.isNotEmpty(model.getTripRecords())) {
			Integer companyId = model.getCompanyId();
			Integer branchId = model.getBranchId();
			String tagCode = model.getTagCode();
			String driverCode = model.getDriver();
			String truckCode = model.getTruckCode();
			Integer intervalTime = model.getIntervalTime();
			LocalDateTime now = LocalDateTime.now();
			Integer lastTruck = TypeConvertionUtils.convert(model.getLastTruck(), Integer.class);
			String username = SecurityUtils.getUsername();
			String routeCode = model.getRoute();
			String containerNumber = model.getContainerNumber();
			String vehicleType = model.getVehicleType();
	
			tagStatusRepository.deleteAllByTagId(companyId, branchId, tagCode);
			
			List<String> tripIds = new ArrayList<>();
			String tripFrom = null, tripTo = null;
	
			for (TripRecord record : model.getTripRecords()) {
				
				TransferPk transferPk = new TransferPk();
				transferPk.setCompanyId(companyId);
				transferPk.setBranchId(branchId);
				transferPk.setTrfId(record.getTripId());
				Transfer transfer = transferRepository.getById(transferPk);
				
				tripIds.add(transfer.getTrfId());
				tripFrom = transfer.getTrfFrom();
				tripTo = transfer.getTrfTo();
				
				RelationPk relationPk = new RelationPk();
				relationPk.setCompanyId(transfer.getCompanyId());
				relationPk.setBranchId(transfer.getBranchId());
				relationPk.setTrfId(transfer.getTrfId());
				relationPk.setTagCode(tagCode);
				relationPk.setDrvCode(driverCode);
				
				Relation relation = new Relation();
				relation.setPk(relationPk);
				relation.setTruckCode(truckCode);
				relation.setRelStartDate(now);
				relation.setRelTravelTime(intervalTime);
				relation.setRelInspect(0);
				relation.setRelLastTruck(lastTruck);
				relation.setStatus("Normal");
				relation.setLastUser(username);
				relation.setTamperedFlag(0);
				relation.setLateFlag(0);
				relation.setType("TRO");
				relation.setToQuantity(transfer.getQuantity());
				relation.setToBathvalue(transfer.getBathvalue());
				relation.setCorridorCode(routeCode);
				relation.setToDescription(transfer.getDescription());
				relation.setContainerNumber(containerNumber);
				relation.setVehicleType(vehicleType);
				relationRepository.save(relation);
				
				transfer.setTrfTravelTime(intervalTime);
				transfer.setTrfLt(lastTruck);
				transfer.setTruckCode(truckCode);
				transferRepository.save(transfer);
			}
			
			TagStatus tagStatus = new TagStatus();
			tagStatus.setTagId(tagCode);
			tagStatus.setCompanyId(companyId);
			tagStatus.setBranchId(branchId);
			tagStatus.setTruckCode(truckCode);
			tagStatus.setTrfIds(StringUtils.arrayToDelimitedString(tripIds.toArray(), ","));
			tagStatus.setTrfFrom(tripFrom); 
			tagStatus.setTrfTo(tripTo);
			tagStatus.setState("1");
			tagStatusRepository.save(tagStatus);
		}

		return Data.of(model);
	}
}
