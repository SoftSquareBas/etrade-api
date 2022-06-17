package com.tiffa.wd.elock.paperless.core.service;

import com.tiffa.wd.elock.paperless.core.GridData;
import com.tiffa.wd.elock.paperless.core.Sort;
import com.tiffa.wd.elock.paperless.core.entity.BaseEntity;
import com.tiffa.wd.elock.paperless.core.entity.Inperiod;
import com.tiffa.wd.elock.paperless.core.entity.InperiodPk;

import com.tiffa.wd.elock.paperless.core.model.Rt11Model;
import com.tiffa.wd.elock.paperless.core.repository.CoreRepository;
import com.tiffa.wd.elock.paperless.core.repository.InperiodRepository;
import com.tiffa.wd.elock.paperless.core.repository.SqlParams;
import com.tiffa.wd.elock.paperless.core.repository.SqlSort;
import com.tiffa.wd.elock.paperless.core.util.CoreUtils;
import com.tiffa.wd.elock.paperless.core.util.SecurityUtils;
import org.springframework.transaction.annotation.Transactional;

//import com.github.javaparser.utils.Log;

// import org.springframework.transaction.annotation.Transactional;

// import java.util.Map;

 import com.tiffa.wd.elock.paperless.core.Data;

import java.time.Period;

import org.aspectj.apache.bcel.classfile.Module.Require;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Sort.Direction;

@Service
public class Rt11Service {

    @Autowired
    private  InperiodRepository  InperiodRepository;

    @Autowired
    private CoreRepository coreRepository;



    public Data save(Rt11Model model) throws Exception {
        Inperiod period = new Inperiod();
        InperiodPk pk   = new InperiodPk();
        
        
       
        pk.setYeAr(model.getYeAr());
        pk.setPerIod(model.getPerIod());
        pk.setOuCode(SecurityUtils.getCompanyCode());
        
        period.setPk(pk);
        System.out.println("this model");
        System.out.println(model);
        period.setRealStatus(model.getRealStatus());
        period.setStatusFg(model.getStatusFg());  
        period.setStatusSp(model.getStatusSp());
        period.setStartDate(model.getStartDate());
        period.setEndDate(model.getEndDate());
        
        String.valueOf(InperiodRepository.saveAndFlush(period).getPk());
        return searchDetail(model);
        
    }

    // public <DdlModel> GridData getItemCode(){
    //     SqlParams params = SqlParams.create();
    //     StringBuilder sql = new StringBuilder();

    //     sql.append("SELECT CAST( god.item_code AS VARCHAR ) AS\"value\" ");
    //     sql.append(",(god.item_code || ' : ' || god.item_name) AS\"label\" ");
    //     sql.append(" , god.item_name AS \"desc\" ");
    //     sql.append(" , ums.ums_code AS \"pack_code\" ");
    //     sql.append(" , ums.ums_name AS \"pack_name\" ");
    //     sql.append(" , god.ware_code AS \"wareCode\" ");
    //     sql.append(" FROM in_goods god ");
    //     sql.append(" JOIN in_ums ums ON god.ums_code = ums.ums_code ");
        

    //     return coreRepository.searchGridData(sql.toString(), params);
    // }
    public GridData search(Rt11Model model) {
        System.out.println(model);
        SqlParams params = SqlParams.createPageParam(model);
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT  ip.year AS\"yeAr\" ");
        sql.append(" , ip.period AS \"perIod\" ");
        sql.append(" , ip.start_date AS  \"startDate\" ");
        sql.append(" , ip.end_date AS  \"endDate\" ");
        sql.append(" , ip.real_status AS  \"realStatus\" ");
        sql.append(" , ip.status_fg AS  \"statusFg\" ");
        sql.append(" , ip.status_Sp AS  \"statusSp\" ");
        sql.append(" FROM in_period ip     ");
        sql.append(" WHERE 1=1 ");
        
        if (CoreUtils.isNotNull(model.getYeAr())) {
			sql.append(" AND ip.year::varchar  like '%' || :yeAr || '%'  ");
			params.add("yeAr", model.getYeAr());
		}
		
        sql.append(" ORDER BY ip.year ASC ");
        SqlSort sort = SqlSort.create(model, Sort.by("yeAr", Direction.ASC),
        Sort.by("yeAr", Direction.ASC), Sort.by("yeAr", Direction.ASC));
        return coreRepository.searchPagingGridData(sql.toString(), params, sort);

    }
    
    public Data check(Rt11Model model) {

        System.out.println(model);
        SqlParams params = SqlParams.createPageParam(model);
        StringBuilder sql = new StringBuilder();

        // sql.append(" SELECT case when isc.category_code is not null then 1 else 0 end
        // as check ");
        // sql.append(" ,coalesce(isc.sub_cat_code,'null')as periodegory ");
        // sql.append(" ,coalesce(isc.category_code,'null')as categorycode ");
        sql.append(" SELECT  count(1) ");
        sql.append(" FROM in_period ip  ");
        sql.append(" WHERE ip.year = :year  AND ip.period  =:period ");
        if (CoreUtils.isNotNull(model.getUpdDate())) {
            sql.append(" and upd_date  is null ");
            
        }
        params.add("yeAr", model.getYeAr());
        params.add("perIod", model.getPerIod());

        System.out.println("testtttttttttttttttttttttttttt");
        System.out.println(model);

        return coreRepository.getData(sql.toString(), params);
    }
    
    


    @Transactional
    public Data delete(final Rt11Model model) {

        InperiodPk pk = new InperiodPk();

        pk.setOuCode(SecurityUtils.getCompanyCode());
        pk.setYeAr(model.getYeAr());
        pk.setPerIod(model.getPerIod());

        System.out.println(pk.getOuCode());
        System.out.println(pk.getYeAr());
        System.out.println(pk.getPerIod());
        
        InperiodRepository.deleteById(pk);
        return Data.of();
    }

    @Transactional
    public Data searchDetail(final Rt11Model model) {

        InperiodPk pk     = new InperiodPk();
        pk.setOuCode(SecurityUtils.getCompanyCode());
        pk.setYeAr(model.getYeAr());
        pk.setPerIod(model.getPerIod());
        System.out.println("Hellpdddddddddd");
        System.out.println(pk);
        
        Inperiod period = InperiodRepository.findById(pk).get();
        model.setOuCode(pk.getOuCode());
        model.setYeAr(pk.getYeAr());
        model.setPerIod(pk.getPerIod());
        model.setStartDate(period.getStartDate());
        model.setEndDate(period.getEndDate());
        model.setRealStatus(period.getRealStatus());
        model.setStatusFg(period.getStatusFg());
        model.setStatusSp(period.getStatusSp());
        System.out.println("ddddddddddddddddddd");
        System.out.println(model);
        return Data.of(model);

    }

    @Transactional
    public Data update(final Rt11Model model) {
        InperiodPk pk = new InperiodPk();
        pk.setYeAr(model.getYeAr());
        pk.setPerIod(model.getPerIod());
        pk.setOuCode(SecurityUtils.getCompanyCode());
        
        
        Inperiod period = InperiodRepository.findById(pk).get();
        period.setRealStatus(model.getRealStatus());
        period.setStatusFg(model.getStatusFg());
        period.setStatusSp(model.getStatusSp());
        
        
         InperiodRepository.saveAndFlush(period).getPk();
        return Data.of(period);
    }



    


}
