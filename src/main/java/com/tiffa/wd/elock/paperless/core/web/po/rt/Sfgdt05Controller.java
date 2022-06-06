package com.tiffa.wd.elock.paperless.core.web.po.rt;

import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;
import com.tiffa.wd.elock.paperless.core.util.Response;
import com.tiffa.wd.elock.paperless.core.model.WareHouseModel;
import com.tiffa.wd.elock.paperless.core.service.Sfgdt05Service;

@Slf4j
@RestController
@RequestMapping("/sfgdt05")

public class Sfgdt05Controller {

    @Autowired
    private Sfgdt05Service sfgdt05service;

    // @PostMapping("/search")
    // @PreAuthorize("hasAuthority('WAREHOUSE_SEARCH')")
    // public Callable<Response> search(@RequestBody WareHouseModel model) {
    // return () -> {
    // log.info("search model : {}", model);
    // return Response.success(sfgdt05service.search(model));
    // };
    // }

}