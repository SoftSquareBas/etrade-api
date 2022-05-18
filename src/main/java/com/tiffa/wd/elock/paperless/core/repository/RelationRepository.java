package com.tiffa.wd.elock.paperless.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tiffa.wd.elock.paperless.core.entity.Relation;
import com.tiffa.wd.elock.paperless.core.entity.RelationPk;

@Repository
public interface RelationRepository extends JpaRepository<Relation, RelationPk> {

}
