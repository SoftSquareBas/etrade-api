package com.tiffa.wd.elock.paperless.core.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity(name = "po_type")
@Table(name = "po_type")
public class DbPoType implements Serializable {

    private static final long serialVersionUID = 4460155681154593524L;

    @Id
    @Column(name = "po_typeCode", length = 4)
    private String poTypeCode;

    @Column(name = "po_type_desc", length = 100)
    private String poTypeDesc;

    @Column(name = "active", length = 1)
    private String active;
}
