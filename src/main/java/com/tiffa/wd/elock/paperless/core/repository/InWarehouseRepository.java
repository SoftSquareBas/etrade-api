package com.tiffa.wd.elock.paperless.core.repository;

import com.tiffa.wd.elock.paperless.core.entity.InWarehouse;
import com.tiffa.wd.elock.paperless.core.entity.InWarehousePK;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InWarehouseRepository extends JpaRepository<InWarehouse, InWarehousePK> {

}