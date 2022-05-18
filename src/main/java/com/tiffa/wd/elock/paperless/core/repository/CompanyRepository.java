package com.tiffa.wd.elock.paperless.core.repository;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tiffa.wd.elock.paperless.core.entity.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Integer> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query(value = "SELECT c FROM company c WHERE cpn_id = :companyId ")
	public Company getByIdWithLock(@Param("companyId") Integer companyId);

}
