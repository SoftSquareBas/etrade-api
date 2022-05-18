package com.tiffa.wd.elock.paperless.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tiffa.wd.elock.paperless.core.entity.Invoice;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {

	
	@Query(value = "SELECT * FROM invoice i WHERE i.cpn_id = :companyId AND i.branch_id = :branchId AND i.trf_id = :tripId", nativeQuery = true)
	public Invoice findInvoice(
		@Param("companyId") Integer companyId, 
		@Param("branchId") Integer branchId,
		@Param("tripId") String tripId
	);
	
	@Modifying
	@Query(value =  
			" MERGE INTO invoice trg "
			+ " USING ( "
			+ " 	SELECT "
			+ " 		i.cpn_id, i.branch_id, i.trf_id, i.inv_id, "
			+ " 		SUBSTR(MAX(TO_CHAR(i.invitem_id, 'FM00000000000000000000')||i.inv_no),21) AS inv_no, "
			+ " 		SUM(NVL(i.invitem_grossweight,0)) AS invitem_grossweight, "
			+ " 		SUBSTR(MAX(TO_CHAR(i.invitem_id, 'FM00000000000000000000') || i.invitem_grossweight_unit), 21) AS invitem_grossweight_unit,  "
			+ " 		SUM(NVL(i.invitem_noofpack,0)) AS invitem_noofpack,  "
			+ " 		SUBSTR(MAX(TO_CHAR(i.invitem_id, 'FM00000000000000000000') || i.invitem_noofpack_unit), 21) AS invitem_noofpack_unit "
			+ " 	FROM invoice_item i "
			+ " 	WHERE i.cpn_id = :companyId "
			+ " 		AND i.branch_id = :branchId "
			+ " 		AND i.trf_id = :tripId "
			+ " 	GROUP BY i.cpn_id, i.branch_id, i.trf_id, i.inv_id "
			+ " ) src "
			+ " ON ( "
			+ " 	trg.cpn_id = src.cpn_id  "
			+ " 	AND trg.branch_id = src.branch_id  "
			+ " 	AND trg.trf_id = src.trf_id "
			+ " 	AND trg.inv_id = src.inv_id "
			+ " ) "
			+ " WHEN MATCHED THEN UPDATE SET "
			+ " 	trg.inv_no = src.inv_no, "
			+ " 	trg.inv_gross_weight = src.invitem_grossweight, "
			+ " 	trg.inv_gross_weight_unit = src.invitem_grossweight_unit, "
			+ " 	trg.inv_package = src.invitem_noofpack, "
			+ " 	trg.inv_package_unit = src.invitem_noofpack_unit "
			, nativeQuery = true)
	public void updateAllSummary(
		@Param("companyId") Integer companyId, 
		@Param("branchId") Integer branchId,
		@Param("tripId") String tripId
	);
	
}
