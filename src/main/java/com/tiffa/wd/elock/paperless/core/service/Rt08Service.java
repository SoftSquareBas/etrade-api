package com.tiffa.wd.elock.paperless.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiffa.wd.elock.paperless.core.Data;
import com.tiffa.wd.elock.paperless.core.GridData;
import com.tiffa.wd.elock.paperless.core.Sort;
import com.tiffa.wd.elock.paperless.core.entity.Inums;
import com.tiffa.wd.elock.paperless.core.model.Rt08Model;
import com.tiffa.wd.elock.paperless.core.repository.CoreRepository;
import com.tiffa.wd.elock.paperless.core.repository.InumsRepository;
import com.tiffa.wd.elock.paperless.core.repository.SqlParams;
import com.tiffa.wd.elock.paperless.core.repository.SqlSort;
import com.tiffa.wd.elock.paperless.core.util.CoreUtils;

@Service
public class Rt08Service {

    @Autowired
    private InumsRepository inumsRepository;
    @Autowired
    private CoreRepository coreRepository;


    public Data check(Rt08Model check) throws Exception{

        SqlParams params = SqlParams.createPageParam(check);
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT  count(1) ");
        sql.append(" FROM in_ums iu  ");
        sql.append(" WHERE iu.ums_code = :umsCode ");
        if(CoreUtils.isNotNull(check.getUpdDate())){
            sql.append("and upd_date is null");
        }
        
        params.add("umsCode", check.getUmsCode());

         

        return coreRepository.getData(sql.toString(), params);
    }

    public Data save(Rt08Model model) throws Exception {

        Inums ums = new Inums();

        ums.setUmsCode(model.getUmsCode());
        ums.setUmsName(model.getUmsName());
        ums.setUmsId(model.getUmsId());
        ums.setActive(model.getActive());

        inumsRepository.saveAndFlush(ums);

        return searchDetail(model);

    }

    public <DdlModel> GridData getUms(){
        SqlParams params = SqlParams.create();
        StringBuilder sql = new StringBuilder();

        sql.append("SELECT unit.unit_code AS \"value\" ");
        sql.append(" , unit.unit_name AS \"name\"");
        sql.append(" ,unit.unit_code|| ' : ' ||unit.unit_name AS \"label\" ");
        sql.append(" FROM db_unit unit");
        sql.append(" ORDER BY unit_code");
        

        return coreRepository.searchGridData(sql.toString(), params);
    }




    public GridData search(Rt08Model model) {
        SqlParams params = SqlParams.createPageParam(model);
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT  iu.ums_code AS \"umsCode\" ");
        sql.append(" , iu.ums_id  AS \"umsId\" ");
        sql.append(" , iu.ums_name   AS \"umsName\" ");
        sql.append(" , iu.active as \"active\"");
        sql.append(" FROM in_ums iu");
        sql.append(" JOIN db_unit du on du.unit_code = iu.ums_code");
        sql.append(" WHERE 1=1 ");

        if (CoreUtils.isNotNull(model.getUmsCode())) {
            sql.append(
                    "and iu.ums_code =:umsCode ");
            sql.append(" or iu.ums_code like '%'|| :umsCode  || '%'");
            sql.append(" or iu.ums_id::varchar like '%'|| :umsCode || '%'");
            sql.append(" or iu.ums_name  like '%'|| :umsCode  || '%'");
            params.add("umsCode", model.getUmsCode());
            System.out.println(params);
        }

        SqlSort sort = SqlSort.create(model, Sort.by("umsCode", Direction.ASC),
                Sort.by("umsCode", Direction.ASC), Sort.by("umsCode", Direction.ASC));
        return coreRepository.searchGridData(sql.toString(), params, sort);

    }

    @Transactional
    public Data delete(final Rt08Model model) {


        inumsRepository.deleteById(model.getUmsCode());

        return Data.of();
    }

    @Transactional
    public Data searchDetail(final Rt08Model model) {

        Inums ums = inumsRepository.findById(model.getUmsCode()).get();

        return Data.of(ums);

    }

    @Transactional
    public Data update(final Rt08Model model) {

        Inums ums = inumsRepository.findById(model.getUmsCode()).get();



        inumsRepository.saveAndFlush(ums);


        return searchDetail(model);

    }

}
