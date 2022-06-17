package com.tiffa.wd.elock.paperless.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity(name = "in_brand")
@Table(name = "in_brand")
public class Inbrand extends BaseEntity {

    @Id
    @Column(name = "brand_code", length = 10 )
    private String brandCode;

    @Column(name = "brand_name", length = 100 )
    private String brandName;

    @Column(name = "brand_name_en", length = 100 )
    private String brandNameEn;

    @Column(name = "active", length = 1 )
    private String active;

    @Column(name = "exporter" )
    private String exporter;
    
    @Column(name = "supplier"  )
    private String supplier;

}
