package com.tiffa.wd.elock.paperless.core.repository;

import com.tiffa.wd.elock.paperless.core.entity.GbEmployee;
import com.tiffa.wd.elock.paperless.core.entity.GbEmployeePK;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GbEmployeeRepository extends JpaRepository<GbEmployee, GbEmployeePK> {

}