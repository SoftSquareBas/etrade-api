package com.tiffa.wd.elock.paperless.core.service;

import com.tiffa.wd.elock.paperless.core.Data;
import com.tiffa.wd.elock.paperless.core.GridData;
import com.tiffa.wd.elock.paperless.core.Sort;
import com.tiffa.wd.elock.paperless.core.entity.Inwarehouse;
import com.tiffa.wd.elock.paperless.core.model.Rt01Model;
import com.tiffa.wd.elock.paperless.core.repository.CoreRepository;
import com.tiffa.wd.elock.paperless.core.repository.InwarehouseRepository;
import com.tiffa.wd.elock.paperless.core.repository.SqlParams;
import com.tiffa.wd.elock.paperless.core.repository.SqlSort;
import com.tiffa.wd.elock.paperless.core.util.CoreUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

@Service
public class Rt01Service {


    @Autowired
    private InwarehouseRepository inwarehouseRepository;
    @Autowired
    private CoreRepository coreRepository;

    public Data save(Rt01Model model) throws Exception {
        System.out.println(model);
        Inwarehouse warehouse = new Inwarehouse();
        warehouse.setOuCode(model.getOuCode());
        warehouse.setWareCode(model.getWareCode());
        warehouse.setWareName(model.getWareName());
        warehouse.setSaleIdBr(model.getSaleIdBr());
        warehouse.setArCodeBr(model.getArCodeBr());
        warehouse.setActive(model.getActive());
        
        String.valueOf(inwarehouseRepository.saveAndFlush(warehouse).getWareCode());
        return Data.of(model);
        
        
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
			sql.append("and iw.ware_code = :wareCode or iw.ware_code like '%'|| :wareCode || '%' or iw.ware_name like '%'|| :wareCode || '%' or iw.active like '%'|| :wareCode || '%' or iw.ar_code_br like '%'|| :wareCode || '%' or iw.sale_id_br like '%'|| :wareCode || '%'");
			params.add("wareCode", model.getWareCode());
		}

        SqlSort sort = SqlSort.create(model, Sort.by("wareCode", Direction.ASC),
                Sort.by("wareCode", Direction.ASC), Sort.by("wareCode", Direction.ASC));
        return coreRepository.searchPagingGridData(sql.toString(), params, sort);
        
    }

    // public Data searchDetail(final Rt01Model model) {
        
    //     Inwarehouse warehouse = inwarehouseRepository.findById(model.getWareCode()).get();
    //     return Data.of(warehouse);

    // }



    

}
