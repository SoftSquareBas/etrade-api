package com.tiffa.wd.elock.paperless.core.service;

import com.tiffa.wd.elock.paperless.core.GridData;
import com.tiffa.wd.elock.paperless.core.Sort;
 import com.tiffa.wd.elock.paperless.core.entity.DbPoType;
import com.tiffa.wd.elock.paperless.core.model.Rt02Model;
import com.tiffa.wd.elock.paperless.core.repository.CoreRepository;
 import com.tiffa.wd.elock.paperless.core.repository.PoTypeRepository;
import com.tiffa.wd.elock.paperless.core.repository.SqlParams;
import com.tiffa.wd.elock.paperless.core.repository.SqlSort;

 import org.springframework.transaction.annotation.Transactional;

// import java.util.Map;

 import com.tiffa.wd.elock.paperless.core.Data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Sort.Direction;

@Service
public class Rt02Service {

    @Autowired
    private PoTypeRepository poTypeRepository;

    @Autowired
    private CoreRepository coreRepository;

    // public Data save(Stport02Model model) throws Exception {
    //     DbPoType potype = new DbPoType();
    //     potype.setPoTypeCode(model.getPoTypeCode());
    //     potype.setPoTypeDesc(model.getPoTypeDesc());
    //     potype.setActive(model.getActive());
    //     String.valueOf(poTypeRepository.saveAndFlush(potype).getPoTypeCode());
    //     return searchDetail(model);
        
    // }



    public GridData search(Rt02Model model) {
        SqlParams params = SqlParams.createPageParam(model);
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT  ware_code AS\"warCode\" ");
        sql.append(" , location_code AS \"warLocation\" ");
        sql.append(" , location_name \"warName\" ");
        sql.append(" , active AS \"warActive\" ");
        sql.append(" FROM in_location il  ");
        sql.append(" WHERE 1=1 ");
        sql.append( "AND il.ware_code  LIKE '%' || :WarCode || '%'  ");
      

        SqlSort sort = SqlSort.create(model, Sort.by("WarCode", Direction.ASC),
                Sort.by("Warcode", Direction.ASC), Sort.by("Warcode", Direction.ASC));
        return coreRepository.searchPagingGridData(sql.toString(), params, sort);
    }

    @Transactional
    public Data delete(final Rt02Model model) {

        poTypeRepository.deleteById(model.getWareCode());
        return Data.of();
    }

    @Transactional
    public Data searchDetail(final Rt02Model model) {
        
        DbPoType potype = poTypeRepository.findById(model.getWareCode()).get();
        return Data.of(potype);

    }

    // @Transactional
    // public Data update(final Rt02Model model) {

    //     DbPoType potype = poTypeRepository.findById(model.getPoTypeCode()).get();
        
    //     potype.setPoTypeCode(model.getPoTypeCode());
    //     potype.setPoTypeDesc(model.getPoTypeDesc());
    //     potype.setActive(model.getActive());
        
    //     poTypeRepository.saveAndFlush(potype);
    //     return searchDetail(model);
    // }

}
