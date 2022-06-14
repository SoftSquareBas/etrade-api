package com.tiffa.wd.elock.paperless.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tiffa.wd.elock.paperless.core.entity.DbTitle;

public interface DbTitleRepository extends JpaRepository<DbTitle, String> {

}