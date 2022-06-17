package com.tiffa.wd.elock.paperless.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiffa.wd.elock.paperless.core.Data;
import com.tiffa.wd.elock.paperless.core.GridData;
import com.tiffa.wd.elock.paperless.core.Sort;
import com.tiffa.wd.elock.paperless.core.entity.Inmaindoc;
import com.tiffa.wd.elock.paperless.core.model.Rt13Model;
import com.tiffa.wd.elock.paperless.core.repository.CoreRepository;
import com.tiffa.wd.elock.paperless.core.repository.InmaindocRepository;
import com.tiffa.wd.elock.paperless.core.repository.SqlParams;
import com.tiffa.wd.elock.paperless.core.repository.SqlSort;
import com.tiffa.wd.elock.paperless.core.util.CoreUtils;

@Service
public class Rt13Service {

    @Autowired
    private InmaindocRepository inmaindocRepository;
    @Autowired
    private CoreRepository coreRepository;


    public Data check(Rt13Model check) throws Exception{

        SqlParams params = SqlParams.createPageParam(check);
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT  count(1) ");
        sql.append(" FROM in_main_doc imd  ");
        sql.append(" WHERE imd.main_doc = :mainDoc");
        if(CoreUtils.isNotNull(check.getUpdDate())){
            sql.append(" and upd_date is null");
        }
        
        params.add("mainDoc", check.getMainDoc());

         

        return coreRepository.getData(sql.toString(), params);
    }

    public Data save(Rt13Model model) throws Exception {

        Inmaindoc maindoc = new Inmaindoc();

        maindoc.setMainDoc(model.getMainDoc());
        maindoc.setMainDocDesc(model.getMainDocDesc());
        maindoc.setTypeFlag(model.getTypeFlag());
        maindoc.setInoutFlag(model.getInoutFlag());

        inmaindocRepository.saveAndFlush(maindoc);

        return searchDetail(model);

    }





    public GridData search(Rt13Model model) {
        SqlParams params = SqlParams.createPageParam(model);
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT  imd.main_doc AS \"mainDoc\"");
        sql.append(" , imd.main_doc_desc  AS \"mainDocDesc\"");
        sql.append(" , imd.type_flag    AS \"typeFlag\" ");
        sql.append(" , imd.inout_flag AS \"inoutFlag\"");
        sql.append(" FROM in_main_doc imd ");
        sql.append(" WHERE 1=1 ");

        if (CoreUtils.isNotNull(model.getMainDoc())) {
            sql.append(
                    " and imd.main_doc like = :mainDoc  ");
            sql.append(" or imd.main_doc_desc like '%'|| :mainDoc  || '%'");
            sql.append(" or imd.type_flag like '%'|| :mainDoc  || '%'");
            sql.append(" or imd.inout_flag like '%'|| :mainDoc  || '%'");
            params.add("mainDoc", model.getMainDoc());
            System.out.println(params);
        }

        SqlSort sort = SqlSort.create(model, Sort.by("mainDoc", Direction.ASC),
                Sort.by("mainDoc", Direction.ASC), Sort.by("mainDoc", Direction.ASC));
        return coreRepository.searchGridData(sql.toString(), params, sort);

    }

    @Transactional
    public Data delete(final Rt13Model model) {


        inmaindocRepository.deleteById(model.getMainDoc());

        return Data.of();
    }

    @Transactional
    public Data searchDetail(final Rt13Model model) {

        Inmaindoc maindoc = inmaindocRepository.findById(model.getMainDoc()).get();

        return Data.of(maindoc);

    }

    @Transactional
    public Data update(final Rt13Model model) {

        Inmaindoc maindoc = inmaindocRepository.findById(model.getMainDoc()).get();



        inmaindocRepository.saveAndFlush(maindoc);


        return searchDetail(model);

    }

}
