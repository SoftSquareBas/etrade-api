package com.tiffa.wd.elock.paperless.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiffa.wd.elock.paperless.core.Data;
import com.tiffa.wd.elock.paperless.core.GridData;
import com.tiffa.wd.elock.paperless.core.Sort;
import com.tiffa.wd.elock.paperless.core.entity.Incategory;
import com.tiffa.wd.elock.paperless.core.model.Rt04Model;
import com.tiffa.wd.elock.paperless.core.repository.CoreRepository;
import com.tiffa.wd.elock.paperless.core.repository.IncategoryRepository;
import com.tiffa.wd.elock.paperless.core.repository.SqlParams;
import com.tiffa.wd.elock.paperless.core.repository.SqlSort;
import com.tiffa.wd.elock.paperless.core.util.CoreUtils;


@Service
public class Rt04Service {

    @Autowired
    private IncategoryRepository incategoryRepository;

    @Autowired
    private CoreRepository coreRepository;

    public <DdlModel> GridData getGroupCode(){
        SqlParams params = SqlParams.create(); 
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT ig.group_code AS\"value\" ");
        sql.append(" ,ig.group_code ||' : '|| COALESCE(ig.group_name, '')  AS\"label\" ");
        sql.append("FROM in_group ig");

        
        return coreRepository.searchGridData(sql.toString(), params);
    }

    public Data check(Rt04Model check) throws Exception{

        SqlParams params = SqlParams.createPageParam(check);
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT  count(1) ");
        sql.append(" FROM in_category ic ");
        sql.append(" WHERE ic.category_code = :categoryCode  ");
        if(CoreUtils.isNotNull(check.getUpdDate())){
            sql.append("and upd_date is null");
        }
        
        params.add("categoryCode", check.getCategoryCode());

         

        return coreRepository.getData(sql.toString(), params);
    }

    public Data save(Rt04Model model) throws Exception {

        Incategory category = new Incategory();
        
        category.setGroupCode(model.getGroupCode());
        category.setCategoryCode(model.getCategoryCode());
        category.setCategoryDesc(model.getCategoryDesc());
        category.setCategoryDescEng(model.getCategoryDescEng());
        category.setActive(model.getActive());

        String groupCode = String.valueOf(incategoryRepository.saveAndFlush(category).getCategoryCode());
        Incategory rs = incategoryRepository.findById(groupCode).get();
        

        return Data.of(rs);

    }

    public GridData search(Rt04Model model) {
        SqlParams params = SqlParams.createPageParam(model);
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT  ig.group_code AS\"groupCode\" ");
        sql.append(" , ig.group_name AS \"groupName\" ");
        sql.append(" , ic.category_code AS \"categoryCode\" ");
        sql.append(" , ic.category_desc AS \"categoryDesc\" ");
        sql.append(" , ic.category_desc_eng AS \"categoryDescEng\" ");
        sql.append(" , ig.active AS \"active\" ");
        sql.append(" FROM in_group ig JOIN in_category ic  ON ig.group_code = ic.group_code");
        sql.append(" WHERE 1=1 ");

        if (CoreUtils.isNotNull(model.getGroupCode())) {
            sql.append(" and ig.group_code like '%'|| :groupCode || '%'");
            sql.append(" or ig.group_name like '%'|| :groupCode || '%' ");
            sql.append(" or ic.category_code like '%'|| :groupCode || '%' ");
            sql.append(" or ic.category_desc like '%'|| :groupCode || '%' ");
            sql.append(" or ic.category_desc_eng like '%'|| :groupCode || '%' ");
            params.add("groupCode", model.getGroupCode());
        }

        SqlSort sort = SqlSort.create(model, Sort.by("groupCode", Direction.ASC),
                Sort.by("groupCode", Direction.ASC), Sort.by("groupCode", Direction.ASC));
        return coreRepository.searchPagingGridData(sql.toString(), params, sort);
        

    }



    @Transactional
    public Data delete(final Rt04Model model) {

        incategoryRepository.deleteById(model.getCategoryCode());
        

        return Data.of();
    }

    @Transactional
    public Data searchDetail(final Rt04Model model) {

        Incategory category = incategoryRepository.findById(model.getCategoryCode()).get();
        
        return Data.of(category);

    }

    @Transactional
    public Data update(final Rt04Model model) {

        Incategory category = incategoryRepository.findById(model.getCategoryCode()).get();
        
        category.setCategoryDesc(model.getCategoryDesc());
        category.setCategoryDescEng(model.getCategoryDescEng());
        category.setActive(model.getActive());
        return searchDetail(model);

    }

}
