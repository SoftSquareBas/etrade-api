package com.tiffa.wd.elock.paperless.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity(name = "in_ums")
@Table(name = "in_ums")
public class Inums extends BaseEntity {

    @Id
    @Column(name = "ums_code", length = 10 )
    private String umsCode;

    @Column(name = "ums_name", length = 100 )
    private String umsName;

    @Column(name = "ums_id" )
    private Integer umsId;

    @Column(name = "active", length = 1 )
    private String active;



}
