package com.tiffa.wd.elock.paperless.core.service;

import com.tiffa.wd.elock.paperless.core.GridData;
import com.tiffa.wd.elock.paperless.core.Sort;
import com.tiffa.wd.elock.paperless.core.model.Rt01Model;
import com.tiffa.wd.elock.paperless.core.repository.CoreRepository;
import com.tiffa.wd.elock.paperless.core.repository.SqlParams;
import com.tiffa.wd.elock.paperless.core.repository.SqlSort;
import com.tiffa.wd.elock.paperless.core.util.CoreUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

@Service
public class Rt01Service {


    @Autowired
    private CoreRepository coreRepository;




    public GridData search(Rt01Model model) {
        SqlParams params = SqlParams.createPageParam(model);
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT  iw.ware_code AS\"wareCode\" ");
        sql.append(" , iw.ware_name AS \"wareName\" ");
        sql.append(" , iw.active AS \"active\" ");
        sql.append(" , iw.ar_code_br AS \"arCodeBr\" ");
        sql.append(" , iw.sale_id_br AS \"saleCodeBr\" ");
        sql.append(" FROM in_warehouse iw  ");
        sql.append(" WHERE 1=1 ");

        // if (CoreUtils.isNotNull(model.getWareCode())) {
		// 	sql.append("AND iw.ware_code = :wareCode OR iw.ware_code like '%'|| :wareCode || '%' ");
		// 	params.add("wareCode", model.getWareCode());
		// }

        SqlSort sort = SqlSort.create(model, Sort.by("wareCode", Direction.ASC),
                Sort.by("wareCode", Direction.ASC), Sort.by("wareCode", Direction.ASC));
        return coreRepository.searchPagingGridData(sql.toString(), params, sort);
        
    }



    

}
