package com.tiffa.wd.elock.paperless.core.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity(name = "in_doc_type")
@Table(name = "in_doc_type")
public class InDocType implements Serializable {

    private static final long serialVersionUID = 4460155681154593524L;

    @Id
    private InDocTypePK pk;

    @Column(name = "doc_desc", length = 100)
    private String docDesc;

}