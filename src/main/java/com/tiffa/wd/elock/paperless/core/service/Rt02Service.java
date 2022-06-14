package com.tiffa.wd.elock.paperless.core.service;

import com.tiffa.wd.elock.paperless.core.GridData;
import com.tiffa.wd.elock.paperless.core.Sort;
import com.tiffa.wd.elock.paperless.core.entity.Inlocation;
import com.tiffa.wd.elock.paperless.core.entity.InlocationPk;
import com.tiffa.wd.elock.paperless.core.model.Rt02Model;
import com.tiffa.wd.elock.paperless.core.repository.CoreRepository;
 import com.tiffa.wd.elock.paperless.core.repository.InlocationRepository;
import com.tiffa.wd.elock.paperless.core.repository.SqlParams;
import com.tiffa.wd.elock.paperless.core.repository.SqlSort;
import com.tiffa.wd.elock.paperless.core.util.CoreUtils;
import com.tiffa.wd.elock.paperless.core.util.SecurityUtils;
import org.springframework.transaction.annotation.Transactional;

import com.github.javaparser.utils.Log;

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


    // public Data save(Rt02Model model) throws Exception {
    //     System.out.println(model);
    //     Inlocation location = new Inlocation();
    //     InlocationPk  pk = new InlocationPk();

    //     pk.setOuCode( SecurityUtils.getCompanyCode());
    //     pk.setWareCode(model.getWareCode());
    //     pk.setLocationCode(model.getLocationCode());
    //     location.setPk(pk);
    //     location.setLocationName(model.getLocationName());
    //     location.setActive(model.getActive());
    //     String.valueOf(inlocationRepository.saveAndFlush(location).getPk());
    //     System.out.println(location);
    //     return Data.of(location);

    // }


    public Data save(Rt02Model model) throws Exception {
        Inlocation location = new Inlocation();
        InlocationPk pk     = new InlocationPk();

        pk.setOuCode(SecurityUtils.getCompanyCode());
        
        pk.setWareCode(model.getWareCode());
        pk.setLocationCode(model.getLocationCode());
        location.setPk(pk);
        System.out.println(pk.getOuCode());
        System.out.println(pk.getWareCode());
        System.out.println(pk.getLocationCode());
        location.setLocationName(model.getLocationName());
        location.setActive(model.getActive());
        String.valueOf(inlocationRepository.saveAndFlush(location).getPk());
        return Data.of(model);
        
    }


    public GridData search(Rt02Model model) {
        System.out.println(model);
        SqlParams params = SqlParams.createPageParam(model);
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT  il.ware_code AS\"wareCode\" ");
        sql.append(" , il.location_code AS \"locationCode\" ");
        sql.append(" , il.location_name AS  \"locationName\" ");
        sql.append(" , il.ou_code AS  \"ouCode\" ");
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
    public Data check(Rt02Model model) {

        System.out.println(model);
        SqlParams params = SqlParams.createPageParam(model);
        StringBuilder sql = new StringBuilder();

        
        sql.append(" SELECT  count(1) ");
        sql.append(" FROM in_location il  ");
        sql.append(" WHERE il.ware_code = :wareCode AND il.location_code  = :locationCode");
        if (CoreUtils.isNotNull(model.getUpdDate())) {
            sql.append(" and upd_date  is null ");
            
        }

        params.add("wareCode", model.getWareCode());
        params.add("locationCode", model.getLocationCode());

        System.out.println(" Test01");
        System.out.println(model);

        return coreRepository.getData(sql.toString(), params);
    }
    // @Transactional
    // public Data delete(final Rt02Model model) {
    //     InlocationPk pk     = new InlocationPk();
    //     pk.setLocationCode(model.getLocationCode());
    //     pk.setOuCode(SecurityUtils.getCompanyCode());
    //     pk.setWareCode(model.getWareCode());

    //     inlocationRepository.deleteById(pk);
    //     return Data.of();
    // }
    @Transactional
    public Data delete(final Rt02Model model) {

        InlocationPk pk = new InlocationPk();

        pk.setWareCode(model.getWareCode());
        pk.setLocationCode(model.getLocationCode());
        pk.setOuCode(model.getOuCode());
        System.out.println(pk.getOuCode());
        System.out.println(pk.getWareCode());
        System.out.println(pk.getLocationCode());

        inlocationRepository.deleteById(pk);
        return Data.of();
    }

    @Transactional
    public Data searchDetail(final Rt02Model model) {
        InlocationPk pk     = new InlocationPk();
        pk.setLocationCode(model.getLocationCode());
        pk.setOuCode(SecurityUtils.getCompanyCode());
        pk.setWareCode(model.getWareCode());

        Inlocation location = inlocationRepository.findById(pk).get();
        model.setOuCode(pk.getOuCode());
        model.setWareCode(pk.getWareCode());
        model.setLocationCode(pk.getLocationCode());
        model.setLocationName(location.getLocationName());
        model.setActive(location.getActive());
        
        System.out.println(model);
        return Data.of(model);

    }

    @Transactional
    public Data update(final Rt02Model model) {
        
        InlocationPk pk     = new InlocationPk();
        pk.setLocationCode(model.getLocationCode());
        pk.setOuCode(model.getOuCode());
        pk.setWareCode(model.getWareCode());
        

        Inlocation location = inlocationRepository.findById(pk).get();

        location.setLocationName(model.getLocationName());
        location.setActive(model.getActive());
        
        
         inlocationRepository.saveAndFlush(location).getPk();
        return Data.of(location);
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
        // return searchDetail(model);
    // }

    
}
