package com.tiffa.wd.elock.paperless.core.repository;


import com.tiffa.wd.elock.paperless.core.entity.InperiodPk;
import com.tiffa.wd.elock.paperless.core.entity.Inperiod;


import org.springframework.data.jpa.repository.JpaRepository;

public interface InperiodRepository extends JpaRepository<Inperiod,InperiodPk> {

}