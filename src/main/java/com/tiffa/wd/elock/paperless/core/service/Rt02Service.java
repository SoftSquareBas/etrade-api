package com.tiffa.wd.elock.paperless.core.service;

import com.tiffa.wd.elock.paperless.core.GridData;
import com.tiffa.wd.elock.paperless.core.Sort;
import com.tiffa.wd.elock.paperless.core.entity.Inlocation;
import com.tiffa.wd.elock.paperless.core.model.Rt02Model;
import com.tiffa.wd.elock.paperless.core.repository.CoreRepository;
 import com.tiffa.wd.elock.paperless.core.repository.InlocationRepository;
import com.tiffa.wd.elock.paperless.core.repository.SqlParams;
import com.tiffa.wd.elock.paperless.core.repository.SqlSort;
import com.tiffa.wd.elock.paperless.core.util.CoreUtils;

// import org.springframework.transaction.annotation.Transactional;

// import java.util.Map;

 import com.tiffa.wd.elock.paperless.core.Data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Sort.Direction;

@Service
public class Rt02Service {

    @Autowired
    private  InlocationRepository  inlocationRepository;

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


    public Data save(Rt02Model model) throws Exception {
        Inlocation inlocation = new Inlocation();
        inlocation.setOuCode(model.getOuCode());
        inlocation.setWareCode(model.getWareCode());
        inlocation.setLocationCode(model.getLocationCode());
        inlocation.setLocationName(model.getLocationName());
        inlocation.setActive(model.getActive());
        String.valueOf(inlocationRepository.saveAndFlush(inlocation).getWareCode());
        return Data.of(inlocation);
        
    }

    public GridData search(Rt02Model model) {
        System.out.println(model);
        SqlParams params = SqlParams.createPageParam(model);
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT  il.ware_code AS\"wareCode\" ");
        sql.append(" , il.location_code AS \"locationCode\" ");
        sql.append(" , il.location_name AS  \"locationName\" ");
        sql.append(" , il.active AS \"active\" ");
        sql.append(" FROM in_location il  ");
        sql.append(" WHERE 1=1 ");
        
        if (CoreUtils.isNotNull(model.getWareCode())) {
			sql.append(" AND il.ware_code  LIKE '%' || :wareCode || '%'  ");
			params.add("wareCode", model.getWareCode());
		}
		

        SqlSort sort = SqlSort.create(model, Sort.by("wareCode", Direction.ASC),
        Sort.by("wareCode", Direction.ASC), Sort.by("wareCode", Direction.ASC));
        return coreRepository.searchPagingGridData(sql.toString(), params, sort);

    }

    // @Transactional
    // public Data delete(final Rt02Model model) {

    //     poTypeRepository.deleteById(model.getWareCode());
    //     return Data.of();
    // }

    // @Transactional
    // public Data searchDetail(Rt02Model model) {
        
    //     Inlocation inlocation = inlocationRepository.findById(model.getWareCode()).get();
    //     return Data.of(inlocation);

    // }

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
