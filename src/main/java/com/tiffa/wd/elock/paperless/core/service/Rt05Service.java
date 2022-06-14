package com.tiffa.wd.elock.paperless.core.service;

import com.tiffa.wd.elock.paperless.core.GridData;
import com.tiffa.wd.elock.paperless.core.Sort;
import com.tiffa.wd.elock.paperless.core.entity.BaseEntity;
import com.tiffa.wd.elock.paperless.core.entity.Insubcat;
import com.tiffa.wd.elock.paperless.core.entity.InsubcatPk;

import com.tiffa.wd.elock.paperless.core.model.Rt05Model;
import com.tiffa.wd.elock.paperless.core.repository.CoreRepository;
import com.tiffa.wd.elock.paperless.core.repository.InsubcatRepository;
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
public class Rt05Service {

    @Autowired
    private  InsubcatRepository  InsubcatRepository;

    @Autowired
    private CoreRepository coreRepository;



    public Data save(Rt05Model model) throws Exception {
        Insubcat subcat = new Insubcat();
        InsubcatPk pk   = new InsubcatPk();
        
        pk.setCategoreyCode(model.getCategoreyCode());
        pk.setSubCategorey(model.getSubCategorey());
        

        subcat.setPk(pk);
        subcat.setCategoreyThai(model.getCategoreyThai());  
        subcat.setCategoreyEng(model.getCategoreyEng());
        subcat.setActive(model.getActive());
        String.valueOf(InsubcatRepository.saveAndFlush(subcat).getPk());
        return Data.of(model);
        
    }


    public GridData search(Rt05Model model) {
        System.out.println(model);
        SqlParams params = SqlParams.createPageParam(model);
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT  isc.category_code AS\"categoreyCode\" ");
        sql.append(" , isc.sub_cat_code AS \"subCategorey\" ");
        sql.append(" , isc.sub_cat_desc AS  \"categoreyThai\" ");
        sql.append(" ,isc.sub_cat_desc_eng AS  \"categoreyEng\" ");
        sql.append(" , isc.active AS  \"active\" ");
        sql.append(" FROM in_sub_cat isc  ");
        sql.append(" WHERE 1=1 ");
        
        if (CoreUtils.isNotNull(model.getCategoreyCode())) {
			sql.append(" AND isc.category_code  LIKE '%' || :categoreyCode || '%'  ");
			params.add("categoreyCode", model.getCategoreyCode());
		}
		
        sql.append(" ORDER BY isc.sub_cat_code ASC ");
        SqlSort sort = SqlSort.create(model, Sort.by("subCategorey", Direction.ASC),
        Sort.by("subCategorey", Direction.ASC), Sort.by("subCategorey", Direction.ASC));
        return coreRepository.searchPagingGridData(sql.toString(), params, sort);

    }
    
    public Data check(Rt05Model model) {

        System.out.println(model);
        SqlParams params = SqlParams.createPageParam(model);
        StringBuilder sql = new StringBuilder();

        // sql.append(" SELECT case when isc.category_code is not null then 1 else 0 end
        // as check ");
        // sql.append(" ,coalesce(isc.sub_cat_code,'null')as subcategory ");
        // sql.append(" ,coalesce(isc.category_code,'null')as categorycode ");
        sql.append(" SELECT  count(1) ");
        sql.append(" FROM in_sub_cat isc  ");
        sql.append(" WHERE isc.category_code = :category_code  AND isc.sub_cat_code  =:sub_cat_code ");
        if (CoreUtils.isNotNull(model.getUpdDate())) {
            sql.append(" and upd_date  is null ");
            
        }
        params.add("category_code", model.getCategoreyCode());
        params.add("sub_cat_code", model.getSubCategorey());

        System.out.println("testtttttttttttttttttttttttttt");
        System.out.println(model);

        return coreRepository.getData(sql.toString(), params);
    }
    
    


    @Transactional
    public Data delete(final Rt05Model model) {

        InsubcatPk pk = new InsubcatPk();

        pk.setCategoreyCode(model.getCategoreyCode());
        pk.setSubCategorey(model.getSubCategorey());
        
        System.out.println(pk.getCategoreyCode());
        System.out.println(pk.getSubCategorey());
        
        InsubcatRepository.deleteById(pk);
        return Data.of();
    }

    @Transactional
    public Data searchDetail(final Rt05Model model) {
        InsubcatPk pk     = new InsubcatPk();
        pk.setCategoreyCode(model.getCategoreyCode());
        pk.setSubCategorey(model.getSubCategorey());
        
        Insubcat subcat = InsubcatRepository.findById(pk).get();
        model.setCategoreyCode(pk.getCategoreyCode());
        model.setSubCategorey(pk.getSubCategorey());
        model.setCategoreyThai(subcat.getCategoreyThai());
        model.setCategoreyEng(subcat.getCategoreyEng());
        model.setActive(subcat.getActive());
        model.setUpdDate(subcat.getUpdDate());
        
        System.out.println(model);
        return Data.of(model);

    }

    @Transactional
    public Data update(final Rt05Model model) {
        
        InsubcatPk pk     = new InsubcatPk();
        pk.setCategoreyCode(model.getCategoreyCode());
        pk.setSubCategorey(model.getSubCategorey());
        

        Insubcat subcat = InsubcatRepository.findById(pk).get();
        subcat.setCategoreyThai(model.getCategoreyThai());
        subcat.setCategoreyEng(model.getCategoreyEng());
        subcat.setActive(model.getActive());
        
        
         InsubcatRepository.saveAndFlush(subcat).getPk();
        return Data.of(subcat);
    }



    


}
