package com.tiffa.wd.elock.paperless.core.web.po.rt.repository;

import com.tiffa.wd.elock.paperless.core.web.po.rt.entity.DbPoType;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PoTypeRepository extends JpaRepository<DbPoType, String> {

}