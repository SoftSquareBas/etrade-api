package com.tiffa.wd.elock.paperless.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity(name = "in_main_doc")
@Table(name = "in_main_doc")
public class Inmaindoc extends BaseEntity {

    @Id
    @Column(name = "main_doc", length = 10 )
    private String mainDoc;

    @Column(name = "main_doc_desc", length = 100 )
    private String mainDocDesc;

    @Column(name = "type_flag" , length = 1 )
    private String typeFlag;

    @Column(name = "inout_flag", length = 1 )
    private String inoutFlag;



}
