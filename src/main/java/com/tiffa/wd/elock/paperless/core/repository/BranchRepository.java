package com.tiffa.wd.elock.paperless.core.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tiffa.wd.elock.paperless.core.entity.Branch;

@Repository
public interface BranchRepository extends CrudRepository<Branch, Integer> {

}
