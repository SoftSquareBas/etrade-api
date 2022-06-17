package com.tiffa.wd.elock.paperless.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiffa.wd.elock.paperless.core.Data;
import com.tiffa.wd.elock.paperless.core.GridData;
import com.tiffa.wd.elock.paperless.core.Sort;
import com.tiffa.wd.elock.paperless.core.entity.Inbrand;
import com.tiffa.wd.elock.paperless.core.model.Rt06Model;
import com.tiffa.wd.elock.paperless.core.repository.CoreRepository;
import com.tiffa.wd.elock.paperless.core.repository.InbrandRepository;
import com.tiffa.wd.elock.paperless.core.repository.SqlParams;
import com.tiffa.wd.elock.paperless.core.repository.SqlSort;
import com.tiffa.wd.elock.paperless.core.util.CoreUtils;

@Service
public class Rt06Service {

    @Autowired
    private InbrandRepository inbrandRepository;
    @Autowired
    private CoreRepository coreRepository;


    public Data check(Rt06Model check) throws Exception{

        SqlParams params = SqlParams.createPageParam(check);
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT  count(1) ");
        sql.append(" FROM in_brand ib ");
        sql.append(" WHERE ib.brand_code = :brandCode ");
        if(CoreUtils.isNotNull(check.getUpdDate())){
            sql.append("and upd_date is null");
        }
        
        params.add("brandCode", check.getBrandCode());

         

        return coreRepository.getData(sql.toString(), params);
    }

    public Data save(Rt06Model model) throws Exception {

        Inbrand inbrand = new Inbrand();

        inbrand.setBrandCode(model.getBrandCode());
        inbrand.setBrandName(model.getBrandName());
        inbrand.setBrandNameEn(model.getBrandNameEn());
        inbrand.setSupplier(model.getSupplier());
        inbrand.setExporter(model.getExporter());
        inbrand.setActive(model.getActive());

        String.valueOf(inbrandRepository.saveAndFlush(inbrand).getBrandCode());

        return searchDetail(model);

    }

    public <DdlModel> GridData getSupplier(){
        SqlParams params = SqlParams.create();
        StringBuilder sql = new StringBuilder();

        sql.append("SELECT apm.ap_code AS \"value\" ");
        sql.append(" ,apm.ap_code ||' : '|| COALESCE(apm.ap_name_tha, '') AS \"label\" ");
        sql.append(" FROM ap_master apm");
        sql.append(" WHERE ACTIVE = 'Y'");
        

        return coreRepository.searchGridData(sql.toString(), params);
    }


    public <DdlModel> GridData getExporter(){
        SqlParams params = SqlParams.create(); 
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT apm.ap_code AS \"value\" ");
        sql.append(" ,apm.ap_code ||' : '|| COALESCE(apm.ap_name_tha, '') AS\"label\" ");
        sql.append(" FROM ap_master apm");
        sql.append(" WHERE ACTIVE = 'Y' AND ap_type_flag IN ('E','A')");

        
        return coreRepository.searchGridData(sql.toString(), params);
    }

    public GridData search(Rt06Model model) {
        SqlParams params = SqlParams.createPageParam(model);
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT  ib.brand_code AS \"brandCode\" ");
        sql.append(" , ib.brand_name  AS \"brandName\" ");
        sql.append(" , ib.brand_name_en  AS \"brandNameEn\" ");
        sql.append(" , ib.supplier ||' : '|| COALESCE(am.ap_name_tha, '') AS \"supplier\" ");
        sql.append(" , ib.exporter ||' : '|| COALESCE(am.ap_name_tha, '') AS \"exporter\" ");
        sql.append(" , ib.active as \"active\"");
        sql.append(" FROM in_brand ib  ");
        sql.append(" JOIN ap_master am on am.ap_code = ib.supplier");
        sql.append(" WHERE 1=1 ");

        if (CoreUtils.isNotNull(model.getBrandCode())) {
            sql.append(
                    " and ib.brand_code =:brandCode");
            sql.append(" or ib.brand_name like '%'|| :brandCode || '%'");
            sql.append(" or ib.brand_name_en  like '%'|| :brandCode || '%'");
            sql.append(" or ib.supplier   like '%'|| :brandCode || '%'");
            sql.append(" or ib.exporter  like '%'|| :brandCode || '%'");
            params.add("brandCode", model.getBrandCode());
            System.out.println(params);
        }

        SqlSort sort = SqlSort.create(model, Sort.by("brandCode", Direction.ASC),
                Sort.by("brandCode", Direction.ASC), Sort.by("brandCode", Direction.ASC));
        return coreRepository.searchGridData(sql.toString(), params, sort);

    }

    @Transactional
    public Data delete(final Rt06Model model) {


        inbrandRepository.deleteById(model.getBrandCode());

        return Data.of();
    }

    @Transactional
    public Data searchDetail(final Rt06Model model) {

        Inbrand inbrand = inbrandRepository.findById(model.getBrandCode()).get();

        return Data.of(inbrand);

    }

    @Transactional
    public Data update(final Rt06Model model) {

        Inbrand inbrand = inbrandRepository.findById(model.getBrandCode()).get();

        inbrand.setBrandName(model.getBrandName());
        inbrand.setBrandNameEn(model.getBrandNameEn());
        inbrand.setSupplier(model.getSupplier());
        inbrand.setExporter(model.getExporter());
        inbrand.setActive(model.getActive());

        inbrandRepository.saveAndFlush(inbrand);


        return searchDetail(model);

    }

}
