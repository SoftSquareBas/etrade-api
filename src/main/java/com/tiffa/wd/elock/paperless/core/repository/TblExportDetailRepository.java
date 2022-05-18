package com.tiffa.wd.elock.paperless.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tiffa.wd.elock.paperless.core.entity.TblExportDetail;
import com.tiffa.wd.elock.paperless.core.entity.TblExportDetailPk;

@Repository
public interface TblExportDetailRepository extends JpaRepository<TblExportDetail, TblExportDetailPk> {

}
