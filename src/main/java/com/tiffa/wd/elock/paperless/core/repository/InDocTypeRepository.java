package com.tiffa.wd.elock.paperless.core.repository;

import com.tiffa.wd.elock.paperless.core.entity.InDocType;
import com.tiffa.wd.elock.paperless.core.entity.InDocTypePK;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InDocTypeRepository extends JpaRepository<InDocType, InDocTypePK> {

}