package com.tiffa.wd.elock.paperless.core.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tiffa.wd.elock.paperless.core.entity.Role;

@Repository
public interface RoleRepository extends CrudRepository<Role, Integer> {

}
