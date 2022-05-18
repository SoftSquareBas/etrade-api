package com.tiffa.wd.elock.paperless.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tiffa.wd.elock.paperless.core.entity.TagStatus;

@Repository
public interface TagStatusRepository extends JpaRepository<TagStatus, Integer> {

	@Modifying
    @Query(value = "DELETE FROM tag_status t WHERE t.cpn_id = :companyId AND t.branch_id = :branchId AND t.tag_id = :tagId ", nativeQuery = true)
    void deleteAllByTagId(@Param("companyId") Integer companyId, @Param("branchId") Integer branchId, @Param("tagId") String tagId);
}
