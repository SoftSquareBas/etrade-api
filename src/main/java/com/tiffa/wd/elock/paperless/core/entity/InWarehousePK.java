package com.tiffa.wd.elock.paperless.core.entity;

import java.io.Serializable;

import javax.persistence.Column;

import lombok.Data;

@Data
public class InWarehousePK implements Serializable {

    private static final long serialVersionUID = 4460155681154593524L;

    @Column(name = "ou_code", precision = 10)
    private String ouCode;

    @Column(name = "ware_code", precision = 10)
    private String wareCode;

}
