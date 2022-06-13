package com.tiffa.wd.elock.paperless.core.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tiffa.wd.elock.paperless.core.entity.Incategory;

public interface IncategoryRepository extends JpaRepository<Incategory, String>{
    
}
