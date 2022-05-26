package com.tiffa.wd.elock.paperless.core.service;

import com.tiffa.wd.elock.paperless.core.GridData;
import com.tiffa.wd.elock.paperless.core.Sort;
import com.tiffa.wd.elock.paperless.core.entity.DbPoType;
import com.tiffa.wd.elock.paperless.core.model.Stport02Model;
import com.tiffa.wd.elock.paperless.core.repository.CoreRepository;
import com.tiffa.wd.elock.paperless.core.repository.PoTypeRepository;
import com.tiffa.wd.elock.paperless.core.repository.SqlParams;
import com.tiffa.wd.elock.paperless.core.repository.SqlSort;

import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import com.tiffa.wd.elock.paperless.core.Data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Sort.Direction;

@Service
public class Stport02Service {

    @Autowired
    private PoTypeRepository poTypeRepository;

    @Autowired
    private CoreRepository coreRepository;

    public Data save(Stport02Model model) throws Exception {
        DbPoType potype = new DbPoType();
        potype.setPoTypeCode(model.getPoTypeCode());
        potype.setPoTypeDesc(model.getPoTypeDesc());
        potype.setActive(model.getActive());
        String.valueOf(poTypeRepository.saveAndFlush(potype).getPoTypeCode());
        return searchDetail(model);
        
    }



    public GridData search(Stport02Model model) {
        SqlParams params = SqlParams.createPageParam(model);
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT  pt.po_type_code AS\"poTypeCode\" ");
        sql.append(" , pt.po_type_desc AS \"poTypeDesc\" ");
        sql.append(" , pt.oid AS \"id\" ");
        sql.append(" , pt.active AS \"active\" ");
        sql.append(" FROM po_type pt ");
        sql.append(" WHERE 1=1 ");

        SqlSort sort = SqlSort.create(model, Sort.by("poTypeCode", Direction.ASC),
                Sort.by("poTypeCode", Direction.ASC), Sort.by("poTypeCode", Direction.ASC));
        return coreRepository.searchPagingGridData(sql.toString(), params, sort);
    }

    @Transactional
    public Data delete(final Stport02Model model) {

        poTypeRepository.deleteById(model.getPoTypeCode());
        return Data.of();
    }

    @Transactional
    public Data searchDetail(final Stport02Model model) {
        
        DbPoType potype = poTypeRepository.findById(model.getPoTypeCode()).get();
        return Data.of(potype);

    }

    @Transactional
    public Data update(final Stport02Model model) {

        DbPoType potype = poTypeRepository.findById(model.getPoTypeCode()).get();
        
        potype.setPoTypeCode(model.getPoTypeCode());
        potype.setPoTypeDesc(model.getPoTypeDesc());
        potype.setActive(model.getActive());
        
        poTypeRepository.saveAndFlush(potype);
        return searchDetail(model);
    }

}
