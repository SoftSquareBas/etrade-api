package com.tiffa.wd.elock.paperless.core.entity;

import java.io.Serializable;

import javax.persistence.Column;

import lombok.Data;

@Data
public class InDocTypePK implements Serializable {

    private static final long serialVersionUID = 4460155681154593524L;

    @Column(name = "doc_type", precision = 10)
    private String docType;

    @Column(name = "main_doc", precision = 10)
    private String mainDoc;

}
