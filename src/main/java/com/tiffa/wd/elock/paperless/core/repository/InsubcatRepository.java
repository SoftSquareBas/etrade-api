package com.tiffa.wd.elock.paperless.core.repository;


import com.tiffa.wd.elock.paperless.core.entity.Insubcat;
import com.tiffa.wd.elock.paperless.core.entity.InsubcatPk;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InsubcatRepository extends JpaRepository<Insubcat,InsubcatPk> {

}