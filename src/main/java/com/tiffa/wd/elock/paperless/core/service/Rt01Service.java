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

        return searchDetail(model);

    }

    public <DdlModel> GridData getsaleCode(){
        SqlParams params = SqlParams.create();
        StringBuilder sql = new StringBuilder();

        sql.append("SELECT emp_id AS \"value\" ");
        sql.append(" ,emp_id ||' : '|| COALESCE(t_first_name, '') ||' '|| COALESCE(t_last_name, '') AS \"label\" ");
        sql.append(" FROM gb_employee emp");
        sql.append(" LEFT JOIN db_title tit ON emp.pre_name_id = tit.title_code ");
        sql.append(" WHERE emp.ou_code = :v_ou_code OR :v_ou_code IS NULL");
        sql.append(" AND emp.active = 'Y' ");
        sql.append(" ORDER BY CAST(emp_id AS VARCHAR)");
        params.add("v_ou_code","BRM01");
        System.out.println(params);

        return coreRepository.searchGridData(sql.toString(), params);
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
        sql.append(" , arm.ar_name_tha as \"arName\" ");
        sql.append(" , iw.sale_id_br AS \"saleIdBr\" ");
        sql.append(" , emp.t_first_name ||' '|| COALESCE(t_last_name, '') as \"saleName\"");
        sql.append(" FROM in_warehouse iw  ");
        sql.append(" JOIN ar_master arm ON arm.ar_code = iw.ar_code_br ");
        sql.append(" JOIN gb_employee emp ON emp.emp_id = iw.sale_id_br ");
        sql.append(" LEFT JOIN db_title tit ON emp.pre_name_id = tit.title_code");
        sql.append(" WHERE 1=1 ");

        if (CoreUtils.isNotNull(model.getWareCode())) {
            sql.append(
                    "and iw.ware_code = :wareCode");
            sql.append(" or iw.ware_name like '%'|| :wareCode || '%'");
            sql.append(" or iw.ware_code like '%'|| :wareCode || '%'");
            sql.append(" or iw.active like '%'|| :wareCode || '%'");
            sql.append(" or iw.sale_id_br like '%'|| :wareCode || '%'");
            sql.append(" or iw.ar_code_br like '%'|| :wareCode || '%'");
            sql.append(" or arm.ar_name_tha like '%'|| :wareCode || '%'");
            sql.append(" or emp.t_first_name like '%'|| :wareCode || '%'");
            sql.append(" or emp.t_last_name '%'|| :wareCode || '%'");
            params.add("wareCode", model.getWareCode());
            System.out.println(params);
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

        return searchDetail(model);

    }

}
