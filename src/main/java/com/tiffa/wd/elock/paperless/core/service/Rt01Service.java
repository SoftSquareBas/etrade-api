package com.tiffa.wd.elock.paperless.core.service;

import com.tiffa.wd.elock.paperless.core.Data;
import com.tiffa.wd.elock.paperless.core.DdlModel;
import com.tiffa.wd.elock.paperless.core.GridData;
import com.tiffa.wd.elock.paperless.core.Sort;
import com.tiffa.wd.elock.paperless.core.entity.Inwarehouse;
import com.tiffa.wd.elock.paperless.core.entity.InwarehousePk;
import com.tiffa.wd.elock.paperless.core.model.Rt01Model;
import com.tiffa.wd.elock.paperless.core.repository.CoreRepository;
import com.tiffa.wd.elock.paperless.core.repository.InwarehouseRepository;
import com.tiffa.wd.elock.paperless.core.repository.SqlParams;
import com.tiffa.wd.elock.paperless.core.repository.SqlSort;
import com.tiffa.wd.elock.paperless.core.util.CoreUtils;
import com.tiffa.wd.elock.paperless.core.util.SecurityUtils;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class Rt01Service {

    @Autowired
    private InwarehouseRepository inwarehouseRepository;
    @Autowired
    private CoreRepository coreRepository;


    public Data check(Rt01Model check) throws Exception{

        SqlParams params = SqlParams.createPageParam(check);
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT  count(1) ");
        sql.append(" FROM in_warehouse iw ");
        sql.append(" WHERE iw.ware_code  = :wareCode  ");
        if(CoreUtils.isNotNull(check.getUpdDate())){
            sql.append("and upd_date is null");
        }
        
        params.add("wareCode", check.getWareCode());

         

        return coreRepository.getData(sql.toString(), params);
    }

    public Data save(Rt01Model model) throws Exception {

        Inwarehouse warehouse = new Inwarehouse();
        InwarehousePk pk = new InwarehousePk();

        pk.setOuCode(SecurityUtils.getCompanyCode());
        pk.setWareCode(model.getWareCode());
        warehouse.setPk(pk);
        warehouse.setWareName(model.getWareName());
        warehouse.setSaleIdBr(model.getSaleIdBr());
        warehouse.setArCodeBr(model.getArCodeBr());
        warehouse.setActive(model.getActive());

        String.valueOf(inwarehouseRepository.saveAndFlush(warehouse).getPk());
        return Data.of(model);

    }

    public <DdlModel> GridData getarCode(){
        SqlParams params = SqlParams.create();
        StringBuilder sql = new StringBuilder();

        sql.append("SELECT ar_code AS\"value\" ");
        sql.append(",ar_code ||' : '|| COALESCE(ar_name_tha, '')  AS\"label\" ");
        sql.append("FROM ar_master arm");


        return coreRepository.searchGridData(sql.toString(), params);
    }

    public GridData search(Rt01Model model) {
        SqlParams params = SqlParams.createPageParam(model);
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT  iw.ware_code AS\"wareCode\" ");
        sql.append(" , iw.ware_name AS \"wareName\" ");
        sql.append(" , iw.active AS \"active\" ");
        sql.append(" , iw.ar_code_br AS \"arCodeBr\" ");
        sql.append(" , iw.sale_id_br AS \"saleIdBr\" ");
        sql.append(" FROM in_warehouse iw  ");
        sql.append(" WHERE 1=1 ");

        if (CoreUtils.isNotNull(model.getWareCode())) {
            sql.append(
                    "and iw.ware_code = :wareCode or iw.ware_code like '%'|| :wareCode || '%' or iw.ware_name like '%'|| :wareCode || '%' or iw.active like '%'|| :wareCode || '%' or iw.ar_code_br like '%'|| :wareCode || '%' or iw.sale_id_br like '%'|| :wareCode || '%'");
            params.add("wareCode", model.getWareCode());
        }

        SqlSort sort = SqlSort.create(model, Sort.by("wareCode", Direction.ASC),
                Sort.by("wareCode", Direction.ASC), Sort.by("wareCode", Direction.ASC));
        return coreRepository.searchGridData(sql.toString(), params, sort);

    }

    @Transactional
    public Data delete(final Rt01Model model) {

        InwarehousePk pk = new InwarehousePk();

        pk.setWareCode(model.getWareCode());
        pk.setOuCode(SecurityUtils.getCompanyCode());

        inwarehouseRepository.deleteById(pk);

        return Data.of();
    }

    @Transactional
    public Data searchDetail(final Rt01Model model) {

        InwarehousePk pk = new InwarehousePk();
        pk.setWareCode(model.getWareCode());
        pk.setOuCode(SecurityUtils.getCompanyCode());
        Inwarehouse inwarehouse = inwarehouseRepository.findById(pk).get();
        model.setWareCode(pk.getWareCode());
        model.setOuCode(pk.getOuCode());
        model.setWareName(inwarehouse.getWareName());
        model.setSaleIdBr(inwarehouse.getSaleIdBr());
        model.setArCodeBr(inwarehouse.getArCodeBr());
        model.setActive(inwarehouse.getActive());
        model.setUpdDate(inwarehouse.getUpdDate());

        return Data.of(model);

    }

    @Transactional
    public Data update(final Rt01Model model) {

        InwarehousePk pk = new InwarehousePk();
        pk.setWareCode(model.getWareCode());
        pk.setOuCode(model.getOuCode());

        Inwarehouse warehouse = inwarehouseRepository.findById(pk).get();

        warehouse.setWareName(model.getWareName());
        warehouse.setSaleIdBr(model.getSaleIdBr());
        warehouse.setArCodeBr(model.getArCodeBr());
        warehouse.setActive(model.getActive());

        inwarehouseRepository.saveAndFlush(warehouse).getPk();
        return Data.of(warehouse);

    }

}
