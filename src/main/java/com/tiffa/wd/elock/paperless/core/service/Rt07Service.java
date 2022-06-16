package com.tiffa.wd.elock.paperless.core.service;

import com.tiffa.wd.elock.paperless.core.GridData;
import com.tiffa.wd.elock.paperless.core.Sort;
import com.tiffa.wd.elock.paperless.core.entity.BaseEntity;
import com.tiffa.wd.elock.paperless.core.entity.Insafety;


import com.tiffa.wd.elock.paperless.core.model.Rt07Model;
import com.tiffa.wd.elock.paperless.core.repository.CoreRepository;
import com.tiffa.wd.elock.paperless.core.repository.InsafetyRepository;
import com.tiffa.wd.elock.paperless.core.repository.SqlParams;
import com.tiffa.wd.elock.paperless.core.repository.SqlSort;
import com.tiffa.wd.elock.paperless.core.util.CoreUtils;
//import com.tiffa.wd.elock.paperless.core.util.SecurityUtils;
import org.springframework.transaction.annotation.Transactional;

//import com.github.javaparser.utils.Log;

// import org.springframework.transaction.annotation.Transactional;

// import java.util.Map;

 import com.tiffa.wd.elock.paperless.core.Data;

import org.aspectj.apache.bcel.classfile.Module.Require;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Sort.Direction;

@Service
public class Rt07Service {

    @Autowired
    private  InsafetyRepository  InsafetyRepository;

    @Autowired
    private CoreRepository coreRepository;



    public Data save(Rt07Model model) throws Exception {
        Insafety safety = new Insafety();
        
        
        
       
        

        safety.setItemCode(model.getItemCode());
        safety.setAmountSafety(model.getAmountSafety());  
        safety.setActive(model.getActive());
        String.valueOf(InsafetyRepository.saveAndFlush(safety).getItemCode());
        return searchDetail(model);
        
    }

    public <DdlModel> GridData getItemCode(){
        SqlParams params = SqlParams.create();
        StringBuilder sql = new StringBuilder();

        sql.append("SELECT CAST( god.item_code AS VARCHAR ) AS\"value\" ");
        sql.append(",(god.item_code || ' : ' || god.item_name) AS\"label\" ");
        sql.append(" , god.item_name AS \"desc\" ");
        sql.append(" , ums.ums_code AS \"pack_code\" ");
        sql.append(" , ums.ums_name AS \"pack_name\" ");
        sql.append(" , god.ware_code AS \"wareCode\" ");
        sql.append(" FROM in_goods god ");
        sql.append(" JOIN in_ums ums ON god.ums_code = ums.ums_code ");
        

        return coreRepository.searchGridData(sql.toString(), params);
    }
    public GridData search(Rt07Model model) {
        System.out.println(model);
        SqlParams params = SqlParams.createPageParam(model);
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT  is2.item_code AS\"itemCode\" ");
        sql.append(" , ig.item_name AS \"itemName\" ");
        sql.append(" , is2.safety_qty AS  \"amountSafety\" ");
        sql.append(" , is2.active AS  \"active\" ");
        sql.append(" FROM in_safety is2    ");
        sql.append(" JOIN in_goods ig  on is2.item_code  = ig.item_code  ");
        sql.append(" WHERE 1=1 ");
        
        if (CoreUtils.isNotNull(model.getItemCode())) {
			sql.append(" AND is2.item_code  LIKE '%' || :itemCode || '%'  ");
			params.add("ItemCode", model.getItemCode());
		}
		
        sql.append(" ORDER BY is2.item_code ASC ");
        SqlSort sort = SqlSort.create(model, Sort.by("itemCode", Direction.ASC),
        Sort.by("itemCode", Direction.ASC), Sort.by("itemCode", Direction.ASC));
        return coreRepository.searchPagingGridData(sql.toString(), params, sort);

    }
    
    // public Data check(Rt07Model model) {

    //     System.out.println(model);
    //     SqlParams params = SqlParams.createPageParam(model);
    //     StringBuilder sql = new StringBuilder();

    //     // sql.append(" SELECT case when isc.category_code is not null then 1 else 0 end
    //     // as check ");
    //     // sql.append(" ,coalesce(isc.sub_cat_code,'null')as safetyegory ");
    //     // sql.append(" ,coalesce(isc.category_code,'null')as categorycode ");
    //     sql.append(" SELECT  count(1) ");
    //     sql.append(" FROM in_sub_cat isc  ");
    //     sql.append(" WHERE isc.category_code = :category_code  AND isc.sub_cat_code  =:sub_cat_code ");
    //     if (CoreUtils.isNotNull(model.getUpdDate())) {
    //         sql.append(" and upd_date  is null ");
            
    //     }
    //     params.add("category_code", model.getItsetItemCode());
    //     params.add("sub_cat_code", model.getsafetyegorey());

    //     System.out.println("testtttttttttttttttttttttttttt");
    //     System.out.println(model);

    //     return coreRepository.getData(sql.toString(), params);
    // }
    
    


    @Transactional
    public Data delete(final Rt07Model model) {

        Insafety sa = new Insafety();

        sa.setItemCode(model.getItemCode());
        
        
        System.out.println(sa.getItemCode());
        
        
        InsafetyRepository.deleteById(sa.getItemCode());
        return Data.of();
    }

    @Transactional
    public Data searchDetail(final Rt07Model model) {
        
       
        
        Insafety safety = InsafetyRepository.findById(model.getItemCode()).get();
        model.setItemCode(safety.getItemCode());
        model.setAmountSafety(safety.getAmountSafety());
        model.setActive(safety.getActive());
        
        System.out.println(model);
        return Data.of(model);

    }

    // @Transactional
    // public Data update(final Rt07Model model) {
        
    //     InsafetyPk pk     = new InsafetyPk();
    //     pk.setItemCode(model.getItsetItemCode());
    //     pk.setsafetyegorey(model.getsafetyegorey());
        

    //     Insafety safety = InsafetyRepository.findById(pk).get();
    //     safety.setCategoreyThai(model.getCategoreyThai());
    //     safety.setCategoreyEng(model.getCategoreyEng());
    //     safety.setActive(model.getActive());
        
        
    //      InsafetyRepository.saveAndFlush(safety).getPk();
    //     return Data.of(safety);
    // }



    


}
