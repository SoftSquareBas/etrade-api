package com.tiffa.wd.elock.paperless.core.repository;

import com.tiffa.wd.elock.paperless.core.entity.Inlocation;
import com.tiffa.wd.elock.paperless.core.entity.InlocationPk;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InlocationRepository extends JpaRepository<Inlocation, InlocationPk> {

}