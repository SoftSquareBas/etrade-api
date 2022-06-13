package com.tiffa.wd.elock.paperless.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity(name = "in_category")
@Table(name = "in_category")
public class Incategory extends BaseEntity {

    @Id
    @Column(name = "category_code", length = 10 )
    private String categoryCode;

    @Column(name = "category_desc", length = 100 )
    private String categoryDesc;

    @Column(name = "category_desc_eng", length = 100 )
    private String categoryDescEng;

    @Column(name = "active", length = 1 )
    private String active;

    @Column(name = "group_code", length = 10 )
    private String groupCode;
}
