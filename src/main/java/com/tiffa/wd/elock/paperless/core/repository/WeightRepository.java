package com.tiffa.wd.elock.paperless.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tiffa.wd.elock.paperless.core.entity.Weight;

@Repository
public interface WeightRepository extends JpaRepository<Weight, Integer> {

}
