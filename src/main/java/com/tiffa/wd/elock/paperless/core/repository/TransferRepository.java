package com.tiffa.wd.elock.paperless.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tiffa.wd.elock.paperless.core.entity.Transfer;
import com.tiffa.wd.elock.paperless.core.entity.TransferPk;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, TransferPk> {

	
	@Modifying
	@Query(value = 
			" UPDATE transfer t "
			+ " SET t.description = ( "
			+ " 		SELECT LISTAGG(TRIM(i.invitem_dec) || ' = ' || TO_CHAR(i.invitem_qty, 'FM99999999999999999990.0') || ' ' || i.invitem_uom , ', ') WITHIN GROUP (ORDER BY i.invitem_uom, i.invitem_dec) "
			+ " 		FROM invoice_item i "
			+ " 		WHERE i.cpn_id = :companyId "
			+ " 			AND i.branch_id = :branchId "
			+ " 			AND i.trf_id = :tripId "
			+ " 	), "
			+ " 	t.quantity = ( "
			+ " 		SELECT LISTAGG(TO_CHAR(invitem_qty, 'FM9,999,999,999,999,999,990') || ' ' || uom_name, '<br>') WITHIN GROUP (ORDER BY uom_name ASC NULLS FIRST) "
			+ " 		FROM ( "
			+ " 			SELECT SUM(NVL(i.invitem_qty,0)) AS invitem_qty, u.uom_name "
			+ " 			FROM invoice_item i "
			+ " 				LEFT JOIN uom u ON u.cpn_id = i.cpn_id AND u.branch_id = i.branch_id AND u.uom_code = i.invitem_uom  "
			+ " 			WHERE i.cpn_id = :companyId "
			+ " 				AND i.branch_id = :branchId "
			+ " 				AND i.trf_id = :tripId "
			+ " 			GROUP BY u.uom_name "
			+ " 		) "
			+ " 	), "
			+ " 	t.bathvalue = ( "
			+ " 		SELECT TO_CHAR(SUM(i.invitem_value), 'FM9,999,999,999,999,999,990.0000') "
			+ " 		FROM invoice_item i "
			+ " 		WHERE i.cpn_id = :companyId "
			+ " 			AND i.branch_id = :branchId "
			+ " 			AND i.trf_id = :tripId "
			+ " 	), "
			+ " 	t.declaration_filename = ( "
			+ " 		SELECT SUBSTR(MAX(TO_CHAR(i.invitem_id, 'FM00000000000000000000')||i.declaration_filename),21) "
			+ " 		FROM invoice_item i "
			+ " 		WHERE i.cpn_id = :companyId "
			+ " 			AND i.branch_id = :branchId "
			+ " 			AND i.trf_id = :tripId "
			+ " 	), "
			+ " 	t.declaration_no = ( "
			+ " 		SELECT SUBSTR(MAX(TO_CHAR(i.invitem_id, 'FM00000000000000000000')||i.declarationnumber),21) "
			+ " 		FROM invoice_item i "
			+ " 		WHERE i.cpn_id = :companyId "
			+ " 			AND i.branch_id = :branchId "
			+ " 			AND i.trf_id = :tripId "
			+ " 	), "
			+ " 	t.invoice_no = ( "
			+ " 		SELECT SUBSTR(MAX(TO_CHAR(i.invitem_id, 'FM00000000000000000000')||i.inv_no),21) "
			+ " 		FROM invoice_item i "
			+ " 		WHERE i.cpn_id = :companyId "
			+ " 			AND i.branch_id = :branchId "
			+ " 			AND i.trf_id = :tripId "
			+ " 	), "
			+ " 	t.truck_code = ( "
			+ " 		SELECT pih.truck_code "
			+ " 		FROM pre_invoice_hdr pih "
			+ " 			JOIN pre_invoice_dtl pid ON pid.cpn_id = pih.cpn_id AND pid.branch_id = pih.branch_id AND pid.invhdr_id = pih.id "
			+ " 		WHERE pih.cpn_id = :companyId "
			+ " 			AND pih.branch_id = :branchId "
			+ " 			AND pid.id = ( "
			+ " 				SELECT TO_NUMBER(SUBSTR(MAX(TO_CHAR(i.invitem_id, 'FM00000000000000000000')||i.id),21)) "
			+ " 				FROM invoice_item i "
			+ " 				WHERE i.cpn_id = :companyId "
			+ " 					AND i.branch_id = pih.branch_id "
			+ " 					AND i.trf_id = :tripId "
			+ " 					AND i.inv_no = pih.invoiceno "
			+ " 			) "
			+ " 	) "
			+ " WHERE t.cpn_id = :companyId "
			+ " 	AND t.branch_id = :branchId "
			+ " 	AND t.trf_id = :tripId "
			, nativeQuery = true)
	public void updateAllSummary(
		@Param("companyId") Integer companyId, 
		@Param("branchId") Integer branchId,
		@Param("tripId") String tripId
	);
}
