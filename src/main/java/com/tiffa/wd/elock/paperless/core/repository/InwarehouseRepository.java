package com.tiffa.wd.elock.paperless.core.repository;

import com.tiffa.wd.elock.paperless.core.entity.Inwarehouse;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InwarehouseRepository extends JpaRepository<Inwarehouse, String> {
    
}
