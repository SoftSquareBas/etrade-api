package com.tiffa.wd.elock.paperless.core.repository;

import com.tiffa.wd.elock.paperless.core.entity.DbPoType;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PoTypeRepository extends JpaRepository<DbPoType, String> {

}