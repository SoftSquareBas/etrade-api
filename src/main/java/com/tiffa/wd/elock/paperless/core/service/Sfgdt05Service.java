package com.tiffa.wd.elock.paperless.core.service;

import com.tiffa.wd.elock.paperless.core.GridData;
import com.tiffa.wd.elock.paperless.core.entity.InWarehouse;
import com.tiffa.wd.elock.paperless.core.model.WareHouseModel;
import com.tiffa.wd.elock.paperless.core.repository.CoreRepository;
import com.tiffa.wd.elock.paperless.core.repository.InWarehouseRepository;
import com.tiffa.wd.elock.paperless.core.repository.SqlParams;
import com.tiffa.wd.elock.paperless.core.util.CoreUtils;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import com.tiffa.wd.elock.paperless.core.Data;
import com.tiffa.wd.elock.paperless.core.GridData;
import org.springframework.transaction.annotation.Transactional;

import com.tiffa.wd.elock.paperless.core.ComboBox;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.tiffa.wd.elock.paperless.core.util.ValidationUtils;

@Service
public class Sfgdt05Service {

    @Autowired
    private CoreRepository coreRepository;

    private InWarehouseRepository inWarehouseRepository;

    // @Transactional
    // @CacheEvict(cacheNames = { "warehouseComboBox" }, allEntries = true)
    // public Data search(final WareHouseModel model) {
    // ValidationUtils.checkRequired(model.getWareName(), "Ware Code");

    // InWarehouse warehouse = inWarehouseRepository.getById(model.getWareCode());
    // warehouse.setWareName(model.getWareName());

    // inWarehouseRepository.saveAndFlush(warehouse);
    // return Data.of(model);
    // }

    // @Cacheable(value = "warehouseComboBox", key = "#model")
    // public GridData searchWarehouse(ComboBox model) {
    // SqlParams params = SqlParams.createComboBox(model);

    // StringBuilder sql = new StringBuilder();
    // sql.append(" SELECT wh.ware_code AS \"value\", ");
    // sql.append(" wh.ware_name AS \"text\" ");
    // sql.append(" FROM in_warehouse wh ");
    // sql.append(" WHERE 1 = 1 ");

    // sql.append(" AND wh.ware_code = :warecode ");
    // params.add("warecode", model.getWarecode());

    // if (CoreUtils.isNotEmpty(model.getQuery())) {
    // sql.append(" AND wh.ware_code LIKE :query");
    // }

    // return coreRepository.searchGridData(sql.toString(), params);
    // }

}
