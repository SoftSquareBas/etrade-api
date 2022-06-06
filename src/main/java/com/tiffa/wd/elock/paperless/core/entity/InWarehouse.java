package com.tiffa.wd.elock.paperless.core.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity(name = "in_warehouse")
@Table(name = "in_warehouse")
public class InWarehouse implements Serializable {

    private static final long serialVersionUID = 4460155681154593524L;

    @Id
    private InWarehousePK pk;

    @Column(name = "ware_name", length = 100)
    private String wareName;

    @Column(name = "active", length = 1)
    private String active;
}
